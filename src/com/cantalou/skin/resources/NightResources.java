package com.cantalou.skin.resources;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.DrawableContainer.DrawableContainerState;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RotateDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.util.TypedValue;

import com.cantalou.android.util.ReflectUtil;
import com.cantalou.skin.resources.ProxyResources;

public class NightResources extends ProxyResources
{

	/**
	 * 夜间模式资源名称前缀
	 */
	public static final String NIGHT_RESOURCE_NAME_SUF = "_night";

	/**
	 * 夜间模式资源id映射
	 */
	protected SparseIntArray nightIdMap = new SparseIntArray();

	/**
	 * 夜间模式资源不存在的id
	 */
	protected SparseIntArray notFoundedNightIds = new SparseIntArray();

	public NightResources(String packageName, Resources skinRes, Resources defRes, String skinName) {
		super(packageName, skinRes, defRes, skinName);
	}

	/**
	 * 将应用资源id转成夜间模式资源id
	 *
	 * @param id
	 * @return 夜间模式资源id
	 */
	public synchronized int toNightId(int id) {

		if (id == 0) {
			return 0;
		}

		if ((id & APP_ID_MASK) != APP_ID_MASK) {
			return id;
		}

		// 如果皮肤资源包不存在当前资源项,直接返回id
		if (notFoundedNightIds.get(id) > 0) {
			return id;
		}

		int nightId = nightIdMap.get(id);
		if (nightId != 0) {
			return nightId;
		}

		String name = getResourceName(id);
		if (TextUtils.isEmpty(name)) {
			return id;
		}
		int index = name.lastIndexOf('.');
		if (index != -1) {
			name = name.substring(0, index) + NIGHT_RESOURCE_NAME_SUF + name.substring(index);
		} else {
			name = name + NIGHT_RESOURCE_NAME_SUF;
		}
		nightId = getIdentifier(name, null, packageName);
		if (nightId == 0) {
			notFoundedNightIds.put(id, id);
			nightId = id;
		} else {
			nightIdMap.put(id, nightId);
		}
		return nightId;
	}

	@Override
	public void getValue(int id, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
		super.getValue(toNightId(id), outValue, resolveRefs);
	}

	@Override
	public void getValueForDensity(int id, int density, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
		super.getValueForDensity(toNightId(id), density, outValue, resolveRefs);
	}

	Drawable loadDrawable(TypedValue value, int id) throws NotFoundException {
		Drawable result = super.loadDrawable(value, toNightId(id));
		setColorFilter(result);
		return result;
	}

	/**
	 * 给图片添加灰层
	 * 
	 * @author LinZhiWei
	 * @date 2015年11月3日 下午4:08:56
	 * @param drawable
	 */
	private void setColorFilter(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			drawable.setColorFilter(Color.GRAY, android.graphics.PorterDuff.Mode.MULTIPLY);
		} else if (drawable instanceof DrawableContainer) {
			DrawableContainerState dcs = ReflectUtil.get(drawable, "mDrawableContainerState");
			if (dcs == null) {
				return;
			}
			for (Drawable d : dcs.getChildren()) {
				setColorFilter(d);
			}
		} else if (drawable instanceof LayerDrawable) {
			LayerDrawable ld = ((LayerDrawable) drawable);
			for (int i = 0; i < ld.getNumberOfLayers(); i++) {
				setColorFilter(ld.getDrawable(i));
			}
		} else if (drawable instanceof ScaleDrawable) {
			setColorFilter(((ScaleDrawable) drawable).getDrawable());
		} else if (drawable instanceof ClipDrawable) {
			setColorFilter((Drawable) ReflectUtil.get(drawable, "mClipState.mDrawable"));
		} else if (drawable instanceof RotateDrawable) {
			setColorFilter(((RotateDrawable) drawable).getDrawable());
		} else if (drawable instanceof InsetDrawable) {
			setColorFilter((Drawable) ReflectUtil.get(drawable, "mInsetState.mDrawable"));
		}
	}

	public void clearCache() {
		super.clearCache();
		nightIdMap.clear();
		notFoundedNightIds.clear();
	}

}
