package com.cantalou.skin.content.res.replce;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

import com.cantalou.android.util.Log;

/**
 * @author cantalou
 * @date 2017-06-04 11:52
 */
public class ReplaceResources extends Resources {

    private Resources defaultResources;

    public ReplaceResources(AssetManager am, Resources defaultResources) {
        super(am, defaultResources.getDisplayMetrics(), defaultResources.getConfiguration());
        this.defaultResources = defaultResources;
    }

    @Override
    public XmlResourceParser getLayout(int id) throws NotFoundException {
        return defaultResources.getLayout(id);
    }

    @Override
    public CharSequence getText(int id) throws NotFoundException {
        return defaultResources.getText(id);
    }
}
