package com.lynx.argus.biz.plugin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.lynx.argus.R;
import com.lynx.argus.app.BizFragment;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-11-18 下午8:00
 */
public class DownloadCenterFragment extends BizFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_downloadcenter, container,
				false);
		ImageButton ibBack = (ImageButton) v
				.findViewById(R.id.ib_downloadcenter_back);
		ibBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tabActivity.onBackPressed();
			}
		});
		return v;
	}

}
