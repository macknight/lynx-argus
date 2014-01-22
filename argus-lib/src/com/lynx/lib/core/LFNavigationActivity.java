package com.lynx.lib.core;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import java.util.Stack;

/**
 * 类似IOS NavigationController,以栈式结构管理其中Fragment
 * 
 * @author zhufeng.liu
 * 
 * @version 13-10-29 上午10:49
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

	public void setPushAnimation(int resIn, int resOut) {
		animResPushIn = resIn;
		animResPushOut = resOut;
	}

	public void setPopAnimation(int resIn, int resOut) {
		animResPopIn = resIn;
		animResPopOut = resOut;
	}

	public void pushFragment(LFFragment fragment) {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if (fragment.shouldAnimate()) {
			if (animResPopIn != -1 && animResPushOut != -1) {
				ft.setCustomAnimations(animResPushIn, animResPushOut);
			}
		}
		if (fragment.shouldAdd()) {
			stack.push(fragment);
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
	public boolean onTouchEvent(MotionEvent event) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (getCurrentFocus() != null) {
				if (getCurrentFocus().getWindowToken() != null) {
					imm.hideSoftInputFromWindow(getCurrentFocus()
							.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void onBackPressed() {
		if (!stack.lastElement().onBackPressed()) {
			if (stack.size() == 1) {
				super.onBackPressed();
			} else {
				popFragment(true);
			}
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
