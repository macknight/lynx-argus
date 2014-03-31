package com.lynx.lib.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * 
 * @author chris.liu
 * @version 14-1-13 上午11:49
 */
public class ViewUtil {

	public static final String Tag = "ViewUtil";

	private ViewUtil() {
		throw new AssertionError("ViewUtil shouldn't be instanced");
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
		RelativeLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		view.setLayoutParams(params);

		ImageView iv = new ImageView(context);
		int width = DisplayUtil.dip2px(context, 86);
		int height = DisplayUtil.dip2px(context, 169);
		params = new LayoutParams(width, height);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		params.setMargins(100, 0, 0, 50);
		iv.setLayoutParams(params);
		iv.setImageDrawable(ImageUtil.getImageDrawableFromAssets(context, "err.png"));
		view.addView(iv);

		TextView tv = new TextView(context);
		params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
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
