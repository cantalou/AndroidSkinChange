package com.cantalou.skin.holder;

import android.util.AttributeSet;
import android.view.View;

import com.cantalou.skin.holder.AttrHolder;

public abstract class AbstractHolder implements AttrHolder, Cloneable {

    /**
     * 父类的parse是否有被调用
     */
    private boolean called = false;

    @Override
    public final void parse(View view, AttributeSet attrs) {
	called = false;
	boolean result = parse(attrs);
	if (!called) {
	    throw new IllegalStateException("super parse(AttributeSet attrs) must be call");
	}
	if (result) {
	    view.setTag(ATTR_HOLDER_KEY, this);
	}
    }

    /**
     * 解析组件属性
     *
     * @param attrs
     * @return 组件使用app资源 true
     */
    protected boolean parse(AttributeSet attrs) {
	called = true;
	return false;
    }

    protected int getResourceId(AttributeSet attrs, String name) {
	int len = attrs.getAttributeCount();
	for (int i = 0; i < len; i++) {
	    String attributeName = attrs.getAttributeName(i);
	    if (name.equals(attributeName)) {
		int id = attrs.getAttributeResourceValue(i, 0);
		return (id & APP_RESOURCE_ID_PACKAGE) == APP_RESOURCE_ID_PACKAGE ? id : 0;
	    }
	}
	return 0;
    }

    protected int getResourceId(AttributeSet attrs, int index) {
	int id = attrs.getAttributeResourceValue(index, 0);
	return (id & APP_RESOURCE_ID_PACKAGE) == APP_RESOURCE_ID_PACKAGE ? id : 0;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
	return super.clone();
    }
}
