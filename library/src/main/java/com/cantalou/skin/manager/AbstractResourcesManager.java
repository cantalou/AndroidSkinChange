package com.cantalou.skin.manager;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.cantalou.android.util.Log;
import com.cantalou.android.util.ReflectUtil;
import com.cantalou.android.util.StringUtils;
import com.cantalou.android.util.array.BinarySearchIntArray;
import com.cantalou.skin.content.res.SkinResources;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * @author cantalou
 * @date 2017-06-03 12:21
 */
public abstract class AbstractResourcesManager implements ResourcesManager {

    protected HashMap<String, WeakReference<Resources>> cacheResources = new HashMap<String, WeakReference<Resources>>();

    /**
     * 确保替换布局文件是安全的<br/>
     * 由于代码的实现是和布局文件是相对应的, 所有在进行换肤的时候布局文件的更换是容易出错的 如:点击事件, 界面动画. <br/>
     * 如果皮肤资源包中不存在代码引用的View元素, 图片, 色值, 则会产生Crash.<br/>
     * 当确定布局文件是安全的时候可调用registerSafeLayout进行注册<br/>
     */
    protected BinarySearchIntArray safeLayout = new BinarySearchIntArray();

    protected Context context;

    public AbstractResourcesManager(Context context) {
        this.context = context;
    }

    /**
     * 创建资源
     *
     * @param resourcesPath 资源文件路径
     * @return 资源对象
     */
    public Resources createResources(Context context, String resourcesPath) {

        if (StringUtils.isBlank(resourcesPath)) {
            Log.w("param resourcesPath is blank");
            return context.getResources();
        }

        if (DEFAULT_RESOURCES.equals(resourcesPath)) {
            Log.d("default resources");
            return context.getResources();
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
                Log.w("AssetManager.addAssetPath {} return 0. Fail to initialize AssetManager. ", resourcesPath);
                return null;
            } else {
                Log.i("Add new asset path {} to {}, resultCookie:{}", context.getApplicationInfo().sourceDir, am, result);
            }

            Resources defResources = context.getResources();
            skinResources = createResources(am, defResources, resourcesPath);
        } catch (Throwable e) {
            Log.e(e, "Fail to init AssetManager");
        }
        return skinResources;
    }

    protected Resources createResources(AssetManager am, Resources defResources, String resourcesPath) {
        return new SkinResources(am, defResources, resourcesPath);
    }

    public void registerSafeLayout(int layoutId) {
        safeLayout.put(layoutId);
    }

    public boolean isSafeLayout(int layoutId) {
        return safeLayout.contains(layoutId);
    }

}
