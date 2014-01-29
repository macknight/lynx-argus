package com.lynx.lib.core;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lynx.lib.util.ViewUtil;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-9-26 下午3:23
 */
public abstract class LFFragment extends Fragment {
	public static final String Tag = "LFFragment";

	protected LFTabActivity tabActivity = null;
	protected LFNavigationActivity navActivity = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (this.getActivity() instanceof LFTabActivity) {
			tabActivity = (LFTabActivity) this.getActivity();
		} else if (this.getActivity() instanceof LFDexActivity) {
			navActivity = (LFNavigationActivity) this.getActivity();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		try {
			return onLoadView(inflater, container, savedInstanceState);
		} catch (Exception e) {
            Logger.e(Tag, "onLoadView error", e);
			// 当加载页面错误时载入默认错误提示页面
			return ViewUtil.createLoadErrorView(inflater);
		}
	}

	/**
	 * 载入页面
	 * 
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 * @return
	 */
	protected abstract View onLoadView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) throws Exception;

	public boolean shouldAdd() {
		return true;
	}

	public boolean shouldAnimate() {
		return true;
	}

	public boolean onBackPressed() {
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

}
