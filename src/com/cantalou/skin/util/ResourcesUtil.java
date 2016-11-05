package com.cantalou.skin.util;

import android.util.TypedValue;

/**
 * @author Lin Zhiwei
 * @date 16-10-11 下午10:43
 */
public class ResourcesUtil {
    protected boolean isColor(TypedValue value) {
        return value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT;
    }
}
