# SkinChange
Android 换肤/夜间模式, 基于插件式的资源切换方案

#特点
1. 插件换肤  
2. 浸入性低  
3. 支持重启或者不重启Activity  

#缺陷
1. 已创建的Activity自动替换资源不支持如下操作:  
   <ul>
     <li> 在代码中动态设置View的background, ImageView的src, TextView的textColor, drawableLeft等</li>
     <li> 在代码中通过new View()生成的视图</li>
   </ul>
2. 不支持layout资源切换, 即layout资源只会使用App内自带的资源文件, 不会使用皮肤包的布局文件  
3. 不支持drawable中xml类型资源的切换, 不同版本间同一个xml文件不得修改  
4. 获取LayoutInflater必须使用Activity.getLayoutInflater  

#使用方法
##集成
1. 在build.gradle文件中如下代码:  
    dependencies {  
        compile 'com.cantalou:android-skin-change:1+'  
    }  
    apply from: 'https://raw.githubusercontent.com/cantalou/GradlePublic/master/keepResourcesId.gradle'  
    apply from: 'https://raw.githubusercontent.com/cantalou/GradlePublic/master/genNameId.gradle  
2. 代码集成:  
2.1 在自定义的Application的onCreate中添加如下代码:  
        SkinManager.getInstance().init(this);  
2.2 更换皮肤代码:  
        SkinManager.getInstance().changeResources(activityInsatance, "/filepath/red.apk");  

#效果
![image](https://github.com/cantalou/androidSkinChange/blob/master/jdfw.gif)
#实现原理  
<a href="http://blog.csdn.net/cantalou/article/details/49708469">http://blog.csdn.net/cantalou/article/details/49708469</a>
