package com.lynx.argus.plugin.parenting;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.lynx.argus.plugin.parenting.model.ShopInfo;
import com.lynx.argus.plugin.parenting.model.ShopListAdapter;
import com.lynx.lib.core.LFFragment;
import com.lynx.lib.geo.entity.GeoPoint;
import com.lynx.lib.http.HttpCallback;
import com.lynx.lib.widget.pulltorefresh.PullToRefreshBase;
import com.lynx.lib.widget.pulltorefresh.PullToRefreshListView;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-11-22 下午4:29
 */
public class ShopListFragment extends LFFragment {

	private ShopListAdapter adapter;
	private List<ShopInfo> shopList = new ArrayList<ShopInfo>();
	private PullToRefreshListView prlvShoplist;

	private static int curPage = 1;
	private static int pageSize = 0;

	private HttpCallback<Object> httpCallback = new HttpCallback<Object>() {
		@Override
		public void onStart() {
			super.onStart();
		}

		@Override
		public void onSuccess(Object s) {
			parseShops(s.toString());
			handler.sendEmptyMessage(ParentingFragment.MSG_LOAD_SUCCESS);
		}

		@Override
		public void onFailure(Throwable throwable, String s) {
			handler.sendEmptyMessage(ParentingFragment.MSG_LOAD_FAIL);
		}
	};

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			prlvShoplist.onRefreshComplete();
			switch (msg.what) {
			case ParentingFragment.MSG_LOAD_SUCCESS:
				adapter.setData(shopList);
				break;
			case ParentingFragment.MSG_LOAD_FAIL:
				break;
			}
		}
	};

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			throws Exception {
		View view = inflater.inflate(R.layout.layout_shoplist, container, false);
		if (view == null) {
			throw new Exception("页面初始化错误");
		}

		prlvShoplist = (PullToRefreshListView) view.findViewById(R.id.prlv_shoplist);
		adapter = new ShopListAdapter(navActivity, shopList);
		prlvShoplist.getRefreshableView().setAdapter(adapter);
		Drawable drawable = getResources().getDrawable(R.drawable.ptr_refresh);
		prlvShoplist.setLoadingDrawable(drawable);
		prlvShoplist.getRefreshableView().setOnItemClickListener(
				new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						ShopDetailFragment sdf = new ShopDetailFragment();
						ShopInfo shopSnap = shopList.get(position);
						Bundle bundle = new Bundle();
						bundle.putSerializable("shopInfo", shopSnap);
						sdf.setArguments(bundle);
						navActivity.pushFragment(sdf);
					}
				});

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
				ParentingFragment.LM_API_PARENT_INFO, param);
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
				ShopInfo shopInfo = new ShopInfo();
				try {
					JSONObject joShop = jaResult.getJSONObject(i);
					shopInfo.setStoreName(joShop.getString("store_name"));
					shopInfo.setStoreId(joShop.getString("store_id"));
					shopInfo.setShopName(joShop.getString("shop_name"));
					shopInfo.setStoreId(joShop.getString("store_id"));
					shopInfo.setRegion(joShop.getString("region_name"));
					shopInfo.setSnapUrl(joShop.getString("default_image"));

					String lng = joShop.getString("map_lng");
					String lat = joShop.getString("map_lat");
					GeoPoint latlng = new GeoPoint(Double.parseDouble(lat), Double.parseDouble(lng));
					shopInfo.setLatlng(latlng);
					int reviewNum = Integer.parseInt(joShop.getString("reviews"));
					shopInfo.setReviewNum(reviewNum);

					shopList.add(shopInfo);
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
