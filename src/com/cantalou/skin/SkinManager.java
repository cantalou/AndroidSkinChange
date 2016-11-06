package com.cantalou.skin;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.*;
import android.util.AndroidRuntimeException;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.cantalou.android.manager.lifecycle.ActivityLifecycleCallbacksAdapter;
import com.cantalou.android.manager.lifecycle.ActivityLifecycleManager;
import com.cantalou.android.util.Log;
import com.cantalou.android.util.PrefUtil;
import com.cantalou.android.util.StringUtils;
import com.cantalou.skin.content.SkinContextWrapper;
import com.cantalou.skin.content.res.ProxyResources;
import com.cantalou.skin.content.res.ResourcesManager;
import com.cantalou.skin.handler.AbstractHandler;
import com.cantalou.skin.handler.ViewHandler;
import com.cantalou.skin.layout.factory.ViewFactory;
import com.cantalou.skin.layout.factory.ViewFactoryAfterGingerbread;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import static com.cantalou.android.util.ReflectUtil.forName;
import static com.cantalou.android.util.ReflectUtil.get;
import static com.cantalou.android.util.ReflectUtil.invoke;
import static com.cantalou.android.util.ReflectUtil.set;

/**
 * 皮肤资源Manager
 *
 * @author cantalou
 * @date 2015年10月31日 下午3:49:46
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class SkinManager extends ActivityLifecycleCallbacksAdapter {

    /**
     * 当前皮肤存储key
     */
    public static final String PREF_KEY_CURRENT_SKIN = "com.cantalou.skin.PREF_KEY_CURRENT_SKIN";

    /**
     * activity
     */
    ArrayList<Activity> activities = new ArrayList<Activity>();

    /**
     * 当前是否正在切换资源
     */
    volatile boolean changingResource = false;

    /**
     * 默认资源
     */
    private ProxyResources defaultResources;

    /**
     * 资源名称
     */
    String currentSkinPath = ResourcesManager.DEFAULT_RESOURCES;

    /**
     * 资源
     */
    private ProxyResources currentResources;

    /**
     * 资源切换时提交View刷新任务到UI线程
     */
    private Handler uiHandler = new Handler(Looper.getMainLooper());

    /**
     * 资源切换结束回调
     */
    private ArrayList<OnResourcesChangeFinishListener> onResourcesChangeFinishListeners = new ArrayList<OnResourcesChangeFinishListener>();

    /**
     * 资源缓存key和资源id管理对象
     */
    private CacheKeyAndIdManager cacheKeyAndIdManager;

    private ResourcesManager resourcesManager;

    private ActivityLifecycleManager activityLifecycleManager;

    ArrayDeque<Runnable> serialTasks = new ArrayDeque<Runnable>() {
        Runnable mActive;

        public synchronized boolean offer(final Runnable e) {
            boolean result = super.offer(new Runnable() {
                @Override
                public void run() {
                    try {
                        e.run();
                    } finally {
                        scheduleNext();
                    }
                }
            });
            if (mActive == null) {
                scheduleNext();
            }
            return result;
        }

        public synchronized void scheduleNext() {
            mActive = serialTasks.poll();
            if (mActive != null) {
                uiHandler.post(mActive);
            }
        }
    };

    private SkinManager() {

        cacheKeyAndIdManager = new CacheKeyAndIdManager();
        cacheKeyAndIdManager.setSkinManager(this);

        resourcesManager = ResourcesManager.getInstance();

        activityLifecycleManager = ActivityLifecycleManager.getInstance();
        activityLifecycleManager.registerActivityLifecycleCallbacks(this);

        Log.LOG_TAG_FLAG = "-skin";
    }

    private static class InstanceHolder {
        static final SkinManager INSTANCE = new SkinManager();
    }

    public static com.cantalou.skin.SkinManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 将Activity或者Context的资源替换成toRes指定资源
     *
     * @param activity 触发切换资源的Activity
     * @param toRes    新资源
     */
    public void changeActivityResources(Activity activity, Resources toRes) {
        // ContextThemeWrapper add mResources field in JELLY_BEAN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Log.v("after JELLY_BEAN change Activity:{} to Resources :{} ,result:{} ", activity, toRes, set(activity, "mResources", toRes));
        } else {
            Log.v("before JELLY_BEAN change context:{} to Resources :{} ,result:{} ", activity.getBaseContext(), toRes, set(activity.getBaseContext(), "mResources", toRes));
        }
        Log.v("reset theme to null ", set(activity, "mTheme", null));
    }

    /**
     * 注册自定义的ViewFactory到LayoutInflater中,实现对View生成的拦截
     *
     * @param li
     */
    public void registerViewFactory(LayoutInflater li) {
        Factory factory = li.getFactory();
        if (factory instanceof ViewFactory) {
            Log.w("Had register factory");
            return;
        }

        if (factory != null && get(factory, "mF1") instanceof ViewFactory) {
            Log.w("Had register factory");
            return;
        }

        ViewFactory vf;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            vf = new ViewFactory();
        } else {
            vf = new ViewFactoryAfterGingerbread();
        }
        vf.register(li);
        Log.d("LayoutInflater:{} register custom factory:{}", li, vf);
    }

    /**
     * 更换资源
     *
     * @param activity
     * @param path     资源文件路径
     */
    @SuppressWarnings("unchecked")
    public void changeResources(Activity activity, final String path) {
        if (StringUtils.isBlank(path)) {
            throw new IllegalArgumentException("skinPath could not be empty");
        }

        if (defaultResources == null) {
            throw new IllegalStateException("defaultResources is not initialized. Call the method onAttach of SkinManage in Activity onAttach()");
        }

        final Context cxt = activity.getApplicationContext();
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                changingResource = true;
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                ProxyResources originalResources = currentResources;
                try {
                    Log.d("start change resource");
                    final ProxyResources res = resourcesManager.createProxyResource(path, defaultResources);
                    if (res == null) {
                        return false;
                    }
                    currentResources = res;
                    List<Activity> temp = (List<Activity>) activities.clone();
                    for (int i = temp.size() - 1; i >= 0; i--) {
                        Log.d("change :{} resources to :{}", temp.get(i), res);
                        change(temp.get(i), res);
                    }
                    Log.d("finish change resource");
                    currentSkinPath = path;
                    PrefUtil.setString(cxt, PREF_KEY_CURRENT_SKIN, path);
                    return true;
                } catch (Exception e) {
                    Log.e(e);
                    currentResources = originalResources;
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                Log.i("changeResources doInBackground return :{}, currentSkin:{}", result, currentSkinPath);
                ArrayList<OnResourcesChangeFinishListener> list = (ArrayList<OnResourcesChangeFinishListener>) onResourcesChangeFinishListeners.clone();
                for (OnResourcesChangeFinishListener listener : list) {
                    listener.onResourcesChangeFinish(result);
                }
                changingResource = false;
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

        // showSkinChangeAnimation(activity);
    }

    /**
     * 更换所有activity的皮肤资源, 调用OnResourcesChangeListener回调进行自定义资源的更新
     *
     * @param a   activity
     * @param res 资源
     */
    public void change(final Activity a, Resources res) {

        changeActivityResources(a, res);

        if (a instanceof Skinnable) {
            serialTasks.offer(new Runnable() {
                @Override
                public void run() {
                    // ((Skinnable) a).onResourcesChange();
                }
            });
        }

        final List<?> fragments = get(a, "mFragments.mAdded");
        if (fragments != null && fragments.size() > 0) {
            serialTasks.offer(new Runnable() {
                @Override
                public void run() {
                    for (Object f : fragments) {
                        if (f instanceof Skinnable) {
                            Skinnable listener = (Skinnable) f;
                            listener.onResourcesChange();
                        }
                    }
                }
            });
        }

        final Window w = a.getWindow();
        if (w != null) {
            serialTasks.offer(new Runnable() {
                @Override
                public void run() {
                    onResourcesChange(w.getDecorView());
                }
            });
        }

    }

    /**
     * 递归调用实现了OnResourcesChangeListener接口的View
     *
     * @param v
     */
    private void onResourcesChange(View v) {

        if (Looper.myLooper() == null || Looper.getMainLooper() != Looper.myLooper()) {
            throw new AndroidRuntimeException("Only the original thread that created a view hierarchy can touch its views.");
        }

        if (v == null) {
            return;
        }

        if (v instanceof Skinnable) {
            ((Skinnable) v).onResourcesChange();
            v.invalidate();
        }

        Object tag = v.getTag(ViewHandler.ATTR_HANDLER_KEY);
        if (tag != null && tag instanceof ViewHandler) {
            ((AbstractHandler) tag).reloadAttr(v, currentResources);
        } else {
            AbstractHandler ah = ViewFactory.getHandler(v.getClass().getName());
            if (ah != null) {
                ah.reloadAttr(v, currentResources);
            }
        }

        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            int size = vg.getChildCount();
            for (int i = 0; i < size; i++) {
                onResourcesChange(vg.getChildAt(i));
            }
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        activities.remove(activity);
    }

    @Override
    public void beforeActivityOnCreate(Activity activity, Bundle savedInstanceState) {

        if (defaultResources == null) {
            defaultResources = new ProxyResources(activity.getResources());
            Log.v("init defaultResources and registerViewFactory ");
        }

        activities.add(activity);

        Context baseContext = activity.getBaseContext();
        if (!(baseContext instanceof SkinContextWrapper)) {
            set(activity, "mBase", new SkinContextWrapper(baseContext));
            Log.v("replace Activity baseContext to :{} ", baseContext);
        }

        LayoutInflater li = activity.getLayoutInflater();
        registerViewFactory(li);

        String prefSkinPath = PrefUtil.getString(activity, PREF_KEY_CURRENT_SKIN);
        if (StringUtils.isNotBlank(prefSkinPath)) {
            currentSkinPath = prefSkinPath;
        }

        ProxyResources res = resourcesManager.createProxyResource(currentSkinPath, defaultResources);
        currentResources = res;
        try {
            changeActivityResources(activity, res);
        } catch (Exception e) {
            Log.e(e);
        }
    }

    /**
     * 对当前界面截图, 模糊渐变消失
     *
     * @param activity 要显示渐变动画的界面
     */
    private void showSkinChangeAnimation(Activity activity) {
        try {
            final ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
            if (decor == null) {
                return;
            }

            decor.setDrawingCacheEnabled(true);
            Bitmap temp = Bitmap.createBitmap(decor.getDrawingCache());
            decor.setDrawingCacheEnabled(false);
            final ImageView iv = new ImageView(activity);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // consume all event
                }
            });
            iv.setImageBitmap(temp);

            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            decor.addView(iv, lp);

            final AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
            alphaAnimation.setDuration(800);
            iv.setAnimation(alphaAnimation);
            alphaAnimation.startNow();
            decor.postDelayed(new Runnable() {
                @Override
                public void run() {
                    alphaAnimation.reset();
                    alphaAnimation.cancel();
                    iv.setAnimation(null);
                    iv.setVisibility(View.GONE);
                    decor.removeView(iv);
                }
            }, 800);

        } catch (Exception e) {
            Log.e(e);
        }
    }

    /**
     * 当前是否正在切换资源
     *
     * @return 是 true
     */
    public boolean isChangingResource() {
        return changingResource;
    }

    public String getCurrentSkin() {
        return currentSkinPath;
    }

    public synchronized void addOnResourcesChangeFinishListener(OnResourcesChangeFinishListener listener) {
        onResourcesChangeFinishListeners.add(listener);
    }

    public synchronized void removeOnResourcesChangeFinishListener(OnResourcesChangeFinishListener listener) {
        onResourcesChangeFinishListeners.remove(listener);
    }

    public ProxyResources getCurrentResources() {
        return currentResources;
    }

    public void setCurrentResources(ProxyResources currentResources) {
        this.currentResources = currentResources;
    }

    public Resources getDefaultResources() {
        return defaultResources;
    }

    public ArrayList<Activity> getActivities() {
        return activities;
    }

    public CacheKeyAndIdManager getCacheKeyAndIdManager() {
        return cacheKeyAndIdManager;
    }

}
