package com.lynx.service.location.impl1v1;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import com.lynx.lib.dataservice.NetworkManager;
import com.lynx.lib.dataservice.NetworkManager.NetworkState;
import com.lynx.lib.location.entity.Wifi;
import com.lynx.service.location.impl1v1.LocationCenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chris.liu
 */
public class WifiInfoManager {
    private static final int INTERVAL_WIFI = 4000;
    private static final int INTERVAL_COUNT = 2;

    private LocationCenter locationCenter;
    private android.net.wifi.WifiManager wifiManager;
    private NetworkManager networkManager = null;
    private DhcpInfo dhcpInfo = null;
    private List<Wifi> wifis = null;
    private boolean isEnable = false; // wifi是否打开了

    private AtomicInteger loop;
    private static Timer timer;
    private static TimerTask timerTask;

    public WifiInfoManager(Context context) {
        this.locationCenter = null;
        wifiManager = (android.net.wifi.WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        networkManager = new NetworkManager(context);
    }

    public WifiInfoManager(LocationCenter locationCenter) {
        this(locationCenter.context());
        this.locationCenter = locationCenter;
    }


    public void start() {
        isEnable = wifiManager.isWifiEnabled();
        if (!isEnable) {
            wifiManager.setWifiEnabled(true);
        }

        stop();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (loop.intValue() >= INTERVAL_COUNT) {
                    stop();
                    return;
                }

                if (loop == null) {
                    loop = new AtomicInteger(1);
                } else {
                    loop.set(loop.intValue() + 1);
                }
                wifiScan();
            }
        };

        timer = new Timer();
        timer.schedule(timerTask, INTERVAL_WIFI, INTERVAL_WIFI);
    }

    public void stop() {
        // 还原状态
        wifiManager.setWifiEnabled(isEnable);
        loop = null;
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (locationCenter != null) {
            locationCenter.wifiScanFin();
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
        if (networkManager.state() == NetworkState.NETWORK_WIFI
                && conn_wifi != null) {
            cur_wifi = new Wifi(conn_wifi.getSSID(), conn_wifi.getBSSID(),
                    conn_wifi.getRssi());
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
        if ((wifis == null || wifis.size() == 0) &&
                wifiOld != null && wifiOld.size() > 0) {
            wifis = wifiOld;
        }
        if (wifis != null && wifiOld != null && wifis.size() < wifiOld.size()) {
            wifis = wifiOld;
        }
    }


    public List<Wifi> wifis() {
        return wifis;
    }

    public DhcpInfo dhcpInfo() {
        return this.dhcpInfo;
    }
}
