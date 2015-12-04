# SkinChange
Android换肤,基于插件式的资源切换方案

#特点
1. 插件换肤</br>
2. 对已有项目改动量少</br>
3. 支持重启或者不重启Activity</br>

#缺陷
1. 已创建的Activity自动替换资源不支持如下操作:</br>
   <ul>
     <li> 在代码中动态设置View的background, ImageView的src, TextView的textColor, drawableLeft等</li>
     <li> 在代码中通过new View()生成的视图</li>
   </ul>
2. 不支持layout资源切换, 即layout资源只会使用app内自带的资源文件
3. 不支持drawable中xml类型资源的切换, 不同版本间同一个xml文件不得修改
4. 不支持padding, margin, textSize等类型的资源切换

#使用方法
##集成
1. 添加依赖文件:</br>
    dependencies {</br>
        compile 'com.cantalou:android-skin-change:1+@aar'</br>
    }</br>
2. 代码集成:</br>
    2.1 在BaseActivity(项目所有Activity的父类)或者所有的Activity的中添加如下代码</br>
    @Override</br>
	protected void onCreate() {</br>
	    SkinManager.getInstance().onAttach(this);</br>
		super.onCreate();</br>
	}</br>

	@Override</br>
	protected void onDestroy() {</br>
		SkinManager.getInstance().onDestroy(this);</br>
		super.onDestroy();</br>
	}</br>

#实现原理
通过替换Activity的mResources和mTheme两个属性</br>
<a href="http://blog.csdn.net/cantalou/article/details/49708469">http://blog.csdn.net/cantalou/article/details/49708469</a>