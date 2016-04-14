package com.cantalou.skin.handler.actionbar;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;

import static com.cantalou.android.util.ReflectUtil.get;
import static com.cantalou.android.util.ReflectUtil.invokeByMethodName;

/**
 * ActionBar菜单图标刷新
 *
 * @author cantalou
 * @date 2016年1月23日 下午11:30:17
 */
@SuppressWarnings("deprecation")
public class ActionMenuItemViewHolder extends ActionBarHolder {

    @Override
    protected void reload(View view, Resources res) {
	super.reload(view, res);
	MenuItem itemData = get(view, "mItemData");
	if (itemData == null) {
	    return;
	}

	int itemId = itemData.getItemId();
	if (itemId == 0) {
	    return;
	}

	int iconResId = cacheKeyAndIdManager.getMenuItemIdAndIconIdMap().get(itemId);
	if (iconResId != 0) {
	    invokeByMethodName(view, "setIcon", res.getDrawable(iconResId));
	}
    }

    @Override
    public boolean parseAttr(Context context, AttributeSet attrs) {
	return super.parseAttr(context, attrs);
    }
}
