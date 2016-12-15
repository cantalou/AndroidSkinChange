package com.cantalou.skin.content.res;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import com.cantalou.android.util.Log;
import com.cantalou.android.util.array.BinarySearchIntArray;
import com.cantalou.skin.ResourcesManager;

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

    /**
     * @param skin 皮肤资源
     * @param def  默认资源
     */
    public KeepIdSkinProxyResources(Resources skin, Resources def) {
        super(skin);
        this.def = def;
        resourcesManager = ResourcesManager.getInstance();
    }

    public Drawable loadDrawable(int id) throws NotFoundException {

        if (id == 0) {
            return null;
        }

        //系统资源直接返回
        if ((id & APP_ID_MASK) != APP_ID_MASK) {
            return getDrawable(id);
        }

        //皮肤资源包中不存在, 直接从默认资源包中返回
        if (notFoundedSkinIds.contains(id)) {
            Log.d("从皮肤资源包中不存在资源id:{}", toHex(id));
            return def.getDrawable(id);
        }

        Drawable result = null;
        try {
            result = super.getDrawable(id);
            Log.d("从皮肤资源包中加载资源id:{},result:{}", toHex(id), result);
        } catch (NotFoundException e) {
            Log.e(e);
            notFoundedSkinIds.put(id);
            result = def.getDrawable(id);
            Log.d("从皮肤资源包中加载资源失败id:{}, 从默认资源包加载", toHex(id));
        }

        return result;
    }

    public ColorStateList loadColorStateList(int id) throws NotFoundException {

        if (id == 0) {
            return null;
        }

        //系统资源直接返回
        if ((id & APP_ID_MASK) != APP_ID_MASK) {
            return getColorStateList(id);
        }

        //皮肤资源包中不存在, 直接从默认资源包中返回
        if (notFoundedSkinIds.contains(id)) {
            Log.d("从皮肤资源包中不存在资源id:{}", toHex(id));
            return def.getColorStateList(id);
        }

        ColorStateList result = null;
        try {
            result = getColorStateList(id);
            Log.d("从皮肤资源包中加载资源id:{},result:{}", toHex(id), result);
        } catch (NotFoundException e) {
            Log.e(e);
            notFoundedSkinIds.put(id);
            result = def.getColorStateList(id);
            Log.d("从皮肤资源包中加载资源失败id:{}, 从默认资源包加载", toHex(id));
        }

        return result;
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