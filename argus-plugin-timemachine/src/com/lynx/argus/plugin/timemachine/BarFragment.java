package com.lynx.argus.plugin.timemachine;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import com.lynx.lib.core.LFFragment;
import com.lynx.lib.util.ImageUtil;
import com.lynx.lib.widget.charts.BarView;

/**
 * 
 * @author chris
 * 
 * @version 3/8/14 6:34 PM
 */
public class BarFragment extends LFFragment {

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			throws Exception {
		View view = inflater.inflate(R.layout.layout_bar, container, false);
		final BarView barView = (BarView) view.findViewById(R.id.bar_view);
		Button button = (Button) view.findViewById(R.id.bar_button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				randomSet(barView);
			}
		});

		randomSet(barView);

		return view;
	}

	private void randomSet(BarView barView) {
		int random = (int) (Math.random() * 10) + 6;
		ArrayList<String> test = new ArrayList<String>();
		for (int i = 0; i < random; i++) {
			test.add("test");
			test.add("pqg");
			// test.add(String.valueOf(i+1));
		}
		barView.setBottomTextList(test);

		ArrayList<Integer> barDataList = new ArrayList<Integer>();
		for (int i = 0; i < random * 2; i++) {
			barDataList.add((int) (Math.random() * 100));
		}
		barView.setDataList(barDataList, 100);
	}
}
