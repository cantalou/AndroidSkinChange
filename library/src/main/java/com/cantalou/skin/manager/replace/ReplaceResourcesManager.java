package com.cantalou.skin.manager.replace;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.cantalou.android.util.Log;
import com.cantalou.android.util.ReflectUtil;
import com.cantalou.skin.content.res.replce.ReplaceResources;
import com.cantalou.skin.manager.AbstractResourcesManager;

/**
 * @author cantalou
 * @date 2017-06-03 18:05
 */
public class ReplaceResourcesManager extends AbstractResourcesManager {

    public ReplaceResourcesManager(Context context) {
        super(context);
    }

    @Override
    public Resources createResources(Context context, String resourcesPath) {
        Resources res = super.createResources(context, resourcesPath);
            try {
            AssetManager am = res.getAssets();
            int result = ReflectUtil.invoke(am, "addAssetPath", new Class<?>[]{String.class}, context.getApplicationInfo().sourceDir);
            if (result == 0) {
                Log.w("AssetManager.addAssetPath {} return 0. Fail to initialize AssetManager. ", resourcesPath);
            } else {
                Log.i("Add new asset path {} to {}, resultCookie:{}", context.getApplicationInfo().sourceDir, am, result);
            }
            res = new ReplaceResources(am, context.getResources());
        } catch (Throwable e) {
            Log.e(e, "Fail to init AssetManager");
        }
        return res;
    }
}
