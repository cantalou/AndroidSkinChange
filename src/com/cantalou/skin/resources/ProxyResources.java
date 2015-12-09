package com.cantalou.skin.resources;

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
import android.text.TextUtils;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TypedValue;

import com.cantalou.android.util.ReflectUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import static com.cantalou.android.util.ReflectUtil.get;
import static com.cantalou.android.util.ReflectUtil.invoke;

/**
 * Resources代理类<br>
 * 重写loadDrawable,loadColorStateList方法进行资源加载的拦截. 加载时使用资源包中的资源进行替换, 资源包
 * 不存在指定的资源时,使用默认资源.
 *
 * @author LinZhiWei
 * @date 2015年11月29日 下午3:15:02
 */
public class ProxyResources extends Resources
{

    static final String TAG = "ProxyResources";

    /**
     * app资源id前缀
     */
    public static final int APP_ID_MASK = 0x7F000000;

    /**
     * 资源名称缓存数量
     */
    public static final int RESOURCE_NAME_CACHE_SIZE = 31;

    /**
     * 皮肤资源id映射
     */
    protected SparseIntArray skinIdMap = new SparseIntArray();

    /**
     * 皮肤资源不存在的id
     */
    protected SparseIntArray notFoundedSkinIds = new SparseIntArray();

    /**
     * 皮肤资源
     */
    protected Resources skinResources;

    /**
     * 默认资源
     */
    protected Resources defaultResources;

    /**
     * 应用包名
     */
    protected String packageName;

    /**
     * 皮肤资源包名
     */
    protected String skinName;

    /**
     * 资源名称缓存id
     */
    protected int[] resourceNameIdCache = new int[RESOURCE_NAME_CACHE_SIZE + 1];

    /**
     * 资源名称缓存
     */
    protected String[] resourceNameCache = new String[RESOURCE_NAME_CACHE_SIZE + 1];

    /**
     * Drawable资源缓存(图片,xml)
     */
    protected LongSparseArray<WeakReference<ConstantState>> drawableCache = new LongSparseArray<WeakReference<ConstantState>>();

    /**
     * colorDrawable资源缓存(单色值)
     */
    protected LongSparseArray<WeakReference<ConstantState>> colorDrawableCache = new LongSparseArray<WeakReference<ConstantState>>();

    /**
     * colorStateList资源缓存(多状态色值)
     */
    protected SparseArray<WeakReference<ColorStateList>> colorStateListCache = new SparseArray<WeakReference<ColorStateList>>();

    /**
     * Create a new SkinResources object on top of an existing set of assets in
     * an AssetManager.
     *
     * @param skinRes skin resources
     * @param defRes  default resources
     */
    public ProxyResources(String packageName, Resources skinRes, Resources defRes, String skinName)
    {
        super(defRes.getAssets(), defRes.getDisplayMetrics(), defRes.getConfiguration());
        skinResources = skinRes;
        defaultResources = defRes;
        this.packageName = packageName;
        this.skinName = skinName;
    }

    /**
     * 将应用资源id转成皮肤资源id
     *
     * @param id
     * @return 皮肤资源id, 不存在皮肤资源时,返回0
     */
    public synchronized int toSkinId(int id)
    {

        if (id == 0 || skinResources == null)
        {
            return 0;
        }

        if ((id & APP_ID_MASK) != APP_ID_MASK)
        {
            return id;
        }

        // 如果皮肤资源包不存在当前资源项,直接返回0
        if (notFoundedSkinIds.get(id) > 0)
        {
            return 0;
        }

        int skinId = skinIdMap.get(id);
        if (skinId != 0)
        {
            return skinId;
        }

        String name = getResourceName(id);
        if (TextUtils.isEmpty(name))
        {
            return 0;
        }

        skinId = skinResources.getIdentifier(name, null, packageName);
        if (skinId == 0)
        {
            notFoundedSkinIds.put(id, id);
        }
        else
        {
            skinIdMap.put(id, skinId);
        }
        return skinId;
    }

