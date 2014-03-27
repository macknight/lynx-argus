package com.lynx.argus.plugin.weather;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import org.json.JSONObject;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lynx.argus.plugin.weather.model.WeatherInfo;
import com.lynx.lib.core.*;
import com.lynx.lib.http.HttpCallback;
import com.lynx.lib.http.core.HttpParam;
import com.lynx.lib.util.DateUtil;
import com.lynx.lib.util.DisplayUtil;
import com.lynx.lib.widget.charts.LineView;

/**
 * 天气插件
 * 
 * @author zhufeng.liu
 * @version 13-11-19 下午11:55
 */
public class WeatherFragment extends LFFragment {

	private static final String LM_API_WEATHER_FORECAST = "/weather/forecast";
	private static final int WEATHER_UPDATE_DONE = 1;

	private static final String CELSIUS = "℃";
	private TextView tvTempMax, tvTempMin, tvWeatherDetail, tvWind;
	private LineView lvTemp;

	private Gson gson;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
	private WeatherInfo weatherInfo;
	private List<String> colors = new ArrayList<String>();
	private Animation animRotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f,
			Animation.RELATIVE_TO_SELF, 0.5f);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        navActivity.setPopAnimation(R.anim.slide_in_left, R.anim.slide_out_right);
        navActivity.setPushAnimation(R.anim.slide_in_right, R.anim.slide_out_left);
    }

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			throws Exception {
		View view = inflater.inflate(R.layout.layout_weather, container, false);
		if (view == null) {
			throw new Exception("页面初始化错误");
		}

		gson = LFApplication.instance().gson();

		animRotate.setDuration(1500);
		animRotate.setRepeatCount(-1);
		animRotate.setRepeatMode(Animation.RESTART);


        ImageButton ib = (ImageButton)view.findViewById(R.id.ib_weather_addcity);
        ib.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCityFragment fragment = new AddCityFragment();
                navActivity.pushFragment(fragment);
            }
        });

		lvTemp = (LineView) view.findViewById(R.id.lv_temp);
		Point screenSize = DisplayUtil.getScreenSize(navActivity);
		lvTemp.setBackgroundGridWidth(screenSize.x / 5);
		lvTemp.setBottomTextList(generateBottomTips());
		lvTemp.setDrawDotLine(true);
		lvTemp.setShowPopup(LineView.SHOW_POPUPS_NONE);
		lvTemp.setColors(colors);
		lvTemp.setPopupResId(R.drawable.popup_bg);

		tvTempMax = (TextView) view.findViewById(R.id.tv_weather_temp_max);
		tvTempMin = (TextView) view.findViewById(R.id.tv_weather_temp_min);
		tvWeatherDetail = (TextView) view.findViewById(R.id.tv_weather_detail);
		tvWind = (TextView) view.findViewById(R.id.tv_weather_wind);

		ImageButton ibRefresh = (ImageButton) view.findViewById(R.id.ib_weather_refresh);
		ibRefresh.setAnimation(animRotate);
		animRotate.cancel();
		ibRefresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				animRotate.cancel();
				animRotate.startNow();
				getWeather(1);
			}
		});

		colors.add("#ffe74c3c");
		colors.add("#ff2980b9");

		getWeather(1);

		return view;
	}

	private HttpCallback forecastCallback = new HttpCallback() {
		@Override
		public void onSuccess(Object o) {
			try {
				JSONObject joResult = new JSONObject(o.toString());
				Logger.e("chris", joResult.toString());
				if (joResult.getInt("status") == 0) {
					JSONObject joWeatherInfo = joResult.getJSONObject("result");
					weatherInfo = gson.fromJson(joWeatherInfo.toString(), WeatherInfo.class);
					handler.sendEmptyMessage(WEATHER_UPDATE_DONE);
				} else {
					Toast.makeText(navActivity, "更新天气失败", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {

			}

		}

		@Override
		public void onFailure(Throwable t, String strMsg) {
			handler.sendEmptyMessage(WEATHER_UPDATE_DONE);
			Logger.e("chris", strMsg, t);
			Toast.makeText(navActivity, "更新天气失败", Toast.LENGTH_SHORT).show();
		}
	};

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WEATHER_UPDATE_DONE:
				updateWeather();
				animRotate.cancel();
				break;
			}
		}
	};

	private void getWeather(int cityId) {
		HttpParam param = new HttpParam();
		param.put("ua", LFEnvironment.userAgent());
		Logger.e("chris", LFEnvironment.userAgent());
		param.put("cityId", String.valueOf(cityId));
		String url = String.format("%s%s", Const.LM_API_DOMAIN, LM_API_WEATHER_FORECAST);
		httpService.post(url, null, forecastCallback);
	}

	private void updateWeather() {
		try {
			tvTempMax.setText(String.format("%s%s", CELSIUS, weatherInfo.getMaxTemp()[0]));
			tvTempMin.setText(String.format("%s%s", CELSIUS, weatherInfo.getMinTemp()[0]));
			tvWeatherDetail.setText(weatherInfo.getWeather()[0]);
			tvWind.setText(weatherInfo.getWind()[0]);
			tempSet();
		} catch (Exception e) {

		}
	}

	private List<String> generateBottomTips() {
		List<String> bottomTips = new ArrayList<String>();
		Date today = new Date();
		for (int i = 0; i <= 4; ++i) {
			Date date = DateUtil.dateOffset(today, i);
			String tip = String.format("%s %s", sdf.format(date), DateUtil.getWeekOfDate(date));
			bottomTips.add(tip);
		}
		return bottomTips;
	}

	private void tempSet() {
		List maxTemp = Arrays.asList(weatherInfo.getMaxTemp());
		List minTemp = Arrays.asList(weatherInfo.getMinTemp());
		List<List<Integer>> dataLists = new ArrayList<List<Integer>>();
		dataLists.add(maxTemp);
		dataLists.add(minTemp);
		lvTemp.setDataList(dataLists);
	}
}
