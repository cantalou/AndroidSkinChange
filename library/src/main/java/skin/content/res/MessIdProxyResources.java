package skin.content.res;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.util.TypedValue;

import com.cantalou.android.util.Log;
import com.cantalou.android.util.array.BinarySearchIntArray;

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
    protected SparseIntArray[] idMap = new SparseIntArray[7];

    /**
     * App内存在,但资源包中不存在的id
     */
    protected BinarySearchIntArray notFoundedIds = new BinarySearchIntArray();

    protected String packageName;

    public MessIdProxyResources(Resources skin, Resources def, String packageName) {
        super(skin, def);
        this.packageName = packageName;
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

        skinId = skinResource.getIdentifier(name, null, packageName);
        if (skinId == 0) {
            notFoundedIds.put(id);
            Log.v("resource id :{} getIdentifier(name, null, packageName) return null", toHex(id));
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

        Resources res = skinResource;
        TypedValue value = typedValueCache;

        Drawable result = null;
        try {
            res.getValue(skinId, value, true);
            result = loadDrawable(res, value, skinId);
        } catch (Exception e) {
            Log.e(e);
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
            res = skinResource;
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

        return result;
    }

    public void clearCache() {
        super.clearCache();
        for (SparseIntArray sia : idMap) {
            sia.clear();
        }
        notFoundedIds.clear();
    }

}