package com.lynx.service.geo.impl1v1;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.lynx.lib.core.LFConst;
import com.lynx.lib.core.LFLogger;
import com.lynx.lib.geo.GeoService;
import com.lynx.lib.geo.GeoService.LocationStatus;
import com.lynx.lib.geo.entity.Address;
import com.lynx.lib.geo.entity.Cell;
import com.lynx.lib.geo.entity.Coord;
import com.lynx.lib.geo.entity.Coord.CoordSource;
import com.lynx.lib.geo.entity.Wifi;
import com.lynx.lib.http.HttpCallback;
import com.lynx.lib.http.HttpService;
import com.lynx.lib.http.core.HttpParam;

/**
 * 
 * @author zhufeng.liu
 * @version 13-11-8 下午5:01
 */
public class LocationCenter {
	public static final String Tag = "LocationCenter";

	private static final String LM_API_LOCATION = "/geo/location";
	private static final String LM_API_RGC = "/geo/rgc.php";

	private static final int INTERVAL_PRE = 1000; // 预处理状态check时间间隔
	private static final int INTERVAL_COUNT = 8;

	private static final int CELL_SCAN_FIN = 0x00001;
	private static final int WIFI_SCAN_FIN = 0x00010;
	private static final int BMAP_LOC_FIN = 0x00100;
	private static final int NETWORK_LOC_FIN = 0x01000;
	private static final int GPS_LOC_FIN = 0x10000;

	private Context context = null;
	private CellInfoManager cellInfoManager = null;
	private WifiInfoManager wifiInfoManager = null;
	private LocationManager locationManager = null;

	private static List<Coord> coords3thPart = new ArrayList<Coord>(); // 第三方定位服务获取到的经纬度

	// 定位结果
	private static Coord coord = null;
	private static Address addr = null;

	private AtomicInteger loop = new AtomicInteger(0);
	private AtomicInteger status = new AtomicInteger(0x0000);
	private static Timer timer;
	private static TimerTask timerTask;
	private GeoServiceImpl geoService;
	private HttpService httpService;

	private static final String tip = LFLogger.level() == LFLogger.AppLevel.PRODUCT ? ""
			: ("(impl1v1)");

	public LocationCenter(GeoServiceImpl geoService) {
		this.geoService = geoService;
		this.context = geoService.context();
		cellInfoManager = new CellInfoManager(this);
		wifiInfoManager = new WifiInfoManager(this);
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		try {
			Method getService = context.getClass().getMethod("service", String.class);
			httpService = (HttpService) getService.invoke(context, "http");
		} catch (Exception e) {
			LFLogger.e(Tag, "location center init error", e);
		}
	}

