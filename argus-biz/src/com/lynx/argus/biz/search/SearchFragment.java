package com.lynx.argus.biz.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.Toast;
import com.lynx.argus.R;
import com.lynx.lib.core.Const;
import com.lynx.lib.core.LFApplication;
import com.lynx.lib.core.LFFragment;
import com.lynx.lib.http.HttpCallback;
import com.lynx.lib.http.HttpService;
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
 * @version 13-10-27 下午9:29
 */
public class SearchFragment extends LFFragment {
	public static final String Tag = "Search";

	private static final String BMAP_API_PLACE_SUGGESSTION = "/suggestion";

	private HttpService httpService;

	public SearchFragment() {
		httpService = (HttpService) LFApplication.instance().service("http");
	}

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_search, container, false);
		EditText edSegguestion = (EditText)view.findViewById(R.id.et_search_keyword);


		return view;
	}

	private HttpCallback<Object> httpCallback = new HttpCallback<Object>() {
		@Override
		public void onSuccess(Object o) {
			super.onSuccess(o);
			try {
				JSONObject joResult = new JSONObject(o.toString());
				if (joResult.getInt("status") == 0) {
					JSONArray jaResult = joResult.getJSONArray("result");
					parseSuggestion(jaResult);
				} else {
					Toast.makeText(navActivity, "刷新失败", Toast.LENGTH_SHORT)
							.show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onFailure(Throwable t, String strMsg) {
			super.onFailure(t, strMsg);
		}
	};

	private void getSuggestion(String query, String city) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("ak", Const.BMAP_API_KEY));
		params.add(new BasicNameValuePair("output", "json"));
		params.add(new BasicNameValuePair("query", query));
		params.add(new BasicNameValuePair("region", city));
		String param = URLEncodedUtils.format(params, "UTF-8");
		String url = String.format("%s%s?%s", Const.BMAP_API_PLACE,
				BMAP_API_PLACE_SUGGESSTION, param);
		httpService.get(url, null, httpCallback);
	}

	private void parseSuggestion(JSONArray data) {
		if (data == null || data.length() == 0) {
			return;
		}
		for (int i = 0; i < data.length(); ++i) {
			try {
				Map<String, Object> suggestion = new HashMap<String, Object>();
				JSONObject joSuggestion = data.getJSONObject(i);

				String name = joSuggestion.getString("name");
				String district = joSuggestion.getString("district");
				String city = "";
				try {
					city = joSuggestion.getString("city");
				} catch (Exception e) {

				}

			} catch (Exception e) {

			}
		}
	}
}
