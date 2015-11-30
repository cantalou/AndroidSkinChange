# SkinChange
Android换肤,基于插件式的资源切换方案

#特点
1. 插件换肤</br>
2. 对已有项目改动量少</br>
3. 支持重启或者不重启Activity</br>

#原理
通过替换Activity的mResources和mTheme两个属性

#缺陷
1. 已创建的Activity自动替换资源不支持如下操作:</br>
   <ul>
     <li> 在代码中动态设置View的background, ImageView的src, TextView的textColor, drawableLeft等</li>
     <li> 在代码中通过new View()生成的视图</li>
   </ul>
2. 不支持layout资源切换, 即layout资源只会使用app内自带的资源文件
3. 不支持drawable中xml类型资源的切换, 不同版本间同一个xml文件不得修改
4. 暂不支持padding, margin, textSize等类型的资源切换
