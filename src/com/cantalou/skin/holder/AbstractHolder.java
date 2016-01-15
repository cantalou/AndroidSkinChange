package com.cantalou.skin.holder;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;

import com.cantalou.android.util.Log;
import com.cantalou.skin.res.SkinProxyResources;

public abstract class AbstractHolder implements Cloneable {

	public static final int ATTR_HOLDER_KEY = 0x7FFFFFFF;

	/**
	 * 父类的parse是否有被调用
	 */
	private boolean called = false;

	public final AbstractHolder parse(AttributeSet attrs) {
		called = false;
		boolean result = parseAttr(attrs);
		if (!called) {
			throw new IllegalStateException("super parse(AttributeSet attrs) must be call");
		}
		return result ? this : null;
	}

	/**
	 * 重新加载资源
	 *
	 * @param view
	 *            view对象
	 * @param res
	 *            资源对象
	 */
	public abstract void reload(View view, Resources res);

	/**
	 * 解析组件属性
	 *
	 * @param attrs
	 * @return 组件使用app资源 true
	 */
	protected boolean parseAttr(AttributeSet attrs) {
		called = true;
		return false;
	}

	protected int getResourceId(AttributeSet attrs, String name) {
		int len = attrs.getAttributeCount();
		for (int i = 0; i < len; i++) {
			String attributeName = attrs.getAttributeName(i);
			if (name.equals(attributeName)) {
				int id = attrs.getAttributeResourceValue(i, 0);
				return (id & SkinProxyResources.APP_ID_MASK) == SkinProxyResources.APP_ID_MASK ? id : 0;
			}
		}
		return 0;
	}

	protected int getResourceId(AttributeSet attrs, int index) {
		int id = attrs.getAttributeResourceValue(index, 0);
		return (id & SkinProxyResources.APP_ID_MASK) == SkinProxyResources.APP_ID_MASK ? id : 0;
	}

	@Override
	public AbstractHolder clone() {
		try {
			return (AbstractHolder) super.clone();
		} catch (CloneNotSupportedException e) {
			Log.w(e);
			return null;
		}
	}
}
