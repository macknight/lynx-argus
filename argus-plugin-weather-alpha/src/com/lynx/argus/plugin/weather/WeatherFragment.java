package com.lynx.argus.plugin.weather;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lynx.lib.core.LFFragment;

/**
 * 天气插件
 * 
 * @author zhufeng.liu
 * 
 * @addtime 13-11-19 下午11:55
 */
public class WeatherFragment extends LFFragment {
	private static final String LM_API_NEWS = "/parenting";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_weather, container, false);

		return v;
	}

	@Override
	public boolean onBackPressed() {
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

	}
}
