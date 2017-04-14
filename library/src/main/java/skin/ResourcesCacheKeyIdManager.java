package skin;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.util.SparseIntArray;
import android.util.TypedValue;

import com.cantalou.android.util.FileUtil;
import com.cantalou.android.util.Log;
import com.cantalou.android.util.ReflectUtil;
import com.cantalou.android.util.array.BinarySearchIntArray;
import com.cantalou.android.util.array.SparseLongIntArray;
import skin.content.res.ProxyResources;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static com.cantalou.android.util.ReflectUtil.findMethod;
import static com.cantalou.android.util.ReflectUtil.forName;
import static com.cantalou.android.util.ReflectUtil.get;
import static com.cantalou.android.util.ReflectUtil.invoke;

/**
 * Resources中资源id和缓存key映射的管理类, 以实现缓存key到资源id的反向查询(key -> id)<p>
 * TypeValue value; <br>
 * 1.Drawable Resources : key = (((long) value.assetCookie) << 32) | value.data<br>
 * 2.Color Resources : key = value.data<br>
 * 3.Color Selector : key = (((long) value.assetCookie) << 32) | value.data;
 *
 * @author cantalou
 * @date 2016年1月31日 下午5:30:09
 */
public final class ResourcesCacheKeyIdManager {

    /**
     * 资源缓存key与资源id的映射
     */
    private SparseLongIntArray drawableCacheKeyIdMap = new SparseLongIntArray();

    /**
     * 颜色StateList缓存key与资源id的映射
     */
    private SparseLongIntArray colorStateListCacheKeyIdMap = new SparseLongIntArray();

    /**
     * 菜单id和菜单icon资源id映射
     */
    private SparseIntArray menuItemIdAndIconIdMap = new SparseIntArray();

    /**
     * 已注册扫描过的布局文件
     */
    private BinarySearchIntArray registeredLayout = new BinarySearchIntArray();

    /**
     * 已注册的资源id
     */
    private BinarySearchIntArray registeredId = new BinarySearchIntArray();

    /**
     * 资源管理对象
     */
    private SkinManager skinManager;

    private Constructor<?> menuConstructor;

    private Method inflateMethod;

    private Object menuInflater;

    private Thread parseNameIdThread = new Thread("parseNameIdThread") {
        @Override
        public void run() {
            Resources res = skinManager.getDefaultResources();
            TypedValue out = new TypedValue();
            BufferedReader br = null;
            String line;
            long startTime = System.currentTimeMillis();
            try {
                InputStream is = res.getAssets().open("nameId.txt");
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((line = br.readLine()) != null) {
                    //type:name:id
                    String[] typeNameId = line.split(":");
                    int id = 0;
                    try {
                        id = Integer.parseInt(typeNameId[2], 16);
                        res.getValue(id, out, true);
                    } catch (Resources.NotFoundException e) {
                        continue;
                    }
                    if ("color".equals(typeNameId[0])) {
                        if (out.string != null && out.string.toString().endsWith(".xml")) {
                            registerColorStateList(id, out);
                        } else {
                            registerDrawable(id, out);
                        }
                    } else if ("drawable".equals(typeNameId[0]) || "mipmap".equals(typeNameId[0])) {
                        registerDrawable(id, out);
                    }
                }
            } catch (Exception e) {
                Log.w("Preload resource id key map error, {}", e);
            } finally {
                FileUtil.close(br);
            }
            Log.i("Parse nameId.txt finish , time {}ms", System.currentTimeMillis() - startTime);
        }
    };

    ResourcesCacheKeyIdManager() {
    }

    /**
     * 注册图片资源id
     */
    public synchronized void registerDrawable(int id, TypedValue value) {
        registeredId.put(id);
        Resources defaultResources = skinManager.getDefaultResources();
        long key;
        boolean isColorDrawable = value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT;

        key = isColorDrawable ? value.data : (((long) value.assetCookie) << 32) | value.data;
        drawableCacheKeyIdMap.put(key, id);
        Log.v("register drawable {} 0x{} to key:{}", defaultResources.getResourceName(id), Integer.toHexString(id), key);

    }

