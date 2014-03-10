package com.lynx.argus.plugin.timemachine;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.lynx.argus.plugin.timemachine.R.layout;
import com.lynx.lib.widget.charts.PieHelper;
import com.lynx.lib.widget.charts.PieView;

/**
 * Created by Dacer on 11/16/13.
 */
public class ClockPieFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(layout.layout_pie, container, false);
		final PieView pieView = (PieView) rootView.findViewById(R.id.pie_view);
		Button button = (Button) rootView.findViewById(R.id.pie_button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				randomSet(pieView);
			}
		});
		set(pieView);
		return rootView;
	}

	private void randomSet(PieView pieView) {
		ArrayList<PieHelper> pieHelperArrayList = new ArrayList<PieHelper>();
		for (int i = 0; i < 20; i++) {
			int startHour = (int) (24 * Math.random());
			int startMin = (int) (60 * Math.random());
			int duration = (int) (50 * Math.random());
			pieHelperArrayList.add(new PieHelper(startHour, startMin, 0, startHour, startMin
					+ duration, 0));
		}
		pieView.setDate(pieHelperArrayList);
	}

	private void set(PieView pieView) {
		ArrayList<PieHelper> pieHelperArrayList = new ArrayList<PieHelper>();
		pieHelperArrayList.add(new PieHelper(1, 50, 2, 30));
		pieHelperArrayList.add(new PieHelper(6, 50, 8, 30));
		pieView.setDate(pieHelperArrayList);
	}
}
