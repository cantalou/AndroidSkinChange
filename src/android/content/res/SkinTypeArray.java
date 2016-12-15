package android.content.res;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import com.cantalou.skin.SkinManager;

/**
 * @author Lin Zhiwei
 * @date 16-10-11 下午10:43
 */
public class SkinTypeArray extends TypedArray {

    private SkinManager skinManager ;

    public void setSkinManager(SkinManager skinManager) {
        this.skinManager = skinManager;
    }

    @Override
    public int getColor(int index, int defValue) {

        TypedValue typedValue = new TypedValue();
        if (!getValue(index, typedValue)) {
            return defValue;
        }

        Resources res = skinManager.getCurrentResources();

        final int type = typedValue.type;
        if (type == TypedValue.TYPE_NULL) {
            return defValue;
        } else if (type >= TypedValue.TYPE_FIRST_INT && type <= TypedValue.TYPE_LAST_INT) {
            return res.getColor(typedValue.resourceId);
        } else if (type == TypedValue.TYPE_STRING) {
            ColorStateList csl = res.getColorStateList(typedValue.resourceId);
            return csl.getDefaultColor();
        }
        return defValue;
    }

    @Override
    public Drawable getDrawable(int index) {

        TypedValue typedValue = new TypedValue();
        if (!getValue(index, typedValue)) {
            return super.getDrawable(index);
        }

        Resources res = skinManager.getCurrentResources();
        return res.getDrawable(typedValue.resourceId);
    }
}
