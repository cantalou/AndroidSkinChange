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
public class SkinContextWrapper extends Context {

	private Context delegate;

	public SkinContextWrapper(Context delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public boolean bindService(Intent service, ServiceConnection conn, int flags) {
		return delegate.bindService(service, conn, flags);
	}

	@Override
	public int checkCallingOrSelfPermission(String permission) {
		return delegate.checkCallingOrSelfPermission(permission);
	}

	@Override
	public void clearWallpaper() throws IOException {
		delegate.clearWallpaper();
	}

	@Override
	public int checkPermission(String permission, int pid, int uid) {
		return delegate.checkPermission(permission, pid, uid);
	}

	@Override
	public int checkCallingPermission(String permission) {
		return delegate.checkCallingPermission(permission);
	}

	@Override
	public int checkUriPermission(Uri uri, int pid, int uid, int modeFlags) {
		return delegate.checkUriPermission(uri, pid, uid, modeFlags);
	}

	@Override
	public int checkCallingUriPermission(Uri uri, int modeFlags) {
		return delegate.checkCallingUriPermission(uri, modeFlags);
	}

	@Override
	public int checkCallingOrSelfUriPermission(Uri uri, int modeFlags) {
		return delegate.checkCallingOrSelfUriPermission(uri, modeFlags);
	}

	@Override
	public int checkSelfPermission(String arg0) {
		return delegate.checkSelfPermission(arg0);
	}

	@Override
	public int checkUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid, int modeFlags) {
		return delegate.checkUriPermission(uri, readPermission, writePermission, pid, uid, modeFlags);
	}

	@Override
	public Context createConfigurationContext(Configuration arg0) {
		return delegate.createConfigurationContext(arg0);
	}

	@Override
	public Context createDisplayContext(Display arg0) {
		return delegate.createDisplayContext(arg0);
	}

	@Override
	public Context createPackageContext(String packageName, int flags) throws NameNotFoundException {
		return delegate.createPackageContext(packageName, flags);
	}

	@Override
	public AssetManager getAssets() {
		return delegate.getAssets();
	}

	@Override
	public ContentResolver getContentResolver() {
		return delegate.getContentResolver();
	}

	@Override
	public Context getApplicationContext() {
		return delegate.getApplicationContext();
	}

	@Override
	public ClassLoader getClassLoader() {
		return delegate.getClassLoader();
	}

	@Override
	public ApplicationInfo getApplicationInfo() {
		return delegate.getApplicationInfo();
	}

	@Override
	public boolean deleteFile(String name) {
		return delegate.deleteFile(name);
	}

	@Override
	public File getCacheDir() {
		return delegate.getCacheDir();
	}

	@Override
	public String[] fileList() {
		return delegate.fileList();
	}

	@Override
	public File getDir(String name, int mode) {
		return delegate.getDir(name, mode);
	}

	@Override
	public boolean deleteDatabase(String name) {
		return delegate.deleteDatabase(name);
	}

	@Override
	public File getDatabasePath(String name) {
		return delegate.getDatabasePath(name);
	}

	@Override
	public String[] databaseList() {
		return delegate.databaseList();
	}

	@Override
	public void enforcePermission(String permission, int pid, int uid, String message) {
		delegate.enforcePermission(permission, pid, uid, message);
	}

	@Override
	public void enforceCallingPermission(String permission, String message) {
		delegate.enforceCallingPermission(permission, message);
	}

	@Override
	public void enforceCallingOrSelfPermission(String permission, String message) {
		delegate.enforceCallingOrSelfPermission(permission, message);
	}

	@Override
	public void enforceUriPermission(Uri uri, int pid, int uid, int modeFlags, String message) {
		delegate.enforceUriPermission(uri, pid, uid, modeFlags, message);
	}

	@Override
	public void enforceCallingUriPermission(Uri uri, int modeFlags, String message) {
		delegate.enforceCallingUriPermission(uri, modeFlags, message);
	}

	@Override
	public void enforceCallingOrSelfUriPermission(Uri uri, int modeFlags, String message) {
		delegate.enforceCallingOrSelfUriPermission(uri, modeFlags, message);
	}

