package com.lynx.service.location.impl1v1;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import com.lynx.lib.location.entity.Address;
import com.lynx.lib.location.entity.Coord;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chris.liu
 */
public class LocationCenter extends Handler {
    public static final String Tag = "LocationCenter";

    private static final int INTERVAL_PRE = 1000; // 预处理状态check时间间隔
    private static final int INTERVAL_COUNT = 8;

    private static final int CELL_SCAN_FIN = 1;
    private static final int WIFI_SCAN_FIN = 2;
    private static final int NETWORK_LOC_FIN = 4;
    private static final int GPS_LOC_FIN = 8;

    private Context context = null;
    private CellInfoManager cellManager = null;
    private WifiInfoManager wifiManager = null;
    private LocationManager locationManager = null;

    private static List<Coord> coords = null;
    private static Address addr = null;

    private AtomicInteger loop, status;
    private static Timer timer;
    private static TimerTask timerTask;

    private long elapsePre = 0; // 定位预处理耗时

    public LocationCenter(Context context) {
        this.context = context;
        cellManager = new CellInfoManager(this);
        wifiManager = new WifiInfoManager(this);
        locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
    }

    public void start() {
        stop();
        Log.d(Tag, "start location center");
        status = new AtomicInteger(0);


        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (loop.intValue() >= INTERVAL_COUNT || status.intValue() > 4) {
                    stop();
                    return;
                }

                if (loop == null) {
                    loop = new AtomicInteger(1);
                } else {
                    loop.set(loop.intValue() + 1);
                }
            }
        };

        timer = new Timer();
        timer.schedule(timerTask, INTERVAL_PRE, INTERVAL_PRE);
        cellManager.start();
        wifiManager.start();
        // 监听数据
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                10000, 500, locationListener);
    }

    public void stop() {
        Log.d(Tag, "stop location center");
        status = null;
        loop = null;
        // 移除系统定位监听
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
        if (cellManager != null) {
            cellManager.stop();
        }
        if (wifiManager != null) {
            wifiManager.stop();
        }
    }

    private final android.location.LocationListener locationListener = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double lat = LocationUtil.format(location.getLatitude(), 5);
            double lng = LocationUtil.format(location.getLongitude(), 5);
            Coord coord = new Coord(Coord.CoordSource.GPS, lat, lng,
                    (int) location.getAccuracy(), System.currentTimeMillis()
                    - location.getTime());
            if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
                coord.setSource(Coord.CoordSource.NETWORK);
                coords.add(coord);
                status.set(status.intValue() + NETWORK_LOC_FIN);
                return;
            }
            coords.add(coord);
            coord.setSource(Coord.CoordSource.GPS);
            status.set(status.intValue() + GPS_LOC_FIN);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(context, provider + "服务未打开",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(context, "GPS定位开启一次，将在60s后关闭",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        }
    };

    /**
     * 完成基站数据扫描
     */
    public void cellScanFin() {
        Log.d(Tag, "cell scan fin");
        status.set(status.intValue() + CELL_SCAN_FIN);
    }

    /**
     * 完成wifi数据扫描
     */
    public void wifiScanFin() {
        Log.d(Tag, "wifi scan fin");
        status.set(status.intValue() + WIFI_SCAN_FIN);
    }

    public Context context() {
        return context;
    }

    /**
     * get current coordinate of your cell phone.
     *
     * @return
     */
    public List<Coord> coords() {
        return coords;
    }

    /**
     * get current location address.
     */
    public Address address() {
        return addr;
    }
}
