package com.cantalou.skin.holder;

import com.cantalou.android.util.ReflectUtil;
import com.cantalou.skin.SkinManager;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 
 *
 * @author cantalou
 * @date 2016年1月28日 下午11:02:42
 */
public class AppCompactToolBarHolder extends AbstractHolder {

	int collapseIcon;

	int navIcon;

	@SuppressWarnings("deprecation")
	@Override
	protected void reload(View view, Resources res) {
		super.reload(view, res);
		if (navIcon != 0) {
			ReflectUtil.invokeByMethodName(view, "setNavigationIcon", res.getDrawable(navIcon));
		}
		
		if(collapseIcon != 0){
			Drawable dr = res.getDrawable(collapseIcon);
			ReflectUtil.set(view, "mCollapseIcon", dr);
			Object collapseButtonView = ReflectUtil.get(view, "mCollapseButtonView");
			if(collapseButtonView != null){
				ReflectUtil.invokeByMethodName(collapseButtonView, "setImageDrawable", dr);
			}
			
		}
	}

	@Override
	protected boolean parseAttr(AttributeSet attrs) {
		SkinManager skinManager = SkinManager.getInstance();

		collapseIcon = getResourceId(attrs, "collapseIcon");
		if (collapseIcon != 0) {
			skinManager.registerDrawable(collapseIcon);
		}

		navIcon = getResourceId(attrs, "navigationIcon");
		if (navIcon != 0) {
			skinManager.registerDrawable(navIcon);
		}

		return super.parseAttr(attrs) || collapseIcon != 0 || navIcon != 0;
	}
}
