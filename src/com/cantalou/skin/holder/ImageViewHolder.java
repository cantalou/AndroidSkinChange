package com.cantalou.skin.holder;

import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.cantalou.skin.holder.ViewHolder;

public class ImageViewHolder extends ViewHolder
{

	protected int src;

	@SuppressWarnings("deprecation")
	@Override
	public void reload(View view, Resources res) {
		super.reload(view, res);
		if (src != 0) {
			((ImageView) view).setImageDrawable(res.getDrawable(src));
		}
	}

	@Override
	public boolean parse(AttributeSet attrs) {
		src = getResourceId(attrs, "src");
		return super.parse(attrs) || src != 0;
	}
}
