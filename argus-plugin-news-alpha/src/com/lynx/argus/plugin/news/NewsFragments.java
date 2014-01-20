package com.lynx.argus.plugin.news;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lynx.lib.core.LFFragment;

/**
 * 新闻资讯
 * 
 * @author zhufeng.liu
 * 
 * @addtime 13-11-19 下午11:02
 */
public class NewsFragments extends LFFragment {
	private static final String LM_API_NEWS = "/news";

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) throws Exception {
		View v = inflater.inflate(R.layout.layout_news, container, false);

		return v;
	}

	@Override
	public boolean onBackPressed() {
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

	}
}
