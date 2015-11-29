package com.cantalou.skin.holder;

import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

public class ListViewHolder extends ViewHolder {

	protected int divider;

	@Override
	public void reload(View view, Resources res) {
		super.reload(view, res);
		if (divider != 0) {
			((ListView) view).setDivider(res.getDrawable(divider));
		}
	}

	@Override
	public boolean parse(AttributeSet attrs) {
		divider = getResourceId(attrs, "divider");
		return super.parse(attrs) && divider != 0;
	}
}