    /**
     * 注册资源id
     */
    public synchronized void registerColorStateList(int id, TypedValue value) {
        registeredId.put(id);
        Resources defaultResources = skinManager.getDefaultResources();
        long key;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            key = (((long) value.assetCookie) << 32) | value.data;
        } else {
            key = (value.assetCookie << 24) | value.data;
        }
        colorStateListCacheKeyIdMap.put(key, id);
        Log.v("register drawable {} 0x{} to key:{}", defaultResources.getResourceName(id), Integer.toHexString(id), key);
    }

    /**
     * 注册菜单资源id
     *
     * @param id
     */
    public synchronized void registerMenu(int id) {
        if ((ProxyResources.APP_ID_MASK & id) != ProxyResources.APP_ID_MASK) {
            return;
        }
        ArrayList<Activity> activities = skinManager.getActivities();
        int size = activities.size();
        if (size == 0) {
            return;
        }

        Activity activity = activities.get(size - 1);
        try {

            if (menuInflater == null) {
                menuInflater = ReflectUtil.invoke(activity, "getSupportMenuInflater");

                if (menuInflater == null) {
                    menuInflater = ReflectUtil.invoke(activity, "getMenuInflater");
                }
                inflateMethod = ReflectUtil.findMethod(menuInflater.getClass(), "inflate");

                Class<?>[] parameterTypes = inflateMethod.getParameterTypes();
                Class<?> menuBuilderKlass = ReflectUtil.forName("com.android.internal.view.menu.MenuBuilder");
                if (menuBuilderKlass == null || !parameterTypes[1].isAssignableFrom(menuBuilderKlass)) {
                    menuBuilderKlass = ReflectUtil.forName("android.support.v7.view.menu.MenuBuilder");
                }
                if (menuBuilderKlass == null || !parameterTypes[1].isAssignableFrom(menuBuilderKlass)) {
                    menuBuilderKlass = ReflectUtil.forName("com.actionbarsherlock.internal.view.menu.MenuBuilder");
                }
                menuConstructor = menuBuilderKlass.getConstructors()[0];
            }

            Object menu = menuConstructor.newInstance(activity);
            inflateMethod.invoke(menuInflater, id, menu);
            ArrayList<?> items = ReflectUtil.get(menu, "mItems");
            for (Object menuItem : items) {
                int iconResId = ReflectUtil.get(menuItem, "mIconResId");
                if (iconResId > 0) {
                    int itemId = ReflectUtil.get(menuItem, "mId");
                    menuItemIdAndIconIdMap.put(itemId, iconResId);
                }
            }
        } catch (Exception e) {
            Log.e(e);
        }
    }

    /**
     * 注册layout资源id
     *
     * @param id
     */
    public synchronized void registerLayout(int id) {

        if ((ProxyResources.APP_ID_MASK & id) != ProxyResources.APP_ID_MASK) {
            return;
        }

        if (registeredLayout.contains(id)) {
            return;
        }

        ArrayList<Activity> activities = skinManager.getActivities();
        int size = activities.size();
        if (size == 0) {
            return;
        }

        Resources defaultResources = skinManager.getDefaultResources();
        Log.v("register layout {} 0x{}", defaultResources.getResourceName(id), Integer.toHexString(id));

        if (isMenuLayout(defaultResources, id)) {
            registerMenu(id);
        }
        registeredLayout.put(id);
    }

    /**
     * 判断当前layout文件类型是否为菜单
     *
     * @param defaultResources
     * @param resId
     * @return
     */
    private boolean isMenuLayout(Resources defaultResources, int resId) {
        XmlResourceParser parser = null;
        try {
            parser = defaultResources.getLayout(resId);
            int eventType = parser.getEventType();
            String tagName;
            // This loop will skip to the menu start tag
            do {
                if (eventType == XmlPullParser.START_TAG) {
                    tagName = parser.getName();
                    if (tagName.equals("menu")) {
                        return true;
                    }
                }
                eventType = parser.next();
            } while (eventType != XmlPullParser.END_DOCUMENT);
        } catch (Throwable e) {
            Log.e(e);
        } finally {
            if (parser != null) {
                parser.close();
            }
        }
        return false;
    }

    public SparseLongIntArray getDrawableCacheKeyIdMap() {
        return drawableCacheKeyIdMap;
    }

    public SparseLongIntArray getColorStateListCacheKeyIdMap() {
        return colorStateListCacheKeyIdMap;
    }

    public SparseIntArray getMenuItemIdAndIconIdMap() {
        return menuItemIdAndIconIdMap;
    }

    public void setSkinManager(SkinManager skinManager) {
        this.skinManager = skinManager;
        parseNameIdThread.start();
    }

    public void reset() {
        drawableCacheKeyIdMap.clear();
        colorStateListCacheKeyIdMap.clear();
        menuItemIdAndIconIdMap.clear();
    }
}
