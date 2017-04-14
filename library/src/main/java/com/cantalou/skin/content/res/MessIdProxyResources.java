package com.cantalou.skin.content.res;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.util.TypedValue;

import com.cantalou.android.util.Log;
import com.cantalou.android.util.array.BinarySearchIntArray;
import com.cantalou.skin.util.ResourcesUtil;

/**
 * Resources代理类<p>
 * 加载资源时使用资源包中的资源进行替换, 资源包不存在指定的资源时, 使用默认资源.
 *
 * @author cantalou
 * @date 2016年11月29日 下午3:15:02
 */
public class MessIdProxyResources extends KeepIdProxyResources {

    /**
     * 资源id映射, App内置资源和资源包中资源的同名资源id映射<br/>
     * App内:   name:icon, id:0x7F010001<br/>
     * 资源包内: name:icon, id:0x7F010002<br/>
     * 0x7F010001 -> 0x7F010002
     */
    protected SparseIntArray[] idMap = new SparseIntArray[8];

    {
        for (int i = 0; i < idMap.length; i++) {
            idMap[i] = new SparseIntArray();
        }
    }

    /**
     * App内存在,但资源包中不存在的id
     */
    protected BinarySearchIntArray notFoundedIds = new BinarySearchIntArray();

    protected String appPackageName;

    protected String proxyPackageName;

    public MessIdProxyResources(Resources proxyResource, Resources def, String packageName) {
        super(proxyResource, def);
        this.appPackageName = packageName;
    }

    /**
     * 将应用资源id转成皮肤资源id
     *
     * @param id
     * @return 皮肤资源id, 不存在皮肤资源时,返回0
     */
    public synchronized int toSkinId(int id) {

        if (id == 0) {
            return 0;
        }

        if ((id & APP_ID_MASK) != APP_ID_MASK) {
            return 0;
        }

        if (notFoundedIds.contains(id)) {
            Log.v("resource id :{} toSkinId not found ", toHex(id));
            return 0;
        }

        int index = id & 0x00000007;
        int skinId = idMap[index].get(id);
        if (skinId != 0) {
            return skinId;
        }

        String name = getResourceName(id);
        if (TextUtils.isEmpty(name)) {
            Log.v("resource id :{} getResourceName(id) return null", toHex(id));
            return 0;
        }

        //不同的资源包可能包名不一致
        if (proxyPackageName != null) {
            name = name.replace(appPackageName, proxyPackageName);
        }

        skinId = proxyResource.getIdentifier(name, null, appPackageName);
        if (skinId == 0) {
            if (proxyPackageName == null) {
                proxyPackageName = ResourcesUtil.loadPackageFromManifest(proxyResource);
                name = name.replace(appPackageName, proxyPackageName);
                skinId = proxyResource.getIdentifier(name, null, proxyPackageName);
            }

            if (skinId == 0) {
                notFoundedIds.put(id);
            }
        } else {
            idMap[index].put(id, skinId);
        }
        Log.v("convert name:{},id:{} to skin id:{}", name, toHex(id), toHex(skinId));
        return skinId;
    }

    public Drawable loadDrawable(int id) throws NotFoundException {

        if (id == 0) {
            return null;
        }

        if ((id & APP_ID_MASK) != APP_ID_MASK) {
            return getDrawable(id);
        }

        int skinId = toSkinId(id);
        if (skinId == 0) {
            return super.loadDrawable(id);
        }

        return super.loadDrawable(skinId);
    }

    public ColorStateList loadColorStateList(int id) throws NotFoundException {

        if (id == 0) {
            return null;
        }

        if ((id & APP_ID_MASK) != APP_ID_MASK) {
            return getColorStateList(id);
        }

        int skinId = toSkinId(id);
        if (skinId == 0) {
            return super.loadColorStateList(id);
        }

        return super.loadColorStateList(skinId);

    }

    @Override
    public void getValue(int id, TypedValue outValue, boolean resolveRefs) throws NotFoundException {

        if (id == 0) {
            return;
        }

        if ((id & APP_ID_MASK) != APP_ID_MASK) {
            super.getValue(id, outValue, resolveRefs);
            return;
        }

        super.getValue(id, outValue, resolveRefs);
        if (outValue.string != null && outValue.string.toString().contains("layout")) {
            return;
        }

        int skinId = toSkinId(id);
        if (skinId == 0) {
            super.getValue(id, outValue, resolveRefs);
            return;
        }

        Resources res = proxyResource;
        try {
            super.getValue(skinId, outValue, resolveRefs);
        } catch (Exception e) {
            Log.e(e);
        }
    }

    public void clearCache() {
        super.clearCache();
        for (SparseIntArray sia : idMap) {
            sia.clear();
        }
        notFoundedIds.clear();
    }

}