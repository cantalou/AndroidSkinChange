package com.cantalou.skin.holder;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.cantalou.android.util.ReflectUtil;

public class TextViewHolder extends ViewHolder {

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

	@Override
	public void reload(View view, Resources res) {
		super.reload(view, res);

		TextView tv = (TextView) view;

		if (textColorHighlight != 0) {
			tv.setHighlightColor(res.getColor(textColorHighlight));
		}
		if (textColor != 0) {
			tv.setTextColor(res.getColorStateList(textColor));
		}
		if (textColorHint != 0) {
			tv.setHintTextColor(res.getColorStateList(textColorHint));
		}
		if (textColorLink != 0) {
			tv.setLinkTextColor(res.getColorStateList(textColorLink));
		}
		if (drawableLeft != 0 || drawableTop != 0 || drawableRight != 0 || drawableBottom != 0) {
			tv.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
		}
		if (shadowColor != 0) {
			tv.setShadowLayer(tv.getShadowRadius(), tv.getShadowDx(), tv.getShadowDy(), shadowColor);
		}
		if (textCursorDrawable != 0 && (textCursorDrawable & APP_RESOURCE_ID_PACKAGE) == APP_RESOURCE_ID_PACKAGE) {
			Drawable[] mCursorDrawable = ReflectUtil.get(tv, "mCursorDrawable");
			mCursorDrawable[0] = null;
			mCursorDrawable[1] = null;
		}
	}

	@Override
	public boolean parse(AttributeSet attrs) {

		for (int i = 0; i < attrs.getAttributeCount(); i++) {
			String name = attrs.getAttributeName(i);
			if ("textColorHighlight".equals(name)) {
				textColorHighlight = getResourceId(attrs, i);
			} else if ("textColor".equals(name)) {
				textColor = getResourceId(attrs, i);
			} else if ("textColorHint".equals(name)) {
				textColorHint = getResourceId(attrs, i);
			} else if ("textColorLink".equals(name)) {
				textColorLink = getResourceId(attrs, i);
			} else if ("drawableLeft".equals(name)) {
				drawableLeft = getResourceId(attrs, i);
			} else if ("drawableTop".equals(name)) {
				drawableTop = getResourceId(attrs, i);
			} else if ("drawableRight".equals(name)) {
				drawableRight = getResourceId(attrs, i);
			} else if ("drawableBottom".equals(name)) {
				drawableBottom = getResourceId(attrs, i);
			} else if ("shadowColor".equals(name)) {
				shadowColor = getResourceId(attrs, i);
			} else if ("textCursorDrawable".equals(name)) {
				textCursorDrawable = getResourceId(attrs, i);
			}
		}
		return super.parse(attrs) && ( textColorHighlight | textColor | textColorHint | textColorLink | drawableLeft | drawableTop | drawableRight
				| drawableBottom | shadowColor | textCursorDrawable) != 0;
	}
}