package com.lynx.lib.core;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lynx.lib.util.ViewUtil;

/**
 * UI模块动态加载失败提示fragment
 * 
 * @author zhufeng.liu
 * 
 * @version 13-9-26 下午3:23
 */
public class PluginLoadErrorFragment extends LFFragment {
	private static final String Tag = "PluginLoadErrorFragment";

	private String message; // 加载失败提示

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return ViewUtil.createLoadErrorView(inflater);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public boolean onBackPressed() {
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

	}
}
