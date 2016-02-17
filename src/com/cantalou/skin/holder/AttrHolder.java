package com.cantalou.skin.holder;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;

/**
 *
 * @author cantalou
 * @date 2016年1月23日  下午11:30:39
 */
public interface AttrHolder {

	public static final int APP_RESOURCE_ID_PACKAGE = 0x7F000000;

	public static final int ATTR_HOLDER_KEY = 0x7FFFFFFF;

	/**
	 * 重新加载资源
	 * 
	 * @param view
	 */
	public void reloadAttr(View view, Resources res);

	/**
	 * 解析组件内的app资源属性
	 * 
	 * @param context
	 * @param attrs
	 */
	public void parse(Context context, AttributeSet attrs);

}