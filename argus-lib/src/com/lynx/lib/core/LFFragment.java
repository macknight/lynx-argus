package com.lynx.lib.core;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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

		Log.d("chris", this.getActivity().getLocalClassName());
		if (this.getActivity() instanceof LFTabActivity) {
			Log.d("chris", "tab");
			tabActivity = (LFTabActivity)this.getActivity();
		} else if (this.getActivity() instanceof LFDexActivity) {
			Log.d("chris", "nav");
			navActivity = (LFNavigationActivity) this.getActivity();
		}
	}

	public abstract boolean onBackPressed();

	public abstract void onActivityResult(int requestCode, int resultCode, Intent data);
}
