package com.cantalou.skin.holder;

import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.cantalou.skin.SkinManager;
import com.cantalou.skin.holder.ViewHolder;

public class ImageViewHolder extends ViewHolder {

	protected int src;

	@Override
	public void reload(View view, Resources res) {
		super.reload(view, res);
		if (src != 0) {
			((ImageView) view).setImageDrawable(res.getDrawable(src));
		}
	}

	@Override
	public boolean parseAttr(AttributeSet attrs) {
		src = getResourceId(attrs, "src");
		if (src != 0) {
			SkinManager.getInstance().registerDrawable(src);
		}
		return super.parseAttr(attrs) || src != 0;
	}
}
