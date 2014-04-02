package com.lynx.argus.plugin.local;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lynx.argus.plugin.local.model.ShopInfo;
import com.lynx.lib.core.LFApplication;
import com.lynx.lib.core.LFConst;
import com.lynx.lib.core.LFFragment;
import com.lynx.lib.http.HttpCallback;
import com.lynx.lib.http.HttpService;
import com.lynx.lib.widget.pulltorefresh.PullToRefreshListView;

/**
 * @author chris.liu
 * @version 4/2/14 7:03 PM
 */
public class ShopTuanListFragment extends LFFragment {

	private static final String BMAP_API_PLACE_SHOP_TUAN = "/eventdetail";

	private HttpService httpService;
	private Gson gson;
	private PullToRefreshListView prlvShopTuan;
	private ShopInfo shopInfo;

	public ShopTuanListFragment() {
		httpService = (HttpService) LFApplication.instance().service("http");
		gson = LFApplication.instance().gson();
	}

	@Override
	protected View onLoadView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) throws Exception {
		View view = inflater.inflate(R.layout.layout_shop_tuanlist, container, false);
		if (view == null) {
			throw new Exception("页面初始化错误");
		}

		shopInfo = getArguments().getParcelable("shop_info");
		prlvShopTuan = (PullToRefreshListView) view.findViewById(R.id.prlv_shop_tuan);
		return view;
	}

	private HttpCallback httpCallback = new HttpCallback() {
		@Override
		public void onSuccess(Object o) {
			try {
				JSONObject joResult = new JSONObject(o.toString());
				if (joResult.getInt("status") == 0) {
					JSONObject joShop = joResult.getJSONObject("result");
					ShopInfo info = gson.fromJson(joShop.toString(), ShopInfo.class);
					shopInfo.detailInfo = info.detailInfo;

					updateUI();
				} else {
					Toast.makeText(navActivity, "获取商家信息失败", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {

			}

		}

		@Override
		public void onFailure(Throwable t, String strMsg) {
			super.onFailure(t, strMsg);
		}
	};

	private void getShopTuan() {
		if (shopInfo != null && !TextUtils.isEmpty(shopInfo.uid)) {
			if (shopInfo.detailInfo != null) {
				updateUI();
			} else {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("ak", LFConst.BMAP_API_KEY));
				params.add(new BasicNameValuePair("output", "json"));
				params.add(new BasicNameValuePair("uid", shopInfo.uid));
				String param = URLEncodedUtils.format(params, "UTF-8");
				String url = String.format("%s%s?%s", LFConst.BMAP_API_PLACE,
						BMAP_API_PLACE_SHOP_TUAN, param);
				httpService.get(url, null, httpCallback);
			}
		}

	}

	private void updateUI() {

	}
}
