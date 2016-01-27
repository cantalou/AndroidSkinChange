package com.cantalou.skin.layout.factory;

import android.content.Context;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.View;

import com.cantalou.android.util.Log;
import com.cantalou.android.util.ReflectUtil;
import com.cantalou.android.util.StringUtils;
import com.cantalou.skin.holder.SherlockActionBarContainerHolder;
import com.cantalou.skin.holder.SherlockActionBarViewHolder;
import com.cantalou.skin.holder.SherlockActionMenuItemViewHolder;
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

	static final HashMap<String, ViewHolder> viewAttrHolder = new HashMap<String, ViewHolder>();
	static {
		viewAttrHolder.put("android.view.View", new ViewHolder());// for super class
		viewAttrHolder.put("View", new ViewHolder());// for layout file
		viewAttrHolder.put("android.widget.TextView", new TextViewHolder());
		viewAttrHolder.put("android.widget.ImageView", new ImageViewHolder());
		viewAttrHolder.put("android.widget.ListView", new ListViewHolder());
		
		//compact actionbarsherlock
		viewAttrHolder.put("com.actionbarsherlock.internal.widget.ActionBarContainer", new SherlockActionBarContainerHolder());
		viewAttrHolder.put("com.actionbarsherlock.internal.view.menu.ActionMenuItemView", new SherlockActionMenuItemViewHolder());
		viewAttrHolder.put("com.actionbarsherlock.internal.widget.ActionBarView", new SherlockActionBarViewHolder());
		

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
		ViewHolder attrHolder = getHolder(name);
		if (attrHolder != null) {
			attrHolder.parse(attrs);
		}

		if (factoryProxy != null) {
			view = factoryProxy.onCreateView(name, context, attrs);
		}

		if (view == null) {
			// android.support.v4.app
			// 在20.0.0及其以下版本中Fragment.getLayoutInflater()返回的是Activity.getLayoutInflater()
			// 在21.0.0以上版本中的Fragment.getLayoutInflater()返回的是Activity.getLayoutInflater().cloneInContext(mActivity)
			// 由于在21版本以后Activity和Fragment实例化布局用的LayoutInflater不是同一个对象,会导致Fragment在onCreateView方法中的layoutInflater参数与当前
			// ViewFactory.onCreateView方法用到的成员变量layoutInflater不一致,而是mConstructorArgs没有得到初始化
			Object[] constructorArgs = ReflectUtil.get(layoutInflater, "mConstructorArgs");
			Object lastContext = constructorArgs[0];
			if (lastContext == null) {
				constructorArgs[0] = context;
			}
			try {
				if (-1 == name.indexOf('.')) {
					for (String prefix : sClassPrefixList) {
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
				if (lastContext != null) {
					constructorArgs[0] = lastContext;
				}
			}
		}

		if (view != null) {
			view.setTag(ViewHolder.ATTR_HOLDER_KEY, attrHolder);
		}

		return view;
	}

	ViewHolder getHolder(String name) {

		if (StringUtils.isBlank(name)) {
			return null;
		}

		ViewHolder attrHolder = viewAttrHolder.get(name);
		if (attrHolder != null) {
			return attrHolder.clone();
		}

		if (-1 == name.indexOf('.')) {
			for (String prefix : sClassPrefixList) {
				try {
					attrHolder = getHolder(getSuperClassName(prefix + name));
					if (attrHolder != null) {
						break;
					}
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

	public void registerAttrHolder(String name, ViewHolder attrHolder) {
		viewAttrHolder.put(name, attrHolder);
	}
}
