package com.lynx.argus.plugin.parenting;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lynx.argus.plugin.parenting.model.ShopListAdapter;
import com.lynx.argus.plugin.parenting.model.ShopListItem;
import com.lynx.lib.core.LFApplication;
import com.lynx.lib.core.LFFragment;
import com.lynx.lib.geo.entity.GeoPoint;
import com.lynx.lib.http.HttpCallback;
import com.lynx.lib.http.HttpService;
import com.lynx.lib.widget.pulltorefresh.PullToRefreshBase;
import com.lynx.lib.widget.pulltorefresh.PullToRefreshListView;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-11-22 下午4:29
 */
public class ShopListFragment extends LFFragment {

	private static final String LM_API_PARENTING_SHOPLIST = "/index.php";

	private static final int MSG_LOAD_SHOP_LIST_SUCCESS = 1;
	private static final int MSG_LOAD_SHOP_LIST_FAIL = 2;

	private ShopListAdapter adapter;
	private List<ShopListItem> shopList = new ArrayList<ShopListItem>();
	private PullToRefreshListView prlvShoplist;

	private static int curPage = 1;
	private static int pageSize = 0;

	private HttpService httpService;

	private HttpCallback<Object> httpCallback = new HttpCallback<Object>() {
		@Override
		public void onStart() {
			super.onStart();
		}

		@Override
		public void onSuccess(Object s) {
			parseShops(s.toString());
			handler.sendEmptyMessage(MSG_LOAD_SHOP_LIST_SUCCESS);
		}

		@Override
		public void onFailure(Throwable throwable, String s) {
			handler.sendEmptyMessage(MSG_LOAD_SHOP_LIST_FAIL);
		}
	};

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			prlvShoplist.onRefreshComplete();
			switch (msg.what) {
			case MSG_LOAD_SHOP_LIST_SUCCESS:
				adapter.setData(shopList);
				break;
			case MSG_LOAD_SHOP_LIST_FAIL:
				break;
			}
		}
	};

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			throws Exception {
		httpService = (HttpService) LFApplication.instance().service("http");
		View view = inflater.inflate(R.layout.layout_shoplist, container, false);
		if (view == null) {
			throw new Exception("页面初始化错误");
		}

		prlvShoplist = (PullToRefreshListView) view.findViewById(R.id.prlv_shoplist);
		adapter = new ShopListAdapter(navActivity, shopList);
		prlvShoplist.getRefreshableView().setAdapter(adapter);
		Drawable drawable = getResources().getDrawable(R.drawable.ptr_refresh);
		prlvShoplist.setLoadingDrawable(drawable);

		prlvShoplist.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
			@Override
			public void onRefresh() {
				getShops();
			}
		});

		return view;
	}

	private void getShops() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("app", "list"));
		params.add(new BasicNameValuePair("act", "mlist"));
		params.add(new BasicNameValuePair("page", curPage + ""));
		String param = URLEncodedUtils.format(params, "UTF-8");
		String url = String.format("%s%s?%s", ParentingFragment.LM_API_PARENT_DOMAIN,
				LM_API_PARENTING_SHOPLIST, param);
		httpService.get(url, null, httpCallback);
	}

	private void parseShops(String data) {
		try {
			JSONObject joResult = new JSONObject(data);
			JSONArray jaResult = joResult.getJSONArray("data");
			if (jaResult == null || jaResult.length() == 0) {
				return;
			}
			shopList.clear();
			for (int i = 0; i < jaResult.length(); ++i) {
				try {
					JSONObject joShop = jaResult.getJSONObject(i);
					String storeName = joShop.getString("store_name");
					String storeId = joShop.getString("store_id");
					String shopName = joShop.getString("shop_name");
					String shopId = joShop.getString("store_id");
					String snapUrl = String.format("%s/%s", ParentingFragment.LM_API_PARENT_DOMAIN,
							joShop.getString("default_image"));
					String lng = joShop.getString("map_lng");
					String lat = joShop.getString("map_lat");
					GeoPoint latlng = new GeoPoint(Double.parseDouble(lat), Double.parseDouble(lng));
					String region = joShop.getString("region_name");
					int reviewNum = Integer.parseInt(joShop.getString("reviews"));
					ShopListItem shopListItem = new ShopListItem(storeId, storeName, shopId,
							shopName, snapUrl, latlng, region, reviewNum);
					shopList.add(shopListItem);
				} catch (Exception e) {

				}
			}

			JSONObject joPage = joResult.getJSONObject("page");
			curPage = joPage.getInt("curr_page");
			pageSize = joPage.getInt("page_count");
		} catch (Exception e) {

		}
	}
}
