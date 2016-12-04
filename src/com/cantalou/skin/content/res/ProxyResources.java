package com.cantalou.skin.content.res;

import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;

import com.cantalou.android.util.Log;
import com.cantalou.android.util.ReflectUtil;
import com.cantalou.skin.CacheKeyAndIdManager;
import com.cantalou.skin.SkinManager;

import java.io.InputStream;

import static com.cantalou.android.util.ReflectUtil.invoke;

/**
 * 代理获取资源<p>
 * 1.拦截所有调用方法注册资源ID和key的对应关系<p>
 * 2.实现loadDrawable(int id)和loadColorStateList(int id)自定义加载资源<p>
 *
 * @author cantalou
 * @date 2015年12月12日 下午11:07:07
 */
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

    /**
     * 资源名称缓存id
     */
    protected int[] resourceNameIdCache = new int[RESOURCE_NAME_CACHE_SIZE + 1];

    /**
     * 资源名称缓存
     */
    protected String[] resourceNameCache = new String[RESOURCE_NAME_CACHE_SIZE + 1];

    protected CacheKeyAndIdManager cacheKeyAndIdManager;

    protected final TypedValue typedValueCache = new TypedValue();

    public ProxyResources(Resources res) {
        super(res.getAssets(), res.getDisplayMetrics(), res.getConfiguration());
        cacheKeyAndIdManager = SkinManager.getInstance().getCacheKeyAndIdManager();
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

    @Override
    public XmlResourceParser getLayout(int id) throws NotFoundException {
        cacheKeyAndIdManager.registerLayout(id);
        return super.getLayout(id);
    }

    protected String toString(TypedValue value) {
        StringBuilder sb = new StringBuilder();
        sb.append("TypedValue{t=0x").append(Integer.toHexString(value.type));
        sb.append("/d=0x").append(Integer.toHexString(value.data));
        if (value.type == TypedValue.TYPE_STRING) {
            sb.append(" \"").append(value.string != null ? value.string : getResourceName(value.resourceId)).append("\"");
        }
        if (value.assetCookie != 0) {
            sb.append(" a=").append(value.assetCookie);
        }
        if (value.resourceId != 0) {
            sb.append(" r=0x").append(Integer.toHexString(value.resourceId));
        }
        sb.append("}");
        return sb.toString();
    }

    public static final String toHex(int id) {
        return "0x" + Integer.toHexString(id);
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

    public void clearCache() {
        resourceNameIdCache = new int[RESOURCE_NAME_CACHE_SIZE];
        resourceNameCache = new String[RESOURCE_NAME_CACHE_SIZE];
    }

}
