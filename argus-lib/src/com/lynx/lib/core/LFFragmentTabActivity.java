package com.lynx.lib.core;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: chris.liu
 * Date: 13-11-25
 * Time: 下午2:35
 */
public class LFFragmentTabActivity extends LFActivity {
	private static final String Tag = "LFFragmentTabActivity";

	protected TabHost mTabHost;
	protected TabManager mTabManager;
	protected int resContent = -1; // fragment根容器ID

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setOnContentView();
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();

		mTabManager = new TabManager(this, mTabHost, resContent);

		setTabWidgetBackground(0);
	}

	protected void setOnContentView() {

	}

	public void addTab(String title, Class<?> clazz, Bundle args) {
		addTab(title, 0, clazz, args);
	}

	public void addTab(String title, int indicatorView, Class<?> clazz,
	                   Bundle args) {
		if (title == null) {
			throw new IllegalArgumentException("title cann't be null!");
		}

//		TabHost.TabSpec tabSpec = mTabHost.newTabSpec(title).setIndicator(new LabelAndIconIndicatorStrategy());
//		mTabManager.addTab(tabSpec, clazz, args);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}

	public void onTabChanged(String tabId) {

	}

	protected void setTabWidgetBackground(int drawableId) {
		if (drawableId > 0) {
			mTabHost.getTabWidget().setBackgroundResource(drawableId);
		}
	}

	/**
	 * This is a helper class that implements a generic mechanism for
	 * associating fragments with the tabs in a tab host. It relies on a trick.
	 * Normally a tab host has a simple API for supplying a View or Intent that
	 * each tab will show. This is not sufficient for switching between
	 * fragments. So instead we make the content part of the tab host 0dp high
	 * (it is not shown) and the TabManager supplies its own dummy view to show
	 * as the tab content. It listens to changes in tabs, and takes care of
	 * switch to the correct fragment shown in a separate content area whenever
	 * the selected tab changes.
	 */
	public static class TabManager implements TabHost.OnTabChangeListener {
		private final LFFragmentTabActivity mActivity;
		private final TabHost mTabHost;
		private final int mContainerId;
		private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
		TabInfo mLastTab;

		static final class TabInfo {
			private final String tag;
			private final Class<?> clazz;
			private final Bundle args;
			private Fragment fragment;

			TabInfo(String tag, Class<?> clazz, Bundle args) {
				this.tag = tag;
				this.clazz = clazz;
				this.args = args;
			}
		}

		static class DummyTabFactory implements TabHost.TabContentFactory {
			private final Context mContext;

			public DummyTabFactory(Context context) {
				mContext = context;
			}

			@Override
			public View createTabContent(String tag) {
				View v = new View(mContext);
				v.setMinimumWidth(0);
				v.setMinimumHeight(0);
				return v;
			}
		}

		public TabManager(LFFragmentTabActivity activity, TabHost tabHost,
		                  int containerId) {
			mActivity = activity;
			mTabHost = tabHost;
			mContainerId = containerId;
			mTabHost.setOnTabChangedListener(this);
		}

		public void addTab(TabHost.TabSpec tabSpec, Class<?> clazz, Bundle args) {
			tabSpec.setContent(new DummyTabFactory(mActivity));
			String tag = tabSpec.getTag();

			TabInfo info = new TabInfo(tag, clazz, args);

			// Check to see if we already have a fragment for this tab, probably
			// from a previously saved state. If so, deactivate it, because our
			// initial state is that a tab isn't shown.
			info.fragment = mActivity.getFragmentManager()
					.findFragmentByTag(tag);
			if (info.fragment != null && !info.fragment.isHidden()) {
				FragmentTransaction ft = mActivity.getFragmentManager()
						.beginTransaction();
				ft.hide(info.fragment);
				ft.commitAllowingStateLoss();
			}

			mTabs.put(tag, info);
			mTabHost.addTab(tabSpec);
		}

		@Override
		public void onTabChanged(String tabId) {
			TabInfo newTab = mTabs.get(tabId);
			if (mLastTab != newTab) {
				FragmentTransaction ft = mActivity.getFragmentManager()
						.beginTransaction();
				if (mLastTab != null) {
					if (mLastTab.fragment != null) {
						ft.hide(mLastTab.fragment);
					}
				}
				if (newTab != null) {
					if (newTab.fragment == null) {
						newTab.fragment = Fragment.instantiate(mActivity,
								newTab.clazz.getName(), newTab.args);
						ft.add(mContainerId, newTab.fragment, newTab.tag);
						Log.i(Tag, "onTabChanged with tabId:" + tabId
								+ ", newTab.fragment is null, newTab.tag is "
								+ newTab.tag);
					} else {
						ft.show(newTab.fragment);
						Log.i(Tag, "onTabChanged with tabId:" + tabId
								+ ", show fragment success");
					}
				} else {
					Log.i(Tag, "onTabChanged with tabId:" + tabId
							+ ", newTab is null");
				}

				mLastTab = newTab;
				ft.commitAllowingStateLoss();
				mActivity.getFragmentManager()
						.executePendingTransactions();
			}
			mActivity.onTabChanged(tabId);
		}
	}
}
