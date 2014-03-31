package com.lynx.lib.core;

import java.util.Stack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * 类似IOS NavigationController,以栈式结构管理其中Fragment
 * 
 * @author zhufeng.liu
 * @version 13-10-29 上午10:49
 */
public abstract class LFNavigationActivity extends LFActivity {
	private Stack<LFFragment> stack;

	protected int animResPushIn = -1, animResPushOut = -1;
	protected int animResPopIn = -1, animResPopOut = -1;

	protected int resContent = android.R.id.primary; // fragment根容器ID

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

	public void pushFragment(LFFragment fragment, boolean shouldAnimate) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if (shouldAnimate) {
			if (animResPushIn != -1 && animResPushOut != -1) {
				ft.setCustomAnimations(animResPushIn, animResPushOut);
			}
		}
		if (fragment.shouldAdd()) {
			stack.push(fragment);
		}
		ft.replace(resContent, fragment);

		ft.commit();
	}

	public void pushFragment(LFFragment fragment) {
		pushFragment(fragment, fragment.shouldAnimate());
	}

	private void popFragment() {
		LFFragment fragment = stack.elementAt(stack.size() - 2);
		stack.pop();

		FragmentManager fm = getSupportFragmentManager();

		FragmentTransaction ft = fm.beginTransaction();
		if (fragment.shouldAnimate()) {
			if (animResPopIn != -1 && animResPopOut != -1) {
				ft.setCustomAnimations(animResPopIn, animResPopOut);
			}
		}

		ft.replace(resContent, fragment);

		ft.commit();
	}

	@Override
	public void onBackPressed() {
		if (!stack.lastElement().onBackPressed()) {
			if (stack.size() == 1) {
				rollback();
				super.onBackPressed();
			} else {
				popFragment();
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

	/**
	 * 重置动态加载模块的环境变量
	 */
	protected abstract void rollback();
}
