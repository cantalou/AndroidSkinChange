package com.cantalou.skin.holder;

import android.util.AttributeSet;
import android.view.View;

public abstract class AbstractHolder implements AttrHolder {

	@Override
	public final void parse(View view, AttributeSet attrs) {
		if (parse(attrs)) {
			view.setTag(ATTR_HOLDER_KEY, this);
		}
	}

	/**
	 * 解析组件属性
	 * 
	 * @param attrs
	 * @return 组件使用app资源 true
	 */
	public abstract boolean parse(AttributeSet attrs);

	protected int getResourceId(AttributeSet attrs, String name) {
		int len = attrs.getAttributeCount();
		for (int i = 0; i < len; i++) {
			if (name.equals(attrs.getAttributeName(i))) {
				int id = attrs.getAttributeNameResource(i);
				return (id & APP_RESOURCE_ID_PACKAGE) == APP_RESOURCE_ID_PACKAGE ? id : 0;
			}
		}
		return 0;
	}

	protected int getResourceId(AttributeSet attrs, int index) {
		int id = attrs.getAttributeNameResource(index);
		return (id & APP_RESOURCE_ID_PACKAGE) == APP_RESOURCE_ID_PACKAGE ? id : 0;
	}
}