	public void start() {
		stop();
		LFLogger.i(Tag, "start location center");
		coords3thPart.clear();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				if (loop.intValue() >= INTERVAL_COUNT || status.intValue() >= 0x11111) {
					stopPrv();
					locate();
					return;
				}
				loop.getAndAdd(1);
			}
		};

		timer = new Timer();
		timer.schedule(timerTask, 0, INTERVAL_PRE);
		cellInfoManager.start();
		wifiInfoManager.start();
		// 监听系统network定位数据
		if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 500,
					locationListener);
		}
		// 监听系统network定位数据
		if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 500,
					locationListener);
		}

		geoService.onLocationChanged(GeoService.LocationStatus.ONGOING);
	}

	/**
	 * 停止预处理阶段的task
	 */
	private void stopPrv() {
		LFLogger.i(Tag, "stop location center");
		status.set(0x0000);
		loop.set(0);
		if (timerTask != null) {
			timerTask.cancel();
			timerTask = null;
		}
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		// 移除系统定位监听
		if (locationManager != null) {
			locationManager.removeUpdates(locationListener);
		}
		if (cellInfoManager != null) {
			cellInfoManager.stop();
		}
		if (wifiInfoManager != null) {
			wifiInfoManager.stop();
		}
	}

	public void stop() {
		stopPrv();
		if (geoService.status() == LocationStatus.ONGOING) {
			geoService.onLocationChanged(LocationStatus.FAIL);
		}
	}

	private final LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			double lat = LocationUtil.format(location.getLatitude(), 5);
			double lng = LocationUtil.format(location.getLongitude(), 5);
			CoordSource source;
			if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
				source = CoordSource.NETWORK;
				status.set(status.intValue() | NETWORK_LOC_FIN);
			} else {
				source = CoordSource.GPS;
				status.set(status.intValue() | GPS_LOC_FIN);
			}
			Coord coord = new Coord(source, lat, lng, (int) location.getAccuracy(),
					System.currentTimeMillis() - location.getTime());
			coords3thPart.add(coord);
			LFLogger.i(Tag, coord.toString());
		}

		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText(context, provider + "服务未打开", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText(context, "GPS定位开启一次，将在60s后关闭", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// 在可用、暂时不可用和无服务三个状态直接切换时触发此函数
		}
	};

	private final HttpCallback<Object> httpCallback = new HttpCallback<Object>() {
		@Override
		public void onSuccess(Object o) {
			super.onSuccess(o);
			try {
				JSONObject jo = new JSONObject(o.toString());
				if (jo.getInt("status") == 200) {
					JSONArray jaLocation = jo.getJSONArray("data");
					if (jaLocation.length() <= 0) {
						geoService.onLocationChanged(LocationStatus.FAIL);
						return;
					}
					if (jaLocation.length() == 1) {
						JSONObject jolocation = jaLocation.getJSONObject(0);
						double lat = jolocation.getDouble("lat");
						lat = LocationUtil.format(lat, 4);
						double lng = jolocation.getDouble("lng");
						lng = LocationUtil.format(lng, 4);
						int acc = jolocation.getInt("acc");
						coord = new Coord(Coord.CoordSource.AMAP, lat, lng, acc, 0);
						try {
							JSONObject joAddr = jolocation.getJSONObject("addr");
							String province = joAddr.getString("province");
							String city = joAddr.getString("city");
							String region = joAddr.getString("region");
							String street = joAddr.getString("street");
							String num = joAddr.getString("num");

							addr = new Address(province, city, region, street + tip, num);
						} catch (Exception e) {
							LFLogger.e(Tag, "cant get address now", e);
						}

						geoService.onLocationChanged(LocationStatus.SUCCESS);
						return;
					} else if (jaLocation.length() > 1) {
						geoService.onLocationChanged(LocationStatus.FAIL);
						return;
					}
				} else {
					geoService.onLocationChanged(LocationStatus.FAIL);
					return;
				}
			} catch (Exception e) {
				LFLogger.e(Tag, "cant get location now", e);
			}
			geoService.onLocationChanged(LocationStatus.FAIL);
		}

		@Override
		public void onFailure(Throwable t, String strMsg) {
			geoService.onLocationChanged(LocationStatus.FAIL);
			LFLogger.e(Tag, "unknown network error " + strMsg, t);
		}
	};

	/**
	 * 完成基站数据扫描
	 */
	public void cellScanFin() {
		LFLogger.i(Tag, "cell scan fin");
		status.set(status.intValue() | CELL_SCAN_FIN);
	}

	/**
	 * 完成wifi数据扫描
	 */
	public void wifiScanFin() {
		LFLogger.i(Tag, "wifi scan fin");
		status.set(status.intValue() | WIFI_SCAN_FIN);
	}

	/**
	 * 完成百度定位
	 */
	public void bmapLocFin() {
		LFLogger.i(Tag, "baidu map locate fin");
		status.set(status.intValue() | BMAP_LOC_FIN);
	}

	/**
	 * 完成信息收集步骤后开始定位
	 */
	private void locate() {
		HttpParam param = new HttpParam();
		if (cellInfoManager.cells2str() != null) {
			Cell.CellType type = cellInfoManager.cells().get(0).type();
			param.put(type.name().toLowerCase(), cellInfoManager.cells2str());
		}
		if (wifiInfoManager.wifis2str() != null) {
			param.put("wifi", wifiInfoManager.wifis2str());
		}
		httpService.post(String.format("%s%s", LFConst.LM_API_DOMAIN, LM_API_LOCATION), param,
				httpCallback);
	}

	public Context context() {
		return context;
	}

	public List<Cell> cells() {
		return cellInfoManager.cells();
	}

	public List<Wifi> wifis() {
		return wifiInfoManager.wifis();
	}

	public List<Coord> coords() {
		return null;
	}

	public Coord coord() {
		return coord;
	}

	/**
	 * get current geo address.
	 */
	public Address address() {
		return addr;
	}
}
