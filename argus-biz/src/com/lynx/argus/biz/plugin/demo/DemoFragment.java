package com.lynx.argus.biz.plugin.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lynx.argus.R;
import com.lynx.lib.core.LFFragment;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-9-16 上午10:29
 */
public class DemoFragment extends LFFragment {

	public DemoFragment() {

	}

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) throws Exception {
		View v = inflater.inflate(R.layout.layout_demo, container,
				false);
		return v;
	}
}