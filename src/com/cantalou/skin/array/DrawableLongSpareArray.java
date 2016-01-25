package com.cantalou.skin.array;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.LongSparseArray;
import android.util.TypedValue;

import com.cantalou.skin.SkinManager;
import com.cantalou.skin.content.res.ProxyResources;
import com.cantalou.skin.content.res.SkinProxyResources;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class DrawableLongSpareArray extends LongSparseArray<Drawable.ConstantState> {

	private LongSparseArray<Integer> resourceIdKeyMap;

	/**
	 * Resources mColorStateListCache
	 */
	private LongSparseArray<Drawable.ConstantState> originalCache;

	private ProxyResources resources;

	public DrawableLongSpareArray(ProxyResources resources, LongSparseArray<Drawable.ConstantState> originalCache,
			LongSparseArray<Integer> resourceIdKeyMap) {
		this.resources = resources;
		this.originalCache = originalCache;
		this.resourceIdKeyMap = resourceIdKeyMap;
	}

	@Override
	public Drawable.ConstantState get(long key) {
		Integer id;
		if (resources != null && (id = resourceIdKeyMap.get(key)) != null) {
			Drawable dr = resources.loadDrawable(id);
			if (dr != null) {
				return dr.getConstantState();
			} else {
				return null;
			}
		} else {
			return originalCache.get(key);
		}
	}
}
