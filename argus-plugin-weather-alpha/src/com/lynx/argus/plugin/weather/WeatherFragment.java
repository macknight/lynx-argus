package com.lynx.argus.plugin.weather;

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
 * @version 13-11-19 下午11:55
 */
public class WeatherFragment extends LFFragment {
	private static final String LM_API_NEWS = "/parenting";

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			throws Exception {
		View view = inflater.inflate(R.layout.layout_weather, container, false);
		if (view == null) {
			throw new Exception("页面初始化错误");
		}

		return view;
	}

}
