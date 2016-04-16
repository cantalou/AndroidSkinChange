/**
 *
 */
package com.cantalou.skin;

import static com.cantalou.android.util.ReflectUtil.findByMethod;
import static com.cantalou.android.util.ReflectUtil.forName;
import static com.cantalou.android.util.ReflectUtil.get;
import static com.cantalou.android.util.ReflectUtil.invoke;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.cantalou.android.util.Log;
import com.cantalou.android.util.array.BinarySearchIntArray;
import com.cantalou.android.util.array.SparseLongIntArray;
import com.cantalou.skin.content.res.SkinProxyResources;

/**
 * @author cantalou
 * @date 2016年1月31日 下午5:30:09
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public final class CacheKeyAndIdManager {

    /**
     * 是否检测不同资源id相同资源key的情况
     */
    public static boolean checkDuplicatedKey = true;

    /**
     * 资源缓存key与资源id的映射
     */
    private SparseLongIntArray drawableCacheKeyIdMap = new SparseLongIntArray();

    /**
     * 颜色缓存key与资源id的映射
     */
    private SparseLongIntArray colorDrawableCacheKeyIdMap = new SparseLongIntArray();

    /**
     * 颜色StateList缓存key与资源id的映射
     */
    private SparseLongIntArray colorStateListCacheKeyIdMap = new SparseLongIntArray();

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

    private Constructor<?> menuConstructor;

    private Method inflateMethod;

    private Object menuInflater;

    CacheKeyAndIdManager() {
    }

    /**
     * 注册图片资源id
     */
    public synchronized void registerDrawable(int id) {

	if ((SkinProxyResources.APP_ID_MASK & id) != SkinProxyResources.APP_ID_MASK) {
	    return;
	}

	if (handledDrawableId.contains(id)) {
	    Log.v("Registered drawable id:{}, ignore", id);
	    return;
	}
	Resources defaultResources = skinManager.getDefaultResources();

	TypedValue value = cacheValue;
	defaultResources.getValue(id, value, true);
	long key;
	int exitsId;
	if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR2) {
	    boolean isColorDrawable = value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT;
	    key = isColorDrawable ? value.data : (((long) value.assetCookie) << 32) | value.data;
	    if (isColorDrawable) {
		exitsId = colorDrawableCacheKeyIdMap.put(key, id);
	    } else {
		exitsId = drawableCacheKeyIdMap.put(key, id);
	    }
	} else {
	    key = (((long) value.assetCookie) << 32) | value.data;
	    exitsId = drawableCacheKeyIdMap.put(key, id);
	}
	if (checkDuplicatedKey && exitsId > 0) {
	    throw new IllegalStateException("Different resources id maps to the same key");
	}
	Log.v("register drawable {} 0x{} to key:{}", defaultResources.getResourceName(id), Integer.toHexString(id), key);
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
	    Log.v("Registered colorStateList id:{}, ignore", id);
	    return;
	}
	Resources defaultResources = skinManager.getDefaultResources();

	TypedValue value = cacheValue;
	defaultResources.getValue(id, value, true);
	long key;
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
	    key = (((long) value.assetCookie) << 32) | value.data;
	} else {
	    key = (value.assetCookie << 24) | value.data;
	}
	if (checkDuplicatedKey && colorStateListCacheKeyIdMap.put(key, id) > 0) {
	    throw new IllegalStateException("Different resources id maps to the same key");
	}
	Log.v("register drawable {} 0x{} to key:{}", defaultResources.getResourceName(id), Integer.toHexString(id), key);
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
	ArrayList<Activity> activities = skinManager.getActivitys();
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
		inflateMethod = findByMethod(menuInflater.getClass(), "inflate");

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
		    registerDrawable(iconResId);
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
	if ((SkinProxyResources.APP_ID_MASK & id) != SkinProxyResources.APP_ID_MASK) {
	    return;
	}
	ArrayList<Activity> activities = skinManager.getActivitys();
	int size = activities.size();
	if (size == 0) {
	    return;
	}

	if (handledDrawableId.contains(id)) {
	    Log.v("Registered layout id:{}, ignore", id);
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
		// Spcify parent view if layout contains merge element
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

    public SparseLongIntArray getColorDrawableCacheKeyIdMap() {
	return colorDrawableCacheKeyIdMap;
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

}
