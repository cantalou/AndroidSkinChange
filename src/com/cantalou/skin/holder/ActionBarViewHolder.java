package com.cantalou.skin.holder;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.cantalou.android.util.ReflectUtil;

/**
 * ActionBar logo,icon刷新
 *
 * @author cantalou
 * @date 2016年1月24日 上午12:03:49
 */
@SuppressWarnings("deprecation")
public class ActionBarViewHolder extends ViewHolder
{

    private int logo;

    private int icon;

    @Override
    protected void reload(View view, Resources res)
    {
        super.reload(view, res);
        if (logo != 0)
        {
            ReflectUtil.invoke(view, "setLogo", new Class[]{Drawable.class}, res.getDrawable(logo));
        }
        if (icon != 0)
        {
            ReflectUtil.invoke(view, "setIcon", new Class[]{Drawable.class}, res.getDrawable(icon));
        }
    }

    @Override
    public boolean parseAttr(Context context, AttributeSet attrs)
    {

        logo = getResourceId(attrs, "logo");
        if (logo != 0)
        {
            cacheKeyAndIdManager.registerDrawable(logo);
        }

        icon = getResourceId(attrs, "icon");
        if (icon != 0)
        {
            cacheKeyAndIdManager.registerDrawable(icon);
        }

        return super.parseAttr(context, attrs) || logo != 0 || icon != 0;
    }

}
