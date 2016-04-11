package com.cantalou.skin.content.res;

import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.os.Build;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.TypedValue;

import com.cantalou.android.util.Log;
import com.cantalou.android.util.ReflectUtil;
import com.cantalou.skin.CacheKeyAndIdManager;
import com.cantalou.skin.SkinManager;
import com.cantalou.skin.array.ColorStateListLongSpareArray;
import com.cantalou.skin.array.ColorStateListSpareArray;
import com.cantalou.skin.array.DrawableLongSpareArray;

import java.io.InputStream;

import static com.cantalou.android.util.ReflectUtil.get;
import static com.cantalou.android.util.ReflectUtil.invoke;
import static com.cantalou.android.util.ReflectUtil.set;

/**
 * 重写getDrawable,getColorStateList,getColor方法进行资源加载的拦截, 将资源id注册到SkinManger中
 *
 * @author cantalou
 * @date 2015年12月12日 下午11:07:07
 */
@SuppressWarnings("deprecation")
public class ProxyResources extends Resources {

    public static final boolean logEnable = true;

    /**
     * app资源id前缀
     */
    public static final int APP_ID_MASK = 0x7F000000;

    /**
     * 资源名称缓存数量
     */
    public static final int RESOURCE_NAME_CACHE_SIZE = 31;

    protected static final Class<?>[] loadXmlResourceParserParam = new Class[] { String.class, int.class, int.class, String.class };

    protected static final Class<?>[] openNonAssetParam = new Class[] { int.class, String.class, int.class };

    /**
     * 资源名称缓存id
     */
    protected int[] resourceNameIdCache = new int[RESOURCE_NAME_CACHE_SIZE + 1];

    /**
     * 资源名称缓存
     */
    protected String[] resourceNameCache = new String[RESOURCE_NAME_CACHE_SIZE + 1];

    protected static LongSparseArray<ConstantState> proxyPreloadedDrawables;

    protected static LongSparseArray<ConstantState> proxyPreloadedColorDrawables;

    protected static LongSparseArray<ColorStateList> proxyPreloadedColorStateLists16;

    protected static SparseArray<ColorStateList> proxyPreloadedColorStateLists;

    protected static CacheKeyAndIdManager cacheKeyAndIdManager;

    /**
     * 替换Resources的相关preload对象引用
     */
    static {

	SkinManager skinManager = SkinManager.getInstance();

	cacheKeyAndIdManager = CacheKeyAndIdManager.getInstance();

	// drawable
	LongSparseArray<ConstantState> originalPreloadedDrawables;
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
	    LongSparseArray<ConstantState>[] sPreloadedDrawablesArray = get(Resources.class, "sPreloadedDrawables");
	    originalPreloadedDrawables = sPreloadedDrawablesArray[0];
	} else {
	    originalPreloadedDrawables = get(Resources.class, "sPreloadedDrawables");
	}

