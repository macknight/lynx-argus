package com.lynx.argus.biz.search;

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
 * @addtime 13-10-27 下午9:29
 */
public class SearchFragment extends BizFragment {
	public static final String Tag = "Search";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_search, container, false);
		return v;
	}
}
