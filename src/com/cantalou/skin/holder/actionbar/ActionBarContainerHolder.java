package com.cantalou.skin.holder.actionbar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.cantalou.android.util.ReflectUtil;

/**
 * ActionBar背景刷新
 *
 * @author cantalou
 * @date 2016年1月23日 下午11:30:25
 */
@SuppressWarnings("deprecation")
public class ActionBarContainerHolder extends ActionBarHolder {

	private int background;

	private int stackedBackground;

	private int[] actionBarAttr;

	private Integer actionBarBackground;

	private Integer actionBarBackgroundStacked;

	@Override
	protected void reload(View view, Resources res) {
		super.reload(view, res);

//		if (background != 0) {
//			ReflectUtil.invokeByMethodName(view, "setPrimaryBackground", res.getDrawable(background));
//		}
//
//		if (stackedBackground != 0) {
//			// Fix for issue #379
//			Drawable stackBackgroundDrawable = res.getDrawable(stackedBackground);
//			if (stackBackgroundDrawable instanceof ColorDrawable && Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
//				stackBackgroundDrawable = ReflectUtil.newInstance("com.actionbarsherlock.internal.widget.IcsColorDrawable", stackBackgroundDrawable);
//			}
//			if (stackBackgroundDrawable != null) {
//				ReflectUtil.invokeByMethodName(view, "setStackedBackground", stackBackgroundDrawable);
//			}
//		}
	}

	@Override
	public boolean parseAttr(Context context, AttributeSet attrs) {

		TypedValue value = new TypedValue();

		// com.android.internal.R.styleable.ActionBar
		if (actionBarAttr == null) {
			actionBarAttr = getCompactValue("styleable", "ActionBar", "SherlockActionBar");
		}
		TypedArray a = context.obtainStyledAttributes(attrs, actionBarAttr);

		// com.android.internal.R.styleable.ActionBar_background
		if (actionBarBackground == null) {
			actionBarBackground = getCompactValue("styleable", "ActionBar_background", "SherlockActionBar_background");
		}
		a.getValue(actionBarBackground, value);
		background = value.resourceId;
		if (background != 0) {
			cacheKeyAndIdManager.registerDrawable(background);
		}

		// com.android.internal.R.styleable.ActionBar_backgroundStacked
		if (actionBarBackgroundStacked == null) {
			actionBarBackgroundStacked = getCompactValue("styleable", "ActionBar_backgroundStacked", "SherlockActionBar_backgroundStacked");
		}
		a.getValue(actionBarBackgroundStacked, value);
		stackedBackground = value.resourceId;
		if (stackedBackground != 0) {
			cacheKeyAndIdManager.registerDrawable(stackedBackground);
		}

		return super.parseAttr(context, attrs) || background != 0 || stackedBackground != 0;
	}
}
