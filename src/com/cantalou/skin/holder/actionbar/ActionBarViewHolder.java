package com.cantalou.skin.holder.actionbar;

import android.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import static com.cantalou.android.util.ReflectUtil.*;

import com.cantalou.skin.content.res.ResourcesCompat;

/**
 * ActionBar logo,icon刷新
 *
 * @author cantalou
 * @date 2016年1月24日 上午12:03:49
 */
@SuppressLint({ "NewApi" })
@SuppressWarnings("deprecation")
public class ActionBarViewHolder extends ActionBarHolder {
	private int[] actionBarAttr;

	private Integer actionBarStyle;

	private Integer actionBarLogo;

	private int logo;

	private Integer actionBarIcon;

	private int icon;

	private Integer actionBarUp;

	private int up;

	@Override
	protected void reload(View view, Resources res) {
		super.reload(view, res);
		if (logo != 0) {
			invoke(view, "setLogo", new Class[] { Drawable.class }, res.getDrawable(logo));
		}
		if (icon != 0) {
			invoke(view, "icon", new Class[] { Drawable.class }, res.getDrawable(logo));
		}

		if (up != 0) {
			invoke(get(view, "mHomeLayout"), "setUpIndicator", new Class[] { Drawable.class }, res.getDrawable(up));
		}
	}

	@Override
	public boolean parseAttr(Context context, AttributeSet attrs) {

		TypedValue value = new TypedValue();

		// com.android.internal.R.styleable.ActionBar
		if (actionBarAttr == null) {
			actionBarAttr = getCompactValue("styleable", "ActionBar", "SherlockActionBar");
		}

		// com.android.internal.R.attr.actionBarStyle
		if (actionBarStyle == null) {
			actionBarStyle = getCompactValue("attr", "actionBarStyle");
		}
		TypedArray a = context.obtainStyledAttributes(attrs, actionBarAttr, actionBarStyle, 0);

		// logo com.android.internal.R.styleable.ActionBar_logo
		if (actionBarLogo == null) {
			actionBarLogo = getCompactValue("styleable", "ActionBar_logo", "SherlockActionBar_logo");
		}
		a.getValue(actionBarLogo, value);
		logo = value.resourceId;
		if (logo == 0 && context instanceof Activity) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				ActivityInfo info = get(context, "mActivityInfo");
				if (info != null) {
					logo = info.getLogoResource();
				}
			}
			if (logo == 0) {
				logo = ResourcesCompat.loadLogoFromManifest((Activity) context);
			}
		}
		if (logo != 0) {
			cacheKeyAndIdManager.registerDrawable(logo);
		}

		// icon
		if (actionBarIcon == null) {
			actionBarIcon = getCompactValue("styleable", "ActionBar_icon", "SherlockActionBar_icon");
		}
		a.getValue(actionBarIcon, value);
		icon = value.resourceId;
		if (icon == 0 && context instanceof Activity) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				ActivityInfo info = get(context, "mActivityInfo");
				if (info != null) {
					icon = info.getIconResource();
				}
			}
			if (icon == 0) {
				icon = ResourcesCompat.loadIconFromManifest((Activity) context);
			}
		}
		if (icon != 0) {
			cacheKeyAndIdManager.registerDrawable(icon);
		}

		// up
		a.recycle();
		up = icon;
		return super.parseAttr(context, attrs) || logo != 0 || icon != 0 || up != 0;
	}

}
