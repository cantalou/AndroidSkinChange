package com.cantalou.skin;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.util.SparseIntArray;
import android.util.TypedValue;

import com.cantalou.android.util.Log;
import com.cantalou.android.util.array.BinarySearchIntArray;
import com.cantalou.android.util.array.SparseLongIntArray;
import com.cantalou.skin.content.res.ProxyResources;

import org.xmlpull.v1.XmlPullParser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static com.cantalou.android.util.ReflectUtil.findMethod;
import static com.cantalou.android.util.ReflectUtil.forName;
import static com.cantalou.android.util.ReflectUtil.get;
import static com.cantalou.android.util.ReflectUtil.invoke;

/**
 * @author cantalou
 * @date 2016年1月31日 下午5:30:09
 */
public final class CacheKeyIdManager {

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
     * 已注册的Color资源id
     */
    private BinarySearchIntArray registeredColorId = new BinarySearchIntArray();

    /**
     * 资源管理对象
     */
    private SkinManager skinManager;

    private Constructor<?> menuConstructor;

    private Method inflateMethod;

    private Object menuInflater;

    CacheKeyIdManager() {
    }

    /**
     * 注册图片资源id
     */
    public synchronized void registerDrawable(int id, TypedValue value) {
        registeredId.put(id);
        Resources defaultResources = skinManager.getDefaultResources();
        long key;
        boolean isColorDrawable = value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT;

        if (!isColorDrawable) {
            key = isColorDrawable ? value.data : (((long) value.assetCookie) << 32) | value.data;
            drawableCacheKeyIdMap.put(key, id);
            Log.v("register drawable {} 0x{} to key:{}", defaultResources.getResourceName(id), Integer.toHexString(id), key);
        } else {
            registeredColorId.put(id);
        }
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
                menuInflater = invoke(activity, "getSupportMenuInflater");

                if (menuInflater == null) {
                    menuInflater = invoke(activity, "getMenuInflater");
                }
                inflateMethod = findMethod(menuInflater.getClass(), "inflate");

                Class<?>[] parameterTypes = inflateMethod.getParameterTypes();
                Class<?> menuBuilderKlass = forName("com.android.internal.view.menu.MenuBuilder");
                if (menuBuilderKlass == null || !parameterTypes[1].isAssignableFrom(menuBuilderKlass)) {
                    menuBuilderKlass = forName("android.support.v7.view.menu.MenuBuilder");
                }
                if (menuBuilderKlass == null || !parameterTypes[1].isAssignableFrom(menuBuilderKlass)) {
                    menuBuilderKlass = forName("com.actionbarsherlock.internal.view.menu.MenuBuilder");
                }
                menuConstructor = menuBuilderKlass.getConstructors()[0];
            }

            Object menu = menuConstructor.newInstance(activity);
            inflateMethod.invoke(menuInflater, id, menu);
            ArrayList<?> items = get(menu, "mItems");
            for (Object menuItem : items) {
                int iconResId = get(menuItem, "mIconResId");
                if (iconResId > 0) {
                    int itemId = get(menuItem, "mId");
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
    }

    public void reset() {
        drawableCacheKeyIdMap.clear();
        colorStateListCacheKeyIdMap.clear();
        menuItemIdAndIconIdMap.clear();
    }
}
