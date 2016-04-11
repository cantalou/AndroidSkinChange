package com.cantalou.skin.array;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.os.Build;
import android.util.LongSparseArray;

import com.cantalou.android.util.Log;
import com.cantalou.skin.SkinManager;
import com.cantalou.skin.content.res.ProxyResources;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ColorStateListLongSpareArray extends LongSparseArray<ColorStateList> {

    private LongSparseArray<Integer> resourceIdKeyMap;;

    /**
     * Resources mColorStateListCache
     */
    private LongSparseArray<ColorStateList> originalCache;

    private SkinManager skinManager;

    public ColorStateListLongSpareArray(SkinManager skinManager, LongSparseArray<ColorStateList> originalCache, LongSparseArray<Integer> resourceIdKeyMap) {
	this.skinManager = skinManager;
	this.originalCache = originalCache;
	this.resourceIdKeyMap = resourceIdKeyMap;
    }

    @Override
    public ColorStateList get(long key) {
	ProxyResources resources = skinManager.getCurrentSkinResources();
	Integer id;
	ColorStateList csl;
	if (resources != null && (id = resourceIdKeyMap.get(key)) != null) {
	    csl = resources.loadColorStateList(id);
	    Log.v("load ColorStateList from {} id:{} ", resources, ProxyResources.toHex(id));
	} else {
	    csl = originalCache.get(key);
	}
	return csl;
    }
}
