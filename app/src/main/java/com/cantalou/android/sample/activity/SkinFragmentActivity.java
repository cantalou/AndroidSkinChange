package com.cantalou.android.sample.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.cantalou.android.sample.R;
import com.cantalou.android.util.Log;

import com.cantalou.skin.SkinManager;

public class SkinFragmentActivity extends FragmentActivity {

	private SkinManager skinManager = SkinManager.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//skinManager.onAttach(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_skin);
		ViewGroup container = (ViewGroup) findViewById(R.id.skin_container);
		container.removeAllViews();
		FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
		fr.replace(R.id.skin_container, new NestFragment());
		fr.commitAllowingStateLoss();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.skin, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		//skinManager.onDestroy(this);
		super.onDestroy();
	}

	public static class NestFragment extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.activity_skin, container, false);
			Log.i("LayoutInflater :{}", inflater);
			return v;
		}
	}
}
