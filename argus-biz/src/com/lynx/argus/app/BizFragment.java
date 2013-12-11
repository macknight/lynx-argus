package com.lynx.argus.app;

import android.content.Intent;
import android.os.Bundle;
import com.lynx.lib.core.LFFragment;

/**
 * 
 * @author zhufeng.liu
 * 
 * @addtime 13-9-12 下午6:18
 */
public class BizFragment extends LFFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onBackPressed() {
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

}
