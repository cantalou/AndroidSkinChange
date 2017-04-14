package com.cantalou.skin.handler.actionbar;

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
public class ActionBarContainerHandler extends ActionBarHandler {

    private int background;

    private int stackedBackground;

    private int[] actionBarAttr;

    private Integer actionBarBackground;

    private Integer actionBarBackgroundStacked;

    @Override
    protected void reloadAttr(View view, Resources res, boolean onlyColor) {
        super.reloadAttr(view, res, onlyColor);

        if (background != 0) {
            ReflectUtil.invoke(view, "setPrimaryBackground", new Class<?>[]{Drawable.class}, res.getDrawable(background));
        }

        if (stackedBackground != 0) {
            // Fix for issue #379
            Drawable stackBackgroundDrawable = res.getDrawable(stackedBackground);
            if (stackBackgroundDrawable instanceof ColorDrawable && Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                stackBackgroundDrawable = ReflectUtil.newInstance("com.actionbarsherlock.internal.widget.IcsColorDrawable", stackBackgroundDrawable);
            }
            if (stackBackgroundDrawable != null) {
                ReflectUtil.invoke(view, "setStackedBackground", new Class<?>[]{Drawable.class}, stackBackgroundDrawable);
            }
        }
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

        // com.android.internal.R.styleable.ActionBar_backgroundStacked
        if (actionBarBackgroundStacked == null) {
            actionBarBackgroundStacked = getCompactValue("styleable", "ActionBar_backgroundStacked", "SherlockActionBar_backgroundStacked");
        }
        a.getValue(actionBarBackgroundStacked, value);
        stackedBackground = value.resourceId;

        return super.parseAttr(context, attrs) || background != 0 || stackedBackground != 0;
    }
}