    protected String toString(TypedValue value)
    {
        return " skinId:" + toHex(toSkinId(value.resourceId)) + " " + (TextUtils.isEmpty(value.string) ? value + ",name:" + getResourceName(value.resourceId) : value.toString());
    }

    protected String toHex(Object id)
    {
        if (id == null)
        {
            return "null";
        }
        if (id instanceof Number)
        {
            return "0x" + Integer.toHexString(((Number) id).intValue());
        }
        else
        {
            return id.toString();
        }

    }

    @Override
    public synchronized String getResourceName(int resId) throws NotFoundException
    {
        if (resId == 0)
        {
            return "";
        }

        int index = resId & RESOURCE_NAME_CACHE_SIZE;
        if (resourceNameIdCache[index] == resId)
        {
            return resourceNameCache[index];
        }

        try
        {
            String name = super.getResourceName(resId);
            resourceNameIdCache[index] = resId;
            resourceNameCache[index] = name;
            return name;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    protected boolean isColor(TypedValue value)
    {
        return value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT;
    }

    protected Class<?>[] loadParamType = new Class<?>[]{TypedValue.class, int.class};

    @SuppressWarnings("deprecation")
    Drawable loadDrawable(TypedValue value, int id) throws NotFoundException
    {

        if (id == 0)
        {
            return null;
        }

        long key = (((long) value.assetCookie) << 32) | value.data;
        boolean isColor = isColor(value);
        LongSparseArray<WeakReference<ConstantState>> cache = isColor ? colorDrawableCache : drawableCache;

        Drawable result = getCachedDrawable(cache, key);
        if (result != null)
        {
            Log.v(TAG, "loadDrawable " + toString(value) + " from " + this + " cache ");
            return result;
        }

        // 系统资源
        if ((id & APP_ID_MASK) != APP_ID_MASK)
        {
            result = invoke(defaultResources, "loadDrawable", loadParamType, value, id);
        }
        else
        {

            int skinId = toSkinId(id);
            String log = "loadDrawable " + toString(value);
            Resources res;
            if (skinId == 0)
            {
                Log.v(TAG, log + " toSkinId() not found ");
                res = defaultResources;
            }
            else
            {
                res = skinResources;
                if (isColor)
                {
                    // 由于色值的资源是直接保存在value的data中,这里需要再次获取
                    res.getValue(skinId, value, true);
                }
            }

            if (isColor)
            {
                result = invoke(res, "loadDrawable", loadParamType, value, id);
            }
            else
            {
                String file = value.string.toString();
                if (file.endsWith(".xml"))
                {
                    try
                    {
                        XmlResourceParser rp = invoke(res, "loadXmlResourceParser", new Class[]{
                                String.class, int.class, int.class, String.class
                        }, file, id, value.assetCookie, "drawable");
                        result = Drawable.createFromXml(this, rp);
                        rp.close();
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.getMessage());
                    }
                }
                else
                {
                    InputStream is = null;
                    try
                    {
                        is = invoke(res.getAssets(), "openNonAsset", new Class[]{int.class, String.class, int.class}, value.assetCookie, file, AssetManager.ACCESS_STREAMING);
                        BitmapFactory.Options opts = new BitmapFactory.Options();
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                        {
                            opts.inPreferredConfig = Bitmap.Config.RGB_565;
                            ReflectUtil.set(opts, "inNativeAlloc", true);
                        }
                        opts.inPurgeable = true;
                        opts.inInputShareable = true;
                        result = Drawable.createFromResourceStream(res, value, is, file, opts);
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.getMessage());
                    }
                    finally
                    {
                        if (is != null)
                        {
                            try
                            {
                                is.close();
                            }
                            catch (IOException e)
                            {
                                // ignore
                            }
                        }
                    }
                }
            }

            Object resultInfo = result instanceof ColorDrawable ? toHex(get(result, "mState.mUseColor")) : result;
            Log.v(TAG, log + ",result:" + resultInfo + " from " + res);
            // 如果皮肤中存在资源, 但加载失败则直接从默认资源中加载
            if (result == null && skinId != 0)
            {
                result = invoke(defaultResources, "loadDrawable", loadParamType, value, id);
            }
        }

        if (result != null)
        {
            synchronized (this)
            {
                cache.put(key, new WeakReference<ConstantState>(result.getConstantState()));
            }
        }
        return result;
    }