	@Override
	public void enforceUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid, int modeFlags, String message) {
		delegate.enforceUriPermission(uri, readPermission, writePermission, pid, uid, modeFlags, message);
	}

	@Override
	public File getCodeCacheDir() {
		return delegate.getCodeCacheDir();
	}

	@Override
	public File getExternalCacheDir() {
		return delegate.getExternalCacheDir();
	}

	@Override
	public File[] getExternalCacheDirs() {
		return delegate.getExternalCacheDirs();
	}

	@Override
	public File getExternalFilesDir(String type) {
		return delegate.getExternalFilesDir(type);
	}

	@Override
	public File[] getExternalFilesDirs(String type) {
		return delegate.getExternalFilesDirs(type);
	}

	@Override
	public File[] getExternalMediaDirs() {
		return delegate.getExternalMediaDirs();
	}

	@Override
	public Resources getResources() {
		return delegate.getResources();
	}

	@Override
	public PackageManager getPackageManager() {
		return delegate.getPackageManager();
	}

	@Override
	public Looper getMainLooper() {
		return delegate.getMainLooper();
	}

	@Override
	public Theme getTheme() {
		return delegate.getTheme();
	}

	@Override
	public String getPackageName() {
		return delegate.getPackageName();
	}

	@Override
	public String getPackageResourcePath() {
		return delegate.getPackageResourcePath();
	}

	@Override
	public File getFileStreamPath(String name) {
		return delegate.getFileStreamPath(name);
	}

	@Override
	public File getFilesDir() {
		return delegate.getFilesDir();
	}

	@Override
	public File getNoBackupFilesDir() {
		return delegate.getNoBackupFilesDir();
	}

	@Override
	public File getObbDir() {
		return delegate.getObbDir();
	}

	@Override
	public File[] getObbDirs() {
		return delegate.getObbDirs();
	}

	@Override
	public String getPackageCodePath() {
		return delegate.getPackageCodePath();
	}

	@Override
	public SharedPreferences getSharedPreferences(String name, int mode) {
		return delegate.getSharedPreferences(name, mode);
	}

	@Override
	public Object getSystemService(String name) {
		Object obj = delegate.getSystemService(name);
		if (obj instanceof LayoutInflater) {
			SkinManager.getInstance().registerViewFactory((LayoutInflater) obj);
		}
		return obj;
	}

	@Override
	public String getSystemServiceName(Class<?> arg0) {
		return delegate.getSystemServiceName(arg0);
	}

	@Override
	public FileInputStream openFileInput(String name) throws FileNotFoundException {
		return delegate.openFileInput(name);
	}

	@Override
	public FileOutputStream openFileOutput(String name, int mode) throws FileNotFoundException {
		return delegate.openFileOutput(name, mode);
	}

	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory) {
		return delegate.openOrCreateDatabase(name, mode, factory);
	}

	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory, DatabaseErrorHandler errorHandler) {
		return delegate.openOrCreateDatabase(name, mode, factory, errorHandler);
	}

	@Override
	public Drawable getWallpaper() {
		return delegate.getWallpaper();
	}

	@Override
	public Drawable peekWallpaper() {
		return delegate.peekWallpaper();
	}

	@Override
	public int getWallpaperDesiredMinimumWidth() {
		return delegate.getWallpaperDesiredMinimumWidth();
	}

	@Override
	public int getWallpaperDesiredMinimumHeight() {
		return delegate.getWallpaperDesiredMinimumHeight();
	}

	@Override
	public void sendBroadcast(Intent intent) {
		delegate.sendBroadcast(intent);
	}

	@Override
	public void sendBroadcast(Intent intent, String receiverPermission) {
		delegate.sendBroadcast(intent, receiverPermission);
	}

	@Override
	public void removeStickyBroadcast(Intent intent) {
		delegate.removeStickyBroadcast(intent);
	}

	@Override
	public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
		return delegate.registerReceiver(receiver, filter);
	}

	@Override
	public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler) {
		return delegate.registerReceiver(receiver, filter, broadcastPermission, scheduler);
	}

	@Override
	public void grantUriPermission(String toPackage, Uri uri, int modeFlags) {
		delegate.grantUriPermission(toPackage, uri, modeFlags);
	}

	@Override
	public void removeStickyBroadcastAsUser(Intent intent, UserHandle user) {
		delegate.removeStickyBroadcastAsUser(intent, user);
	}

	@Override
	public void revokeUriPermission(Uri uri, int modeFlags) {
		delegate.revokeUriPermission(uri, modeFlags);
	}

	@Override
	public void sendBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission) {
		delegate.sendBroadcastAsUser(intent, user, receiverPermission);
	}

	@Override
	public void sendBroadcastAsUser(Intent intent, UserHandle user) {
		delegate.sendBroadcastAsUser(intent, user);
	}

	@Override
	public void sendOrderedBroadcast(Intent intent, String receiverPermission) {
		delegate.sendOrderedBroadcast(intent, receiverPermission);
	}

	@Override
	public void sendOrderedBroadcast(Intent intent, String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode,
			String initialData, Bundle initialExtras) {
		delegate.sendOrderedBroadcast(intent, receiverPermission, resultReceiver, scheduler, initialCode, initialData, initialExtras);
	}

	@Override
	public void sendOrderedBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission, BroadcastReceiver resultReceiver,
			Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
		delegate.sendOrderedBroadcastAsUser(intent, user, receiverPermission, resultReceiver, scheduler, initialCode, initialData, initialExtras);
	}

	@Override
	public void sendStickyBroadcast(Intent intent) {
		delegate.sendStickyBroadcast(intent);
	}

	@Override
	public void sendStickyBroadcastAsUser(Intent intent, UserHandle user) {
		delegate.sendStickyBroadcastAsUser(intent, user);
	}

	@Override
	public void sendStickyOrderedBroadcast(Intent intent, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData,
			Bundle initialExtras) {
		delegate.sendStickyOrderedBroadcast(intent, resultReceiver, scheduler, initialCode, initialData, initialExtras);
	}

	@Override
	public void sendStickyOrderedBroadcastAsUser(Intent intent, UserHandle user, BroadcastReceiver resultReceiver, Handler scheduler,
			int initialCode, String initialData, Bundle initialExtras) {
		delegate.sendStickyOrderedBroadcastAsUser(intent, user, resultReceiver, scheduler, initialCode, initialData, initialExtras);
	}

	@Override
	public void setTheme(int resid) {
		delegate.setTheme(resid);
	}

	@Override
	public void setWallpaper(Bitmap bitmap) throws IOException {
		delegate.setWallpaper(bitmap);
	}

	@Override
	public void setWallpaper(InputStream data) throws IOException {
		delegate.setWallpaper(data);
	}

	@Override
	public void startActivities(Intent[] intents, Bundle options) {
		delegate.startActivities(intents, options);
	}

	@Override
	public void startActivities(Intent[] intents) {
		delegate.startActivities(intents);
	}

	@Override
	public void startActivity(Intent intent, Bundle options) {
		delegate.startActivity(intent, options);
	}

	@Override
	public void startActivity(Intent intent) {
		delegate.startActivity(intent);
	}

	@Override
	public boolean startInstrumentation(ComponentName className, String profileFile, Bundle arguments) {
		return delegate.startInstrumentation(className, profileFile, arguments);
	}

	@Override
	public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options)
			throws SendIntentException {
		delegate.startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags, options);
	}

	@Override
	public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags)
			throws SendIntentException {
		delegate.startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags);
	}

	@Override
	public void unregisterReceiver(BroadcastReceiver receiver) {
		delegate.unregisterReceiver(receiver);
	}

	@Override
	public ComponentName startService(Intent service) {
		return delegate.startService(service);
	}

	@Override
	public boolean stopService(Intent service) {
		return delegate.stopService(service);
	}

	@Override
	public void unbindService(ServiceConnection conn) {
		delegate.unbindService(conn);
	}

	@Override
	public void registerComponentCallbacks(ComponentCallbacks callback) {
		delegate.registerComponentCallbacks(callback);
	}

	@Override
	public void unregisterComponentCallbacks(ComponentCallbacks callback) {
		delegate.unregisterComponentCallbacks(callback);
	}

	@Override
	public boolean isRestricted() {
		return delegate.isRestricted();
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	@Override
	public String toString() {
		return delegate.toString();
	}

}