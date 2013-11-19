package com.lynx.lib.core;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * UI模块动态加载失败提示fragment
 * <p/>
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-10-18 下午1:32
 */
public class PluginLoadErrorFragment extends LFFragment {
	private static final String Tag = "PluginLoadErrorFragment";

	private String message; // 加载失败提示

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Context context = inflater.getContext();
		AssetManager am = context.getAssets();

		ViewGroup view = new RelativeLayout(context);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		view.setLayoutParams(params);

		ImageView iv = new ImageView(context);
		params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		params.setMargins(50, 0, 0, 50);
		iv.setLayoutParams(params);
		try {
			BitmapDrawable bg = new BitmapDrawable(null, am.open("err.png"));
			iv.setImageDrawable(bg);
		} catch (Exception e) {
			Logger.e(Tag, "load icon error", e);
		}
		view.addView(iv);

		TextView tv = new TextView(context);
		params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.setMargins(0, 0, 100, 0);
		tv.setLayoutParams(params);
		tv.setPadding(0, 100, 0, 0);
		tv.setTextSize(16);
		tv.setTextColor(0xFF000000);
		tv.setText("页面载入失败@_@");
		view.addView(tv);

		return view;
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
