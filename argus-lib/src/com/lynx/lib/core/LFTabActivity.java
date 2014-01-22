package com.lynx.lib.core;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * 类似IOS中UITabbarController,以复合栈式结构管理其中的Fragment
 * 
 * @author zhufeng.liu
 * 
 * @version 13-9-26 下午3:08
 */
public abstract class LFTabActivity extends LFActivity {
	protected static Map<String, Stack<LFFragment>> stacks = new HashMap<String, Stack<LFFragment>>();
	protected TabHost tabHost;
	protected String curTab;
	protected int resContent = -1; // fragment根容器ID
	private int animResPushIn = android.R.anim.fade_in,
			animResPushOut = android.R.anim.fade_out;
	private int animResPopIn = android.R.anim.fade_in,
			animResPopOut = android.R.anim.fade_out;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initUI();
		initTabHost();
		initTabContents();
	}

	protected abstract void initUI();

	protected abstract void initTabHost();

	protected abstract void initTabContents();

	public void setPushAnimation(int resIn, int resOut) {
		animResPushIn = resIn;
		animResPushOut = resOut;
	}

	public void setPopAnimation(int resIn, int resOut) {
		animResPopIn = resIn;
		animResPopOut = resOut;
	}

	public void pushFragment(LFFragment fragment, boolean shouldAnimate) {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if (shouldAnimate) {
			ft.setCustomAnimations(animResPushIn, animResPushOut);
		}
		ft.replace(resContent, fragment);
		stacks.get(tabHost.getCurrentTabTag()).add(fragment);
		ft.commit();
	}

	@Override
	public void onBackPressed() {
		Stack<LFFragment> stack = stacks.get(tabHost.getCurrentTabTag());

		if (stack.isEmpty()) {
			super.onBackPressed();
		} else {
			Fragment fragment = stack.pop();

			if (fragment.isVisible()) {
				if (stack.isEmpty()) {
					super.onBackPressed();
				} else {
					LFFragment frg = stack.pop();
					stacks.get(tabHost.getCurrentTabTag()).push(frg);

					FragmentTransaction ft = getFragmentManager()
							.beginTransaction();
					ft.setCustomAnimations(animResPopIn, animResPopOut);

					ft.replace(resContent, frg).commit();
				}
			} else {
				getFragmentManager().beginTransaction()
						.replace(resContent, fragment).commit();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (stacks.get(curTab).size() == 0) {
			return;
		}
		stacks.get(curTab).lastElement()
				.onActivityResult(requestCode, requestCode, data);
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

		private final LFTabActivity mActivity;
		private final TabHost mTabHost;
		private final int mContainerId;

		private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();

		private TabInfo mLastTab;

		static final class TabInfo {
			private final String tag;
			private final Class<?> clss;
			private final Bundle args;
			private Fragment fragment;

			TabInfo(String _tag, Class<?> _class, Bundle _args) {
				tag = _tag;
				clss = _class;
				args = _args;
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

		public TabManager(LFTabActivity activity, TabHost tabHost,
				int mContainerId) {
			mActivity = activity;
			mTabHost = tabHost;
			this.mContainerId = mContainerId;
			mTabHost.setOnTabChangedListener(this);
		}

		public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
			tabSpec.setContent(new DummyTabFactory(mActivity));
			String tag = tabSpec.getTag();

			TabInfo info = new TabInfo(tag, clss, args);

			info.fragment = mActivity.getFragmentManager().findFragmentByTag(
					tag);
			if (info.fragment != null && !info.fragment.isDetached()) {
				FragmentTransaction ft = mActivity.getFragmentManager()
						.beginTransaction();
				ft.detach(info.fragment);
				ft.commit();
			}

			mTabs.put(tag, info);
			mTabHost.addTab(tabSpec);
		}

		public void addInvisibleTab(TabHost.TabSpec tabSpec, Class<?> clss,
				Bundle args, int childID) {
			tabSpec.setContent(new DummyTabFactory(mActivity));
			String tag = tabSpec.getTag();
			TabWidget tabWidget = mTabHost.getTabWidget();
			TabInfo info = new TabInfo(tag, clss, args);

			info.fragment = mActivity.getFragmentManager().findFragmentByTag(
					tag);
			if (info.fragment != null && !info.fragment.isDetached()) {
				FragmentTransaction ft = mActivity.getFragmentManager()
						.beginTransaction();
				ft.detach(info.fragment);
				ft.commit();
			}

			mTabs.put(tag, info);
			mTabHost.addTab(tabSpec);

			// Makes tab invisible
			tabWidget.getChildAt(childID).setVisibility(View.GONE);
		}

		@Override
		public void onTabChanged(String tabId) {
			TabInfo newTab = mTabs.get(tabId);
			if (mLastTab != newTab) {
				FragmentTransaction ft = mActivity.getFragmentManager()
						.beginTransaction();
				if (mLastTab != null) {
					if (mLastTab.fragment != null) {
						ft.detach(mLastTab.fragment);
					}
				}
				if (newTab != null) {
					if (newTab.fragment == null) {
						if (!stacks.get(tabId).isEmpty()) {
							LFFragment fragment = stacks.get(tabId).pop();
							stacks.get(tabId).push(fragment);
							ft.replace(mContainerId, fragment);
						}
					} else {
						if (!stacks.get(tabId).isEmpty()) {
							LFFragment fragment = stacks.get(tabId).pop();
							stacks.get(tabId).push(fragment);
							ft.replace(mContainerId, fragment);
						}
					}
				}

				mLastTab = newTab;
				ft.commit();
				mActivity.getFragmentManager().executePendingTransactions();
			}
		}
	}
}
