package com.cantalou.skin.holder;

import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;

import com.cantalou.skin.holder.AbstractHolder;

public class ViewHolder extends AbstractHolder
{

	protected int background;

	@SuppressWarnings("deprecation")
	@Override
	public void reload(View view, Resources res) {
		if (background != 0) {
			view.setBackgroundDrawable(res.getDrawable(background));
		}
	}

	@Override
	public boolean parse(AttributeSet attrs) {
		background = getResourceId(attrs, "background");
		return super.parse(attrs) || background != 0;
	}

}