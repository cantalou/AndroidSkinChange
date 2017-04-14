package com.cantalou.skin;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.cantalou.android.util.ReflectUtil;

/**
 * 重写Instrumentation的callActivityOnCreate, callActivityOnDestroy方法,
 * 在调用Activity的生命周期方法onCreate前调用ResourcesManager.callActivityOnCreate()方法
 *
 * @author cantalou
 * @date 2015年12月5日 下午4:53:44
 */
@SuppressWarnings("deprecation")
public class SkinInstrumentation extends Instrumentation {

    private SkinManager skinManager;

    private Instrumentation targetInstrumentation;

    public SkinInstrumentation(SkinManager skinManager, Instrumentation target) {
        this.skinManager = skinManager;
        this.targetInstrumentation = target;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void callActivityOnCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
        skinManager.beforeActivityOnCreate(activity, icicle);
        targetInstrumentation.callActivityOnCreate(activity, icicle, persistentState);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        skinManager.beforeActivityOnCreate(activity, icicle);
        targetInstrumentation.callActivityOnCreate(activity, icicle);
    }

    @Override
    public void callActivityOnDestroy(Activity activity) {
        targetInstrumentation.callActivityOnDestroy(activity);
        skinManager.onActivityDestroyed(activity);
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return targetInstrumentation.newActivity(cl, className, intent);
    }

    @Override
    public void endPerformanceSnapshot() {
        targetInstrumentation.endPerformanceSnapshot();
    }

    @Override
    public Context getContext() {
        return targetInstrumentation.getContext();
    }

    @Override
    public ComponentName getComponentName() {
        return targetInstrumentation.getComponentName();
    }

    @Override
    public Context getTargetContext() {
        return targetInstrumentation.getTargetContext();
    }

    @Override
    public boolean isProfiling() {
        return targetInstrumentation.isProfiling();
    }

    @Override
    public void addMonitor(ActivityMonitor monitor) {
        targetInstrumentation.addMonitor(monitor);
    }

    @Override
    public ActivityMonitor addMonitor(IntentFilter filter, ActivityResult result, boolean block) {
        return targetInstrumentation.addMonitor(filter, result, block);
    }

    @Override
    public ActivityMonitor addMonitor(String cls, ActivityResult result, boolean block) {
        return targetInstrumentation.addMonitor(cls, result, block);
    }

    @Override
    public void callActivityOnNewIntent(Activity activity, Intent intent) {
        targetInstrumentation.callActivityOnNewIntent(activity, intent);
    }

    @Override
    public void callActivityOnPause(Activity activity) {
        targetInstrumentation.callActivityOnPause(activity);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void callActivityOnPostCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
        targetInstrumentation.callActivityOnPostCreate(activity, icicle, persistentState);
    }

    @Override
    public void callActivityOnPostCreate(Activity activity, Bundle icicle) {
        targetInstrumentation.callActivityOnPostCreate(activity, icicle);
    }

    @Override
    public void callActivityOnRestart(Activity activity) {
        targetInstrumentation.callActivityOnRestart(activity);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState, PersistableBundle persistentState) {
        targetInstrumentation.callActivityOnRestoreInstanceState(activity, savedInstanceState, persistentState);
    }

    @Override
    public boolean checkMonitorHit(ActivityMonitor monitor, int minHits) {
        return targetInstrumentation.checkMonitorHit(monitor, minHits);
    }

