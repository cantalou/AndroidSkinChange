package com.cantalou.skin.handler;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * @author cantalou
 * @date 2016年1月23日 下午11:31:01
 */
public class ImageViewHandler extends ViewHandler {

    protected int src;

    @SuppressWarnings("deprecation")
    @Override
    protected void reload(View view, Resources res) {
        super.reload(view, res);
        if (src != 0) {
            ((ImageView) view).setImageDrawable(res.getDrawable(src));
        }
    }

    @Override
    public boolean parseAttr(Context context, AttributeSet attrs) {
        src = getResourceId(attrs, "src");
        return super.parseAttr(context, attrs) || src != 0;
    }
}
