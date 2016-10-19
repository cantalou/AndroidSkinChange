package com.cantalou.skin.array;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.LongSparseArray;

import com.cantalou.android.util.Log;
import com.cantalou.android.util.array.SparseLongIntArray;
import com.cantalou.skin.SkinManager;
import com.cantalou.skin.content.res.ProxyResources;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class DrawableLongSpareArray extends LongSparseArray<Drawable.ConstantState> {

    private SparseLongIntArray resourceIdKeyMap;

    /**
     * Resources mColorStateListCache
     */
    private LongSparseArray<Drawable.ConstantState> originalCache;

    private SkinManager skinManager;

    public DrawableLongSpareArray(SkinManager skinManager, LongSparseArray<Drawable.ConstantState> originalCache, SparseLongIntArray resourceIdKeyMap) {
	this.originalCache = originalCache;
	this.resourceIdKeyMap = resourceIdKeyMap;
	this.skinManager = skinManager;
    }

    @Override
    public Drawable.ConstantState get(long key) {
	int id = resourceIdKeyMap.get(key);
	if (id != 0) {
	    Drawable dr = skinManager.getCurrentSkinResources().loadDrawable(id);
	    return dr != null ? dr.getConstantState() : null;
	} else {
	    return originalCache.get(key);
	}
    }
}
