package com.lynx.argus.biz.shopping;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lynx.argus.R;
import com.lynx.argus.app.BizFragment;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-9-12 下午6:20
 */
public class ShoppingFragment extends BizFragment {

	public static final String Tag = "shopping";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

    @Override
    protected View onLoadView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return null;
    }

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_shopping, container, false);

		return v;
	}

}
