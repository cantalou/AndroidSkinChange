package com.cantalou.skin.array;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.util.SparseArray;

import com.cantalou.android.util.array.SparseLongIntArray;
import com.cantalou.skin.SkinManager;
import com.cantalou.skin.content.res.ProxyResources;

/**
 * 系统版本低于Build.VERSION_CODES.
 * JELLY_BEAN时Resources类的静态变量sPreloadedColorStateLists使用的是SparseArray&lt;ColorStateList> 类型
 *
 * @author cantalou
 * @date 2016年4月13日 下午11:00:14
 */
@SuppressLint("NewApi")
public class ColorStateListSpareArray extends SparseArray<ColorStateList> {

    private SparseLongIntArray resourceIdKeyMap;

    /**
     * Resources mColorStateListCache
     */
    private SparseArray<ColorStateList> originalCache;

    private SkinManager skinManager;

    public ColorStateListSpareArray(SkinManager skinManager, SparseArray<ColorStateList> originalCache, SparseLongIntArray resourceIdKeyMap) {
        this.skinManager = skinManager;
        this.originalCache = originalCache;
        this.resourceIdKeyMap = resourceIdKeyMap;
    }

    @Override
    public ColorStateList get(int key) {
        ColorStateList result = null;
        int id = resourceIdKeyMap.get(key);
        if (id != 0) {
            ProxyResources res = skinManager.getCurrentResources();
            if (res != null) {
                result = res.getColorStateList(id);
            }
        }
        return result != null ? result : skinManager.getDefaultResources().getColorStateList(id);
    }

    public SparseArray<ColorStateList> getOriginalCache() {
        return originalCache;
    }
}
