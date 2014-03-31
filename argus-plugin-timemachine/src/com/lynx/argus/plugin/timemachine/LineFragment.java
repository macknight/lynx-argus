package com.lynx.argus.plugin.timemachine;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lynx.lib.core.LFFragment;
import com.lynx.lib.widget.charts.LineView;

/**
 *
 * @author chris.liu
 * @version 3/8/14 6:34 PM
 */
public class LineFragment extends LFFragment {
	private int randomint = 9;

    private List<String> colors = new ArrayList<String>();

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			throws Exception {
		View rootView = inflater.inflate(R.layout.layout_line, container, false);
		final LineView lineView = (LineView) rootView.findViewById(R.id.line_view);

        colors.add("#ffe74c3c");
        colors.add("#ff2980b9");
        colors.add("#ff1abc9c");
        colors.add("#ff005876");
        colors.add("#ffffeed7");

		ArrayList<String> test = new ArrayList<String>();
		for (int i = 0; i < randomint; i++) {
			test.add(String.valueOf(i + 1));
		}
		lineView.setBottomTextList(test);
		lineView.setDrawDotLine(true);
		lineView.setShowPopup(LineView.SHOW_POPUPS_NONE);
        lineView.setColors(colors);
        lineView.setPopupResId(R.drawable.popup_bg);

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
		int random = (int) (Math.random() * randomint + 1);
		for (int i = 0; i < randomint; i++) {
			dataList.add((int) (Math.random() * random));
		}

		ArrayList<Integer> dataList2 = new ArrayList<Integer>();
		random = (int) (Math.random() * randomint + 1);
		for (int i = 0; i < randomint; i++) {
			dataList2.add((int) (Math.random() * random));
		}

		ArrayList<Integer> dataList3 = new ArrayList<Integer>();
		random = (int) (Math.random() * randomint + 1);
		for (int i = 0; i < randomint; i++) {
			dataList3.add((int) (Math.random() * random));
		}

        ArrayList<Integer> dataList4 = new ArrayList<Integer>();
        random = (int) (Math.random() * randomint + 1);
        for (int i = 0; i < randomint; i++) {
            dataList4.add((int) (Math.random() * random));
        }

        ArrayList<Integer> dataList5 = new ArrayList<Integer>();
        random = (int) (Math.random() * randomint + 1);
        for (int i = 0; i < randomint; i++) {
            dataList5.add((int) (Math.random() * random));
        }

		List<List<Integer>> dataLists = new ArrayList<List<Integer>>();
		dataLists.add(dataList);
		dataLists.add(dataList2);
		dataLists.add(dataList3);
        dataLists.add(dataList4);
        dataLists.add(dataList5);

		lineView.setDataList(dataLists);
	}
}
