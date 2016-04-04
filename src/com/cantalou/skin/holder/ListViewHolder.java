package com.cantalou.skin.holder;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.WrapperListAdapter;

import com.cantalou.android.util.ReflectUtil;

/**
 *
 * 
 * @author cantalou
 * @date 2016年2月29日 上午10:48:12
 */
public class ListViewHolder extends ViewHolder {

	protected int divider;

	@SuppressWarnings("deprecation")
	@Override
	protected void reload(View view, Resources res) {
		super.reload(view, res);
		ListView lv = (ListView) view;
		if (divider != 0) {
			lv.setDivider(res.getDrawable(divider));
		}
		int index = lv.getFirstVisiblePosition();  
		View v = lv.getChildAt(0);  
		int top = (v == null) ? 0 : v.getTop();  
		ListAdapter adapter = lv.getAdapter();
		if(adapter instanceof WrapperListAdapter){
            adapter = ((WrapperListAdapter)adapter).getWrappedAdapter();
        }
		lv.setAdapter(adapter);
		lv.setSelectionFromTop(index, top); 
	}

	@Override
	public boolean parseAttr(Context context, AttributeSet attrs) {
		divider = getResourceId(attrs, "divider");
		if (divider != 0) {
			cacheKeyAndIdManager.registerDrawable(divider);
		}
		return super.parseAttr(context, attrs) || divider != 0;
	}
}
