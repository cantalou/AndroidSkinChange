package com.cantalou.skin.array;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.content.res.ConstantState;
import android.content.res.Resources;
import android.os.Build;
import android.util.LongSparseArray;

import com.cantalou.android.util.ReflectUtil;
import com.cantalou.android.util.array.SparseLongIntArray;
import com.cantalou.skin.SkinManager;
import com.cantalou.skin.content.res.ProxyResources;

/**
 * 系统版本高于Build.VERSION_CODES.M时Resources类的静态变量mPreloadedColorStateLists使用的是LongSparseArray<android.content.res.ConstantState<ColorStateList>>类型
 *
 * @author cantalou
 * @date 2016年4月13日 下午11:00:14
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
public class ColorStateListLongSpareArrayForM extends LongSparseArray<ConstantState<ColorStateList>> {

    private SparseLongIntArray resourceIdKeyMap;

    /**
     * Resources mColorStateListCache
     */
    private LongSparseArray<ConstantState<ColorStateList>> originalCache;

    private SkinManager skinManager;

    public ColorStateListLongSpareArrayForM(SkinManager skinManager, LongSparseArray<ConstantState<ColorStateList>> originalCache, SparseLongIntArray resourceIdKeyMap) {
        this.skinManager = skinManager;
        this.originalCache = originalCache;
        this.resourceIdKeyMap = resourceIdKeyMap;
    }

    @Override
    public ConstantState<ColorStateList> get(long key) {

        int id = resourceIdKeyMap.get(key);
        if (id != 0) {
            Resources res = skinManager.getCurrentResources();
            if (res != null && res instanceof ProxyResources && !((ProxyResources) res).isCurrentLoading()) {
                ColorStateList csl = ((ProxyResources) res).loadColorStateList(id);
                return ReflectUtil.invoke(csl, "getConstantState");
            }
        }
        return originalCache.get(key);
    }

    public LongSparseArray<ConstantState<ColorStateList>> getOriginalCache() {
        return originalCache;
    }
}
