package com.lynx.argus.biz.more;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lynx.argus.R;
import com.lynx.argus.app.BizFragment;

/**
 * Created by chris.liu
 * Created at 13-10-27-下午9:29.
 */
public class MoreFragment extends BizFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_more, container, false);
		return v;
	}
}
