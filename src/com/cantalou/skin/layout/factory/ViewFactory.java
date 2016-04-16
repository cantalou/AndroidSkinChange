package com.cantalou.skin.layout.factory;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.LayoutInflater.Factory;
import android.view.View;

import com.cantalou.android.util.Log;
import com.cantalou.android.util.ReflectUtil;
import com.cantalou.android.util.StringUtils;
import com.cantalou.skin.handler.AbstractHandler;
import com.cantalou.skin.handler.ImageViewHandler;
import com.cantalou.skin.handler.ListViewHandler;
import com.cantalou.skin.handler.TextViewHandler;
import com.cantalou.skin.handler.ViewHandler;
import com.cantalou.skin.handler.actionbar.ActionBarContainerHandler;
import com.cantalou.skin.handler.actionbar.ActionBarViewHandler;
import com.cantalou.skin.handler.actionbar.ActionMenuItemViewHandler;
import com.cantalou.skin.handler.actionbar.AppCompactToolBarHandler;

import java.util.HashMap;

/**
 * 自定义Factory的实现, 保存View中属性的资源信息, 如:background赋值的资源id
 *
 * @author cantalou
 * @date 2015年11月29日 下午10:22:41
 */
public class ViewFactory implements Factory {

    static final String[] classPrefixList = { "android.widget.", "android.webkit.", "android.app." };

    static final HashMap<String, String> superNameCache = new HashMap<String, String>();

    static final HashMap<String, AbstractHandler> viewAttrHandler = new HashMap<String, AbstractHandler>();

    static {
	// for super class
	viewAttrHandler.put("android.view.View", new ViewHandler());
	viewAttrHandler.put("View", new ViewHandler());// for layout file
	viewAttrHandler.put("android.widget.TextView", new TextViewHandler());
	viewAttrHandler.put("android.widget.ImageView", new ImageViewHandler());
	viewAttrHandler.put("android.widget.ListView", new ListViewHandler());

	// ActionBarSherlock
	viewAttrHandler.put("com.actionbarsherlock.internal.widget.ActionBarContainer", new ActionBarContainerHandler());
	viewAttrHandler.put("com.actionbarsherlock.internal.view.menu.ActionMenuItemView", new ActionMenuItemViewHandler());
	viewAttrHandler.put("com.actionbarsherlock.internal.widget.ActionBarView", new ActionBarViewHandler());

	// native actionbar
	viewAttrHandler.put("com.android.internal.widget.ActionBarContainer", new ActionBarContainerHandler());
	viewAttrHandler.put("com.android.internal.view.menu.ActionMenuItemView", new ActionMenuItemViewHandler());
	viewAttrHandler.put("com.android.internal.widget.ActionBarView", new ActionBarViewHandler());

	// AppCompact actionbar
	viewAttrHandler.put("android.support.v7.internal.widget.ActionBarContainer", new ActionBarContainerHandler());
	viewAttrHandler.put("android.support.v7.internal.view.menu.ActionMenuItemView", new ActionMenuItemViewHandler());
	viewAttrHandler.put("android.support.v7.widget.Toolbar", new AppCompactToolBarHandler());
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
	AbstractHandler attrHandler = getHandler(name);
	if (attrHandler != null) {
	    attrHandler.parse(context, attrs);
	}

	if (factoryProxy != null) {
	    view = factoryProxy.onCreateView(name, context, attrs);
	}

	if (view == null) {
	    // android.support.v4.app
	    // 在20.0.0及其以下版本中Fragment.getLayoutInflater()返回的是Activity.getLayoutInflater()
	    // 在21.0.0以上版本中的Fragment.getLayoutInflater()返回的是Activity.getLayoutInflater().cloneInContext(mActivity)
	    // 由于在21版本以后Activity和Fragment实例化布局用的LayoutInflater不是同一个对象,会导致Fragment在onCreateView方法中的layoutInflater参数与当前
	    // ViewFactory.onCreateView方法用到的成员变量layoutInflater不一致,而使mConstructorArgs没有得到初始化
	    Object[] constructorArgs = ReflectUtil.get(layoutInflater, "mConstructorArgs");
	    Object lastContext = constructorArgs[0];
	    if (lastContext == null) {
		constructorArgs[0] = context;
	    }
	    try {
		if (-1 == name.indexOf('.')) {
		    for (String prefix : classPrefixList) {
			try {
			    view = layoutInflater.createView(name, prefix, attrs);
			    if (view != null) {
				break;
			    }
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
	    } finally {
		constructorArgs[0] = lastContext;
	    }
	}

	if (view != null) {
	    view.setTag(ViewHandler.ATTR_HANDLER_KEY, attrHandler);
	}

	return view;
    }

    public static AbstractHandler getHandler(String name) {

	if (StringUtils.isBlank(name)) {
	    return null;
	}

	AbstractHandler attrHandler = viewAttrHandler.get(name);
	if (attrHandler != null) {
	    return attrHandler.clone();
	}

	if (-1 == name.indexOf('.')) {
	    outer: for (String prefix : classPrefixList) {
		try {
		    String superClassName = getSuperClassName(prefix + name);
		    while (superClassName != null) {
			attrHandler = getHandler(superClassName);
			if (attrHandler != null) {
			    break outer;
			}
			superClassName = getSuperClassName(superClassName);
		    }
		} catch (ClassNotFoundException e) {
		}
	    }
	} else {
	    try {
		attrHandler = getHandler(getSuperClassName(name));
	    } catch (ClassNotFoundException e) {
	    }
	}

	if (attrHandler == null) {
	    Log.w("can not find a AttrHandler associated with name :{}", name);
	    return null;
	} else {
	    viewAttrHandler.put(name, attrHandler);
	}
	return attrHandler.clone();
    }

    private static String getSuperClassName(String name) throws ClassNotFoundException {
	String superName = null;
	Class<?> clazz = Class.forName(name);
	if (clazz != null && clazz.getSuperclass() != null) {
	    superName = clazz.getSuperclass().getName();
	}
	return superName;
    }

    public static void registerAttrHandler(String name, ViewHandler attrHandler) {
	viewAttrHandler.put(name, attrHandler);
    }

    public void clearMemoryLeak(Activity activity) {

	if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
	    return;
	}

	Window w = activity.getWindow();
	if (w == null) {
	    return;
	}
	View decor = w.getDecorView();
	if (decor == null) {
	    return;
	}
	rClearMemoryLeak(decor);
    }

    private void rClearMemoryLeak(View view) {
	view.setTag(ViewHandler.ATTR_HANDLER_KEY, null);
	if (view instanceof ViewGroup) {
	    ViewGroup container = (ViewGroup) view;
	    for (int i = 0, len = container.getChildCount(); i < len; i++) {
		rClearMemoryLeak(container.getChildAt(i));
	    }
	}
    }
}
