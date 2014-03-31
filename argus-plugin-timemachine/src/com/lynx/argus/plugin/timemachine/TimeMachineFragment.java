package com.lynx.argus.plugin.timemachine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.lynx.lib.core.LFFragment;

/**
 * 时光机
 * 
 * @author chris.liu
 * @version 3/7/14 4:24 PM
 */
public class TimeMachineFragment extends LFFragment {

	@Override
	protected View onLoadView(LayoutInflater inflater, ViewGroup container, Bundle bundle)
			throws Exception {
		View view = inflater.inflate(R.layout.layout_timemachine, container, false);

		Button btn = (Button) view.findViewById(R.id.btn_line);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LineFragment lineFragment = new LineFragment();
				navActivity.pushFragment(lineFragment);
			}
		});

		btn = (Button) view.findViewById(R.id.btn_pie);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ClockPieFragment clockPieFragment = new ClockPieFragment();
				navActivity.pushFragment(clockPieFragment);
			}
		});

		btn = (Button) view.findViewById(R.id.btn_bar);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				BarFragment barFragment = new BarFragment();
				navActivity.pushFragment(barFragment);
			}
		});

		return view;
	}
}
