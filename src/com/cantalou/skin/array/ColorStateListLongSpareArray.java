package com.cantalou.skin.array;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.os.Build;
import android.util.LongSparseArray;

import com.cantalou.android.util.Log;
import com.cantalou.skin.content.res.ProxyResources;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ColorStateListLongSpareArray extends LongSparseArray<ColorStateList> {

    private LongSparseArray<Integer> resourceIdKeyMap;;

    /**
     * Resources mColorStateListCache
     */
    private LongSparseArray<ColorStateList> originalCache;

    private ProxyResources resources;

    public ColorStateListLongSpareArray(ProxyResources resources, LongSparseArray<ColorStateList> originalCache, LongSparseArray<Integer> resourceIdKeyMap) {
	this.resources = resources;
	this.originalCache = originalCache;
	this.resourceIdKeyMap = resourceIdKeyMap;
    }

    @Override
    public ColorStateList get(long key) {
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
