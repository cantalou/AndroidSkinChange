package com.cantalou.skin.manager;

import android.content.Context;

import com.cantalou.skin.manager.replace.ReplaceResourcesManager;

/**
 * @author cantalou
 * @date 2017-06-03 18:38
 */
public class ResourcesManagerFactory {

    public ResourcesManager createResourcesManager(Context context) {
        return new ReplaceResourcesManager(context);
    }
}
