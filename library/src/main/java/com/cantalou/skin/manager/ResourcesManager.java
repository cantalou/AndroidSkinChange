package com.cantalou.skin.manager;

import android.content.Context;
import android.content.res.Resources;

/**
 * @author cantalou
 * @date 2017/6/3 10:03
 */
public interface ResourcesManager {

    /**
     * Default resources file path
     */
    public static final String DEFAULT_RESOURCES = "defaultResources";

    /**
     * @param context
     * @param sourcePath
     * @return
     */
    public Resources createResources(Context context, String sourcePath);

    /**
     * Register the layout that can be used when skin changing safely
     *
     * @param layoutId
     */
    public void registerSafeLayout(int layoutId);

    /**
     * Check the layout could be used or not
     *
     * @param layoutId
     * @return
     */
    public boolean isSafeLayout(int layoutId);
}
