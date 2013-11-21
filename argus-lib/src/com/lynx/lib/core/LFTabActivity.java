package com.lynx.lib.core;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * 类似IOS中UITabbarController,以复合栈式结构管理其中的Fragment
 * <p/>
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 9/26/13 3:08 PM
 */
public abstract class LFTabActivity extends LFActivity {
	protected TabHost tabHost;
	protected Map<String, Stack<LFFragment>> stacks;
	protected String curTab;
	protected int resContent = -1; // fragment根容器ID
	private int animResPushIn = android.R.anim.fade_in, animResPushOut = android.R.anim.fade_out;
	private int animResPopIn = android.R.anim.fade_in, animResPopOut = android.R.anim.fade_out;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		stacks = new HashMap<String, Stack<LFFragment>>();

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

	public void pushFragment(String tag, LFFragment fragment, boolean shouldAnimate, boolean shouldAdd) {
		if (shouldAdd) {
			stacks.get(tag).push(fragment);
		}
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if (shouldAnimate) {
			ft.setCustomAnimations(animResPushIn, animResPushOut);
		}
		ft.replace(resContent, fragment);
		ft.commit();
	}

	private void popFragment(boolean shouldAnimate) {
		Fragment fragment = stacks.get(curTab).elementAt(stacks.get(curTab).size() - 2);

		stacks.get(curTab).pop();

		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if (shouldAnimate) {
			ft.setCustomAnimations(animResPopIn, animResPopOut);
		}

		ft.replace(resContent, fragment);

		ft.commit();
	}

	@Override
	public void onBackPressed() {
		if (!stacks.get(curTab).lastElement().onBackPressed()) {
			if (stacks.get(curTab).size() == 1) {
				super.onBackPressed();
			} else {
				popFragment(true);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (stacks.get(curTab).size() == 0) {
			return;
		}
		stacks.get(curTab).lastElement().onActivityResult(requestCode, requestCode, data);
	}
}