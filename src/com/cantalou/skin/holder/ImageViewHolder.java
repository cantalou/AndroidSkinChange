package com.cantalou.skin.holder;

import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.cantalou.skin.SkinManager;
import com.cantalou.skin.holder.ViewHolder;

/**
 *
 * @author cantalou
 * @date 2016年1月23日  下午11:31:01
 */
public class ImageViewHolder extends ViewHolder {

	protected int src;

	@Override
	protected void reload(View view, Resources res) {
		super.reload(view, res);
		if (src != 0) {
			((ImageView) view).setImageDrawable(res.getDrawable(src));
		}
	}

	@Override
	public boolean parseAttr(AttributeSet attrs) {
		src = getResourceId(attrs, "src");
		if (src != 0) {
			cacheKeyAndIdManager.registerDrawable(src);
		}
		return super.parseAttr(attrs) || src != 0;
	}
}
