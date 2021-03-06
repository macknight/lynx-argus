package com.lynx.argus.biz.plugin.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.*;
import com.lynx.argus.R;
import com.lynx.argus.app.BizApplication;
import com.lynx.argus.app.BizFragment;
import com.lynx.argus.biz.plugin.demo.model.ShopListAdapter;
import com.lynx.argus.biz.plugin.demo.model.ShopListItem;
import com.lynx.lib.core.Const;
import com.lynx.lib.geo.GeoService;
import com.lynx.lib.geo.LocationListener;
import com.lynx.lib.geo.entity.Address;
import com.lynx.lib.geo.entity.Coord;
import com.lynx.lib.http.HttpCallback;
import com.lynx.lib.http.HttpService;
import com.lynx.lib.widget.pulltorefresh.PullToRefreshListView;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author zhufeng.liu
 * 
 * @addtime 13-9-16 上午10:29
 */
public class ShopListFragment extends BizFragment {
	private GeoService geoService;
	private HttpService httpService;

	private List<ShopListItem> shops = new ArrayList<ShopListItem>();
	private ShopListAdapter adapter;
	private PullToRefreshListView ptrlvShop;

	private static final String BMAP_API_PLACE_SEARCH = "/search";

	private Animation animRotate;
	private AnimationDrawable adIndicator;
	private TextView tvLocAddr;
	private ImageView ivLocIndicator, ivLocRefresh;

	private String query = "美食";

	public ShopListFragment() {

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			httpService = (HttpService) BizApplication.instance().service(
					"http");
		} catch (Exception e) {
			e.printStackTrace();
		}

		query = getArguments().getString("query");

		adapter = new ShopListAdapter(tabActivity, shops);

		animRotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animRotate.setDuration(1500);
		animRotate.setRepeatCount(-1);
		animRotate.setRepeatMode(Animation.RESTART);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_local_shoplist, container,
				false);

		initLocationModule(v);

		ptrlvShop = (PullToRefreshListView) v.findViewById(R.id.prlv_shoplist);
		ptrlvShop.getRefreshableView().setAdapter(adapter);
		Drawable drawable = getResources().getDrawable(R.drawable.ptr_refresh);
		ptrlvShop.setLoadingDrawable(drawable);

		ptrlvShop
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					@Override
					public void onRefresh() {
						if (geoService == null) {
							Toast.makeText(tabActivity, "定位模块不可用",
									Toast.LENGTH_SHORT).show();
							return;
						} else if (geoService.coord() == null) {
							geoService.locate(false);
							return;
						} else {
							getLocalShop();
						}
					}
				});

		ptrlvShop.getRefreshableView().setOnItemClickListener(
				new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						ShopListItem shop = shops.get(position - 1);
						if (shop != null) {
							ShopDetailFragment sdf = new ShopDetailFragment();
							Bundle bundle = new Bundle();
							bundle.putString("uid", shop.getUid());
							try {
								Log.d("chris", shop.getUid());
							} catch (Exception e) {
								e.printStackTrace();
							}
							sdf.setArguments(bundle);
							tabActivity.pushFragment(sdf, true);
						} else {
							Toast.makeText(tabActivity, "未能正常获得商户信息",
									Toast.LENGTH_SHORT).show();
						}
					}
				});

		ImageButton ibMap = (ImageButton) v
				.findViewById(R.id.ib_local_shoplist_map);
		ibMap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(tabActivity, ShopListMapActivity.class);
				startActivity(intent);
			}
		});

		return v;
	}

	private HttpCallback<Object> httpCallback = new HttpCallback<Object>() {
		@Override
		public void onSuccess(Object o) {
			ptrlvShop.onRefreshComplete();
			super.onSuccess(o);
			try {
				JSONObject joResult = new JSONObject(o.toString());
				if (joResult.getInt("status") == 0) {
					JSONArray jaResult = joResult.getJSONArray("results");
					if (jaResult == null || jaResult.length() == 0) {
						return;
					}
					shops.clear();
					for (int i = 0; i < jaResult.length(); ++i) {
						try {
							Map<String, Object> shopInfo = new HashMap<String, Object>();
							JSONObject joShop = jaResult.getJSONObject(i);

							String name = joShop.getString("name");

							String addr = "";
							try {
								addr = joShop.getString("address");
							} catch (Exception e) {
							}

							String tele = "";
							try {
								tele = joShop.getString("telephone");
							} catch (Exception e) {

							}

							String uid = joShop.getString("uid");
							ShopListItem shop = new ShopListItem(uid, name,
									addr, tele);
							shops.add(shop);
						} catch (Exception e) {

						}
					}
					adapter.setData(shops);
				} else {
					Toast.makeText(tabActivity, "刷新失败", Toast.LENGTH_SHORT)
							.show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onFailure(Throwable t, String strMsg) {
			super.onFailure(t, strMsg);
			ptrlvShop.onRefreshComplete();
			Toast.makeText(tabActivity, "刷新失败", Toast.LENGTH_SHORT).show();
		}
	};

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				ivLocIndicator.setBackgroundResource(R.anim.anim_indicator);
				adIndicator = (AnimationDrawable) ivLocIndicator
						.getBackground();
				adIndicator.setOneShot(false);
				if (adIndicator.isRunning()) {
					adIndicator.stop();
				}
				adIndicator.start();
				animRotate.startNow();
				tvLocAddr.setText("正在定位...");
				break;
			case 1: // 定位成功
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
					tip = String
							.format("%s,%s", coord.getLat(), coord.getLng());
				} else {
					tip = addr.getStreet();
				}
				tvLocAddr.setText(tip);
				getLocalShop();
				break;
			case 2:
				if (adIndicator != null) {
					adIndicator.stop();
				}
				if (animRotate != null) {
					animRotate.cancel();
				}
				ivLocIndicator.setBackgroundResource(R.drawable.gray_point);
				tvLocAddr.setText("定位失败");
				if (ptrlvShop.isRefreshing()) {
					ptrlvShop.onRefreshComplete();
				}
				break;
			}
		}
	};

	/**
	 * 初始化定位相关模块
	 */
	private void initLocationModule(View v) {
		geoService = (GeoService) BizApplication.instance().service("geo");

		ivLocIndicator = (ImageView) v.findViewById(R.id.iv_loc_indicator);
		tvLocAddr = (TextView) v.findViewById(R.id.tv_loc_addr);
		ivLocRefresh = (ImageView) v.findViewById(R.id.iv_loc_refresh);
		ivLocRefresh.setAnimation(animRotate);
		animRotate.cancel();

		if (geoService == null) {
			Toast.makeText(this.getActivity(), "定位模块不可用", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		if (geoService.coord() != null) {
			ivLocIndicator.setBackgroundResource(R.drawable.green_point);
			if (geoService.address() != null) {
				tvLocAddr.setText(geoService.address().getStreet());
			} else {
				String tip = String.format("%s,%s",
						geoService.coord().getLat(), geoService.coord()
								.getLng());
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
					handler.sendEmptyMessage(0);
					break;
				case SUCCESS:
					handler.sendEmptyMessage(1);
					break;
				case FAIL:
					handler.sendEmptyMessage(2);
					break;
				}
			}
		});
	}

	private void getLocalShop() {
		try {
			if (geoService == null) {
				Toast.makeText(tabActivity, "定位模块不可用", Toast.LENGTH_SHORT)
						.show();
				return;
			} else if (geoService.coord() == null) {
				geoService.locate(false);
				return;
			} else {
				double lat = geoService.coord().getLat();
				double lng = geoService.coord().getLng();
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("ak", Const.BMAP_API_KEY));
				params.add(new BasicNameValuePair("output", "json"));
				params.add(new BasicNameValuePair("query", query));
				params.add(new BasicNameValuePair("page_size", 20 + ""));
				params.add(new BasicNameValuePair("location", lat + "," + lng));
				params.add(new BasicNameValuePair("radius", 5000 + ""));
				String param = URLEncodedUtils.format(params, "UTF-8");
				String url = String.format("%s%s?%s", Const.BMAP_API_PLACE,
						BMAP_API_PLACE_SEARCH, param);
				httpService.get(url, null, httpCallback);

				ptrlvShop.setRefreshing();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}