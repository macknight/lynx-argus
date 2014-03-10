package com.lynx.argus.plugin.timemachine;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.lynx.argus.plugin.timemachine.NavigationDrawerFragment.NavigationDrawerCallbacks;
import com.lynx.lib.core.LFFragment;

/**
 * 时光机
 * 
 * @author chris
 * 
 * @version 3/7/14 4:24 PM
 */
public class TimeMachineFragment extends LFFragment implements NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected View onLoadView(LayoutInflater inflater, ViewGroup container, Bundle bundle)
			throws Exception {
		View view = inflater.inflate(R.layout.layout_timemachine, container, false);

		navActivity.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = navActivity.getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) view.findViewById(R.id.drawer_layout));

		return view;
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getFragmentManager();
		switch (position) {
		case 0:
			fragmentManager.beginTransaction().replace(R.id.container, new LineFragment()).commit();
			break;
		case 1:
			fragmentManager.beginTransaction().replace(R.id.container, new BarFragment()).commit();
			break;
		case 2:
			fragmentManager.beginTransaction().replace(R.id.container, new ClockPieFragment())
					.commit();
			break;
		}
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = navActivity.getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

}
