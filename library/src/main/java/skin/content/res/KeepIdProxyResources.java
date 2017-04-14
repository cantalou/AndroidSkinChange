package skin.content.res;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import com.cantalou.android.util.Log;
import com.cantalou.android.util.array.BinarySearchIntArray;
import skin.ResourcesManager;
import skin.SkinManager;

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
     * 默认资源
     */
    protected Resources skinResource;

    /**
     * 资源包不存在的id
     */
    protected BinarySearchIntArray notFoundedSkinIds = new BinarySearchIntArray();

    protected ResourcesManager resourcesManager;

    protected SkinManager skinManager;

    /**
     * @param skin 皮肤资源
     * @param def  默认资源
     */
    public KeepIdProxyResources(Resources skin, Resources def) {
        super(def);
        skinResource = skin;
        skinManager = SkinManager.getInstance();
        resourcesManager = ResourcesManager.getInstance();
    }

    @Override
    public Drawable loadDrawable(int id) throws NotFoundException {

        if (notFoundedSkinIds.contains(id)) {
            return null;
        }

        Resources res = skinResource;
        TypedValue value = typedValueCache;
        try {
            res.getValue(id, value, true);
            return loadDrawable(res, value, id);
        } catch (Exception e) {
            Log.w("Fail to loadDrawable from Resources {} ,{}", res, e);
            if (e instanceof NotFoundException) {
                notFoundedSkinIds.put(id);
            }
            return super.getDrawable(id);
        }
    }

    @Override
    public ColorStateList loadColorStateList(int id) throws NotFoundException {

        if (notFoundedSkinIds.contains(id)) {
            return null;
        }

        Resources res = skinResource;
        TypedValue value = typedValueCache;
        try {
            res.getValue(id, value, true);
            return loadColorStateList(res, value, id);
        } catch (Exception e) {
            Log.w("Fail to loadColorStateList from Resources {}  ,{}", res, e);
            if (e instanceof NotFoundException) {
                notFoundedSkinIds.put(id);
            }
            return super.getColorStateList(id);
        }
    }

    @Override
    public void getValue(int id, TypedValue outValue, boolean resolveRefs) throws NotFoundException {

        if (notFoundedSkinIds.contains(id)) {
            super.getValue(id, outValue, resolveRefs);
            return;
        }

        Resources res = skinResource;
        try {
            super.getValue(id, outValue, resolveRefs);
            if (isColor(outValue)) {
                res.getValue(id, outValue, resolveRefs);
            }
        } catch (Exception e) {
            Log.w("Fail to getValue from Resources {}  ,{}", res, e);
            super.getValue(id, outValue, resolveRefs);
            if (e instanceof NotFoundException) {
                notFoundedSkinIds.put(id);
            }
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public void getValueForDensity(int id, int density, TypedValue outValue, boolean resolveRefs) throws NotFoundException {

        if (notFoundedSkinIds.contains(id)) {
            super.getValueForDensity(id, density, outValue, resolveRefs);
            return;
        }

        Resources res = skinResource;
        try {
            super.getValueForDensity(id, density, outValue, resolveRefs);
            if (isColor(outValue)) {
                res.getValueForDensity(id, density, outValue, resolveRefs);
            }
        } catch (Exception e) {
            Log.w("Fail to getValue from Resources {}  ,{}", res, e);
            super.getValueForDensity(id, density, outValue, resolveRefs);
            if (e instanceof NotFoundException) {
                notFoundedSkinIds.put(id);
            }
        }

    }

    public void clearCache() {
        super.clearCache();
        notFoundedSkinIds.clear();
    }

}