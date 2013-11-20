package com.lynx.argus.plugin.tools;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lynx.lib.core.LFFragment;

/**
 * Created with IntelliJ IDEA.
 * User: chris.liu
 * Date: 13-11-20
 * Time: 下午6:48
 */
public class ToolsFragment extends LFFragment {
	private static final String LM_API_NEWS = "/tools";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_tools, container, false);
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
