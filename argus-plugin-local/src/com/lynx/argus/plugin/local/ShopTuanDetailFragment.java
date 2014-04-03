package com.lynx.argus.plugin.local;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lynx.lib.core.LFFragment;

/**
 * @author chris.liu
 * @version 4/3/14 3:07 PM
 */
public class ShopTuanDetailFragment extends LFFragment {

	@Override
	protected View onLoadView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) throws Exception {
        View view = inflater.inflate(R.layout.layout_shop_tuan_detail, container, false);
        if (view == null) {
            throw new Exception("页面初始化错误");
        }


		return view;
	}
}
