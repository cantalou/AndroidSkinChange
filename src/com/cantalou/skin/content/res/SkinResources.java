package com.cantalou.skin.content.res;

import static com.cantalou.android.util.ReflectUtil.get;
import static com.cantalou.android.util.ReflectUtil.set;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable.ConstantState;
import android.os.Build;
import android.util.LongSparseArray;
import android.util.SparseArray;

import com.cantalou.android.util.Log;
import com.cantalou.skin.SkinManager;
import com.cantalou.skin.array.ColorStateListLongSpareArray;
import com.cantalou.skin.array.ColorStateListSpareArray;
import com.cantalou.skin.array.DrawableLongSpareArray;

public class SkinResources extends Resources {

	
	/**
	 * 皮肤资源包名
	 */
	protected String skinName;

	/**
	 * Create a new SkinResources object on top of an existing set of assets in
	 * an AssetManager.
	 *
	 * @param assets
	 *            Previously created AssetManager.
	 * @param res
	 */
	public SkinResources(AssetManager assets, Resources res, String skinName) {
		super(assets, res.getDisplayMetrics(), res.getConfiguration());
		this.skinName = skinName;
	}
	

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" + skinName + "}";
	}
}
