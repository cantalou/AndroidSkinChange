package com.cantalou.skin.array;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.util.LongSparseArray;
import android.util.SparseArray;
import com.cantalou.skin.content.res.ProxyResources;

@SuppressLint("NewApi")
public class ColorStateListSpareArray extends SparseArray<ColorStateList> {

	private LongSparseArray<Integer> resourceIdKeyMap;;

	/**
	 * Resources mColorStateListCache
	 */
	private SparseArray<ColorStateList> originalCache;

	private ProxyResources resources;

	public ColorStateListSpareArray(ProxyResources resources, SparseArray<ColorStateList> originalCache,
			LongSparseArray<Integer> resourceIdKeyMap) {
		this.resources = resources;
		this.originalCache = originalCache;
		this.resourceIdKeyMap = resourceIdKeyMap;
	}

	@Override
	public ColorStateList get(int key) {
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
