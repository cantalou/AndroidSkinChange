package com.cantalou.skin.content.res;

import android.annotation.TargetApi;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.cantalou.android.util.Log;
import com.cantalou.android.util.array.BinarySearchIntArray;
import com.cantalou.skin.ResourcesManager;
import com.cantalou.skin.SkinManager;

/**
 * Resources代理类<p>
 * 加载资源时优先加载资源包中的资源, 资源包不存在时, 使用默认资源.<br>
 * 1.重写 loadDrawable(int)和loadColorStateList(int)方法先从资源包中加载数据,不存在是从默认资源中加载   <br>
 *
 * @author cantalou
 * @date 2016年11月5日 下午3:15:02
 */
public class KeepIdProxyResources extends ProxyResources {

    /**
     * 被代理资源
     */
    protected Resources proxyResource;

    /**
     * 资源包不存在的id
     */
    protected BinarySearchIntArray notFoundedInProxyIds = new BinarySearchIntArray();

    protected ResourcesManager resourcesManager;

    protected SkinManager skinManager;

    /**
     * @param proxyResource 被代理资源
     * @param def           默认资源
     */
    public KeepIdProxyResources(Resources proxyResource, Resources def) {
        super(def);
        this.proxyResource = proxyResource;
        skinManager = SkinManager.getInstance();
        resourcesManager = ResourcesManager.getInstance();
    }

    @Override
    public Drawable loadDrawable(int id) throws NotFoundException {

        if (notFoundedInProxyIds.contains(id)) {
            return null;
        }

        TypedValue value = typedValueCache;
        try {
            proxyResource.getValue(id, value, true);
            return loadDrawable(proxyResource, value, id);
        } catch (Exception e) {
            Log.w("Fail to loadDrawable from Resources {} ,{}", proxyResource, e);
            if (e instanceof NotFoundException) {
                notFoundedInProxyIds.put(id);
            }
            return super.getDrawable(id);
        }
    }

    @Override
    public ColorStateList loadColorStateList(int id) throws NotFoundException {

        if (notFoundedInProxyIds.contains(id)) {
            return null;
        }

        TypedValue value = typedValueCache;
        try {
            proxyResource.getValue(id, value, true);
            return loadColorStateList(proxyResource, value, id);
        } catch (Exception e) {
            Log.w("Fail to loadColorStateList from Resources {}  ,{}", proxyResource, e);
            if (e instanceof NotFoundException) {
                notFoundedInProxyIds.put(id);
            }
            return super.getColorStateList(id);
        }
    }

    @Override
    public void getValue(int id, TypedValue outValue, boolean resolveRefs) throws NotFoundException {

        if (notFoundedInProxyIds.contains(id)) {
            super.getValue(id, outValue, resolveRefs);
            return;
        }

        Resources res = proxyResource;
        try {
            super.getValue(id, outValue, resolveRefs);
            if (isColor(outValue)) {
                res.getValue(id, outValue, resolveRefs);
            }
        } catch (Exception e) {
            Log.w("Fail to getValue from Resources {}  ,{}", res, e);
            super.getValue(id, outValue, resolveRefs);
            if (e instanceof NotFoundException) {
                notFoundedInProxyIds.put(id);
            }
        }
    }

    public void superGetValue(int id, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
        super.getValue(id, outValue, resolveRefs);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public void getValueForDensity(int id, int density, TypedValue outValue, boolean resolveRefs) throws NotFoundException {

        if (notFoundedInProxyIds.contains(id)) {
            super.getValueForDensity(id, density, outValue, resolveRefs);
            return;
        }

        Resources res = proxyResource;
        try {
            super.getValueForDensity(id, density, outValue, resolveRefs);
            if (isColor(outValue)) {
                res.getValueForDensity(id, density, outValue, resolveRefs);
            }
        } catch (Exception e) {
            Log.w("Fail to getValue from Resources {}  ,{}", res, e);
            super.getValueForDensity(id, density, outValue, resolveRefs);
            if (e instanceof NotFoundException) {
                notFoundedInProxyIds.put(id);
            }
        }

    }

    public void clearCache() {
        super.clearCache();
        notFoundedInProxyIds.clear();
    }

}