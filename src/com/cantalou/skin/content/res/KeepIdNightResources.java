package com.cantalou.skin.content.res;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.*;
import android.graphics.drawable.DrawableContainer.DrawableContainerState;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.util.TypedValue;
import com.cantalou.android.util.ReflectUtil;
import com.cantalou.android.util.array.BinarySearchIntArray;

/**
 * 夜间模式资源类<br>
 * 1.对于Drawable资源添加灰色遮罩层<br>
 *
 * @author cantalou
 * @date 2016年4月13日 下午11:02:27
 */
public class KeepIdNightResources extends KeepIdSkinProxyResources {

    public KeepIdNightResources(Resources skin, Resources def) {
        super(skin, def);
    }


    public Drawable loadDrawable(int id) throws NotFoundException {
        Drawable result = super.loadDrawable(id);
        setColorFilter(result);
        return result;
    }

    /**
     * 给图片添加灰层
     *
     * @param drawable
     * @author cantalou
     * @date 2015年11月3日 下午4:08:56
     */
    private void setColorFilter(Drawable drawable) {

        if(drawable instanceof ColorDrawable){
            return;
        }

        if (drawable instanceof BitmapDrawable) {
            drawable.setColorFilter(Color.GRAY, android.graphics.PorterDuff.Mode.MULTIPLY);
        } else if (drawable instanceof DrawableContainer) {
            DrawableContainerState dcs = ReflectUtil.get(drawable, "mDrawableContainerState");
            if (dcs == null) {
                return;
            }
            for (Drawable d : dcs.getChildren()) {
                setColorFilter(d);
            }
        } else if (drawable instanceof LayerDrawable) {
            LayerDrawable ld = ((LayerDrawable) drawable);
            for (int i = 0; i < ld.getNumberOfLayers(); i++) {
                setColorFilter(ld.getDrawable(i));
            }
        } else if (drawable instanceof ScaleDrawable) {
            setColorFilter(((ScaleDrawable) drawable).getDrawable());
        } else if (drawable instanceof ClipDrawable) {
            setColorFilter((Drawable) ReflectUtil.get(drawable, "mClipState.mDrawable"));
        } else if (drawable instanceof RotateDrawable) {
            setColorFilter(((RotateDrawable) drawable).getDrawable());
        } else if (drawable instanceof InsetDrawable) {
            setColorFilter((Drawable) ReflectUtil.get(drawable, "mInsetState.mDrawable"));
        }
    }

}
