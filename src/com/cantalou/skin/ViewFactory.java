package com.cantalou.skin;

import android.content.Context;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cantalou.skin.holder.AbstractHolder;
import com.cantalou.skin.holder.AttrHolder;
import com.cantalou.skin.holder.ImageViewHolder;
import com.cantalou.skin.holder.ListViewHolder;
import com.cantalou.skin.holder.TextViewHolder;
import com.cantalou.skin.holder.ViewHolder;

import java.util.HashMap;

/**
 * 自定义Factory的实现, 保存View中属性的资源信息, 如:background赋值的资源id
 *
 * @author LinZhiWei
 * @date 2015年11月29日 下午10:22:41
 */
public class ViewFactory implements Factory {

	private final String[] sClassPrefixList = { "android.widget.", "android.webkit.", "android.app." };

	private final HashMap<Class<?>, AbstractHolder> viewAttrHolder = new HashMap<Class<?>, AbstractHolder>() {
		{
			put(View.class, new ViewHolder());
			put(TextView.class, new TextViewHolder());
			put(ImageView.class, new ImageViewHolder());
			put(ListView.class, new ListViewHolder());
		}
	};

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		View view = null;
		try {
			LayoutInflater inflater = LayoutInflater.from(context);
			if (-1 == name.indexOf('.')) {
				for (String prefix : sClassPrefixList) {
					try {
						view = inflater.createView(name, prefix, attrs);
					} catch (ClassNotFoundException e) {
						// In this case we want to let the base class take a
						// crack at it.
					}
				}
			} else {
				view = inflater.createView(name, null, attrs);
			}

			if (view == null) {
				return null;
			}

			getHolder(view.getClass()).parse(view, attrs);

			return view;

		} catch (InflateException e) {
			throw e;
		} catch (Exception e) {
			InflateException ie = new InflateException(attrs.getPositionDescription() + ": Error inflating class " + name);
			ie.initCause(e);
			throw ie;
		}
	}

	private AttrHolder getHolder(Class<?> clazz) throws CloneNotSupportedException {
		AbstractHolder attrHolder = viewAttrHolder.get(clazz);
		if (attrHolder != null) {
			return (AbstractHolder)attrHolder.clone();
		} else {
			return getHolder(clazz.getSuperclass());
		}
	}
}
