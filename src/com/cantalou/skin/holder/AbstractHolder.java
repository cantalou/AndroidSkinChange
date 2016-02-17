package com.cantalou.skin.holder;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;

import com.cantalou.android.util.Log;
import com.cantalou.skin.CacheKeyAndIdManager;
import com.cantalou.skin.content.res.SkinProxyResources;

public abstract class AbstractHolder implements Cloneable
{

    public static final int ATTR_HOLDER_KEY = 0x7FFFFFFF;

    /**
     * 父类的parse是否有被调用
     */
    private boolean called = false;

    protected CacheKeyAndIdManager cacheKeyAndIdManager = CacheKeyAndIdManager.getInstance();

    public final AbstractHolder parse(Context context, AttributeSet attrs)
    {
        called = false;
        boolean result = parseAttr(context, attrs);
        if (!called)
        {
            throw new IllegalStateException("super parse(AttributeSet attrs) must be call");
        }
        return result ? this : null;
    }

    /**
     * 重新加载资源
     *
     * @param view view对象
     * @param res  资源对象
     */
    public final void reloadAttr(View view, Resources res)
    {
        called = false;
        reload(view, res);
        if (!called)
        {
            throw new IllegalStateException("super reload(View view, Resources res) must be call");
        }
    }

    /**
     * 重新加载资源
     *
     * @param view view对象
     * @param res  资源对象
     */
    protected void reload(View view, Resources res)
    {
        called = true;
    }

    /**
     * 解析组件属性
     *
     * @param attrs
     * @return 组件使用app资源 true
     */
    protected boolean parseAttr(Context context, AttributeSet attrs)
    {
        called = true;
        return false;
    }

    public final int getResourceId(AttributeSet attrs, String name)
    {
        int id = 0;
        int len = attrs.getAttributeCount();
        for (int i = 0; i < len; i++)
        {
            String attributeName = attrs.getAttributeName(i);
            if (name.equals(attributeName))
            {
                id = attrs.getAttributeResourceValue(i, 0);
                break;
            }
        }
        return (id & SkinProxyResources.APP_ID_MASK) == SkinProxyResources.APP_ID_MASK ? id : 0;
    }

    public final int getResourceId(AttributeSet attrs, int index)
    {
        int id = attrs.getAttributeResourceValue(index, 0);
        return (id & SkinProxyResources.APP_ID_MASK) == SkinProxyResources.APP_ID_MASK ? id : 0;
    }

    @Override
    public final ViewHolder clone()
    {
        try
        {
            return (ViewHolder) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            Log.w(e);
            return null;
        }
    }
}
