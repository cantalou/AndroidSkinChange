package skin.handler;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.cantalou.android.util.ReflectUtil;

/**
 * @author cantalou
 * @date 2016年2月29日 上午10:48:12
 */
public class ListViewHandler extends ViewHandler {

    protected int divider;

    @SuppressWarnings("deprecation")
    @Override
    protected void reloadAttr(View view, Resources res, boolean onlyColor) {
        super.reloadAttr(view, res, onlyColor);
        if (divider != 0) {
            ((ListView) view).setDivider(res.getDrawable(divider));
        }

        Object recycler = ReflectUtil.get(view, "mRecycler");
        ReflectUtil.invoke(recycler, "scrapActiveViews");
        ReflectUtil.invoke(recycler, "clear");
        Adapter adapter = ((AbsListView) view).getAdapter();
        if (adapter instanceof BaseAdapter) {
            ((BaseAdapter) adapter).notifyDataSetChanged();
        }
    }

    @Override
    public boolean parseAttr(Context context, AttributeSet attrs) {
        divider = getResourceId(attrs, "divider");
        return super.parseAttr(context, attrs) || divider != 0;
    }
}
