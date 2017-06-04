package com.cantalou.skin.array;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build;
import android.util.LongSparseArray;

import com.cantalou.android.util.array.SparseLongIntArray;
import com.cantalou.skin.SkinManager;
import com.cantalou.skin.content.res.hook.ProxyResources;

/**
 * 系统版本高于Build.VERSION_CODES.
 * JELLY_BEAN时Resources类的静态变量mPreloadedColorStateLists使用的是LongSparseArray
 * <ColorStateList>类型
 *
 * @author cantalou
 * @date 2016年4月13日 下午11:00:14
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ColorStateListLongSpareArray extends LongSparseArray<ColorStateList> {

    private SparseLongIntArray resourceIdKeyMap;

    /**
     * Resources mColorStateListCache
     */
    private LongSparseArray<ColorStateList> originalCache;

    private SkinManager skinManager;

    public ColorStateListLongSpareArray(SkinManager skinManager, LongSparseArray<ColorStateList> originalCache, SparseLongIntArray resourceIdKeyMap) {
        this.skinManager = skinManager;
        this.originalCache = originalCache;
        this.resourceIdKeyMap = resourceIdKeyMap;
    }

    @Override
    public ColorStateList get(long key) {

        int id = resourceIdKeyMap.get(key);
        if (id != 0) {
            Resources res = skinManager.getCurrentResources();
            if (res != null && res instanceof ProxyResources && !((ProxyResources) res).isCurrentLoading()) {
                return ((ProxyResources) res).loadColorStateList(id);
            }
        }
        return originalCache.get(key);
    }

    public LongSparseArray<ColorStateList> getOriginalCache() {
        return originalCache;
    }
}
