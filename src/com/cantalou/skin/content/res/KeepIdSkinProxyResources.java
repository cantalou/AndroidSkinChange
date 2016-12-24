package com.cantalou.skin.content.res;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import com.cantalou.android.util.Log;
import com.cantalou.android.util.array.BinarySearchIntArray;
import com.cantalou.skin.ResourcesManager;
import com.cantalou.skin.SkinManager;

/**
 * 应用皮肤Resources代理类, 加载资源时优先加载皮肤资源包中的资源, 皮肤资源包 不存在指定的资源时, 使用默认资源.<br>
 * 1.重写 loadDrawable(int)和loadColorStateList(int)方法直接从皮肤资源中加载数据,不存在是从默认资源中加载   <br>
 * 2.重写 getLayout(int)和getXml(int)方法,
 *
 * @author cantalou
 * @date 2016年11月5日 下午3:15:02
 */
public class KeepIdSkinProxyResources extends ProxyResources {

    /**
     * 默认资源
     */
    protected Resources def;

    /**
     * 皮肤资源不存在的id
     */
    protected BinarySearchIntArray notFoundedSkinIds = new BinarySearchIntArray();

    protected ResourcesManager resourcesManager;

    protected SkinManager skinManager;

    /**
     * @param skin 皮肤资源
     * @param def  默认资源
     */
    public KeepIdSkinProxyResources(Resources skin, Resources def) {
        super(skin);
        this.def = def;
        skinManager = SkinManager.getInstance();
        resourcesManager = ResourcesManager.getInstance();
    }

    public Drawable loadDrawable(int id) throws NotFoundException {

        if (notFoundedSkinIds.contains(id)) {
            return null;
        }

        Resources res = skinManager.getCurrentResources();
        TypedValue value = typedValueCache;
        try {
            res.getValue(id, value, true);
            return loadDrawable(res, value, id);
        } catch (Exception e) {
            Log.w("Fail to loadDrawable from Resources {}  ,{}", res, e);
        }
        return null;
    }

    public ColorStateList loadColorStateList(int id) throws NotFoundException {

        if (notFoundedSkinIds.contains(id)) {
            return null;
        }

        Resources res = skinManager.getCurrentResources();
        TypedValue value = typedValueCache;
        try {
            res.getValue(id, value, true);
            return loadColorStateList(res, value, id);
        } catch (Exception e) {
            Log.w("Fail to loadColorStateList from Resources {}  ,{}", res, e);
        }
        return null;
    }

    @Override
    public void getValue(int id, TypedValue outValue, boolean resolveRefs) throws NotFoundException {

        if (notFoundedSkinIds.contains(id)) {
            super.getValue(id, outValue, resolveRefs);
            return ;
        }

        Resources res = skinManager.getCurrentResources();
        try {
            res.getValue(id, outValue, resolveRefs);
        } catch (Exception e) {
            Log.w("Fail to getValue from Resources {}  ,{}", res, e);
            super.getValue(id, outValue, resolveRefs);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public void getValueForDensity(int id, int density, TypedValue outValue, boolean resolveRefs) throws NotFoundException {

        if (notFoundedSkinIds.contains(id)) {
            super.getValueForDensity(id, density, outValue, resolveRefs);
            return ;
        }

        Resources res = skinManager.getCurrentResources();
        try {
            res.getValueForDensity(id, density, outValue, resolveRefs);
        } catch (Exception e) {
            Log.w("Fail to getValue from Resources {}  ,{}", res, e);
            super.getValueForDensity(id, density, outValue, resolveRefs);
        }
    }

    public void clearCache() {
        super.clearCache();
        notFoundedSkinIds.clear();
    }

    @Override
    public XmlResourceParser getLayout(int id) throws NotFoundException {
        return resourcesManager.isSafeLayout(id) ? super.getLayout(id) : def.getLayout(id);
    }

    @Override
    public XmlResourceParser getXml(int id) throws NotFoundException {
        return resourcesManager.isSafeLayout(id) ? super.getXml(id) : def.getXml(id);
    }

}