/**
 *
 */
package com.cantalou.skin;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.util.LongSparseArray;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.cantalou.android.util.BinarySearchIntArray;
import com.cantalou.android.util.Log;
import com.cantalou.skin.content.res.SkinProxyResources;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

import static com.cantalou.android.util.ReflectUtil.findByMethod;
import static com.cantalou.android.util.ReflectUtil.forName;
import static com.cantalou.android.util.ReflectUtil.get;
import static com.cantalou.android.util.ReflectUtil.invoke;

/**
 * @author cantalou
 * @date 2016年1月31日 下午5:30:09
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public final class CacheKeyAndIdManager {

    /**
     * 资源缓存key与资源id的映射
     */
    private LongSparseArray<Integer> drawableCacheKeyIdMap = new LongSparseArray<Integer>();

    /**
     * 颜色缓存key与资源id的映射
     */
    private LongSparseArray<Integer> colorDrawableCacheKeyIdMap = new LongSparseArray<Integer>();

    /**
     * 颜色StateList缓存key与资源id的映射
     */
    private LongSparseArray<Integer> colorStateListCacheKeyIdMap = new LongSparseArray<Integer>();

    /**
     * 菜单id和菜单icon资源id映射
     */
    private SparseIntArray menuItemIdAndIconIdMap = new SparseIntArray();

    /**
     * 已处理过的资源id,包括图片,颜色,selector文件,xml文件
     */
    private BinarySearchIntArray handledDrawableId = new BinarySearchIntArray();

    /**
     * 缓存对象
     */
    private TypedValue cacheValue = new TypedValue();

    /**
     * 资源管理对象
     */
    private SkinManager skinManager;

    private static class InstanceHolder {
	static final CacheKeyAndIdManager INSTANCE = new CacheKeyAndIdManager();
    }

    private CacheKeyAndIdManager() {
    }

    public static CacheKeyAndIdManager getInstance() {
	return InstanceHolder.INSTANCE;
    }

    /**
     * 注册图片资源id
     */
    public synchronized void registerDrawable(int id) {

	if ((SkinProxyResources.APP_ID_MASK & id) != SkinProxyResources.APP_ID_MASK) {
	    return;
	}

	if (handledDrawableId.contains(id)) {
	    Log.d("Had registered id:{}, ignore", id);
	    return;
	}

	if (skinManager == null) {
	    skinManager = SkinManager.getInstance();
	}
	Resources defaultResources = skinManager.getDefaultResources();
	Log.v("register drawable {} 0x{}", defaultResources.getResourceName(id), Integer.toHexString(id));

	TypedValue value = cacheValue;
	defaultResources.getValue(id, value, true);
	long key = 0;
	if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
	    boolean isColorDrawable = value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT;
	    key = isColorDrawable ? value.data : (((long) value.assetCookie) << 32) | value.data;
	    if (isColorDrawable) {
		colorDrawableCacheKeyIdMap.put(key, id);
	    } else {
		drawableCacheKeyIdMap.put(key, id);
	    }
	} else {
	    key = (((long) value.assetCookie) << 32) | value.data;
	    drawableCacheKeyIdMap.put(key, id);
	}
	handledDrawableId.put(id);
    }

    /**
     * 注册资源id
     */
    public synchronized void registerColorStateList(int id) {

	if ((SkinProxyResources.APP_ID_MASK & id) != SkinProxyResources.APP_ID_MASK) {
	    return;
	}

	if (handledDrawableId.contains(id)) {
	    Log.d("Had registered id:{}, ignore", id);
	    return;
	}
	if (skinManager == null) {
	    skinManager = SkinManager.getInstance();
	}
	Resources defaultResources = skinManager.getDefaultResources();
	Log.v("register ColorStateList {} 0x{}", defaultResources.getResourceName(id), Integer.toHexString(id));

	TypedValue value = cacheValue;
	defaultResources.getValue(id, value, true);
	long key;
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
	    key = (((long) value.assetCookie) << 32) | value.data;
	} else {
	    key = (value.assetCookie << 24) | value.data;
	}
	colorStateListCacheKeyIdMap.put(key, id);
	handledDrawableId.put(id);
    }

    /**
     * 注册菜单资源id
     *
     * @param id
     */
    public synchronized void registerMenu(int id) {
	if ((SkinProxyResources.APP_ID_MASK & id) != SkinProxyResources.APP_ID_MASK) {
	    return;
	}
	if (skinManager == null) {
	    skinManager = SkinManager.getInstance();
	}
	ArrayList<Activity> activities = skinManager.getActivitys();
	int size = activities.size();
	if (size == 0) {
	    return;
	}

	Activity activity = activities.get(size - 1);
	try {
	    // Native ics, appcompat, actionbarsherlock
	    Object menuInflater = invoke(activity, "getSupportMenuInflater");
	    if (menuInflater == null) {
		menuInflater = invoke(activity, "getMenuInflater");
	    }

	    Class<?> menuBuilderKlass = forName("com.android.internal.view.menu.MenuBuilder");
	    if (menuBuilderKlass == null) {
		menuBuilderKlass = forName("android.support.v7.view.menu.MenuBuilder");
	    }
	    if (menuBuilderKlass == null) {
		menuBuilderKlass = forName("com.actionbarsherlock.internal.view.menu.MenuBuilder");
	    }

	    Object menu = menuBuilderKlass.getConstructor(Context.class).newInstance(activity);
	    findByMethod(menuInflater.getClass(), "inflate").invoke(menuInflater, id, menu);
	    ArrayList<?> items = get(menu, "mItems");
	    for (Object menuItem : items) {
		int iconResId = get(menuItem, "mIconResId");
		int itemId = get(menuItem, "mId");
		if (iconResId > 0) {
		    registerDrawable(iconResId);
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
	if ((SkinProxyResources.APP_ID_MASK & id) != SkinProxyResources.APP_ID_MASK) {
	    return;
	}
	if (skinManager == null) {
	    skinManager = SkinManager.getInstance();
	}
	ArrayList<Activity> activities = skinManager.getActivitys();
	int size = activities.size();
	if (size == 0) {
	    return;
	}

	if (handledDrawableId.contains(id)) {
	    return;
	}
	Resources defaultResources = skinManager.getDefaultResources();
	Log.v("register layout {} 0x{}", defaultResources.getResourceName(id), Integer.toHexString(id));
	handledDrawableId.put(id);

	if (isMenuLayout(defaultResources, id)) {
	    registerMenu(id);
	} else {
	    Activity activity = activities.get(size - 1);
	    LayoutInflater li = activity.getLayoutInflater();

	    try {
		// 兼容layout包含有merge标签
		ViewGroup parent = new FrameLayout(activity);
		li.inflate(id, parent);
	    } catch (Exception e) {
		Log.e(e);
	    }
	}
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
	boolean isMenuLayout = false;
	try {
	    parser = defaultResources.getLayout(resId);
	    int eventType = parser.getEventType();
	    String tagName;
	    // This loop will skip to the menu start tag
	    do {
		if (eventType == XmlPullParser.START_TAG) {
		    tagName = parser.getName();
		    if (tagName.equals("menu")) {
			isMenuLayout = true;
			break;
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
	return isMenuLayout;
    }

    public LongSparseArray<Integer> getDrawableCacheKeyIdMap() {
	return drawableCacheKeyIdMap;
    }

    public LongSparseArray<Integer> getColorDrawableCacheKeyIdMap() {
	return colorDrawableCacheKeyIdMap;
    }

    public LongSparseArray<Integer> getColorStateListCacheKeyIdMap() {
	return colorStateListCacheKeyIdMap;
    }

    public SparseIntArray getMenuItemIdAndIconIdMap() {
	return menuItemIdAndIconIdMap;
    }
}
