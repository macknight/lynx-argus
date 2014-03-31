package com.lynx.argus.plugin.parenting;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lynx.lib.core.LFFragment;
import com.lynx.lib.http.HttpCallback;

/**
 * 
 * @author zhufeng.liu
 * @version 14-2-11 下午6:51
 */
public class CampaignDetailFragment extends LFFragment {
	private static final int MSG_LOAD_SUCCESS = 0;
	private static final int MSG_LOAD_FAIL = 1;

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
			parseCampaignDetail(s.toString());
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
		Bundle bundle = getArguments();
		if (bundle == null) {
			throw new Exception("传入参数错误");
		}
		String classId = bundle.getString("classId");
		getCampaignDetail(classId);
		return null;
	}

	private void getCampaignDetail(String classId) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("app", "msearch"));
		params.add(new BasicNameValuePair("act", "minfo"));
		params.add(new BasicNameValuePair("class_id", classId));
		String param = URLEncodedUtils.format(params, "UTF-8");
		String url = String.format("%s%s?%s", ParentingFragment.LM_API_PARENT_DOMAIN,
				ParentingFragment.LM_API_PARENT_INFO, param);
		httpService.get(url, null, httpCallback);
	}

	private void parseCampaignDetail(String data) {

	}

	private void updateUI() {

	}
}
