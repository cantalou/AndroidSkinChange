package com.cantalou.android.sample.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.cantalou.android.sample.R;

import com.cantalou.skin.SkinManager;

public class SkinTwoActivity extends Activity
{

    private SkinManager skinManager = SkinManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //skinManager.onAttach(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.skin, menu);
        return true;
    }

    @Override
    protected void onDestroy()
    {
        //skinManager.onDestroy(this);
        super.onDestroy();
    }
}
