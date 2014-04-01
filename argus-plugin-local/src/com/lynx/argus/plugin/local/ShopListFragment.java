package com.lynx.argus.plugin.local;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Intent;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.*;

import com.google.gson.Gson;
import com.lynx.argus.plugin.local.model.ShopInfo;
import com.lynx.argus.plugin.local.adapter.ShopListAdapter;
import com.lynx.argus.plugin.local.adapter.ShopSearchAdapter;
import com.lynx.lib.core.LFApplication;
import com.lynx.lib.core.LFConst;
import com.lynx.lib.core.LFFragment;
import com.lynx.lib.geo.GeoService;
import com.lynx.lib.geo.LocationListener;
import com.lynx.lib.geo.entity.Address;
import com.lynx.lib.geo.entity.Coord;
import com.lynx.lib.http.HttpCallback;
import com.lynx.lib.widget.pulltorefresh.PullToRefreshGridView;
import com.lynx.lib.widget.pulltorefresh.PullToRefreshListView;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-9-16 上午10:29
 */
public class ShopListFragment extends LFFragment {

	public static final int MSG_LOCATION_ONGOING = 0;
	public static final int MSG_LOCATION_SUCCESS = 1;
	public static final int MSG_LOCATION_FAIL = 2;
	public static final int MSG_LOAD_SHOP_LIST_FIN = 3;

	private GeoService geoService;
    private Gson gson;

    private List<ShopInfo> shopInfos = new LinkedList<ShopInfo>();

	private ShopListAdapter adapter;
	private PullToRefreshGridView prgvShop;

	private static final String BMAP_API_PLACE_SEARCH = "/search";

