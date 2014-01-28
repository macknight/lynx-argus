package com.lynx.argus.biz.my;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lynx.argus.R;
import com.lynx.argus.app.BizFragment;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-10-27-下午9:29.
 */
public class MyFragment extends BizFragment {

	public static final String Tag = "My";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_my, container, false);
		return v;
	}
}
