package skin.array;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.LongSparseArray;

import com.cantalou.android.util.array.SparseLongIntArray;
import skin.SkinManager;
import skin.content.res.ProxyResources;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class DrawableLongSpareArray extends LongSparseArray<Drawable.ConstantState> {

    private SparseLongIntArray resourceIdKeyMap;

    /**
     * Resources mColorStateListCache
     */
    private LongSparseArray<Drawable.ConstantState> originalCache;

    private SkinManager skinManager;

    public DrawableLongSpareArray(SkinManager skinManager, LongSparseArray<Drawable.ConstantState> originalCache, SparseLongIntArray resourceIdKeyMap) {
        this.originalCache = originalCache;
        this.resourceIdKeyMap = resourceIdKeyMap;
        this.skinManager = skinManager;
    }

    @Override
    public Drawable.ConstantState get(long key) {
        int id = resourceIdKeyMap.get(key);
        if (id != 0) {
            Resources res = skinManager.getCurrentResources();
            if (res != null && res instanceof ProxyResources) {
                Drawable dr = ((ProxyResources) res).loadDrawable(id);
                if (dr != null) {
                    return dr.getConstantState();
                }
            }
        }
        return originalCache.get(key);
    }

    public LongSparseArray<Drawable.ConstantState> getOriginalCache() {
        return originalCache;
    }
}
