package com.lynx.lib.core;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 9/26/13 3:23 PM
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

	public abstract void onActivityResult(int requestCode, int resultCode, Intent data);
}
