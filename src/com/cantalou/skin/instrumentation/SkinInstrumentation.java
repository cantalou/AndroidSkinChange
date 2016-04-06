package com.cantalou.skin.instrumentation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.Instrumentation;
import android.app.UiAutomation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.cantalou.android.util.ReflectUtil;
import com.cantalou.skin.SkinManager;

/**
 * 重写Instrumentation的callActivityOnCreate,callActivityOnCreate,
 * callActivityOnDestroy方法, 在调用Activity的生命周期方法前增加换肤功能回调
 *
 * @author cantalou
 * @date 2015年12月5日 下午4:53:44
 */
@SuppressWarnings("deprecation")
public class SkinInstrumentation extends Instrumentation {

    private SkinManager skinManager;

    private Instrumentation targetInstrucmentation;

    public SkinInstrumentation(SkinManager skinManager, Instrumentation target) {
	this.skinManager = skinManager;
	this.targetInstrucmentation = target;
    }


    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
	return targetInstrucmentation.newActivity(cl, className, intent);
    }
    
    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
	skinManager.callActivityOnCreate(activity);
	targetInstrucmentation.callActivityOnCreate(activity, icicle, persistentState);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
	skinManager.callActivityOnCreate(activity);
	targetInstrucmentation.callActivityOnCreate(activity, icicle);
    }

    @Override
    public void callActivityOnDestroy(Activity activity) {
	skinManager.onDestroy(activity);
	targetInstrucmentation.callActivityOnDestroy(activity);
    }

    @Override
    public void endPerformanceSnapshot() {
	targetInstrucmentation.endPerformanceSnapshot();
    }

    @Override
    public Context getContext() {
	return targetInstrucmentation.getContext();
    }

    @Override
    public ComponentName getComponentName() {
	return targetInstrucmentation.getComponentName();
    }

    @Override
    public Context getTargetContext() {
	return targetInstrucmentation.getTargetContext();
    }

    @Override
    public boolean isProfiling() {
	return targetInstrucmentation.isProfiling();
    }

    @Override
    public void addMonitor(ActivityMonitor monitor) {
	targetInstrucmentation.addMonitor(monitor);
    }

    @Override
    public ActivityMonitor addMonitor(IntentFilter filter, ActivityResult result, boolean block) {
	return targetInstrucmentation.addMonitor(filter, result, block);
    }

    @Override
    public ActivityMonitor addMonitor(String cls, ActivityResult result, boolean block) {
	return targetInstrucmentation.addMonitor(cls, result, block);
    }

    @Override
    public void callActivityOnNewIntent(Activity activity, Intent intent) {
	targetInstrucmentation.callActivityOnNewIntent(activity, intent);
    }

    @Override
    public void callActivityOnPause(Activity activity) {
	targetInstrucmentation.callActivityOnPause(activity);
    }

    @Override
    public void callActivityOnPostCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
	targetInstrucmentation.callActivityOnPostCreate(activity, icicle, persistentState);
    }

    @Override
    public void callActivityOnPostCreate(Activity activity, Bundle icicle) {
	targetInstrucmentation.callActivityOnPostCreate(activity, icicle);
    }

    @Override
    public void callActivityOnRestart(Activity activity) {
	targetInstrucmentation.callActivityOnRestart(activity);
    }

    @Override
    public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState, PersistableBundle persistentState) {
	targetInstrucmentation.callActivityOnRestoreInstanceState(activity, savedInstanceState, persistentState);
    }

    @Override
    public boolean checkMonitorHit(ActivityMonitor monitor, int minHits) {
	return targetInstrucmentation.checkMonitorHit(monitor, minHits);
    }

    @Override
    public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState) {
	targetInstrucmentation.callActivityOnRestoreInstanceState(activity, savedInstanceState);
    }

    @Override
    public void callActivityOnStart(Activity activity) {
	targetInstrucmentation.callActivityOnStart(activity);
    }

    @Override
    public void callActivityOnResume(Activity activity) {
	targetInstrucmentation.callActivityOnResume(activity);
    }

    @Override
    public void callActivityOnSaveInstanceState(Activity activity, Bundle outState, PersistableBundle outPersistentState) {
	targetInstrucmentation.callActivityOnSaveInstanceState(activity, outState, outPersistentState);
    }

    @Override
    public void finish(int resultCode, Bundle results) {
	targetInstrucmentation.finish(resultCode, results);
    }

    @Override
    public boolean invokeMenuActionSync(Activity targetActivity, int id, int flag) {
	return targetInstrucmentation.invokeMenuActionSync(targetActivity, id, flag);
    }

    @Override
    public boolean invokeContextMenuAction(Activity targetActivity, int id, int flag) {
	return targetInstrucmentation.invokeContextMenuAction(targetActivity, id, flag);
    }

    @Override
    public void callApplicationOnCreate(Application app) {
	targetInstrucmentation.callApplicationOnCreate(app);
    }

    @Override
    public void callActivityOnStop(Activity activity) {
	targetInstrucmentation.callActivityOnStop(activity);
    }

    @Override
    public void callActivityOnSaveInstanceState(Activity activity, Bundle outState) {
	targetInstrucmentation.callActivityOnSaveInstanceState(activity, outState);
    }

    @Override
    public void callActivityOnUserLeaving(Activity activity) {
	targetInstrucmentation.callActivityOnUserLeaving(activity);
    }

    @Override
    public Bundle getAllocCounts() {
	return targetInstrucmentation.getAllocCounts();
    }

    @Override
    public Bundle getBinderCounts() {
	return targetInstrucmentation.getBinderCounts();
    }

    @Override
    public UiAutomation getUiAutomation() {
	return targetInstrucmentation.getUiAutomation();
    }

    @Override
    public void onCreate(Bundle arguments) {
	targetInstrucmentation.onCreate(arguments);
    }

    @Override
    public void start() {
	targetInstrucmentation.start();
    }

    @Override
    public void onStart() {
	targetInstrucmentation.onStart();
    }

    @Override
    public boolean onException(Object obj, Throwable e) {
	return targetInstrucmentation.onException(obj, e);
    }

    @Override
    public void sendStatus(int resultCode, Bundle results) {
	targetInstrucmentation.sendStatus(resultCode, results);
    }

    @Override
    public void setAutomaticPerformanceSnapshots() {
	targetInstrucmentation.setAutomaticPerformanceSnapshots();
    }

    @Override
    public void startPerformanceSnapshot() {

	targetInstrucmentation.startPerformanceSnapshot();
    }

    @Override
    public void onDestroy() {

	targetInstrucmentation.onDestroy();
    }

    @Override
    public void startProfiling() {

	targetInstrucmentation.startProfiling();
    }

    @Override
    public void stopProfiling() {

	targetInstrucmentation.stopProfiling();
    }

    @Override
    public void setInTouchMode(boolean inTouch) {

	targetInstrucmentation.setInTouchMode(inTouch);
    }

    @Override
    public void waitForIdle(Runnable recipient) {

	targetInstrucmentation.waitForIdle(recipient);
    }

    @Override
    public void waitForIdleSync() {

	targetInstrucmentation.waitForIdleSync();
    }

    @Override
    public void runOnMainSync(Runnable runner) {

	targetInstrucmentation.runOnMainSync(runner);
    }

    @Override
    public Activity startActivitySync(Intent intent) {

	return targetInstrucmentation.startActivitySync(intent);
    }

    @Override
    public Activity waitForMonitor(ActivityMonitor monitor) {

	return targetInstrucmentation.waitForMonitor(monitor);
    }

    @Override
    public Activity waitForMonitorWithTimeout(ActivityMonitor monitor, long timeOut) {
	return targetInstrucmentation.waitForMonitorWithTimeout(monitor, timeOut);
    }

    @Override
    public void removeMonitor(ActivityMonitor monitor) {
	targetInstrucmentation.removeMonitor(monitor);
    }

    @Override
    public void sendStringSync(String text) {
	targetInstrucmentation.sendStringSync(text);
    }

    @Override
    public void sendKeySync(KeyEvent event) {
	targetInstrucmentation.sendKeySync(event);
    }

    @Override
    public void sendKeyDownUpSync(int key) {
	targetInstrucmentation.sendKeyDownUpSync(key);
    }

    @Override
    public void sendCharacterSync(int keyCode) {
	targetInstrucmentation.sendCharacterSync(keyCode);
    }

    @Override
    public void sendPointerSync(MotionEvent event) {
	targetInstrucmentation.sendPointerSync(event);
    }

    @Override
    public void sendTrackballEventSync(MotionEvent event) {
	targetInstrucmentation.sendTrackballEventSync(event);
    }

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
	return targetInstrucmentation.newApplication(cl, className, context);
    }

    @Override
    public Activity newActivity(Class<?> clazz, Context context, IBinder token, Application application, Intent intent, ActivityInfo info, CharSequence title, Activity parent,
	    String id, Object lastNonConfigurationInstance) throws InstantiationException, IllegalAccessException {
	return targetInstrucmentation.newActivity(clazz, context, token, application, intent, info, title, parent, id, lastNonConfigurationInstance);
    }

    @Override
    public void startAllocCounting() {
	targetInstrucmentation.startAllocCounting();
    }

    @Override
    public void stopAllocCounting() {
	targetInstrucmentation.stopAllocCounting();
    }

    public void execStartActivities(Context who, IBinder contextThread, IBinder token, Activity target, Intent[] intents) {
	ReflectUtil.invoke(targetInstrucmentation, "execStartActivities", new Class<?>[] { Context.class, IBinder.class, IBinder.class, Activity.class, intents.getClass() }, who,
		contextThread, token, target, intents);
    }

    @SuppressLint("NewApi")
    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Fragment target, Intent intent, int requestCode) {
	return (ActivityResult) ReflectUtil.invoke(targetInstrucmentation, "execStartActivity", new Class<?>[] { Context.class, IBinder.class, IBinder.class, Activity.class,
		Intent.class, int.class }, who, contextThread, token, target, intent, requestCode);
    }
}