    @SuppressWarnings("deprecation")
    ColorStateList loadColorStateList(TypedValue value, int id) throws NotFoundException
    {

        if (id == 0)
        {
            return null;
        }

        String log = "loadColorStateList " + toString(value);

        int key = (value.assetCookie << 24) | value.data;

        ColorStateList result = getCachedColorStateList(key);
        if (result != null)
        {
            Log.v(TAG, log + " from cache ");
            return result;
        }
        boolean isColor = isColor(value);
        // 系统资源
        if ((id & APP_ID_MASK) != APP_ID_MASK)
        {
            result = invoke(defaultResources, "loadColorStateList", loadParamType, value, id);
        }
        else
        {

            // 将app资源id转换成皮肤资源id
            int skinId = toSkinId(id);

            Resources res;
            if (skinId == 0)
            {
                Log.v(TAG, log + " convertId not found ");
                res = defaultResources;
            }
            else
            {
                res = skinResources;
                if (isColor)
                {
                    res.getValue(skinId, value, true);
                }
            }

            if (isColor)
            {
                result = ColorStateList.valueOf(value.data);
            }
            else
            {
                String file = value.string.toString();
                if (file.endsWith(".xml"))
                {
                    try
                    {
                        XmlResourceParser rp = invoke(res, "loadXmlResourceParser", new Class[]{
                                String.class, int.class, int.class, String.class
                        }, file, id, value.assetCookie, "drawable");
                        result = ColorStateList.createFromXml(res, rp);
                        rp.close();
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            Log.v(TAG, log + ",result:" + result + " from " + skinResources);
            // 如果皮肤中存在资源, 但加载失败则直接从默认资源中加载
            if (result == null && skinId != 0)
            {
                result = invoke(res, "loadColorStateList", loadParamType, value, id);
            }
        }

        if (result != null)
        {
            synchronized (this)
            {
                colorStateListCache.put(key, new WeakReference<ColorStateList>(result));
            }
        }
        return result;
    }

    protected synchronized Drawable getCachedDrawable(LongSparseArray<WeakReference<ConstantState>> cache, long key)
    {
        WeakReference<ConstantState> wr = cache.get(key);
        if (wr != null)
        { // we have the key
            ConstantState entry = wr.get();
            if (entry != null)
            {
                return entry.newDrawable(this);
            }
            else
            { // our entry has been purged
                cache.delete(key);
            }
        }
        return null;
    }

    protected synchronized ColorStateList getCachedColorStateList(int key)
    {
        WeakReference<ColorStateList> wr = colorStateListCache.get(key);
        if (wr != null)
        { // we have the key
            ColorStateList entry = wr.get();
            if (entry != null)
            {
                return entry;
            }
            else
            { // our entry has been purged
                colorStateListCache.delete(key);
            }
        }
        return null;
    }

    public void clearCache()
    {
        skinIdMap.clear();
        notFoundedSkinIds.clear();
        resourceNameIdCache = new int[RESOURCE_NAME_CACHE_SIZE];
        resourceNameCache = new String[RESOURCE_NAME_CACHE_SIZE];
        drawableCache.clear();
        colorDrawableCache.clear();
        colorStateListCache.clear();
    }

    @Override
    public String toString()
    {
        return getClass().getSimpleName() + "{" + skinName + "}";
    }
}