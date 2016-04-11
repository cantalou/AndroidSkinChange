package com.cantalou.skin.array;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.LongSparseArray;

import com.cantalou.android.util.Log;
import com.cantalou.skin.SkinManager;
import com.cantalou.skin.content.res.ProxyResources;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class DrawableLongSpareArray extends LongSparseArray<Drawable.ConstantState> {

    private LongSparseArray<Integer> resourceIdKeyMap;

    /**
     * Resources mColorStateListCache
     */
    private LongSparseArray<Drawable.ConstantState> originalCache;

    private SkinManager skinManager;

    public DrawableLongSpareArray(SkinManager skinManager,LongSparseArray<Drawable.ConstantState> originalCache, LongSparseArray<Integer> resourceIdKeyMap) {
	this.originalCache = originalCache;
	this.resourceIdKeyMap = resourceIdKeyMap;
	this.skinManager = skinManager;
    }

    @Override
    public Drawable.ConstantState get(long key) {
	ProxyResources resources = skinManager.getCurrentSkinResources();
	Integer id;
	if (resources != null && (id = resourceIdKeyMap.get(key)) != null) {
	    Drawable dr = resources.loadDrawable(id);
	    if (dr != null) {
		Log.v("load Drawable from {} id:{} ", resources, ProxyResources.toHex(id));
		return dr.getConstantState();
	    } else {
		Log.v("load Drawable from {} id:{} return null");
		return null;
	    }
	} else {
	    Log.v("load Drawable from originalCache");
	    return originalCache.get(key);
	}
    }
}
