package com.cantalou.skin.array;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.os.Build;
import android.util.LongSparseArray;

import com.cantalou.skin.content.res.ProxyResources;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ColorStateListLongSpareArray extends LongSparseArray<ColorStateList> {

	private LongSparseArray<Integer> resourceIdKeyMap;;

	/**
	 * Resources mColorStateListCache
	 */
	private LongSparseArray<ColorStateList> originalCache;

	private ProxyResources resources;

	public ColorStateListLongSpareArray(ProxyResources resources, LongSparseArray<ColorStateList> originalCache,
			LongSparseArray<Integer> resourceIdKeyMap) {
		this.resources = resources;
		this.originalCache = originalCache;
		this.resourceIdKeyMap = resourceIdKeyMap;
	}

	@Override
	public ColorStateList get(long key) {
		ColorStateList csl;
		Integer id = resourceIdKeyMap.get(key);
		if (id != null) {
			csl = resources.loadColorStateList(id);
		} else {
			csl = originalCache.get(key);
		}
		return csl;
	}
}
