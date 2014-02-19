package com.lynx.argus.plugin.parenting;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
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
import com.lynx.lib.core.Logger;
import com.lynx.lib.http.HttpCallback;
import com.lynx.lib.widget.pageindicator.CirclePageIndicator;
import com.lynx.lib.widget.pageindicator.PageIndicator;
import com.lynx.lib.widget.pulltorefresh.PullToRefreshBase;
import com.lynx.lib.widget.pulltorefresh.PullToRefreshListView;

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

	private CampaignListAdapter adapterCampains;
	private PullToRefreshListView prlvCampaigns;

	private ViewPager viewPager;
	private PageIndicator pageIndicator;
	private HotShopListAdapter hotShopsAdapter;

	private static int curPage = 1;
	private static int pageSize = 0;

	private HttpCallback<Object> httpCallbackShops = new HttpCallback<Object>() {
		@Override
		public void onSuccess(Object s) {
			shopInfos = DataParser.parseShops(s.toString());
			handler.sendEmptyMessage(MSG_SHOP_LOAD_SUCCESS);
		}

		@Override
		public void onFailure(Throwable throwable, String s) {
			handler.sendEmptyMessage(MSG_SHOP_LOAD_FAIL);
		}
	};

	private HttpCallback<Object> httpCallbackCampaigns = new HttpCallback<Object>() {
		@Override
		public void onSuccess(Object o) {
			campaignInfos = DataParser.parseCampaign(o.toString());
			handler.sendEmptyMessage(MSG_CAMPAIN_LOAD_SUCCESS);
		}

		@Override
		public void onFailure(Throwable t, String strMsg) {
			handler.sendEmptyMessage(MSG_CAMPAIN_LOAD_FAIL);
			Toast.makeText(navActivity, "刷新失败", Toast.LENGTH_SHORT).show();
		}
	};

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			prlvCampaigns.onRefreshComplete();
			switch (msg.what) {
			case MSG_SHOP_LOAD_SUCCESS:
				hotShopsAdapter.setData(shopInfos);
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

		navActivity.setPopAnimation(R.anim.slide_in_left, R.anim.slide_out_right);
		navActivity.setPushAnimation(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			throws Exception {
		View view = inflater.inflate(R.layout.layout_parenting, container, false);
		if (view == null) {
			throw new Exception("页面初始化错误");
		}

		hotShopsAdapter = new HotShopListAdapter(getChildFragmentManager(), shopInfos);
		viewPager = (ViewPager) view.findViewById(R.id.vp_hot_shops);
		viewPager.setAdapter(hotShopsAdapter);

		CirclePageIndicator indicator = (CirclePageIndicator) view.findViewById(R.id.cpi_hot_shops);
		indicator.setViewPager(viewPager);

		final float density = getResources().getDisplayMetrics().density;
		indicator.setBackgroundColor(0x00CCCCCC);
		indicator.setRadius(3 * density);
		indicator.setFillColor(0xCCCCCCCC);
		indicator.setStrokeColor(0xFF000000);
		indicator.setStrokeWidth(0);

		pageIndicator = indicator;

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
						// CampaignDetailFragment cdf = new CampaignDetailFragment();
						ShopDetailFragment sdf = new ShopDetailFragment();
						Bundle param = new Bundle();
						param.putSerializable("ShopInfo", shopInfos.get(position));
						sdf.setArguments(param);
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

	@Override
	public void onDetach() {
		super.onDetach();

		try {
			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
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
