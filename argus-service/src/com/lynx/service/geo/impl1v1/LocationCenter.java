package com.lynx.service.geo.impl1v1;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.lynx.lib.core.Const;
import com.lynx.lib.http.HttpService;
import com.lynx.lib.http.core.HttpParam;
import com.lynx.lib.http.handler.HttpCallback;
import com.lynx.service.geo.GeoService.LocationStatus;
import com.lynx.service.geo.entity.Address;
import com.lynx.service.geo.entity.Cell;
import com.lynx.service.geo.entity.Coord;
import com.lynx.service.geo.entity.Coord.CoordSource;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chris.liu
 */
public class LocationCenter {
    public static final String Tag = "LocationCenter";


    private static final String URL_LOCATE = "/locate.php";
    private static final String URL_RGC = "/rgc.php";

    private static final int INTERVAL_PRE = 1000; // 预处理状态check时间间隔
    private static final int INTERVAL_COUNT = 8;

    private static final int CELL_SCAN_FIN = 0x0001;
    private static final int WIFI_SCAN_FIN = 0x0010;
    private static final int NETWORK_LOC_FIN = 0x0100;
    private static final int GPS_LOC_FIN = 0x1000;

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

    public LocationCenter(GeoServiceImpl geoService) {
        this.geoService = geoService;
        this.context = geoService.context();
        cellInfoManager = new CellInfoManager(this);
        wifiInfoManager = new WifiInfoManager(this);
        locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);

        try {
            Method getService = context.getClass().getMethod("service", String.class);
            httpService = (HttpService) getService.invoke(context, "http");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        stop();
        Log.d(Tag, "start location center");
        coords3thPart.clear();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (loop.intValue() >= INTERVAL_COUNT || status.intValue() >= 0x1111) {
                    stopPrv();
                    locate();
                    return;
                }
                loop.set(loop.intValue() + 1);
            }
        };

        timer = new Timer();
        timer.schedule(timerTask, 0, INTERVAL_PRE);
        cellInfoManager.start();
        wifiInfoManager.start();
        // 监听数据
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                10000, 500, locationListener);

        geoService.onLocationChanged(LocationStatus.ONGOING);
    }

    /**
     * 停止预处理阶段的task
     */
    private void stopPrv() {
        Log.d(Tag, "stop location center");
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
            Coord coord = new Coord(CoordSource.GPS, lat, lng,
                    (int) location.getAccuracy(), System.currentTimeMillis()
                    - location.getTime());
            if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
                coord.setSource(CoordSource.NETWORK);
                coords3thPart.add(coord);
                status.set(status.intValue() | NETWORK_LOC_FIN);
                return;
            }
            coords3thPart.add(coord);
            coord.setSource(CoordSource.GPS);
            status.set(status.intValue() | GPS_LOC_FIN);
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
        status.set(status.intValue() | CELL_SCAN_FIN);
        System.out.println(cellInfoManager.cells());
    }

    /**
     * 完成wifi数据扫描
     */
    public void wifiScanFin() {
        Log.d(Tag, "wifi scan fin");
        status.set(status.intValue() | WIFI_SCAN_FIN);
        System.out.println(wifiInfoManager.wifis());
    }

    /**
     * 完成信息收集步骤后开始定位
     */
    private void locate() {
        HttpParam param = new HttpParam();
        if (cellInfoManager.cells2str() != null) {
            Cell.CellType type = cellInfoManager.cells().get(0).type();
            param.put(type.name(), cellInfoManager.cells2str());
        }
        if (wifiInfoManager.wifis2str() != null) {
            param.put("wifi", wifiInfoManager.wifis2str());
        }
        httpService.post(String.format("%s%s", Const.PRODUCT_DOMAIN, URL_LOCATE), null,
                new HttpCallback<Object>() {
                    @Override
                    public void onSuccess(Object o) {
                        super.onSuccess(o);
                        try {
                            JSONObject jo = new JSONObject(o.toString());
                            double lat = jo.getDouble("lat");
                            double lng = jo.getDouble("lng");
                            int acc = jo.getInt("acc");
                            long elapse = System.nanoTime();
                            coord = new Coord(Coord.CoordSource.AMAP, lat, lng, acc, elapse);
                            JSONObject joAddr = jo.getJSONObject("address");
                            String province = joAddr.getString("province");
                            String city = joAddr.getString("city");
                            String region = joAddr.getString("region");
                            String street = joAddr.getString("street");
                            addr = new Address(province, city, region, street, null);
                            geoService.onLocationChanged(LocationStatus.SUCCESS);
                            return;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        geoService.onLocationChanged(LocationStatus.FAIL);
                    }

                    @Override
                    public void onFailure(Throwable t, String strMsg) {
                        super.onFailure(t, strMsg);
                        geoService.onLocationChanged(LocationStatus.FAIL);
                    }
                });
    }

    public Context context() {
        return context;
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
