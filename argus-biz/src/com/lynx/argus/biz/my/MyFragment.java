package com.lynx.argus.biz.my;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lynx.argus.R;
import com.lynx.lib.core.LFFragment;
import com.lynx.lib.widget.AsyncImageView;

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

		AsyncImageView aivTest = (AsyncImageView) view.findViewById(R.id.aiv_my_test);
		aivTest.setFile("http://www.hahaertong.com/data/files/store_46754/shop/small_201306011754221139.jpg");
		return view;
	}

}
