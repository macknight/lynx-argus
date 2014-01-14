package com.lynx.lib.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lynx.lib.core.Logger;

/**
 * @author: chris.liu
 * @addtime: 14-1-13 上午11:49
 */
public class ViewUtil {

    public static final String Tag = "ViewUtil";

	private ViewUtil() {
		throw new AssertionError("this class shouldn't be instanced");
	}

	/**
	 * 创建页面加载错误提示页面
	 * 
	 * @param inflater
	 * @return
	 */
	public static View createLoadErrorView(LayoutInflater inflater) {
		Context context = inflater.getContext();
		AssetManager am = context.getAssets();

		ViewGroup view = new RelativeLayout(context);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		view.setLayoutParams(params);

		ImageView iv = new ImageView(context);
		params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
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
		params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
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
}