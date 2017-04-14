package com.cantalou.android.sample;

import android.app.Application;

import skin.SkinManager;

/**
 * Project Name: AndroidSkinChange<p>
 * File Name:    SampleApplication.java<p>
 * ClassName:    Application<p>
 * <p>
 * TODO.
 *
 * @author LinZhiWei
 * @date 2017年04月14日 10:37
 * <p>
 * Copyright (c) 2017年, 4399 Network CO.ltd. All Rights Reserved.
 */
public class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SkinManager.getInstance().init(this);
    }
}
