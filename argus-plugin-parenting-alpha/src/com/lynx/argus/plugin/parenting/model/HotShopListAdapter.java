package com.lynx.argus.plugin.parenting.model;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lynx.argus.plugin.parenting.HotShopFragment;

/**
 * @author zhufeng.liu
 * @version 14-2-13 下午5:08
 */
public class HotShopListAdapter extends FragmentPagerAdapter {
	private List<ShopInfo> data;

	public HotShopListAdapter(FragmentManager fm, List<ShopInfo> data) {
		super(fm);
		this.data = data;
	}

	@Override
	public Fragment getItem(int position) {
		return HotShopFragment.newInstance(data.get(position));
	}

	@Override
	public int getCount() {
		return data.size();
	}

    public void setData(List<ShopInfo> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}
