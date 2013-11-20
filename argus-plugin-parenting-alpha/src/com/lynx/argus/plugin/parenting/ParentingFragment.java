package com.lynx.argus.plugin.parenting;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lynx.lib.core.LFFragment;

/**
 * 亲子
 * <p/>
 * Created with IntelliJ IDEA.
 * User: chris.liu
 * Date: 13-11-19 下午11:13
 */
public class ParentingFragment extends LFFragment {
	private static final String LM_API_NEWS = "/parenting";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_parenting, container, false);
		return v;
	}

	@Override
	public boolean onBackPressed() {
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

	}
}
