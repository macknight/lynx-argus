package com.lynx.argus.plugin.parenting;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.lynx.argus.plugin.parenting.model.CampaignListAdapter;
import com.lynx.argus.plugin.parenting.model.CampaignListItem;
import com.lynx.lib.core.LFApplication;
import com.lynx.lib.core.LFFragment;
import com.lynx.lib.geo.GeoService;
import com.lynx.lib.http.HttpCallback;
import com.lynx.lib.http.HttpService;
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
 * @addtime 13-11-22 下午3:54
 */
public class CampaignListFragment extends LFFragment {

	private static final int MSG_LOAD_CAMP_LIST_SUCCESS = 1;
	private static final int MSG_LOAD_CAMP_LIST_FAIL = 2;

	private static final String LM_API_CAMPAIGN_LIST = "/index.php";

	private static int curPage = 1;
	private static int pageSize = 0;

	private GeoService geoService;
	private HttpService httpService;

	private List<CampaignListItem> campaignItems = new ArrayList<CampaignListItem>();
	private CampaignListAdapter adapter;
	private PullToRefreshListView prlvCampaign;

	private HttpCallback<Object> httpCallback = new HttpCallback<Object>() {
		@Override
		public void onSuccess(Object o) {
			super.onSuccess(o);
			try {
				JSONObject joResult = new JSONObject(o.toString());
				JSONArray jaResult = joResult.getJSONArray("data");
				if (jaResult == null || jaResult.length() == 0) {
					return;
				}
				campaignItems.clear();
				for (int i = 0; i < jaResult.length(); ++i) {
					try {
						JSONObject joShop = jaResult.getJSONObject(i);
						String id = joShop.getString("goods_id");
						String name = joShop.getString("goods_name");
						String shopId = joShop.getString("store_id");
						String shopName = joShop.getString("store_name");
						String price = joShop.getString("market_price");
						String snapUrl = ParentingFragment.LM_API_PARENT_DOMAIN
								+ joShop.getString("default_image");
						String startTime = joShop.getString("start_time");
						String endTime = joShop.getString("end_time");
						String place = joShop.getString("place");
						String region = joShop.getString("regions");
						CampaignListItem campaignListItem = new CampaignListItem(
								id, name, shopId, shopName, price, snapUrl,
								startTime, endTime, place, region);
						campaignItems.add(campaignListItem);
					} catch (Exception e) {

					}
				}

				JSONObject joPage = joResult.getJSONObject("page");
				curPage = joPage.getInt("curr_page");
				pageSize = joPage.getInt("page_count");
			} catch (Exception e) {
				e.printStackTrace();
			}
			handler.sendEmptyMessage(MSG_LOAD_CAMP_LIST_SUCCESS);
		}

		@Override
		public void onFailure(Throwable t, String strMsg) {
			super.onFailure(t, strMsg);
			handler.sendEmptyMessage(MSG_LOAD_CAMP_LIST_FAIL);
			Toast.makeText(navActivity, "刷新失败", Toast.LENGTH_SHORT).show();
		}
	};

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			prlvCampaign.onRefreshComplete();
			switch (msg.what) {
			case MSG_LOAD_CAMP_LIST_SUCCESS:
				adapter.setData(campaignItems);
				break;
			case MSG_LOAD_CAMP_LIST_FAIL:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		httpService = (HttpService) LFApplication.instance().service("http");
	}

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_campaignlist, container,
				false);
		prlvCampaign = (PullToRefreshListView) view
				.findViewById(R.id.prlv_campaignlist);
		adapter = new CampaignListAdapter(navActivity, campaignItems);
		prlvCampaign.getRefreshableView().setAdapter(adapter);
		Drawable drawable = getResources().getDrawable(R.drawable.ptr_refresh);
		prlvCampaign.setLoadingDrawable(drawable);

		prlvCampaign
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					@Override
					public void onRefresh() {
						getCampaignList();
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

	private void getCampaignList() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("app", "activity"));
		params.add(new BasicNameValuePair("act", "m"));
		params.add(new BasicNameValuePair("page", curPage + ""));
		String param = URLEncodedUtils.format(params, "UTF-8");
		String url = String.format("%s%s?%s",
				ParentingFragment.LM_API_PARENT_DOMAIN, LM_API_CAMPAIGN_LIST,
				param);
		httpService.get(url, null, httpCallback);
	}
}
