package com.lynx.argus.plugin.local;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lynx.argus.plugin.local.model.ShopDetail;
import com.lynx.argus.plugin.local.model.ShopInfo;
import com.lynx.lib.core.LFApplication;
import com.lynx.lib.core.LFConst;
import com.lynx.lib.core.LFFragment;
import com.lynx.lib.http.HttpCallback;

/**
 * 
 * @author zhufeng.liu
 * @version 13-9-16 下午2:00
 */
public class ShopDetailFragment extends LFFragment {
	private Gson gson;

	private TextView tvName, tvAddr, tvTele, tvPrice;
	private TextView tvShopEnv, tvShopService, tvShopTaste;
	private RatingBar rbOverall;

	private ShopInfo shopInfo;

	private static final String BMAP_API_PLACE_SHOP_DETAIL = "/detail";

	public ShopDetailFragment() {
		gson = LFApplication.instance().gson();
	}

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			throws Exception {
		View view = inflater.inflate(R.layout.layout_shop_detail, container, false);
		if (view == null) {
			throw new Exception("页面初始化错误");
		}

		initUI(view);
		shopInfo = getArguments().getParcelable("shop_info");

		getShopDetail();

		return view;
	}

	private HttpCallback httpCallback = new HttpCallback() {
		@Override
		public void onSuccess(Object o) {
			try {
				JSONObject joResult = new JSONObject(o.toString());
				if (joResult.getInt("status") == 0) {
					JSONObject joShop = joResult.getJSONObject("result");
					ShopInfo info = gson.fromJson(joShop.toString(), ShopInfo.class);
					shopInfo.detailInfo = info.detailInfo;

					updateUI();
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

		rbOverall = (RatingBar) view.findViewById(R.id.rb_shop_detail_overall);
		rbOverall.setClickable(false);
		rbOverall.setEnabled(false);

		tvPrice = (TextView) view.findViewById(R.id.tv_shop_detail_price);
		tvShopTaste = (TextView) view.findViewById(R.id.tv_shop_detail_taste);
		tvShopEnv = (TextView) view.findViewById(R.id.tv_shop_detail_env);
		tvShopService = (TextView) view.findViewById(R.id.tv_shop_detail_service);

		tvAddr = (TextView) view.findViewById(R.id.tv_shop_detail_addr);
		tvTele = (TextView) view.findViewById(R.id.tv_shop_detail_tele);
	}

	private void getShopDetail() {
		if (shopInfo != null && !TextUtils.isEmpty(shopInfo.uid)) {
			if (shopInfo.detailInfo != null) {
				updateUI();
			} else {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("ak", LFConst.BMAP_API_KEY));
				params.add(new BasicNameValuePair("output", "json"));
				params.add(new BasicNameValuePair("uid", shopInfo.uid));
				params.add(new BasicNameValuePair("scope", 2 + ""));
				String param = URLEncodedUtils.format(params, "UTF-8");
				String url = String.format("%s%s?%s", LFConst.BMAP_API_PLACE,
						BMAP_API_PLACE_SHOP_DETAIL, param);
				httpService.get(url, null, httpCallback);
			}
		}

	}

	private void updateUI() {
		String name = TextUtils.isEmpty(shopInfo.name) ? "暂未收录" : shopInfo.name;
		tvName.setText(name);

		String addr = TextUtils.isEmpty(shopInfo.address) ? "暂未收录" : shopInfo.address;
		tvAddr.setText(addr);

		String tele = TextUtils.isEmpty(shopInfo.telephone) ? "暂未收录" : shopInfo.telephone;
		tvTele.setText(tele);

		ShopDetail detail = shopInfo.detailInfo;
		if (detail != null) {
			String price = TextUtils.isEmpty(detail.price) ? "0" : detail.price;
			tvPrice.setText(price);

			rbOverall.setRating(detail.overallRating);
            tvShopService.setText(String.format("服务：%s", detail.serviceRating));
            tvShopTaste.setText(String.format("口味：%s", detail.tasteRating));
            tvShopEnv.setText(String.format("环境：%s", detail.envRating));
		}
	}
}
