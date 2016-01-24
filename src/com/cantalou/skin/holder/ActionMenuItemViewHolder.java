package com.cantalou.skin.holder;

import com.cantalou.android.util.ReflectUtil;

import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import static com.cantalou.android.util.ReflectUtil.*;

/**
 * 
 * ActionBar菜单图标刷新
 * 
 * @author cantalou
 * @date 2016年1月23日 下午11:30:17
 */
@SuppressWarnings("deprecation")
public class ActionMenuItemViewHolder extends ViewHolder {

	@Override
	protected void reload(View view, Resources res) {
		super.reload(view, res);
		Object itemData = get(view, "mItemData");
		int id = get(view, "mItemData.mIconResId");
		if (id != 0) {
			invokeByMethodName(view, "setIcon", res.getDrawable(id));
		}
	}

	@Override
	public boolean parseAttr(AttributeSet attrs) {
		return super.parseAttr(attrs) || true;
	}

}