    @Override
    public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState) {
        targetInstrumentation.callActivityOnRestoreInstanceState(activity, savedInstanceState);
    }

    @Override
    public void callActivityOnStart(Activity activity) {
        targetInstrumentation.callActivityOnStart(activity);
    }

    @Override
    public void callActivityOnResume(Activity activity) {
        targetInstrumentation.callActivityOnResume(activity);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void callActivityOnSaveInstanceState(Activity activity, Bundle outState, PersistableBundle outPersistentState) {
        targetInstrumentation.callActivityOnSaveInstanceState(activity, outState, outPersistentState);
    }

    @Override
    public void finish(int resultCode, Bundle results) {
        targetInstrumentation.finish(resultCode, results);
    }

    @Override
    public boolean invokeMenuActionSync(Activity targetActivity, int id, int flag) {
        return targetInstrumentation.invokeMenuActionSync(targetActivity, id, flag);
    }

    @Override
    public boolean invokeContextMenuAction(Activity targetActivity, int id, int flag) {
        return targetInstrumentation.invokeContextMenuAction(targetActivity, id, flag);
    }

    @Override
    public void callApplicationOnCreate(Application app) {
        targetInstrumentation.callApplicationOnCreate(app);
    }

    @Override
    public void callActivityOnStop(Activity activity) {
        targetInstrumentation.callActivityOnStop(activity);
    }

    @Override
    public void callActivityOnSaveInstanceState(Activity activity, Bundle outState) {
        targetInstrumentation.callActivityOnSaveInstanceState(activity, outState);
    }

    @Override
    public void callActivityOnUserLeaving(Activity activity) {
        targetInstrumentation.callActivityOnUserLeaving(activity);
    }

    @Override
    public Bundle getAllocCounts() {
        return targetInstrumentation.getAllocCounts();
    }

    @Override
    public Bundle getBinderCounts() {
        return targetInstrumentation.getBinderCounts();
    }

    @Override
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public UiAutomation getUiAutomation() {
        return targetInstrumentation.getUiAutomation();
    }

    @Override
    public void onCreate(Bundle arguments) {
        targetInstrumentation.onCreate(arguments);
    }

    @Override
    public void start() {
        targetInstrumentation.start();
    }

    @Override
    public void onStart() {
        targetInstrumentation.onStart();
    }

    @Override
    public boolean onException(Object obj, Throwable e) {
        return targetInstrumentation.onException(obj, e);
    }

    @Override
    public void sendStatus(int resultCode, Bundle results) {
        targetInstrumentation.sendStatus(resultCode, results);
    }

    @Override
    public void setAutomaticPerformanceSnapshots() {
        targetInstrumentation.setAutomaticPerformanceSnapshots();
    }

    @Override
    public void startPerformanceSnapshot() {
        targetInstrumentation.startPerformanceSnapshot();
    }

    @Override
    public void onDestroy() {

        targetInstrumentation.onDestroy();
    }

    @Override
    public void startProfiling() {
        targetInstrumentation.startProfiling();
    }

    @Override
    public void stopProfiling() {
        targetInstrumentation.stopProfiling();
    }

    @Override
    public void setInTouchMode(boolean inTouch) {

        targetInstrumentation.setInTouchMode(inTouch);
    }

    @Override
    public void waitForIdle(Runnable recipient) {
        targetInstrumentation.waitForIdle(recipient);
    }

    @Override
    public void waitForIdleSync() {
        targetInstrumentation.waitForIdleSync();
    }

    @Override
    public void runOnMainSync(Runnable runner) {
        targetInstrumentation.runOnMainSync(runner);
    }

    @Override
    public Activity startActivitySync(Intent intent) {
        return targetInstrumentation.startActivitySync(intent);
    }

    @Override
    public Activity waitForMonitor(ActivityMonitor monitor) {
        return targetInstrumentation.waitForMonitor(monitor);
    }

    @Override
    public Activity waitForMonitorWithTimeout(ActivityMonitor monitor, long timeOut) {
        return targetInstrumentation.waitForMonitorWithTimeout(monitor, timeOut);
    }

    @Override
    public void removeMonitor(ActivityMonitor monitor) {
        targetInstrumentation.removeMonitor(monitor);
    }

    @Override
    public void sendStringSync(String text) {
        targetInstrumentation.sendStringSync(text);
    }

    @Override
    public void sendKeySync(KeyEvent event) {
        targetInstrumentation.sendKeySync(event);
    }

    @Override
    public void sendKeyDownUpSync(int key) {
        targetInstrumentation.sendKeyDownUpSync(key);
    }

    @Override
    public void sendCharacterSync(int keyCode) {
        targetInstrumentation.sendCharacterSync(keyCode);
    }

    @Override
    public void sendPointerSync(MotionEvent event) {
        targetInstrumentation.sendPointerSync(event);
    }

    @Override
    public void sendTrackballEventSync(MotionEvent event) {
        targetInstrumentation.sendTrackballEventSync(event);
    }

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return targetInstrumentation.newApplication(cl, className, context);
    }

    @Override
    public Activity newActivity(Class<?> clazz, Context context, IBinder token, Application application, Intent intent, ActivityInfo info, CharSequence title, Activity parent,
                                String id, Object lastNonConfigurationInstance) throws InstantiationException, IllegalAccessException {
        return targetInstrumentation.newActivity(clazz, context, token, application, intent, info, title, parent, id, lastNonConfigurationInstance);
    }

    @Override
    public void startAllocCounting() {
        targetInstrumentation.startAllocCounting();
    }

    @Override
    public void stopAllocCounting() {
        targetInstrumentation.stopAllocCounting();
    }

    public void execStartActivities(Context who, IBinder contextThread, IBinder token, Activity target, Intent[] intents) {
        ReflectUtil.invoke(targetInstrumentation, "execStartActivities", new Class<?>[]{Context.class, IBinder.class, IBinder.class, Activity.class, intents.getClass()}, who,
                contextThread, token, target, intents);
    }

    @SuppressLint("NewApi")
    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Fragment target, Intent intent, int requestCode) {
        return (ActivityResult) ReflectUtil.invoke(targetInstrumentation, "execStartActivity", new Class<?>[]{Context.class, IBinder.class, IBinder.class, Activity.class,
                Intent.class, int.class}, who, contextThread, token, target, intent, requestCode);
    }
}
