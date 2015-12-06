package com.cantalou.skin;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.cantalou.skin.holder.AttrHolder;
import com.cantalou.skin.instrumentation.SkinInstrumentation;
import com.cantalou.skin.resources.NightResources;
import com.cantalou.skin.resources.ProxyResources;
import com.cantalou.skin.resources.SkinResources;
import com.cantalou.android.util.Log;
import com.cantalou.android.util.PrefUtil;
import com.cantalou.android.util.ReflectUtil;
import com.cantalou.android.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.cantalou.android.util.ReflectUtil.*;

/**
 * 
 * @author LinZhiWei
 * @date 2015年10月31日 下午3:49:46
 */
public class SkinManager {

	/**
	 * 存放资源包目录
	 */
	private static final String SKIN_DIR = "/skin";

	public static final String PREF_KEY_SKIN_NAME = "skinName";

	/**
	 * 默认皮肤
	 */
	public static final String DEFAULT_SKIN_NAME = "defaultSkinName";

	/**
	 * 夜间模式皮肤资源名称, 夜间模式属于内置资源包
	 */
	public static final String DEFAULT_SKIN_NAME_NIGHT = "defaultSkinNameNight";

	/**
	 * activity
	 */
	ArrayList<Activity> activitys = new ArrayList<Activity>();

	/**
	 * 以载入的资源
	 */
	private HashMap<String, WeakReference<Resources>> cacheResources = new HashMap<String, WeakReference<Resources>>();

	/**
	 * 当前是否正在切换资源
	 */
	volatile boolean changingResource = false;

	/**
	 * 默认资源
	 */
	private Resources defaultResources;

	/**
	 * 资源名称
	 */
	String currentSkinName;

	/**
	 * 资源切换时提交View刷新任务到UI线程
	 */
	private Handler handler = new Handler(Looper.myLooper());

	/**
	 * 自定义view工厂
	 */
	private Factory viewFactory = new ViewFactory();

	private static class InstanceHolder {
		static final SkinManager INSTANCE = new SkinManager();
	}

	private SkinManager() {
	}

	public static SkinManager getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public void applicationOnCreate(Context cxt) {

		if (Looper.getMainLooper() != Looper.myLooper()) {
			throw new RuntimeException("applicationOnCreate method can only be called in the main thread");
		}

		Class<?> activityThreadClass = forName("android.app.ActivityThread");
		if (activityThreadClass == null) {
			Log.w("Fail to load class android.app.ActivityThread. Try invoking onCreate in Activity.onCreate method before invoking super.onCreate");
			return;
		}

		Object activityThread = invoke(activityThreadClass, "currentActivityThread");
		if (activityThread == null) {
			Log.w("Fail to get ActivityThread instance. Try invoking onCreate in Activity.onCreate method before invoking super.onCreate");
			return;
		}

		Instrumentation instrumentation = invoke(activityThreadClass, "getInstrumentation");
		if (instrumentation == null) {
			Log.w("Can not load class android.app.ActivityThread. Try invoking onCreate in Activity.onCreate method before invoking super.onCreate");
			return;
		}

		SkinInstrumentation skinInstrumentation = new SkinInstrumentation(this, instrumentation);
		if (!set(activityThread, "mInstrumentation", instrumentation)) {
			Log.w("Fail to replace field named mInstrumentation . Try invoking onCreate in Activity.onCreate method before invoking super.onCreate");
		}

	}

	/**
	 * 获取存储皮肤文件目录
	 *
	 * @param cxt
	 * @return 文件目录
	 */
	public File getSkinDir(Context cxt) {
		File dir = new File(cxt.getFilesDir() + File.separator + SKIN_DIR);
		dir.mkdirs();
		return dir;
	}