	proxyPreloadedDrawables = new DrawableLongSpareArray(skinManager, originalPreloadedDrawables, cacheKeyAndIdManager.getDrawableCacheKeyIdMap());
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
	    LongSparseArray<ConstantState>[] sPreloadedDrawablesArray = get(Resources.class, "sPreloadedDrawables");
	    sPreloadedDrawablesArray[0] = proxyPreloadedDrawables;
	} else {
	    set(Resources.class, "sPreloadedDrawables", proxyPreloadedDrawables);
	}

	// colorDrawable
	if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR2) {
	    LongSparseArray<ConstantState> originalPreloadedColorDrawables = get(Resources.class, "sPreloadedColorDrawables");
	    proxyPreloadedColorDrawables = new DrawableLongSpareArray(skinManager, originalPreloadedColorDrawables, cacheKeyAndIdManager.getColorDrawableCacheKeyIdMap());
	    set(Resources.class, "sPreloadedColorDrawables", proxyPreloadedColorDrawables);
	}

	// colorStateList
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
	    LongSparseArray<ColorStateList> originalPreloadedColorStateLists16 = get(Resources.class, "sPreloadedColorStateLists");
	    proxyPreloadedColorStateLists16 = new ColorStateListLongSpareArray(skinManager, originalPreloadedColorStateLists16,
		    cacheKeyAndIdManager.getColorStateListCacheKeyIdMap());
	    set(Resources.class, "sPreloadedColorStateLists", proxyPreloadedColorStateLists16);
	} else {
	    SparseArray<ColorStateList> originalPreloadedColorStateLists = get(Resources.class, "mPreloadedColorStateLists");
	    proxyPreloadedColorStateLists = new ColorStateListSpareArray(skinManager, originalPreloadedColorStateLists, cacheKeyAndIdManager.getColorStateListCacheKeyIdMap());
	    set(Resources.class, "mPreloadedColorStateLists", proxyPreloadedColorStateLists);
	}
    }

    protected static TypedValue logValue = new TypedValue();

    protected final TypedValue typedValueCache = new TypedValue();

    public ProxyResources(Resources res) {
	super(res.getAssets(), res.getDisplayMetrics(), res.getConfiguration());
    }

    @Override
    public int getColor(int id) throws NotFoundException {
	cacheKeyAndIdManager.registerDrawable(id);
	return super.getColor(id);
    }

    @Override
    public ColorStateList getColorStateList(int id) throws NotFoundException {
	cacheKeyAndIdManager.registerColorStateList(id);
	return super.getColorStateList(id);
    }

    @Override
    public Drawable getDrawable(int id, Theme theme) throws NotFoundException {
	cacheKeyAndIdManager.registerDrawable(id);
	return super.getDrawable(id, theme);
    }

    @Override
    public Drawable getDrawable(int id) throws NotFoundException {
	cacheKeyAndIdManager.registerDrawable(id);
	return super.getDrawable(id);
    }

    @Override
    public Drawable getDrawableForDensity(int id, int density, Theme theme) {
	cacheKeyAndIdManager.registerDrawable(id);
	return super.getDrawableForDensity(id, density, theme);
    }

    @Override
    public Drawable getDrawableForDensity(int id, int density) throws NotFoundException {
	cacheKeyAndIdManager.registerDrawable(id);
	return super.getDrawableForDensity(id, density);
    }

    protected String toString(TypedValue value) {
	logValue.setTo(value);
	logValue.string = getResourceName(value.resourceId);
	return logValue.toString();
    }

    public static String toHex(int id) {
	return "0x" + Integer.toHexString(id);
    }

    protected String toHex(Object id) {
	if (id == null) {
	    return "null";
	}
	if (id instanceof Number) {
	    return "0x" + Integer.toHexString(((Number) id).intValue());
	} else {
	    return id.toString();
	}
    }

    @Override
    public synchronized String getResourceName(int resId) throws NotFoundException {

	if (resId == 0) {
	    return "";
	}

	int index = resId & RESOURCE_NAME_CACHE_SIZE;
	if (resourceNameIdCache[index] == resId) {
	    return resourceNameCache[index];
	}

	try {
	    String name = super.getResourceName(resId);
	    resourceNameIdCache[index] = resId;
	    resourceNameCache[index] = name;
	    return name;
	} catch (Exception e) {
	    return null;
	}
    }

    public Drawable loadDrawable(int id) throws NotFoundException {
	TypedValue value = typedValueCache;
	getValue(id, value, true);
	Drawable dr = loadDrawable(this, value, id);
	if (dr == null) {
	    dr = getDrawable(id);
	    if (logEnable && (id & APP_ID_MASK) == APP_ID_MASK) {
		Log.v("loadDrawable(Resources, TypedValue, int) return null, retry load value:{} from :{} result:{} ", toString(value), this, dr);
	    }
	}
	return dr;
    }

    @Override
    public XmlResourceParser getLayout(int id) throws NotFoundException {
	cacheKeyAndIdManager.registerLayout(id);
	return super.getLayout(id);
    }

    /**
     * 重写Resource.loadDrawable方法, 增加在加载图片资源时内存占用调整
     *
     * @param res
     * @param value
     * @param id
     * @return
     * @throws NotFoundException
     */
    protected Drawable loadDrawable(Resources res, TypedValue value, int id) throws NotFoundException {
	boolean isColorDrawable = value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT;
	Drawable dr = null;
	if (isColorDrawable) {
	    dr = new ColorDrawable(value.data);
	} else {
	    if (value.string == null) {
		throw new NotFoundException("Resource is not a Drawable (color or path): " + value);
	    }

	    String file = value.string.toString();
	    if (file.endsWith(".xml")) {
		try {
		    XmlResourceParser rp = invoke(this, "loadXmlResourceParser", loadXmlResourceParserParam, file, id, value.assetCookie, "drawable");
		    dr = Drawable.createFromXml(res, rp);
		    rp.close();
		} catch (Exception e) {
		    Log.w(e, "File {} from drawable resource ID #0x{} not found in {}", file, Integer.toHexString(id), this);
		}
	    } else {
		try {
		    InputStream is = invoke(res.getAssets(), "openNonAsset", openNonAssetParam, value.assetCookie, file, AssetManager.ACCESS_STREAMING);
		    BitmapFactory.Options opts = new BitmapFactory.Options();
		    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			ReflectUtil.set(opts, "inNativeAlloc", true);
		    }
		    opts.inPurgeable = true;
		    opts.inInputShareable = true;
		    dr = Drawable.createFromResourceStream(res, value, is, file, opts);
		    is.close();
		} catch (Exception e) {
		    Log.w("File :{} from drawable resource ID #0x{} not found in :{}", file, Integer.toHexString(id), res);
		    Log.w(e);
		}
	    }
	}

	if (dr != null) {
	    dr.setChangingConfigurations(value.changingConfigurations);
	}

	if (logEnable && (id & APP_ID_MASK) == APP_ID_MASK) {
	    Log.v("load value:{} from :{} result:{} ", toString(value), res, dr);
	}
	return dr;
    }

    public ColorStateList loadColorStateList(int id) throws NotFoundException {
	TypedValue value = typedValueCache;
	this.getValue(id, value, true);
	ColorStateList csl = loadColorStateList(this, value, id);
	if (csl == null) {
	    csl = getColorStateList(id);
	    if (logEnable && (id & APP_ID_MASK) == APP_ID_MASK) {
		Log.v("loadColorStateList(Resources, TypedValue, int) return null, retry load value:{} from :{} result:{} ", toString(value), this, csl);
	    }
	}
	return csl;
    }

    protected ColorStateList loadColorStateList(Resources res, TypedValue value, int id) throws NotFoundException {

	ColorStateList csl = null;
	if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
	    csl = ColorStateList.valueOf(value.data);
	    return csl;
	}

	if (value.string == null) {
	    throw new NotFoundException("Resource is not a ColorStateList (color or path): " + value);
	}

	String file = value.string.toString();

	if (file.endsWith(".xml")) {
	    try {
		XmlResourceParser rp = invoke(this, "loadXmlResourceParser", loadXmlResourceParserParam, file, id, value.assetCookie, "colorstatelist");
		csl = ColorStateList.createFromXml(res, rp);
		rp.close();
	    } catch (Exception e) {
		Log.w("File {} from color state list resource ID #0x{} not found in {}", file, Integer.toHexString(id), res);
		Log.w(e);
	    }
	} else {
	    throw new NotFoundException("File " + file + " from drawable resource ID #0x" + Integer.toHexString(id) + ": .xml extension required");
	}

	if (logEnable && (id & APP_ID_MASK) == APP_ID_MASK) {
	    Log.v("load value:{} from :{} result:{} ", toString(value), res, csl);
	}

	return csl;
    }

    public void clearCache() {
	resourceNameIdCache = new int[RESOURCE_NAME_CACHE_SIZE];
	resourceNameCache = new String[RESOURCE_NAME_CACHE_SIZE];
    }
}
