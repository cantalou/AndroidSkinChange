package com.cantalou.skin.content.res;

import android.content.res.AssetManager;
import android.content.res.Resources;

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
