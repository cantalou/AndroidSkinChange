package com.cantalou.skin;

/**
 * 皮肤资源信息接口<br>
 * {@link SkinManager}将通过这个接口获取当前应用所使用的资源包信息,如了: 资源名,资源路径
 *
 * @author LinZhiWei
 * @date 2015年12月6日 下午10:18:06
 */
public interface SkinResourcesInfoListener {

	/**
	 * 获得当前皮肤资源路径
	 * 
	 * @return 皮肤资源路径
	 */
	public String getCurrentResourcesPath();

	/**
	 * 皮肤资源切换结果回调
	 * 
	 * @param result
	 *            切换结果, 成功true
	 * @param skinPath
	 *            资源路径
	 */
	public void resourcesChangeResult(boolean result, String skinPath);
}
