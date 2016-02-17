package com.cantalou.skin.holder;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.cantalou.android.util.ReflectUtil;

public class ListViewHolder extends ViewHolder
{

    protected int divider;

    @Override
    protected void reload(View view, Resources res)
    {
        super.reload(view, res);
        if (divider != 0)
        {
            ((ListView) view).setDivider(res.getDrawable(divider));
        }

        Object recycler = ReflectUtil.get(view, "mRecycler");
        ReflectUtil.invoke(recycler, "scrapActiveViews");
        ReflectUtil.invoke(recycler, "clear");
        Adapter adapter = ((AbsListView) view).getAdapter();
        if (adapter instanceof BaseAdapter)
        {
            ((BaseAdapter) adapter).notifyDataSetChanged();
        }
    }

    @Override
    public boolean parseAttr(Context context, AttributeSet attrs)
    {
        divider = getResourceId(attrs, "divider");
        if (divider != 0)
        {
            cacheKeyAndIdManager.registerDrawable(divider);
        }
        return super.parseAttr(context, attrs) || divider != 0;
    }
}
