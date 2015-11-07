# SkinChange
基于插件式的资源切换方案

#特点
1. 插件换肤</br>
2. 对已有项目改动量少</br>
3. 支持重启或者不重启Activity</br>

#原理
通过替换Activity的mResources和mTheme两个属性

#缺陷
1.已打开的Activity不支持如下操作</br>
  1.1 不支持动态创建的view,如: 在代码中通过new TextView()创建</br>
  1.2 不支持