	/**
	 * 创建皮肤资源
	 *
	 * @param cxt
	 * @param skinName
	 *            皮肤资源文件名
	 * @return 皮肤资源对象
	 * @throws FileNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private Resources createSkinResource(Context cxt, String skinName) throws FileNotFoundException, InstantiationException,
			IllegalAccessException {
		if (DEFAULT_SKIN_NAME_NIGHT.equals(skinName) || DEFAULT_SKIN_NAME.equals(skinName)) {
			return defaultResources;
		}

		Resources skinResources = null;

		File skinFile = new File(getSkinDir(cxt), skinName);
		if (!skinFile.exists()) {
			throw new FileNotFoundException(skinFile + " not found");
		}

		AssetManager am = AssetManager.class.newInstance();
		invoke(am, "addAssetPath", new Class<?>[] { String.class }, skinFile.getAbsolutePath());
		skinResources = new SkinResources(am, defaultResources, skinName);

		return skinResources;
	}

	/**
	 * 创建代理资源
	 *
	 * @param cxt
	 * @param skinName
	 *            资源名称
	 * @return 代理Resources
	 * @throws FileNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private Resources createProxyResource(Context cxt, String skinName) throws FileNotFoundException, InstantiationException,
			IllegalAccessException {
		Resources res = null;
		WeakReference<Resources> resRef = cacheResources.get(skinName);
		if (resRef != null) {
			res = resRef.get();
			if (res != null) {
				return res;
			}
		}
		if (DEFAULT_SKIN_NAME_NIGHT.equals(skinName)) {
			res = new NightResources(cxt.getPackageName(), createSkinResource(cxt, skinName), defaultResources, skinName);
		} else {
			res = new ProxyResources(cxt.getPackageName(), createSkinResource(cxt, skinName), defaultResources, skinName);
		}
		synchronized (this) {
			cacheResources.put(skinName, new WeakReference<Resources>(res));
		}
		return res;
	}

	/**
	 * 将activity界面的资源替换成指定资源, activity界面
	 *
	 * @param activity
	 *            触发切换资源的Activity
	 * @param toRes
	 *            新资源
	 */
	private void realChangeResources(Activity activity, Resources toRes) {
		// ContextThemeWrapper add mResources field in JELLY_BEAN
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			set(activity, "mResources", toRes);
		} else {
			set(activity.getBaseContext(), "mResources", toRes);
		}
		set(activity, "mTheme", null);
	}

	/**
	 * 注册自定义的ViewFactory
	 *
	 * @param cxt
	 */
	private void registerViewFactory(Context cxt) {
		LayoutInflater li = LayoutInflater.from(cxt);
		if (li.getFactory() != null && li.getFactory() != viewFactory) {
			Log.w("LayoutInflater has setted a customed factory");
		} else {
			li.setFactory(viewFactory);
		}
	}

	/**
	 * 注册自定义的ViewFactory
	 *
	 * @param cxt
	 */
	private void unregisterViewFactory(Context cxt) {
		LayoutInflater li = LayoutInflater.from(cxt);
		if (li.getFactory() == viewFactory) {
			li.setFactory(null);
		}
	}

	/**
	 * 更换所有activity的皮肤资源
	 * 
	 * @param activity
	 * @param skinName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean changeResources(Activity activity, final String skinName) {
		if (StringUtils.isBlank(skinName)) {
			throw new IllegalArgumentException("illegal argument ,skinName could not be null");
		}

		// 在改变activity资源前截图用于渐变动画显示
		skinChangeAnimation(activity);

		final Context cxt = activity.getApplicationContext();
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					Resources res = createProxyResource(cxt, skinName);
					List<Activity> temp = (List<Activity>) activitys.clone();
					for (int i = temp.size() - 1; i >= 0; i--) {
						change(temp.get(i), res);
					}
					return true;
				} catch (Exception e) {
					Log.e(e);
					return false;
				}
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					currentSkinName = skinName;
					PrefUtil.setString(cxt, PREF_KEY_SKIN_NAME, skinName);
				}
			}
		}.execute();

		return true;
	}

	/**
	 * 更换所有activity的皮肤资源, 调用OnResourcesChangeListener回调进行自定义资源的更新
	 * 
	 * @param a
	 * @param res
	 */
	void change(final Activity a, Resources res) {

		realChangeResources(a, res);

		if (a instanceof OnResourcesChangeListener) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					((OnResourcesChangeListener) a).onResourcesChange();
				}
			});
		}

		List<?> fragments = ReflectUtil.get(ReflectUtil.get(a, "getSupportFragmentManager"), "mAdded");
		if (fragments != null && fragments.size() > 0) {
			for (Object f : fragments) {
				if (f instanceof OnResourcesChangeListener) {
					final OnResourcesChangeListener listener = (OnResourcesChangeListener) f;
					handler.post(new Runnable() {
						@Override
						public void run() {
							listener.onResourcesChange();
						}
					});
				}
			}
		}

		final Window w = a.getWindow();
		if (w != null) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					onResourcesChange(w.getDecorView());
				}
			});
		}
	}

	private void onResourcesChange(final View v) {
		if (v == null) {
			return;
		}

		if (v instanceof OnResourcesChangeListener) {
			((OnResourcesChangeListener) v).onResourcesChange();
			v.invalidate();
		}

		if (v instanceof AbsListView) {
			Object recycler = ReflectUtil.get(v, "mRecycler");
			ReflectUtil.invoke(recycler, "scrapActiveViews", new Class[0], new Object[0]);
			ReflectUtil.invoke(recycler, "clear", new Class[0], new Object[0]);
			Adapter adapter = ((AbsListView) v).getAdapter();
			if (adapter instanceof BaseAdapter) {
				((BaseAdapter) adapter).notifyDataSetChanged();
			}
		}

		Object tag = v.getTag(AttrHolder.ATTR_HOLDER_KEY);
		if (tag != null && tag instanceof AttrHolder) {
			((AttrHolder) tag).reload(v, v.getContext().getResources());
		}

		if (v instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) v;
			int size = vg.getChildCount();
			for (int i = 0; i < size; i++) {
				onResourcesChange(vg.getChildAt(i));
			}
		}
	}

	/**
	 * 更换皮肤
	 *
	 * @param activity
	 *            要更新皮肤的界面
	 */
	public void onCreate(Activity activity) {

		if (defaultResources == null) {
			defaultResources = activity.getResources();
		}

		if (StringUtils.isBlank(currentSkinName) || DEFAULT_SKIN_NAME.equals(currentSkinName)) {
			registerViewFactory(activity);
		} else {
			unregisterViewFactory(activity);
		}

		activitys.add(activity);

		if (currentSkinName == null) {
			currentSkinName = PrefUtil.getString(activity, PREF_KEY_SKIN_NAME);
		}

		if (StringUtils.isBlank(currentSkinName) || DEFAULT_SKIN_NAME.equals(currentSkinName)) {
			return;
		}

		try {
			realChangeResources(activity, createProxyResource(activity, currentSkinName));
		} catch (Exception e) {
			Log.e(e);
		}
	}

	/**
	 * 给界面添加更换皮肤转换的动画
	 *
	 * @param activity
	 *            要显示渐变动画的界面
	 */
	private void skinChangeAnimation(Activity activity) {
		try {
			final ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
			if (decor != null) {
				decor.setDrawingCacheEnabled(true);
				Bitmap temp = Bitmap.createBitmap(decor.getDrawingCache());
				decor.setDrawingCacheEnabled(false);
				ImageView iv = new ImageView(activity);
				iv.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// consume all event
					}
				});
				iv.setImageBitmap(temp);
				showAnimation(decor, iv, 800);
			}
		} catch (Exception e) {
			// Log.w(TAG, e);
		}
	}

	/**
	 * 在指定的DecorView上面显示一层遮罩,然后渐变消失
	 *
	 * @param decor
	 *            root View
	 * @param coverLayerView
	 *            覆盖在上面显示过度动画的View
	 * @param duration
	 *            动画时间,单位毫秒
	 */
	private void showAnimation(final ViewGroup decor, final View coverLayerView, final long duration) {
		ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		decor.addView(coverLayerView, lp);

		final AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
		alphaAnimation.setDuration(duration);
		coverLayerView.setAnimation(alphaAnimation);
		alphaAnimation.startNow();
		decor.postDelayed(new Runnable() {
			@Override
			public void run() {
				alphaAnimation.cancel();
				decor.removeView(coverLayerView);
				changingResource = false;
			}
		}, duration + 1);
		changingResource = true;
	}

	public synchronized void onDestroy(Activity activity) {
		activitys.remove(activity);
	}

	/**
	 * 当前是否正在显示切换皮肤动画
	 *
	 * @return 是 true
	 */
	public boolean isChangingResource() {
		return changingResource;
	}
}
