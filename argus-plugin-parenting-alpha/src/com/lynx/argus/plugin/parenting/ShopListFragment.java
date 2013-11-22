package com.lynx.argus.plugin.parenting;

import android.content.Intent;
import com.lynx.lib.core.LFFragment;

/**
 * Created with IntelliJ IDEA.
 * User: chris.liu
 * Date: 13-11-22
 * Time: 下午4:29
 */
public class ShopListFragment extends LFFragment {

	private static final String LM_API_PARENTING_SHOPLIST = "http://www.hahaertong.com/index" +
			".php?app=list&act=mlist&page=#page#";

	private static int page = 1;

	@Override
	public boolean onBackPressed() {
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

	}
}
