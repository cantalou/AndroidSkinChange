package com.cantalou.skin.layout.factory;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory2;
import android.view.View;

import com.cantalou.android.util.ReflectUtil;
import com.cantalou.skin.handler.AbstractHandler;
import com.cantalou.skin.handler.ViewHandler;

/**
 * 自定义Factory的实现, 保存View中属性的资源信息, 如:background赋值的资源id
 *
 * @author cantalou
 * @date 2015年11月29日 下午10:22:41
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ViewFactoryAfterGingerbread extends ViewFactory implements Factory2 {

    private Factory2 factory2Proxy;

    private Factory2 privateProxy;

    public void register(LayoutInflater li) {
	super.register(li);

	factory2Proxy = li.getFactory2();
	if (factory2Proxy != null) {
	    ReflectUtil.set(li, "mFactorySet", false);
	    li.setFactory2(this);
	}

	privateProxy = ReflectUtil.get(li, "mPrivateFactory");
	if (privateProxy != null) {
	    ReflectUtil.set(li, "mPrivateFactory", this);
	}
    }

    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
	View view = null;

	if (factory2Proxy != null) {
	    view = factory2Proxy.onCreateView(parent, name, context, attrs);
	}
	if (view == null && privateProxy != null) {
	    view = privateProxy.onCreateView(parent, name, context, attrs);
	}

	if (view != null) {
	    AbstractHandler attrHandler = getHandler(name);
	    if (attrHandler != null) {
		attrHandler.parse(context, attrs);
	    }
	    view.setTag(ViewHandler.ATTR_HANDLER_KEY, attrHandler);
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
