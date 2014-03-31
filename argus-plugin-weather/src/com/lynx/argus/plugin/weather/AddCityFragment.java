package com.lynx.argus.plugin.weather;

import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lynx.lib.core.LFConst;
import com.lynx.lib.core.LFEnvironment;
import com.lynx.lib.core.LFFragment;
import com.lynx.lib.http.HttpCallback;
import com.lynx.lib.http.core.HttpParam;

/**
 *
 * @author chris.liu
 * @version 3/25/14 9:04 PM
 */
public class AddCityFragment extends LFFragment {
	private static final String LM_API_WEATHER_CITYS = "/weather/citylist";
	private static final int CITYLIST_UPADTE_DONE = 2;

	@Override
	protected View onLoadView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) throws Exception {
		View view = inflater.inflate(R.layout.layout_addcity, container, false);
		if (view == null) {
			throw new Exception("页面初始化错误");
		}
		return view;
	}

	private HttpCallback cityListCallback = new HttpCallback() {
		@Override
		public void onSuccess(Object o) {
			try {
				JSONObject joResult = new JSONObject(o.toString());
				if (joResult.getInt("status") == 0) {
					JSONObject joShop = joResult.getJSONObject("result");
					handler.sendEmptyMessage(CITYLIST_UPADTE_DONE);
				} else {
					Toast.makeText(navActivity, "获取城市列表失败", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {

			}

		}

		@Override
		public void onFailure(Throwable t, String strMsg) {
			handler.sendEmptyMessage(CITYLIST_UPADTE_DONE);
			Toast.makeText(navActivity, "获取城市列表失败", Toast.LENGTH_SHORT).show();
		}
	};

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CITYLIST_UPADTE_DONE:
				updateCityList();
				break;
			}
		}
	};

	private void getCityList() {
		HttpParam param = new HttpParam();
		param.put("ua", LFEnvironment.userAgent());
		String url = String.format("%s%s", LFConst.LM_API_DOMAIN, LM_API_WEATHER_CITYS);
		httpService.post(url, null, cityListCallback);
	}

	private void updateCityList() {

	}
}
