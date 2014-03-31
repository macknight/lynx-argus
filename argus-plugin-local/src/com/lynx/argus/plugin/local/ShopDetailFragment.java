package com.lynx.argus.plugin.local;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lynx.lib.core.LFConst;
import com.lynx.lib.core.LFFragment;
import com.lynx.lib.http.HttpCallback;

/**
 * 
 * @author zhufeng.liu
 * @version 13-9-16 下午2:00
 */
public class ShopDetailFragment extends LFFragment {
	private String uid;

	private TextView tvName, tvAddr, tvTele, tvPrice, tvTags, tvShopHours;
	private RatingBar rbOverall, rbTaste, rbService, rbEnv;

	private static final String BMAP_API_PLACE_SHOP_DETAIL = "/detail";

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			throws Exception {
		View view = inflater.inflate(R.layout.layout_shop_detail, container, false);
		if (view == null) {
			throw new Exception("页面初始化错误");
		}

		initUI(view);
		uid = getArguments().getString("uid");
		getShopDetail(uid);
		return view;
	}

	private HttpCallback httpCallback = new HttpCallback() {
		@Override
		public void onSuccess(Object o) {
			try {
				JSONObject joResult = new JSONObject(o.toString());
				if (joResult.getInt("status") == 0) {
					JSONObject joShop = joResult.getJSONObject("result");
					updateUI(joShop);
				} else {
					Toast.makeText(navActivity, "获取商家信息失败", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {

			}

		}

		@Override
		public void onFailure(Throwable t, String strMsg) {
			super.onFailure(t, strMsg);
		}
	};

	private void initUI(View view) {
		tvName = (TextView) view.findViewById(R.id.tv_shop_detail_name);
		tvAddr = (TextView) view.findViewById(R.id.tv_shop_detail_addr);
		tvTele = (TextView) view.findViewById(R.id.tv_shop_detail_tele);
		tvPrice = (TextView) view.findViewById(R.id.tv_shop_detail_price);
		tvTags = (TextView) view.findViewById(R.id.tv_shop_detail_tags);
		tvShopHours = (TextView) view.findViewById(R.id.tv_shop_detail_shop_hours);
		rbOverall = (RatingBar) view.findViewById(R.id.rb_shop_detail_overall);
		rbOverall.setClickable(false);
		rbOverall.setEnabled(false);
		rbTaste = (RatingBar) view.findViewById(R.id.rb_shop_detail_taste);
		rbTaste.setClickable(false);
		rbTaste.setEnabled(false);
		rbService = (RatingBar) view.findViewById(R.id.rb_shop_detail_service);
		rbService.setClickable(false);
		rbService.setEnabled(false);
		rbEnv = (RatingBar) view.findViewById(R.id.rb_shop_detail_env);
		rbEnv.setClickable(false);
		rbEnv.setEnabled(false);
	}

	private void getShopDetail(String shopId) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("ak", LFConst.BMAP_API_KEY));
		params.add(new BasicNameValuePair("output", "json"));
		params.add(new BasicNameValuePair("uid", shopId));
		params.add(new BasicNameValuePair("scope", 2 + ""));
		String param = URLEncodedUtils.format(params, "UTF-8");
		String url = String.format("%s%s?%s", LFConst.BMAP_API_PLACE, BMAP_API_PLACE_SHOP_DETAIL,
				param);
		httpService.get(url, null, httpCallback);
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
