/**
 *
 */
package com.cantalou.skin.content.res;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable.ConstantState;
import android.os.Build;
import android.util.LongSparseArray;
import android.util.SparseArray;

import com.cantalou.skin.SkinManager;
import com.cantalou.skin.array.ColorStateListLongSpareArray;
import com.cantalou.skin.array.ColorStateListSpareArray;
import com.cantalou.skin.array.DrawableLongSpareArray;

import static com.cantalou.android.util.ReflectUtil.get;
import static com.cantalou.android.util.ReflectUtil.set;

/**
 * @author cantalou
 * @date 2016年5月2日 下午4:12:58
 */
public class StaticProxyResources extends ProxyResources {

    /**
     * Replaced Resource static pre field
     */
    private static volatile boolean replaced = false;

    public StaticProxyResources(Resources res) {
        super(res);
    }

    /**
     * 替换Resources的相关preload对象引用
     */
    public void replaceCacheEntry() {

        synchronized (ProxyResources.class) {

            if (replaced) {
                return;
            }
            replaced = true;

            SkinManager skinManager = SkinManager.getInstance();

            // drawable
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                LongSparseArray<ConstantState>[] sPreloadedDrawablesArray = get(Resources.class, "sPreloadedDrawables");
                LongSparseArray<ConstantState> proxyPreloadedDrawables = new DrawableLongSpareArray(skinManager, sPreloadedDrawablesArray[0],
                        cacheKeyAndIdManager.getDrawableCacheKeyIdMap());
                sPreloadedDrawablesArray[0] = proxyPreloadedDrawables;
            } else {
                LongSparseArray<ConstantState> originalPreloadedDrawables = get(Resources.class, "sPreloadedDrawables");
                LongSparseArray<ConstantState> proxyPreloadedDrawables = new DrawableLongSpareArray(skinManager, originalPreloadedDrawables,
                        cacheKeyAndIdManager.getDrawableCacheKeyIdMap());
                set(Resources.class, "sPreloadedDrawables", proxyPreloadedDrawables);
            }

            // colorDrawable
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR2) {
                LongSparseArray<ConstantState> originalPreloadedColorDrawables = get(Resources.class, "sPreloadedColorDrawables");
                LongSparseArray<ConstantState> proxyPreloadedColorDrawables = new DrawableLongSpareArray(skinManager, originalPreloadedColorDrawables,
                        cacheKeyAndIdManager.getColorDrawableCacheKeyIdMap());
                set(Resources.class, "sPreloadedColorDrawables", proxyPreloadedColorDrawables);
            }

            // colorStateList
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                LongSparseArray<ColorStateList> originalPreloadedColorStateLists16 = get(Resources.class, "sPreloadedColorStateLists");
                LongSparseArray<ColorStateList> proxyPreloadedColorStateLists16 = new ColorStateListLongSpareArray(skinManager, originalPreloadedColorStateLists16,
                        cacheKeyAndIdManager.getColorStateListCacheKeyIdMap());
                set(Resources.class, "sPreloadedColorStateLists", proxyPreloadedColorStateLists16);
            } else {
                SparseArray<ColorStateList> originalPreloadedColorStateLists = get(Resources.class, "mPreloadedColorStateLists");
                SparseArray<ColorStateList> proxyPreloadedColorStateLists = new ColorStateListSpareArray(skinManager, originalPreloadedColorStateLists,
                        cacheKeyAndIdManager.getColorStateListCacheKeyIdMap());
                set(Resources.class, "mPreloadedColorStateLists", proxyPreloadedColorStateLists);
            }
        }

    }

    /**
     * 还原Resources的相关preload对象引用
     */
    public void revertCacheEntry() {

        synchronized (ProxyResources.class) {

            if (!replaced) {
                replaced = false;
            }

            SkinManager skinManager = SkinManager.getInstance();

            // drawable
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                LongSparseArray<ConstantState>[] sPreloadedDrawablesArray = get(Resources.class, "sPreloadedDrawables");
                sPreloadedDrawablesArray[0] = ((DrawableLongSpareArray) sPreloadedDrawablesArray[0]).getOriginalCache();
            } else {
                DrawableLongSpareArray originalPreloadedDrawables = get(Resources.class, "sPreloadedDrawables");
                set(Resources.class, "sPreloadedDrawables", originalPreloadedDrawables.getOriginalCache());
            }

            // colorDrawable
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR2) {
                DrawableLongSpareArray originalPreloadedColorDrawables = get(Resources.class, "sPreloadedColorDrawables");
                set(Resources.class, "sPreloadedColorDrawables", originalPreloadedColorDrawables.getOriginalCache());
            }

            // colorStateList
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ColorStateListLongSpareArray originalPreloadedColorStateLists16 = get(Resources.class, "sPreloadedColorStateLists");
                set(Resources.class, "sPreloadedColorStateLists", originalPreloadedColorStateLists16.getOriginalCache());
            } else {
                ColorStateListSpareArray originalPreloadedColorStateLists = get(Resources.class, "mPreloadedColorStateLists");
                set(Resources.class, "mPreloadedColorStateLists", originalPreloadedColorStateLists.getOriginalCache());
            }
        }
    }
}
