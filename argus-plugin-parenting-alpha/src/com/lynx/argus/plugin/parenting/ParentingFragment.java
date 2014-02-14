package com.lynx.argus.plugin.parenting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.Toast;
import com.lynx.argus.plugin.parenting.model.ColorListViewAdapter;
import com.lynx.argus.plugin.parenting.model.ShopInfo;
import com.lynx.lib.core.LFFragment;
import com.lynx.lib.geo.entity.GeoPoint;
import com.lynx.lib.http.HttpCallback;
import com.lynx.lib.widget.HorizontalListView;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 亲子
 * 
 * @author zhufeng.liu
 * 
 * @version 13-11-22 下午4:29
 */
public class ParentingFragment extends LFFragment implements
		HorizontalListView.OnListItemClickListener {

	public static final String LM_API_PARENT_DOMAIN = "http://www.hahaertong.com";
	public static final String LM_API_PARENT_INFO = "/index.php";

	public static final int MSG_LOAD_SUCCESS = 0;
	public static final int MSG_LOAD_FAIL = 1;

	private List<ShopInfo> shopInfos = new ArrayList<ShopInfo>();

	private ColorListViewAdapter shopAdapter;
	private HorizontalListView hlvShops;

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
			switch (msg.what) {
			case ParentingFragment.MSG_LOAD_SUCCESS:
				shopAdapter.setData(shopInfos);
				break;
			case ParentingFragment.MSG_LOAD_FAIL:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		navActivity.setPopAnimation(R.animator.slide_in_left, R.animator.slide_out_right);
		navActivity.setPushAnimation(R.animator.slide_in_right, R.animator.slide_out_left);
	}

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			throws Exception {
		View view = inflater.inflate(R.layout.layout_parenting, container, false);
		if (view == null) {
			throw new Exception("页面初始化错误");
		}

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

		hlvShops = (HorizontalListView) view.findViewById(R.id.hlv_parenting_shops);

		// Create shopAdapter
		shopAdapter = new ColorListViewAdapter(navActivity, shopInfos);

		// Bind shopAdapter to listview
		if (hlvShops != null) {
			hlvShops.setAdapter(shopAdapter);
			hlvShops.registerListItemClickListener(this);
		} else {
			Log.e("chris", "HorizontalListView not found");
		}
		return view;
	}

	@Override
	public void onClick(View v, int position) {
		Toast.makeText(navActivity, "Click " + String.valueOf(position), Toast.LENGTH_SHORT).show();
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
			shopInfos.clear();
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

					shopInfos.add(shopInfo);
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
