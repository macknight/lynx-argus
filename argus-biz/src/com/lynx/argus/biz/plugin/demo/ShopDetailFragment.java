package com.lynx.argus.biz.plugin.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.lynx.argus.R;
import com.lynx.argus.app.BizApplication;
import com.lynx.argus.app.BizFragment;
import com.lynx.lib.core.Const;
import com.lynx.lib.http.HttpCallback;
import com.lynx.lib.http.HttpService;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-16 下午2:00
 */
public class ShopDetailFragment extends BizFragment {
	private HttpService httpService;
	private String uid;

	private TextView tvName, tvAddr, tvTele, tvPrice, tvTags, tvShopHours;
	private RatingBar rbOverall, rbTaste, rbService, rbEnv;

	private static final String BMAP_API_PLACE_DETAIL = "/detail";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			httpService = (HttpService) BizApplication.instance().service("http");
		} catch (Exception e) {
			e.printStackTrace();
		}

		uid = getArguments().getString("uid");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("ak", Const.BMAP_API_KEY));
		params.add(new BasicNameValuePair("output", "json"));
		params.add(new BasicNameValuePair("uid", uid));
		params.add(new BasicNameValuePair("scope", 2 + ""));
		String param = URLEncodedUtils.format(params, "UTF-8");
		String url = String.format("%s%s?%s", Const.BMAP_API_PLACE,
				                          BMAP_API_PLACE_DETAIL, param);
		httpService.get(url, null, httpCallback);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_local_shop_detail, container, false);

		initUI(v);

		return v;
	}


	private HttpCallback<Object> httpCallback = new HttpCallback<Object>() {
		@Override
		public void onSuccess(Object o) {
			super.onSuccess(o);
			try {
				JSONObject joResult = new JSONObject(o.toString());
				if (joResult.getInt("status") == 0) {
					JSONObject joShop = joResult.getJSONObject("result");
					updateUI(joShop);
				} else {
					Toast.makeText(tabActivity, "获取商家信息失败", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {

			}

		}

		@Override
		public void onFailure(Throwable t, String strMsg) {
			super.onFailure(t, strMsg);
		}
	};


	private void initUI(View v) {
		tvName = (TextView) v.findViewById(R.id.tv_shop_detail_name);
		tvAddr = (TextView) v.findViewById(R.id.tv_shop_detail_addr);
		tvTele = (TextView) v.findViewById(R.id.tv_shop_detail_tele);
		tvPrice = (TextView) v.findViewById(R.id.tv_shop_detail_price);
		tvTags = (TextView) v.findViewById(R.id.tv_shop_detail_tags);
		tvShopHours = (TextView) v.findViewById(R.id.tv_shop_detail_shop_hours);
		rbOverall = (RatingBar) v.findViewById(R.id.rb_shop_detail_overall);
		rbOverall.setClickable(false);
		rbOverall.setEnabled(false);
		rbTaste = (RatingBar) v.findViewById(R.id.rb_shop_detail_taste);
		rbTaste.setClickable(false);
		rbTaste.setEnabled(false);
		rbService = (RatingBar) v.findViewById(R.id.rb_shop_detail_service);
		rbService.setClickable(false);
		rbService.setEnabled(false);
		rbEnv = (RatingBar) v.findViewById(R.id.rb_shop_detail_env);
		rbEnv.setClickable(false);
		rbEnv.setEnabled(false);
	}

	private void updateUI(JSONObject joShop) {
		try {
			String name = "暂未收录";
			try {
				name = joShop.getString("name");
			} catch (Exception e) {
			}
			tvName.setText(name);

			String addr = "暂未收录";
			try {
				addr = joShop.getString("address");
			} catch (Exception e) {
			}
			tvAddr.setText(addr);

			String tele = "暂未收录";
			try {
				tele = joShop.getString("telephone");
			} catch (Exception e) {
			}
			tvTele.setText(tele);

			JSONObject joDetail = joShop.getJSONObject("detail_info");
			String tags = "暂未收录";
			try {
				tags = joDetail.getString("tag");
			} catch (Exception e) {
			}
			tvTags.setText(tags);

			String price = "0";
			try {
				price = joDetail.getString("price");
			} catch (Exception e) {
			}
			tvPrice.setText(price);

			float overallRate = 0;
			try {
				overallRate = Float.parseFloat(joDetail.getString("overall_rating"));
			} catch (Exception e) {
			}
			rbOverall.setRating(overallRate);

			float tasteRate = 0;
			try {
				tasteRate = Float.parseFloat(joDetail.getString("taste_rating"));
			} catch (Exception e) {
			}
			rbTaste.setRating(tasteRate);

			float serviceRate = 0;
			try {
				serviceRate = Float.parseFloat(joDetail.getString("service_rating"));
			} catch (Exception e) {
			}
			rbService.setRating(serviceRate);

			float envRate = 0;
			try {
				envRate = Float.parseFloat(joDetail.getString("environment_rating"));
			} catch (Exception e) {
			}
			rbEnv.setRating(envRate);

			String shopHours = "暂未收录";
			try {
				shopHours = joDetail.getString("shop_hours");
			} catch (Exception e) {
			}
			tvShopHours.setText(shopHours);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
