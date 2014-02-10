package com.lynx.argus.biz.shopping;

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
 * @version 13-9-12 下午6:20
 */
public class ShoppingFragment extends LFFragment {

	public static final String Tag = "shopping";

	@Override
	protected View onLoadView(LayoutInflater inflater, ViewGroup container, Bundle bundle)
			throws Exception {
		View view = inflater.inflate(R.layout.layout_shopping, container, false);
		if (view == null) {
			throw new Exception("页面初始化错误");
		}
		return view;
	}
}
