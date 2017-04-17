package com.cantalou.skin;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.ConstantState;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.LongSparseArray;
import android.util.SparseArray;

import com.cantalou.android.util.Log;
import com.cantalou.android.util.ReflectUtil;
import com.cantalou.android.util.StringUtils;
import com.cantalou.android.util.array.BinarySearchIntArray;
import com.cantalou.android.util.array.SparseLongIntArray;
import com.cantalou.skin.array.ColorStateListLongSpareArray;
import com.cantalou.skin.array.ColorStateListLongSpareArrayForM;
import com.cantalou.skin.array.ColorStateListSpareArray;
import com.cantalou.skin.array.DrawableLongSpareArray;
import com.cantalou.skin.content.res.MessIdProxyResources;
import com.cantalou.skin.content.res.ProxyResources;
import com.cantalou.skin.content.res.SkinResources;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * 资源管理类<br/>
 * 1.创建Resources的代理对象<br/>
 * 2.替换系统资源静态缓存字段, 用于拦截所有的资源请求<br/>
 *
 * @author cantalou
 * @date 2016年5月2日 下午9:11:12
 */
public class ResourcesManager {

    /**
     * 默认资源
     */
    public static final String DEFAULT_RESOURCES = "defaultResources";

    /**
     * 已载入的资源
     */
    private HashMap<String, WeakReference<Resources>> cacheResources = new HashMap<String, WeakReference<Resources>>();

    /**
     * 确保替换布局文件是安全的<br/>
     * 由于代码的实现是和布局文件是相对应的, 所有在进行换肤的时候布局文件的更换是容易出错的 如:点击事件, 界面动画. <br/>
     * 如果皮肤资源包中不存在代码引用的View元素, 图片, 色值, 则会产生Crash.<br/>
     * 当确定布局文件是安全的时候可调用registerSafeLayout进行注册<br/>
     */
    protected BinarySearchIntArray safeLayout = new BinarySearchIntArray();

    protected Context context;

    private ResourcesManager() {
        replaceCacheEntry();
    }

    private static class InstanceHolder {
        static final ResourcesManager INSTANCE = new ResourcesManager();
    }

    public static ResourcesManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 创建资源
     *
     * @param resourcesPath 资源文件路径
     * @return 资源对象
     */
    public Resources createResource(Context context, String resourcesPath, Resources defResources) {

        if (StringUtils.isBlank(resourcesPath)) {
            Log.w("param resourcesPath is blank");
            return null;
        }

        Resources skinResources = null;
        File resourcesFile = new File(resourcesPath);
        if (!resourcesFile.exists()) {
            Log.w(resourcesFile + " does not exist");
            return null;
        }

        try {
            AssetManager am = AssetManager.class.newInstance();
            int result = ReflectUtil.invoke(am, "addAssetPath", new Class<?>[]{String.class}, resourcesFile.getAbsolutePath());
            if (result == 0) {
                Log.w("AssetManager.addAssetPath return 0. Fail to initialize AssetManager . ");
                return null;
            }
            skinResources = new SkinResources(am, defResources, resourcesPath);
        } catch (Throwable e) {
            Log.e(e, "Fail to init AssetManager");
        }
        return skinResources;
    }

    /**
     * 创建代理资源
     *
     * @param path 资源路径
     * @return 代理Resources, 如果path文件不存在或者解析失败返回null
     */
    public Resources createProxyResource(Context context, String path, Resources defResources) {

        if (DEFAULT_RESOURCES.equals(path)) {
            Log.d("resourcePath is:{} , return defaultResources {}", path, defResources);
            return defResources;
        }

        Resources proxyResources = null;
        WeakReference<Resources> resRef = cacheResources.get(path);
        if (resRef != null) {
            proxyResources = resRef.get();
            if (proxyResources != null) {
                return proxyResources;
            }
        }

        Resources skinResources = createResource(context, path, defResources);
        if (skinResources == null) {
            Log.w("Fail to create resources path :{}", path);
            return null;
        }
//        proxyResources = new KeepIdProxyResources(skinResources, defResources);
        proxyResources = new MessIdProxyResources(skinResources, defResources, context.getPackageName());

        synchronized (this) {
            cacheResources.put(path, new WeakReference<Resources>(proxyResources));
        }
        return proxyResources;
    }

    /**
     * Replaced Resource static pre field
     */
    private static volatile boolean replaced = false;

    /**
     * 替换Resources的相关preload对象引用
     */
    public void replaceCacheEntry() {

        synchronized (ProxyResources.class) {

            if (replaced) {
                return;
            }
            replaced = true;

            SkinManager skinManager = SkinManager.getInstance();
            ResourcesCacheKeyIdManager resourcesCacheKeyIdManager = skinManager.getResourcesCacheKeyIdManager();

            // drawable
            SparseLongIntArray keyIdMap = resourcesCacheKeyIdManager.getDrawableCacheKeyIdMap();
            if (Build.VERSION.SDK_INT >= 23) {
                //
                LongSparseArray<Drawable.ConstantState>[] sPreloadedDrawablesArray = ReflectUtil.get(Resources.class, "sPreloadedDrawables");
                LongSparseArray<Drawable.ConstantState> proxyPreloadedDrawables = new DrawableLongSpareArray(skinManager, sPreloadedDrawablesArray[0],
                        keyIdMap);
                sPreloadedDrawablesArray[0] = proxyPreloadedDrawables;

                LongSparseArray<Drawable.ConstantState> sPreloadedColorDrawables = ReflectUtil.get(Resources.class, "sPreloadedColorDrawables");
                LongSparseArray<Drawable.ConstantState> proxyPreloadedColorDrawables = new DrawableLongSpareArray(skinManager, sPreloadedColorDrawables,
                        keyIdMap);
                ReflectUtil.set(Resources.class, "sPreloadedColorDrawables", proxyPreloadedColorDrawables);

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
            Object originalPreCSL = ReflectUtil.get(Resources.class, "sPreloadedColorStateLists");
            Object proxyPreloadedColorStateLists;
            if (Build.VERSION.SDK_INT >= 23) {
                proxyPreloadedColorStateLists = new ColorStateListLongSpareArrayForM(skinManager, (LongSparseArray<ConstantState<ColorStateList>>) originalPreCSL, keyIdMap);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                proxyPreloadedColorStateLists = new ColorStateListLongSpareArray(skinManager, (LongSparseArray<ColorStateList>) originalPreCSL, keyIdMap);
            } else {
                proxyPreloadedColorStateLists = new ColorStateListSpareArray(skinManager, (SparseArray<ColorStateList>) originalPreCSL, keyIdMap);
            }
            ReflectUtil.set(Resources.class, "mPreloadedColorStateLists", proxyPreloadedColorStateLists);
        }

    }

    public void registerSafeLayout(int layoutId) {
        safeLayout.put(layoutId);
    }

    public boolean isSafeLayout(int layoutId) {
        return safeLayout.contains(layoutId);
    }
}
