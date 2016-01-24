package com.cantalou.skin.holder;

import com.cantalou.android.util.ReflectUtil;
import com.cantalou.skin.SkinManager;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 *
 * ActionBar logo,icon刷新
 * 
 * @author cantalou
 * @date 2016年1月24日 上午12:03:49
 */
@SuppressWarnings("deprecation")
public class ActionBarViewHolder extends ViewHolder {

	private int logo;

	private int icon;

	@Override
	protected void reload(View view, Resources res) {
		super.reload(view, res);
		if (logo != 0) {
			ReflectUtil.invoke(view, "setLogo", new Class[] { Drawable.class }, res.getDrawable(logo));
		}
		if (icon != 0) {
			ReflectUtil.invoke(view, "setIcon", new Class[] { Drawable.class }, res.getDrawable(icon));
		}
	}

	@Override
	public boolean parseAttr(AttributeSet attrs) {

		logo = getResourceId(attrs, "logo");
		if (logo != 0) {
			SkinManager.getInstance().registerDrawable(logo);
		}

		icon = getResourceId(attrs, "icon");
		if (icon != 0) {
			SkinManager.getInstance().registerDrawable(icon);
		}

		return super.parseAttr(attrs) || logo != 0 || icon != 0;
	}

}
