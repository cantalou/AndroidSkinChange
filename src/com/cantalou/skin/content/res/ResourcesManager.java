/**
 *
 */
package com.cantalou.skin.content.res;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.cantalou.android.util.Log;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import static com.cantalou.android.util.ReflectUtil.invoke;

/**
 * @author cantalou
 * @date 2016年5月2日 下午9:11:12
 */
public class ResourcesManager {

    /**
     * 默认资源
     */
    public static final String DEFAULT_RESOURCES = "defaultResources";

    /**
     * 夜间模式皮肤资源名称, 夜间模式属于内置资源包
     */
    public static final String DEFAULT_NIGHT_RESOURCES = "defaultNightResources";

    /**
     * 已载入的资源
     */
    private HashMap<String, WeakReference<ProxyResources>> cacheResources = new HashMap<String, WeakReference<ProxyResources>>();

    private ResourcesManager() {
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
    public Resources createResource(String resourcesPath, Resources defResources) {

        Resources skinResources = null;

        File resourcesFile = new File(resourcesPath);
        if (!resourcesFile.exists()) {
            Log.w(resourcesFile + " does not exist");
            return null;
        }

        try {
            AssetManager am = AssetManager.class.newInstance();
            int result = invoke(am, "addAssetPath", new Class<?>[]{String.class}, resourcesFile.getAbsolutePath());
            if (result == 0) {
                Log.w("AssetManager.addAssetPath return 0. Fail to initialze AssetManager . ");
                return null;
            }
            skinResources = new SkinResources(am, defResources, resourcesPath);
        } catch (Throwable e) {
            Log.e(e, "Fail to initialze AssetManager");
        }
        return skinResources;
    }

    /**
     * 创建代理资源
     *
     * @param cxt
     * @param path 资源路径
     * @return 代理Resources, 如果path文件不存在或者解析失败返回null
     */
    public ProxyResources createProxyResource(Context cxt, String path, ProxyResources defResources) {

        if (DEFAULT_RESOURCES.equals(path)) {
            Log.d("skinPath is:{} , return defaultResources");
            return defResources;
        }

        ProxyResources proxyResources = null;
        WeakReference<ProxyResources> resRef = cacheResources.get(path);
        if (resRef != null) {
            proxyResources = resRef.get();
            if (proxyResources != null) {
                return proxyResources;
            }
        }

        if (DEFAULT_NIGHT_RESOURCES.equals(path)) {
            proxyResources = new NightResources(cxt.getPackageName(), defResources, defResources, path);
        } else {
            Resources skinResources = createResource(path, defResources);
            if (skinResources == null) {
                Log.w("Fail to create resources path :{}", path);
                return null;
            }
            proxyResources = new SkinProxyResources(cxt.getPackageName(), skinResources, defResources, path);
        }

        synchronized (this) {
            cacheResources.put(path, new WeakReference<ProxyResources>(proxyResources));
        }
        return proxyResources;
    }
}
