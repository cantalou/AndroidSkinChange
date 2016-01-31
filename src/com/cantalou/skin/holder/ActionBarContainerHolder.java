package com.cantalou.skin.holder;

import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.cantalou.android.util.ReflectUtil;
import com.cantalou.skin.SkinManager;

/**
 *
 * ActionBar背景刷新
 * 
 * @author cantalou
 * @date 2016年1月23日 下午11:30:25
 */
@SuppressWarnings("deprecation")
public class ActionBarContainerHolder extends ViewHolder {

	private int background;

	private int stackedBackground;

	@Override
	protected void reload(View view, Resources res) {
		super.reload(view, res);
		ReflectUtil.invokeByMethodName(view, "setPrimaryBackground", res.getDrawable(background));

		// Fix for issue #379
		Drawable stackBackgroundDrawable = res.getDrawable(stackedBackground);
		if (stackBackgroundDrawable instanceof ColorDrawable && Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			stackBackgroundDrawable = ReflectUtil.newInstance("com.actionbarsherlock.internal.widget.IcsColorDrawable", stackBackgroundDrawable);
		}
		if (stackBackgroundDrawable != null) {
			ReflectUtil.invokeByMethodName(view, "setStackedBackground", stackBackgroundDrawable);
		}

	}

	@Override
	public boolean parseAttr(AttributeSet attrs) {

		background = getResourceId(attrs, "background");
		if (background != 0) {
			cacheKeyAndIdManager.registerDrawable(background);
		}

		stackedBackground = getResourceId(attrs, "backgroundStacked");
		if (stackedBackground != 0) {
			cacheKeyAndIdManager.registerDrawable(stackedBackground);
		}

		return super.parseAttr(attrs) || background != 0 || stackedBackground != 0;
	}
}