	private Animation animRotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f,
			Animation.RELATIVE_TO_SELF, 0.5f);
	private AnimationDrawable adIndicator;
	private TextView tvLocAddr;
	private ImageView ivLocIndicator, ivLocRefresh;
	private EditText etSearch;
	private PopupWindow popupWindow;
	private ListView lvShopSearch;

	private String query = "美食";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		navActivity.setPopAnimation(R.anim.slide_in_left, R.anim.slide_out_right);
		navActivity.setPushAnimation(R.anim.slide_in_right, R.anim.slide_out_left);

		animRotate.setDuration(1500);
		animRotate.setRepeatCount(-1);
		animRotate.setRepeatMode(Animation.RESTART);


        geoService = (GeoService)LFApplication.instance().service("geo");
        gson = LFApplication.instance().gson();
	}

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			throws Exception {
		View view = inflater.inflate(R.layout.layout_shoplist, container, false);
		if (view == null) {
			throw new Exception("页面初始化错误");
		}

		initLocationModule(view);
		searchPopWindowInit();

		prgvShop = (PullToRefreshGridView) view.findViewById(R.id.prgv_shoplist);
		adapter = new ShopListAdapter(navActivity, shopInfos);
		prgvShop.getRefreshableView().setAdapter(adapter);
		Drawable drawable = getResources().getDrawable(R.drawable.ptr_refresh);
		prgvShop.setLoadingDrawable(drawable);

		prgvShop.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			@Override
			public void onRefresh() {
				if (geoService == null) {
					Toast.makeText(navActivity, "定位模块不可用", Toast.LENGTH_SHORT).show();
					return;
				} else if (geoService.coord() == null) {
					geoService.locate(false);
					return;
				} else {
					getLocalShop();
				}
			}
		});

		prgvShop.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ShopInfo shopInfo = shopInfos.get(position);
				if (shopInfo != null) {
					ShopDetailFragment sdf = new ShopDetailFragment();
					Bundle bundle = new Bundle();
                    bundle.putParcelable("shop_info", shopInfo);
					sdf.setArguments(bundle);
					navActivity.pushFragment(sdf);
				} else {
					Toast.makeText(navActivity, "未能正常获得商户信息", Toast.LENGTH_SHORT).show();
				}
			}
		});

		etSearch = (EditText) view.findViewById(R.id.et_shop_detail_search);
		etSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showSearchWindow();
			}
		});
		etSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// 当输入框获取焦点时弹出选项窗，失去焦点时取消选项窗
				if (!hasFocus) {
					dismissSearchWindow();
				}
			}
		});
		return view;
	}

	private HttpCallback<Object> httpCallback = new HttpCallback<Object>() {
		@Override
		public void onSuccess(Object o) {
			prgvShop.onRefreshComplete();
			super.onSuccess(o);
			try {
				JSONObject joResult = new JSONObject(o.toString());
				if (joResult.getInt("status") == 0) {
					JSONArray jaResult = joResult.getJSONArray("results");
					if (jaResult == null || jaResult.length() == 0) {
						return;
					}
					shopInfos.clear();
					for (int i = 0; i < jaResult.length(); ++i) {
						try {
                            ShopInfo shopInfo = gson.fromJson(jaResult.getJSONObject(i).toString(), ShopInfo.class);
                            if (shopInfo != null) {
                                shopInfos.add(shopInfo);
                            }
						} catch (Exception e) {

						}
					}
					handler.sendEmptyMessage(MSG_LOAD_SHOP_LIST_FIN);
				} else {
					Toast.makeText(navActivity, "刷新失败", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onFailure(Throwable t, String strMsg) {
			super.onFailure(t, strMsg);
			prgvShop.onRefreshComplete();
			Toast.makeText(navActivity, "刷新失败", Toast.LENGTH_SHORT).show();
		}
	};

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_LOCATION_ONGOING:
				ivLocIndicator.setBackgroundResource(R.anim.anim_indicator);
				adIndicator = (AnimationDrawable) ivLocIndicator.getBackground();
				adIndicator.setOneShot(false);
				if (adIndicator.isRunning()) {
					adIndicator.stop();
				}
				adIndicator.start();
				animRotate.startNow();
				tvLocAddr.setText("正在定位...");
				break;
			case MSG_LOCATION_SUCCESS: // 定位成功
				if (adIndicator != null) {
					adIndicator.stop();
				}
				if (animRotate != null) {
					animRotate.cancel();
				}
				ivLocIndicator.setBackgroundResource(R.drawable.green_point);
				Address addr = geoService.address();
				Coord coord = geoService.coord();
				String tip = "";
				if (addr == null) {
					tip = String.format("%s,%s", coord.lat(), coord.lng());
				} else {
					tip = addr.street();
				}
				tvLocAddr.setText(tip);
				getLocalShop();
				break;
			case MSG_LOCATION_FAIL:
				if (adIndicator != null) {
					adIndicator.stop();
				}
				if (animRotate != null) {
					animRotate.cancel();
				}
				ivLocIndicator.setBackgroundResource(R.drawable.gray_point);
				tvLocAddr.setText("定位失败");
				if (prgvShop.isRefreshing()) {
					prgvShop.onRefreshComplete();
				}
                getLocalShopList(0, 0);
				break;
			case MSG_LOAD_SHOP_LIST_FIN:
				adapter.setData(shopInfos);
				break;
			}
		}
	};

	/**
	 * 初始化定位相关模块
	 */
	private void initLocationModule(View v) {
		ivLocIndicator = (ImageView) v.findViewById(R.id.iv_loc_indicator);
		tvLocAddr = (TextView) v.findViewById(R.id.tv_loc_addr);
		ivLocRefresh = (ImageView) v.findViewById(R.id.iv_loc_refresh);
		ivLocRefresh.setAnimation(animRotate);
		animRotate.cancel();

		geoService = (GeoService) LFApplication.instance().service("geo");
		if (geoService == null) {
			Toast.makeText(this.getActivity(), "定位模块不可用", Toast.LENGTH_SHORT).show();
			return;
		}

		if (geoService.coord() != null) {
			ivLocIndicator.setBackgroundResource(R.drawable.green_point);
			if (geoService.address() != null) {
				tvLocAddr.setText(geoService.address().street());
			} else {
				String tip = String.format("%s,%s", geoService.coord().lat(), geoService.coord()
						.lng());
				tvLocAddr.setText(tip);
			}
		}

		ivLocRefresh.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				geoService.locate(false);
			}
		});

		geoService.addListener(new LocationListener() {
			@Override
			public void onLocationChanged(GeoService.LocationStatus status) {
				switch (status) {
				case ONGOING:
					handler.sendEmptyMessage(MSG_LOCATION_ONGOING);
					break;
				case SUCCESS:
					handler.sendEmptyMessage(MSG_LOCATION_SUCCESS);
					break;
				case FAIL:
					handler.sendEmptyMessage(MSG_LOCATION_FAIL);
					break;
				}
			}
		});
	}

	private void getLocalShop() {
		try {
			if (geoService == null) {
				Toast.makeText(navActivity, "定位模块不可用", Toast.LENGTH_SHORT).show();
				return;
			} else if (geoService.coord() == null) {
				geoService.locate(false);
				return;
			} else {
				double lat = geoService.coord().lat();
				double lng = geoService.coord().lng();
				getLocalShopList(lat, lng);
				prgvShop.setRefreshing();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    private void getLocalShopList(double lat, double lng) {
        lat = 31.220435;
        lng = 121.418468;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("ak", LFConst.BMAP_API_KEY));
        params.add(new BasicNameValuePair("output", "json"));
        params.add(new BasicNameValuePair("query", query));
        params.add(new BasicNameValuePair("page_size", 20 + ""));
        params.add(new BasicNameValuePair("location", lat + "," + lng));
        params.add(new BasicNameValuePair("radius", 5000 + ""));
        String param = URLEncodedUtils.format(params, "UTF-8");
        String url = String.format("%s%s?%s", LFConst.BMAP_API_PLACE,
                BMAP_API_PLACE_SEARCH, param);
        httpService.get(url, null, httpCallback);
    }

    private void searchPopWindowInit() {
		// 获取要显示在PopupWindow上的窗体视图
		LayoutInflater inflater = navActivity.getLayoutInflater();
		View view = inflater.inflate(R.layout.layout_shop_search, null);
		// 实例化并且设置PopupWindow显示的视图
		popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		// 获取PopupWindow中的控件
		lvShopSearch = (ListView) view.findViewById(R.id.lv_local_shop_search);

		ShopSearchAdapter adapter = new ShopSearchAdapter(navActivity, shopInfos);
		lvShopSearch.setAdapter(adapter);

		// 想要让PopupWindow中的控件能够使用，就必须设置PopupWindow为focusable
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popout_list_bg));

		// 设置ListView点击事件
		lvShopSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ShopInfo shop = (ShopInfo) lvShopSearch.getItemAtPosition(position);
				etSearch.setText(shop.name);
				dismissSearchWindow();
			}
		});
	}

	private void showSearchWindow() {
		// 显示PopupWindow有3个方法
		// popupWindow.showAsDropDown(anchor)
		// popupWindow.showAsDropDown(anchor, xoff, yoff)
		// popupWindow.showAtLocation(parent, gravity, x, y)
		// 需要注意的是以上三个方法必须在触发事件中使用，比如在点击某个按钮的时候
		popupWindow.showAsDropDown(etSearch, 0, 0);
	}

	// 让PopupWindow消失
	private void dismissSearchWindow() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
	}

}
