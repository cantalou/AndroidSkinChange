package com.cantalou.skin.manager.hook;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.ConstantState;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.LongSparseArray;
import android.util.SparseArray;

import com.cantalou.android.util.Log;
import com.cantalou.android.util.ReflectUtil;
import com.cantalou.android.util.array.SparseLongIntArray;
import com.cantalou.skin.SkinManager;
import com.cantalou.skin.array.ColorStateListLongSpareArray;
import com.cantalou.skin.array.ColorStateListLongSpareArrayForM;
import com.cantalou.skin.array.ColorStateListSpareArray;
import com.cantalou.skin.array.DrawableLongSpareArray;
import com.cantalou.skin.content.res.hook.MessIdProxyResources;
import com.cantalou.skin.content.res.hook.ProxyResources;
import com.cantalou.skin.manager.AbstractResourcesManager;

import java.lang.ref.WeakReference;

/**
 * 资源管理类<br/>
 * 1.创建Resources的代理对象<br/>
 * 2.替换系统资源静态缓存字段, 用于拦截所有的资源请求<br/>
 *
 * @author cantalou
 * @date 2016年5月2日 下午9:11:12
 */
public class HookPreloadResourcesManager extends AbstractResourcesManager {

    public HookPreloadResourcesManager(Context context) {
        super(context);
        replaceCacheEntry();
    }

    /**
     * 创建代理资源
     *
     * @param path 资源路径
     * @return 代理Resources, 如果path文件不存在或者解析失败返回null
     */
    @Override
    public Resources createResources(Context context, String path) {
        Resources skinResources = super.createResources(context, path);
        if (skinResources == null) {
            Log.w("Fail to create resources path :{}", path);
            return null;
        }
        Resources proxyResources = new MessIdProxyResources(skinResources, context.getResources(), context.getPackageName());
        synchronized (this) {
            cacheResources.put(path, new WeakReference<Resources>(proxyResources));
        }
        return proxyResources;
    }

    /**
     * Replaced Resource static pre field
     */
    private static boolean replaced = false;

    /**
     * 替换Resources的相关preload对象引用
     */
    public void replaceCacheEntry() {

        if (replaced) {
            return;
        }

        synchronized (ProxyResources.class) {
            if (replaced) {
                return;
            }
            replaced = true;
        }

        SkinManager skinManager = SkinManager.getInstance();
        ResourcesCacheKeyIdManager resourcesCacheKeyIdManager = new ResourcesCacheKeyIdManager(context.getResources());

        // drawable
        SparseLongIntArray keyIdMap = resourcesCacheKeyIdManager.getDrawableCacheKeyIdMap();
        if (Build.VERSION.SDK_INT >= 23) {
            LongSparseArray<Drawable.ConstantState>[] sPreloadedDrawablesArray = ReflectUtil.get(Resources.class, "sPreloadedDrawables");
            LongSparseArray<Drawable.ConstantState> proxyPreloadedDrawables = new DrawableLongSpareArray(skinManager, sPreloadedDrawablesArray[0], keyIdMap);
            sPreloadedDrawablesArray[0] = proxyPreloadedDrawables;

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            LongSparseArray<Drawable.ConstantState>[] sPreloadedDrawablesArray = ReflectUtil.get(Resources.class, "sPreloadedDrawables");
            LongSparseArray<Drawable.ConstantState> proxyPreloadedDrawables = new DrawableLongSpareArray(skinManager, sPreloadedDrawablesArray[0], keyIdMap);
            sPreloadedDrawablesArray[0] = proxyPreloadedDrawables;
        } else {
            LongSparseArray<Drawable.ConstantState> sPreloadedDrawables = ReflectUtil.get(Resources.class, "sPreloadedDrawables");
            LongSparseArray<Drawable.ConstantState> proxyPreloadedDrawables = new DrawableLongSpareArray(skinManager, sPreloadedDrawables, keyIdMap);
            ReflectUtil.set(Resources.class, "sPreloadedDrawables", proxyPreloadedDrawables);
        }

        // colorStateList
        keyIdMap = resourcesCacheKeyIdManager.getColorStateListCacheKeyIdMap();
        Object proxyPreloadedColorStateLists;
        if (Build.VERSION.SDK_INT >= 23) {
            Object originalPreCSL = ReflectUtil.get(Resources.class, "sPreloadedColorStateLists");
            proxyPreloadedColorStateLists = new ColorStateListLongSpareArrayForM(skinManager, (LongSparseArray<ConstantState<ColorStateList>>) originalPreCSL, keyIdMap);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Object originalPreCSL = ReflectUtil.get(Resources.class, "sPreloadedColorStateLists");
            proxyPreloadedColorStateLists = new ColorStateListLongSpareArray(skinManager, (LongSparseArray<ColorStateList>) originalPreCSL, keyIdMap);
        } else {
            Object originalPreCSL = ReflectUtil.get(Resources.class, "mPreloadedColorStateLists");
            proxyPreloadedColorStateLists = new ColorStateListSpareArray(skinManager, (SparseArray<ColorStateList>) originalPreCSL, keyIdMap);
        }
        ReflectUtil.set(Resources.class, "mPreloadedColorStateLists", proxyPreloadedColorStateLists);

    }

}
