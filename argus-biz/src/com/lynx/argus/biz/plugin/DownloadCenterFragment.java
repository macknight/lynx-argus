package com.lynx.argus.biz.plugin;

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
 * @version 13-11-18 下午8:00
 */
public class DownloadCenterFragment extends LFFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			throws Exception {
		View view = inflater.inflate(R.layout.layout_downloadcenter, container, false);
		if (view == null) {
			throw new Exception("页面初始化错误");
		}

		ImageButton ibBack = (ImageButton) view.findViewById(R.id.ib_downloadcenter_back);
		ibBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tabActivity.onBackPressed();
			}
		});
		return view;
	}

}
