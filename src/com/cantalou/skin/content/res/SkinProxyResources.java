package com.cantalou.skin.content.res;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.util.TypedValue;

import com.cantalou.android.util.Log;
import com.cantalou.android.util.array.BinarySearchIntArray;

/**
 * 皮肤Resources代理类<br>
 * 加载资源时使用皮肤资源包中的资源进行替换, 皮肤资源包 不存在指定的资源时, 使用默认资源.
 *
 * @author cantalou
 * @date 2015年11月29日 下午3:15:02
 */
public class SkinProxyResources extends StaticProxyResources {

    /**
     * 皮肤资源
     */
    protected Resources skinResources;

    /**
     * 应用包名
     */
    protected String packageName;

    /**
     * 皮肤资源
     */
    protected String skinPath;

    /**
     * 皮肤资源id映射
     */
    protected SparseIntArray skinIdMap = new SparseIntArray();

    /**
     * 皮肤资源不存在的id
     */
    protected BinarySearchIntArray notFoundedSkinIds = new BinarySearchIntArray();

    /**
     * Create a new ProxyResources object
     *
     * @param skinRes     skin resources
     * @param defRes      default resources
     * @param packageName
     * @param skinPath
     */
    public SkinProxyResources(String packageName, Resources skinRes, Resources defRes, String skinPath) {
        super(defRes);
        this.skinResources = skinRes;
        this.packageName = packageName;
        this.skinPath = skinPath;
    }

    /**
     * Create a new ProxyResources
     *
     * @param defRes default resources
     */
    public SkinProxyResources(Resources defRes) {
        this("", null, defRes, "");
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

        if (notFoundedSkinIds.contains(id)) {
            Log.v("resource id :{} toSkinId not found ", toHex(id));
            return 0;
        }

        int skinId = skinIdMap.get(id);
        if (skinId != 0) {
            return skinId;
        }

        String name = getResourceName(id);
        if (TextUtils.isEmpty(name)) {
            Log.v("resource id :{} getResourceName(id) return null", toHex(id));
            return 0;
        }

        skinId = skinResources.getIdentifier(name, null, packageName);
        if (skinId == 0) {
            notFoundedSkinIds.put(id);
            Log.v("resource id :{} getIdentifier(name, null, packageName) return null", toHex(id));
        } else {
            skinIdMap.put(id, skinId);
        }
        Log.v("convert name:{},id:{} to skin id:{}", name, toHex(id), toHex(skinId));
        return skinId;
    }

    protected boolean isColor(TypedValue value) {
        return value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT;
    }

    public Drawable loadDrawable(int id) throws NotFoundException {
        if (id == 0) {
            return null;
        }

        TypedValue value = typedValueCache;
        getValue(id, value, true);

        Resources res;
        int skinId;
        if ((id & APP_ID_MASK) != APP_ID_MASK || (skinId = toSkinId(id)) == 0) {
            res = this;
            skinId = id;
        } else {
            res = skinResources;
            if (isColor(value)) {
                res.getValue(skinId, value, true);
            }
        }

        Drawable result = null;
        try {
            result = loadDrawable(res, value, skinId);
        } catch (Exception e) {
            Log.e(e);
        }

        // 如果皮肤中存在资源, 但加载失败则直接从默认资源中加载
        if (result == null && skinId != 0) {
            result = loadDrawable(this, value, id);
        }
        return result;
    }

    public ColorStateList loadColorStateList(int id) throws NotFoundException {

        if (id == 0) {
            return null;
        }

        TypedValue value = typedValueCache;
        getValue(id, value, true);

        Resources res;
        int skinId;
        if ((id & APP_ID_MASK) != APP_ID_MASK || (skinId = toSkinId(id)) == 0) {
            res = this;
            skinId = id;
        } else {
            res = skinResources;
            if (isColor(value)) {
                res.getValue(skinId, value, true);
            }
        }

        ColorStateList result = null;
        try {
            result = loadColorStateList(res, value, skinId);
        } catch (Exception e) {
            Log.e(e);
        }

        // 如果皮肤中存在资源, 但加载失败则直接从默认资源中加载
        if (result == null && skinId != 0) {
            result = loadColorStateList(this, value, id);
        }
        return result;
    }

    public void clearCache() {
        super.clearCache();
        skinIdMap.clear();
        notFoundedSkinIds.clear();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + skinPath + "}";
    }

    public Resources getSkinResources() {
        return skinResources;
    }


}