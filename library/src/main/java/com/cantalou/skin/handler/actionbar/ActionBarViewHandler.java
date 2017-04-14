package com.cantalou.skin.handler.actionbar;

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
import com.cantalou.android.util.ReflectUtil;
import com.cantalou.skin.util.ResourcesUtil;

/**
 * ActionBar logo,icon刷新
 *
 * @author cantalou
 * @date 2016年1月24日 上午12:03:49
 */
@SuppressLint({"NewApi"})
@SuppressWarnings("deprecation")
public class ActionBarViewHandler extends ActionBarHandler {
    private int[] actionBarAttr;

    private Integer actionBarStyle;

    private Integer actionBarLogo;

    private int logo;

    private Integer actionBarIcon;

    private int icon;

    @Override
    protected void reloadAttr(View view, Resources res, boolean onlyColor) {
        super.reloadAttr(view, res, onlyColor);
        if (logo != 0) {
            ReflectUtil.invoke(view, "setLogo", new Class[]{Drawable.class}, res.getDrawable(logo));
        }
        if (icon != 0) {
            ReflectUtil.invoke(view, "setIcon", new Class[]{Drawable.class}, res.getDrawable(icon));
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

        // com.android.internal.R.styleable.ActionBar_logo
        if (actionBarLogo == null) {
            actionBarLogo = getCompactValue("styleable", "ActionBar_logo", "SherlockActionBar_logo");
        }
        a.getValue(actionBarLogo, value);
        logo = value.resourceId;
        if (logo == 0 && context instanceof Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ActivityInfo info = ReflectUtil.get(context, "mActivityInfo");
                if (info != null) {
                    logo = info.getLogoResource();
                }
            }
            if (logo == 0) {
                logo = ResourcesUtil.loadLogoFromManifest((Activity) context);
            }
        }

        if (actionBarIcon == null) {
            actionBarIcon = getCompactValue("styleable", "ActionBar_icon", "SherlockActionBar_icon");
        }
        a.getValue(actionBarIcon, value);
        icon = value.resourceId;
        if (icon == 0 && context instanceof Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ActivityInfo info = ReflectUtil.get(context, "mActivityInfo");
                if (info != null) {
                    icon = info.getIconResource();
                }
            }
            if (icon == 0) {
                icon = ResourcesUtil.loadIconFromManifest((Activity) context);
            }
        }

        a.recycle();
        return super.parseAttr(context, attrs) || logo != 0 || icon != 0;
    }

}
