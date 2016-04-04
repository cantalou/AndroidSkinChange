package com.cantalou.skin.content;

import com.cantalou.skin.SkinManager;

import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.LayoutInflater;

/**
 *
 * @author cantalou
 * @date 2016年1月25日 下午10:55:29
 */
public class SkinContextWrapper extends ContextWrapper {

	public SkinContextWrapper(Context base) {
		super(base);
	}

	@Override
	public void registerComponentCallbacks(ComponentCallbacks callback) {
		getBaseContext().registerComponentCallbacks(callback);
	}

	@Override
	public void unregisterComponentCallbacks(ComponentCallbacks callback) {
		getBaseContext().unregisterComponentCallbacks(callback);
	}

	@Override
	public Object getSystemService(String name) {
		Object obj = super.getSystemService(name);
		if (obj instanceof LayoutInflater) {
			SkinManager.getInstance().registerViewFactory((LayoutInflater) obj);
		}
		return obj;
	}

}