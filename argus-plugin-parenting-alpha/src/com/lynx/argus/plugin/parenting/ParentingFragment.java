package com.lynx.argus.plugin.parenting;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.lynx.lib.core.LFFragment;

/**
 * 亲子
 * 
 * @author zhufeng.liu
 * 
 * @version 13-11-22 下午4:29
 */
public class ParentingFragment extends LFFragment {

	public static final String LM_API_PARENT_DOMAIN = "http://www.hahaertong.com";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		navActivity.setPopAnimation(R.animator.slide_in_left,
				R.animator.slide_out_right);
		navActivity.setPushAnimation(R.animator.slide_in_right,
				R.animator.slide_out_left);
	}

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) throws Exception {
		View view = inflater.inflate(R.layout.layout_parenting, container,
				false);

		Button btn = (Button) view.findViewById(R.id.btn_campaign);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				CampaignListFragment cf = new CampaignListFragment();
				navActivity.pushFragment(cf);
			}
		});

		btn = (Button) view.findViewById(R.id.btn_shop);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ShopListFragment slf = new ShopListFragment();
				navActivity.pushFragment(slf);
			}
		});
		return view;
	}

	@Override
	public boolean onBackPressed() {
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

	}
}
