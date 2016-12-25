package android.content.res;

import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import com.cantalou.skin.SkinManager;
import com.cantalou.skin.content.res.ProxyResources;

/**
 * @author Lin Zhiwei
 * @date 16-10-11 下午10:43
 */
public class ProxyTypeArray extends TypedArray {

    protected SkinManager skinManager;

    public void setSkinManager(SkinManager skinManager) {
        this.skinManager = skinManager;
    }

    @Override
    public int getColor(int index, int defValue) {

        TypedValue typedValue = new TypedValue();
        if (!getValue(index, typedValue)) {
            return defValue;
        }

        final int type = typedValue.type;
        if (type == TypedValue.TYPE_NULL) {
            return defValue;
        } else if (type >= TypedValue.TYPE_FIRST_INT && type <= TypedValue.TYPE_LAST_INT) {
            try {
                Resources res = skinManager.getCurrentResources();
                return res.getColor(typedValue.resourceId);
            } catch (Resources.NotFoundException e) {
                return super.getColor(index, defValue);
            }
        } else if (type == TypedValue.TYPE_STRING) {
            return super.getColor(index, defValue);
        }
        return defValue;
    }

}
