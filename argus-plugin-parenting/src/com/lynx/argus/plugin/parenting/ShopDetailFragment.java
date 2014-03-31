package com.lynx.argus.plugin.parenting;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lynx.argus.plugin.parenting.model.GoodsInfo;
import com.lynx.argus.plugin.parenting.model.ShopInfo;
import com.lynx.lib.core.LFFragment;
import com.lynx.lib.http.HttpCallback;

/**
 * 
 * @author zhufeng.liu
 * @version 14-1-29 上午10:13
 */
public class ShopDetailFragment extends LFFragment {

	private static final int MSG_LOAD_SUCCESS = 0;
	private static final int MSG_LOAD_FAIL = 1;

	private ShopInfo shopInfo;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_LOAD_SUCCESS:
				updateUI();
				break;
			case MSG_LOAD_FAIL:
				break;
			}
		}
	};

	private HttpCallback<Object> httpCallback = new HttpCallback<Object>() {
		@Override
		public void onStart() {
			super.onStart();
		}

		@Override
		public void onSuccess(Object s) {
			parseShopDetail(s.toString());
			handler.sendEmptyMessage(MSG_LOAD_SUCCESS);
		}

		@Override
		public void onFailure(Throwable throwable, String s) {
			handler.sendEmptyMessage(MSG_LOAD_FAIL);
		}
	};

	@Override
	protected View onLoadView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) throws Exception {
		View view = inflater.inflate(R.layout.layout_shopdetail, container, false);
		if (view == null) {
			throw new Exception("页面初始化错误");
		}

		Bundle bundle = getArguments();
		if (bundle == null) {
			throw new Exception("页面传入参数非法");
		}
		shopInfo = (ShopInfo) bundle.get("ShopInfo");
		if (shopInfo == null) {
			throw new Exception("页面传入参数非法");
		}

		getShopDetail(shopInfo.getShopId());

		return view;
	}

	private void getShopDetail(String shopId) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("app", "list"));
		params.add(new BasicNameValuePair("act", "minfo"));
		params.add(new BasicNameValuePair("id", shopId));
		String param = URLEncodedUtils.format(params, "UTF-8");
		String url = String.format("%s%s?%s", ParentingFragment.LM_API_PARENT_DOMAIN,
				ParentingFragment.LM_API_PARENT_INFO, param);
		httpService.get(url, null, httpCallback);
	}

	private void parseShopDetail(String data) {
		try {
			JSONObject joResult = new JSONObject(data);
			JSONArray jaResult = joResult.getJSONArray("data");
			if (jaResult == null || jaResult.length() == 0) {
				return;
			}
			for (int i = 0; i < jaResult.length(); ++i)
				try {
					JSONObject joShop = jaResult.getJSONObject(i);
					shopInfo.setRate(Double.parseDouble(joShop.getString("rate")));
					shopInfo.setTransport(joShop.getString("way"));
					shopInfo.setTele(joShop.getString("tel"));
					shopInfo.setAddress(joShop.getString("address"));
					shopInfo.setWorkTime(joShop.getString("work_time"));

					// 获取商户产品
					try {
						JSONArray jaGood = joShop.getJSONArray("_goods");
						if (jaGood != null && jaGood.length() > 0) {
							GoodsInfo[] goodsInfos = new GoodsInfo[jaGood.length()];
							for (int j = 0; j < jaGood.length(); ++j) {
								try {
									JSONObject joGood = jaGood.getJSONObject(j);
									GoodsInfo goodsInfo = new GoodsInfo();
									goodsInfo.setId(joGood.getString("item_id"));
									goodsInfo.setName(joGood.getString("goods_name"));
									goodsInfo.setCategory(joGood.getString("cate_name"));
									goodsInfo.setModel(joGood.getString("model"));
									goodsInfos[j] = goodsInfo;
								} catch (Exception e) {

								}
							}
							shopInfo.setGoods(goodsInfos);
						}
					} catch (Exception e) {

					}

					// 获取商户图片
					try {
						JSONArray jaImage = joShop.getJSONArray("_images");
						if (jaImage != null && jaImage.length() > 0) {
							String[] imgs = new String[jaImage.length()];
							for (int j = 0; i < jaImage.length(); ++i) {
								try {
									imgs[j] = jaImage.getJSONObject(j).getString("thumbshow");
								} catch (Exception e) {

								}
							}
							shopInfo.setImageUrls(imgs);
							shopInfo.setImageNum(imgs.length);
						}
					} catch (Exception e) {

					}
				} catch (Exception e) {

				}
		} catch (Exception e) {

		}
	}

	private void updateUI() {

	}
}
