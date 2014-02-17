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
import com.lynx.argus.plugin.parenting.model.HotShopListAdapter;
import com.lynx.argus.plugin.parenting.model.ShopInfo;
import com.lynx.argus.plugin.parenting.util.DataParser;
import com.lynx.lib.core.LFFragment;
import com.lynx.lib.http.HttpCallback;
import com.lynx.lib.widget.HorizontalListView;
import com.lynx.lib.widget.pulltorefresh.PullToRefreshBase;
import com.lynx.lib.widget.pulltorefresh.PullToRefreshListView;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * 亲子
 * 
 * @author zhufeng.liu
 * 
 * @version 13-11-22 下午4:29
 */
public class ParentingFragment extends LFFragment {

	public static final String LM_API_PARENT_DOMAIN = "http://www.hahaertong.com";
	public static final String LM_API_PARENT_INFO = "/index.php";

	public static final int MSG_SHOP_LOAD_SUCCESS = 0;
	public static final int MSG_SHOP_LOAD_FAIL = 1;
	public static final int MSG_CAMPAIN_LOAD_SUCCESS = 2;
	public static final int MSG_CAMPAIN_LOAD_FAIL = 3;

	private List<ShopInfo> shopInfos = new ArrayList<ShopInfo>();
	private List<CampaignInfo> campaignInfos = new ArrayList<CampaignInfo>();

	private HotShopListAdapter adapterShops;
	private HorizontalListView hlvShops;

	private CampaignListAdapter adapterCampains;
	private PullToRefreshListView prlvCampaigns;

	private static int curPage = 1;
	private static int pageSize = 0;

	private HttpCallback<Object> httpCallbackShops = new HttpCallback<Object>() {
		@Override
		public void onSuccess(Object s) {
			shopInfos = DataParser.parseShops(s.toString());
			handler.sendEmptyMessage(ParentingFragment.MSG_SHOP_LOAD_SUCCESS);
		}

		@Override
		public void onFailure(Throwable throwable, String s) {
			handler.sendEmptyMessage(ParentingFragment.MSG_SHOP_LOAD_FAIL);
		}
	};

	private HttpCallback<Object> httpCallbackCampaigns = new HttpCallback<Object>() {
		@Override
		public void onSuccess(Object o) {
			campaignInfos = DataParser.parseCampaign(o.toString());
			handler.sendEmptyMessage(ParentingFragment.MSG_CAMPAIN_LOAD_SUCCESS);
		}

		@Override
		public void onFailure(Throwable t, String strMsg) {
			handler.sendEmptyMessage(ParentingFragment.MSG_CAMPAIN_LOAD_FAIL);
			Toast.makeText(navActivity, "刷新失败", Toast.LENGTH_SHORT).show();
		}
	};

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			prlvCampaigns.onRefreshComplete();
			switch (msg.what) {
			case MSG_SHOP_LOAD_SUCCESS:
				adapterShops.setData(shopInfos);
				break;
			case MSG_SHOP_LOAD_FAIL:
				break;
			case MSG_CAMPAIN_LOAD_SUCCESS:
				adapterCampains.setData(campaignInfos);
				break;
			case MSG_CAMPAIN_LOAD_FAIL:
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

		hlvShops = (HorizontalListView) view.findViewById(R.id.hlv_parenting_hot_shops);
		adapterShops = new HotShopListAdapter(navActivity, shopInfos);
		hlvShops.setAdapter(adapterShops);
		getShops();

		prlvCampaigns = (PullToRefreshListView) view.findViewById(R.id.prlv_hot_campaigns);
		adapterCampains = new CampaignListAdapter(navActivity, campaignInfos);
		prlvCampaigns.getRefreshableView().setAdapter(adapterCampains);
		Drawable drawable = getResources().getDrawable(R.drawable.ptr_refresh);
		prlvCampaigns.setLoadingDrawable(drawable);
		prlvCampaigns.getRefreshableView().setOnItemClickListener(
				new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						ShopDetailFragment sdf = new ShopDetailFragment();
						navActivity.pushFragment(sdf);
					}
				});
		prlvCampaigns.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
			@Override
			public void onRefresh() {
				getCampaigns();
			}
		});
		getCampaigns();

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
		httpService.get(url, httpCallbackShops);
	}

	private void getCampaigns() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("app", "activity"));
		params.add(new BasicNameValuePair("act", "m"));
		params.add(new BasicNameValuePair("page", curPage + ""));
		String param = URLEncodedUtils.format(params, "UTF-8");
		String url = String.format("%s%s?%s", ParentingFragment.LM_API_PARENT_DOMAIN,
				ParentingFragment.LM_API_PARENT_INFO, param);
		httpService.get(url, httpCallbackCampaigns);
	}
}
