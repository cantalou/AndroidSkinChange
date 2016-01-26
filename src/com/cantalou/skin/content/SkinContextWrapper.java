package com.cantalou.skin.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.cantalou.skin.SkinManager;

import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.view.Display;
import android.view.LayoutInflater;

/**
 *
 * @author cantalou
 * @date 2016年1月25日 下午10:55:29
 */
public class SkinContextWrapper extends ContextWrapper {

	public SkinContextWrapper(Context base) {
		super(base);
	}

	@Override
	public void registerComponentCallbacks(ComponentCallbacks callback) {
		getBaseContext().registerComponentCallbacks(callback);
	}

	@Override
	public void unregisterComponentCallbacks(ComponentCallbacks callback) {
		getBaseContext().unregisterComponentCallbacks(callback);
	}

	@Override
	public Object getSystemService(String name) {
		Object obj = super.getSystemService(name);
		if (obj instanceof LayoutInflater) {
			SkinManager.getInstance().registerViewFactory((LayoutInflater) obj);
		}
		return obj;
	}

}