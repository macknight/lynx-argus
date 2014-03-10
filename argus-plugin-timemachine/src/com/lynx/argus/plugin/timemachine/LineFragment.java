package com.lynx.argus.plugin.timemachine;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.lynx.lib.core.LFFragment;
import com.lynx.lib.widget.charts.LineView;

/**
 *
 * @author chris
 *
 * @version 3/8/14 6:34 PM
 */
public class LineFragment extends LFFragment {
	int randomint = 9;

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			throws Exception {
		View rootView = inflater.inflate(R.layout.layout_line, container, false);
		final LineView lineView = (LineView) rootView.findViewById(R.id.line_view);

		// must*
		ArrayList<String> test = new ArrayList<String>();
		for (int i = 0; i < randomint; i++) {
			test.add(String.valueOf(i + 1));
		}
		lineView.setBottomTextList(test);
		lineView.setDrawDotLine(true);
		lineView.setShowPopup(LineView.SHOW_POPUPS_NONE);

		Button lineButton = (Button) rootView.findViewById(R.id.line_button);
		lineButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				randomSet(lineView);

			}
		});

		randomSet(lineView);
		return rootView;
	}

	private void randomSet(LineView lineView) {
		ArrayList<Integer> dataList = new ArrayList<Integer>();
		int random = (int) (Math.random() * 9 + 1);
		for (int i = 0; i < randomint; i++) {
			dataList.add((int) (Math.random() * random));
		}

		ArrayList<Integer> dataList2 = new ArrayList<Integer>();
		random = (int) (Math.random() * 9 + 1);
		for (int i = 0; i < randomint; i++) {
			dataList2.add((int) (Math.random() * random));
		}

		ArrayList<Integer> dataList3 = new ArrayList<Integer>();
		random = (int) (Math.random() * 9 + 1);
		for (int i = 0; i < randomint; i++) {
			dataList3.add((int) (Math.random() * random));
		}

		ArrayList<ArrayList<Integer>> dataLists = new ArrayList<ArrayList<Integer>>();
		dataLists.add(dataList);
		dataLists.add(dataList2);
		// dataLists.add(dataList3);

		lineView.setDataList(dataLists);
	}
}
