package com.lynx.argus.plugin.parenting;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import com.lynx.argus.plugin.parenting.model.CampaignInfo;
import com.lynx.argus.plugin.parenting.model.CampaignListAdapter;
import com.lynx.lib.core.LFFragment;
import com.lynx.lib.http.HttpCallback;
import com.lynx.lib.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener;
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
 * @version 13-11-22 下午3:54
 */
public class CampaignListFragment extends LFFragment {

	private static int curPage = 1;
	private static int pageSize = 0;

	private List<CampaignInfo> campaigns = new ArrayList<CampaignInfo>();
	private CampaignListAdapter adapter;
	private PullToRefreshListView prlvCampaign;

	private HttpCallback<Object> httpCallback = new HttpCallback<Object>() {
		@Override
		public void onSuccess(Object o) {
			super.onSuccess(o);
			parseCampaign(o.toString());
			handler.sendEmptyMessage(ParentingFragment.MSG_LOAD_SUCCESS);
		}

		@Override
		public void onFailure(Throwable t, String strMsg) {
			super.onFailure(t, strMsg);
			handler.sendEmptyMessage(ParentingFragment.MSG_LOAD_FAIL);
			Toast.makeText(navActivity, "刷新失败", Toast.LENGTH_SHORT).show();
		}
	};

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			prlvCampaign.onRefreshComplete();
			switch (msg.what) {
			case ParentingFragment.MSG_LOAD_SUCCESS:
				adapter.setData(campaigns);
				break;
			case ParentingFragment.MSG_LOAD_FAIL:
				break;
			}
		}
	};

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			throws Exception {
		View view = inflater.inflate(R.layout.layout_campaignlist, container, false);
		if (view == null) {
			throw new Exception("页面初始化错误");
		}

		prlvCampaign = (PullToRefreshListView) view.findViewById(R.id.prlv_campaignlist);
		adapter = new CampaignListAdapter(navActivity, campaigns);
		prlvCampaign.getRefreshableView().setAdapter(adapter);
		Drawable drawable = getResources().getDrawable(R.drawable.ptr_refresh);
		prlvCampaign.setLoadingDrawable(drawable);
		prlvCampaign.getRefreshableView().setOnItemClickListener(
				new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

					}
				});
		prlvCampaign.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				getCampaigns();
			}
		});

		return view;
	}

	private void getCampaigns() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("app", "activity"));
		params.add(new BasicNameValuePair("act", "m"));
		params.add(new BasicNameValuePair("page", curPage + ""));
		String param = URLEncodedUtils.format(params, "UTF-8");
		String url = String.format("%s%s?%s", ParentingFragment.LM_API_PARENT_DOMAIN,
				ParentingFragment.LM_API_PARENT_INFO, param);
		httpService.get(url, null, httpCallback);
	}

	private void parseCampaign(String data) {
		try {
			JSONObject joResult = new JSONObject(data);
			JSONArray jaResult = joResult.getJSONArray("data");
			if (jaResult == null || jaResult.length() == 0) {
				return;
			}
			campaigns.clear();
			for (int i = 0; i < jaResult.length(); ++i) {
				try {
					JSONObject joShop = jaResult.getJSONObject(i);
					CampaignInfo campaignInfo = new CampaignInfo();
					campaignInfo.setId(joShop.getString("goods_id"));
					campaignInfo.setName(joShop.getString("goods_name"));
					campaignInfo.setStoreId(joShop.getString("store_id"));
					campaignInfo.setStoreName(joShop.getString("store_name"));
					campaignInfo.setMarketPrice(joShop.getString("market_price"));
					campaignInfo.setSnapUrl(joShop.getString("default_image"));
					campaignInfo.setStartTime(joShop.getString("start_time"));
					campaignInfo.setEndTime(joShop.getString("end_time"));
					campaignInfo.setPlace(joShop.getString("place"));
					campaignInfo.setRegion(joShop.getString("regions"));
					campaigns.add(campaignInfo);
				} catch (Exception e) {

				}
			}

			JSONObject joPage = joResult.getJSONObject("page");
			curPage = joPage.getInt("curr_page");
			pageSize = joPage.getInt("page_count");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
