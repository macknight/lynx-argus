package com.lynx.lib.core;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

/**
 * 
 * @author zhufeng.liu
 * 
 * @addtime 13-9-26 下午3:23
 */
public abstract class LFFragment extends Fragment {

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

	public abstract boolean onBackPressed();

	public abstract void onActivityResult(int requestCode, int resultCode,
			Intent data);
}
