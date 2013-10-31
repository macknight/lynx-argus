package com.lynx.lib.core;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import java.util.Stack;

/**
 * 类似IOS NavigationController,以栈式结构管理其中Fragment
 * <p/>
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-10-29 上午10:49
 */
public abstract class LFNavigationActivity extends LFActivity {
	private Stack<LFFragment> stack;

	protected int resContent = android.R.id.primary; // fragment根容器ID
	private int animResPushIn = -1, animResPushOut = -1;
	private int animResPopIn = -1, animResPopOut = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		stack = new Stack<LFFragment>();
	}

	protected void setPushAnimation(int resIn, int resOut) {
		animResPushIn = resIn;
		animResPushOut = resOut;
	}

	protected void setPopAnimation(int resIn, int resOut) {
		animResPopIn = resIn;
		animResPopOut = resOut;
	}

	public void pushFragment(LFFragment fragment, boolean shouldAnimate, boolean shouldAdd) {
		if (shouldAdd) {
			stack.push(fragment);
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
		Fragment fragment = stack.elementAt(stack.size() - 2);

		stack.pop();

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
		if (stack.lastElement().onBackPressed() == false) {
			if (stack.size() == 1) {
				super.onBackPressed();
			} else {
				popFragment(false);
			}
		} else {
			// do nothing
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (stack.size() == 0) {
			return;
		}
		stack.lastElement().onActivityResult(requestCode, requestCode, data);
	}
}
