package com.cantalou.skin;

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
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class ViewFactoryAfterGingerbread extends ViewFactory implements Factory2 {

	private Factory2 factory2Proxy;

	private Factory2 privateProxy;

	public void register(LayoutInflater layoutInflater) {
		super.register(layoutInflater);

		factory2Proxy = layoutInflater.getFactory2();
		if (factory2Proxy != null) {
			layoutInflater.setFactory2(this);
		}

		privateProxy = ReflectUtil.get(layoutInflater, "mPrivateFactory");
		if (privateProxy != null) {
			ReflectUtil.set(layoutInflater, "mPrivateFactory", this);
		}
	}

	public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
		View view = null;

		AbstractHolder attrHolder = getHolder(name);
		if (attrHolder != null) {
			attrHolder.parse(attrs);
		}

		if (factory2Proxy != null) {
			view = factory2Proxy.onCreateView(parent, name, context, attrs);
		}
		if (view == null && privateProxy != null) {
			view = privateProxy.onCreateView(parent, name, context, attrs);
		}

		if (view != null) {
			view.setTag(AbstractHolder.ATTR_HOLDER_KEY, attrHolder);
		} else {
			view = super.onCreateView(name, context, attrs);
		}

		return view;
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		return factoryProxy == null ? null : super.onCreateView(name, context, attrs);
	}

}
