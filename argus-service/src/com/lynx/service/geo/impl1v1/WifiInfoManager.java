package com.lynx.service.geo.impl1v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;

import com.lynx.lib.core.LFLogger;
import com.lynx.lib.geo.entity.Wifi;
import com.lynx.lib.http.NetworkManager;
import com.lynx.lib.http.NetworkManager.NetworkState;

/**
 * 
 * @author zhufeng.liu
 * @version 13-11-8 下午5:01
 */
public class WifiInfoManager {
	private static final String Tag = "WifiInfoManager";
	private static final int INTERVAL_WIFI = 3000;
	private static final int INTERVAL_COUNT = 2;

	private LocationCenter locationCenter;
	private android.net.wifi.WifiManager wifiManager;
	private NetworkManager networkManager = null;
	private DhcpInfo dhcpInfo = null;
	private List<Wifi> wifis = null;
	private boolean isEnable; // wifi是否打开了

	private AtomicInteger loop = new AtomicInteger(0);
	private static Timer timer;
	private static TimerTask timerTask;

	public WifiInfoManager(Context context) {
		this.locationCenter = null;
		wifiManager = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		networkManager = new NetworkManager(context);
		isEnable = wifiManager.isWifiEnabled();
	}

	public WifiInfoManager(LocationCenter locationCenter) {
		this(locationCenter.context());
		this.locationCenter = locationCenter;
	}

	public void start() {
		LFLogger.i(Tag, "start wifi info scan");
		stop();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				if (loop.intValue() >= INTERVAL_COUNT) {
					stop();

					if (locationCenter != null) {
						locationCenter.wifiScanFin();
					}
					return;
				} else {
					loop.set(loop.intValue() + 1);
				}
				wifiScan();
			}
		};

		timer = new Timer();
		timer.schedule(timerTask, 0, INTERVAL_WIFI);
	}

	public void stop() {
		LFLogger.i(Tag, "stop wifi info scan");
		loop.set(0);
		if (timerTask != null) {
			timerTask.cancel();
			timerTask = null;
		}
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	public void wifiScan() {
		List<Wifi> wifiOld = null;
		if (wifis == null) {
			wifis = new ArrayList<Wifi>();
		} else if (wifis.size() > 0) {
			wifiOld = wifis;
		}

		wifis = new ArrayList<Wifi>();
		dhcpInfo = wifiManager.getDhcpInfo();
		WifiInfo conn_wifi = wifiManager.getConnectionInfo();
		Wifi cur_wifi = null;
		if (networkManager.state() == NetworkState.NETWORK_WIFI && conn_wifi != null) {
			cur_wifi = new Wifi(conn_wifi.getSSID(), conn_wifi.getBSSID(), conn_wifi.getRssi());
			wifis.add(cur_wifi);
		}

		List<ScanResult> scan_results = wifiManager.getScanResults();

		if (scan_results != null) {
			for (ScanResult res : scan_results) {
				Wifi the_wifi = new Wifi(res.SSID, res.BSSID, res.level);
				if (cur_wifi != null && the_wifi.equals(cur_wifi))
					continue;
				wifis.add(the_wifi);
			}
		}
		// 上一次扫描的结果好于本次，则用上次结果代替本次
		if (wifiOld != null && wifis.size() < wifiOld.size()) {
			wifis = wifiOld;
		}
	}

	public List<Wifi> wifis() {
		return wifis;
	}

	public DhcpInfo dhcpInfo() {
		return this.dhcpInfo;
	}

	public String wifis2str() {
		if (wifis != null && wifis.size() > 0) {
			String result = "";
			for (Wifi wifi : wifis) {
				result = String.format("%s|%s", result, wifi.toString());
			}
			return result.substring(1);
		}

		return null;
	}

}
