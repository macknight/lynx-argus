package com.lynx.argus.biz.more;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lynx.argus.R;
import com.lynx.argus.app.BizFragment;

/**
 * 
 * @author chris.liu
 * 
 * @version 14-1-20 上午11:53
 */
public class AboutFragment extends BizFragment {

	@Override
	protected View onLoadView(LayoutInflater inflater, ViewGroup container,
			Bundle bundle) throws Exception {
		View v = inflater.inflate(R.layout.layout_about, container, false);

		return v;
	}
}
