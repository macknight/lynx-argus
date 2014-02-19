package com.lynx.argus.biz.my;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import com.lynx.argus.R;
import com.lynx.lib.core.LFFragment;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-10-27-下午9:29.
 */
public class MyFragment extends LFFragment {

	public static final String Tag = "My";

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			throws Exception {
		View view = inflater.inflate(R.layout.layout_my, container, false);
		if (view == null) {
			throw new Exception("页面初始化错误");
		}
		return view;
	}

}
