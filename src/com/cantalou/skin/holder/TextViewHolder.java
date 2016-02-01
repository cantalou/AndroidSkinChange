package com.cantalou.skin.holder;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.cantalou.android.util.ReflectUtil;
import com.cantalou.skin.SkinManager;
import com.cantalou.skin.content.res.SkinProxyResources;

public class TextViewHolder extends ViewHolder
{

    protected int textColorHighlight;
    protected int textColor;
    protected int textColorHint;
    protected int textColorLink;
    protected int drawableLeft;
    protected int drawableTop;
    protected int drawableRight;
    protected int drawableBottom;
    protected int shadowColor;
    protected int textCursorDrawable;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void reload(View view, Resources res)
    {
        super.reload(view, res);

        TextView tv = (TextView) view;

        if (textColorHighlight != 0)
        {
            tv.setHighlightColor(res.getColor(textColorHighlight));
        }
        if (textColor != 0)
        {
            tv.setTextColor(res.getColorStateList(textColor));
        }
        if (textColorHint != 0)
        {
            tv.setHintTextColor(res.getColorStateList(textColorHint));
        }
        if (textColorLink != 0)
        {
            tv.setLinkTextColor(res.getColorStateList(textColorLink));
        }
        if (drawableLeft != 0 || drawableTop != 0 || drawableRight != 0 || drawableBottom != 0)
        {
            tv.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
        }
        if (shadowColor != 0)
        {
            tv.setShadowLayer(tv.getShadowRadius(), tv.getShadowDx(), tv.getShadowDy(), shadowColor);
        }
        if (textCursorDrawable != 0 && (textCursorDrawable & SkinProxyResources.APP_ID_MASK) == SkinProxyResources.APP_ID_MASK)
        {
            Drawable[] mCursorDrawable = ReflectUtil.get(tv, "mCursorDrawable");
            mCursorDrawable[0] = null;
            mCursorDrawable[1] = null;
        }
    }

    @Override
    public boolean parseAttr(AttributeSet attrs)
    {
        for (int i = 0; i < attrs.getAttributeCount(); i++)
        {
            String name = attrs.getAttributeName(i);
            if ("textColorHighlight".equals(name))
            {
                textColorHighlight = getResourceId(attrs, i);
                if (textColorHighlight != 0)
                {
                    cacheKeyAndIdManager.registerColorStateList(textColorHighlight);
                }
            }
            else if ("textColor".equals(name))
            {
                textColor = getResourceId(attrs, i);
                if (textColor != 0)
                {
                    cacheKeyAndIdManager.registerColorStateList(textColor);
                }
            }
            else if ("textColorHint".equals(name))
            {
                textColorHint = getResourceId(attrs, i);
                if (textColorHint != 0)
                {
                    cacheKeyAndIdManager.registerColorStateList(textColorHint);
                }
            }
            else if ("textColorLink".equals(name))
            {
                textColorLink = getResourceId(attrs, i);
                if (textColorLink != 0)
                {
                    cacheKeyAndIdManager.registerColorStateList(textColorLink);
                }
            }
            else if ("drawableLeft".equals(name))
            {
                drawableLeft = getResourceId(attrs, i);
                if (drawableLeft != 0)
                {
                    cacheKeyAndIdManager.registerDrawable(drawableLeft);
                }
            }
            else if ("drawableTop".equals(name))
            {
                drawableTop = getResourceId(attrs, i);
                if (drawableTop != 0)
                {
                    cacheKeyAndIdManager.registerDrawable(drawableTop);
                }
            }
            else if ("drawableRight".equals(name))
            {
                drawableRight = getResourceId(attrs, i);
                if (drawableRight != 0)
                {
                    cacheKeyAndIdManager.registerDrawable(drawableRight);
                }
            }
            else if ("drawableBottom".equals(name))
            {
                drawableBottom = getResourceId(attrs, i);
                if (drawableBottom != 0)
                {
                    cacheKeyAndIdManager.registerDrawable(drawableBottom);
                }
            }
            else if ("shadowColor".equals(name))
            {
                shadowColor = getResourceId(attrs, i);
                if (shadowColor != 0)
                {
                    cacheKeyAndIdManager.registerColorStateList(shadowColor);
                }
            }
            else if ("textCursorDrawable".equals(name))
            {
                textCursorDrawable = getResourceId(attrs, i);
                if (textCursorDrawable != 0)
                {
                    cacheKeyAndIdManager.registerDrawable(textCursorDrawable);
                }
            }
        }
        return super.parseAttr(
                attrs) || (textColorHighlight | textColor | textColorHint | textColorLink | drawableLeft | drawableTop | drawableRight | drawableBottom | shadowColor | textCursorDrawable) != 0;
    }
}