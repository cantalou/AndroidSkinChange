package com.cantalou.skin.holder;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;

@SuppressWarnings("deprecation")
public class ViewHolder extends AbstractHolder
{
    protected int background;

    @Override
    protected void reload(View view, Resources res)
    {
        super.reload(view, res);
        if (background != 0)
        {
            view.setBackgroundDrawable(res.getDrawable(background));
        }
    }

    @Override
    public boolean parseAttr(Context context, AttributeSet attrs)
    {
        background = getResourceId(attrs, "background");
        if (background != 0)
        {
            cacheKeyAndIdManager.registerDrawable(background);
        }
        return super.parseAttr(context, attrs) || background != 0;
    }

}