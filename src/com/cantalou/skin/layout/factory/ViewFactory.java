package com.cantalou.skin.layout.factory;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.LayoutInflater.Factory2;
import android.view.View;

import com.cantalou.android.util.Log;
import com.cantalou.android.util.ReflectUtil;
import com.cantalou.android.util.StringUtils;
import com.cantalou.skin.holder.AbstractHolder;
import com.cantalou.skin.holder.ImageViewHolder;
import com.cantalou.skin.holder.ListViewHolder;
import com.cantalou.skin.holder.TextViewHolder;
import com.cantalou.skin.holder.ViewHolder;

import java.util.HashMap;

/**
 * 自定义Factory的实现, 保存View中属性的资源信息, 如:background赋值的资源id
 *
 * @author cantalou
 * @date 2015年11月29日 下午10:22:41
 */
public class ViewFactory implements Factory {

	static final String[] sClassPrefixList = { "android.widget.", "android.webkit.", "android.app." };

	static final HashMap<String, String> superNameCache = new HashMap<String, String>();

	static final HashMap<String, AbstractHolder> viewAttrHolder = new HashMap<String, AbstractHolder>();
	static {
		viewAttrHolder.put("android.view.View", new ViewHolder());// for super class
		viewAttrHolder.put("View", new ViewHolder());// for layout file
		viewAttrHolder.put("android.widget.TextView", new TextViewHolder());
		viewAttrHolder.put("android.widget.ImageView", new ImageViewHolder());
		viewAttrHolder.put("android.widget.ListView", new ListViewHolder());
	}

	LayoutInflater layoutInflater;

	Factory factoryProxy;

	public void register(LayoutInflater li) {
		this.layoutInflater = li;
		factoryProxy = li.getFactory();
		ReflectUtil.set(li, "mFactorySet", false);
		li.setFactory(this);
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {

		View view = null;
		if (name.contains("skin")) {
			view = null;
		}

		AbstractHolder attrHolder = getHolder(name);
		if (attrHolder != null) {
			attrHolder.parse(attrs);
		}

		if (factoryProxy != null) {
			view = factoryProxy.onCreateView(name, context, attrs);
		}

		if (view == null) {
			try {
				if (-1 == name.indexOf('.')) {
					for (String prefix : sClassPrefixList) {
						try {
							view = layoutInflater.createView(name, prefix, attrs);
						} catch (ClassNotFoundException e) {
						}
					}
					if (view == null) {
						view = layoutInflater.createView(name, "android.view.", attrs);
					}
				} else {
					view = layoutInflater.createView(name, null, attrs);
				}
			} catch (InflateException e) {
				throw e;
			} catch (Exception e) {
				InflateException ie = new InflateException(attrs.getPositionDescription() + ": Error inflating class " + name + ", cause " + e);
				ie.initCause(e);
				throw ie;
			}
		}

		if (view != null) {
			view.setTag(AbstractHolder.ATTR_HOLDER_KEY, attrHolder);
		}

		return view;
	}

	AbstractHolder getHolder(String name) {

		if (StringUtils.isBlank(name)) {
			return null;
		}

		AbstractHolder attrHolder = viewAttrHolder.get(name);
		if (attrHolder != null) {
			return attrHolder.clone();
		}

		if (-1 == name.indexOf('.')) {
			for (String prefix : sClassPrefixList) {
				try {
					attrHolder = getHolder(getSuperClassName(prefix + name));
				} catch (ClassNotFoundException e) {
				}
			}
		} else {
			try {
				attrHolder = getHolder(getSuperClassName(name));
			} catch (ClassNotFoundException e) {
			}
		}

		if (attrHolder == null) {
			Log.w("can not find a AttrHolder associated with name :{}", name);
			return null;
		} else {
			viewAttrHolder.put(name, attrHolder);
		}
		return attrHolder.clone();
	}

	private String getSuperClassName(String name) throws ClassNotFoundException {
		String superName = null;
		Class<?> clazz = Class.forName(name);
		if (clazz != null && clazz.getSuperclass() != null) {
			superName = clazz.getSuperclass().getName();
		}
		return superName;
	}

	public void registerAttrHolder(String name, AbstractHolder attrHolder) {
		viewAttrHolder.put(name, attrHolder);
	}
}
