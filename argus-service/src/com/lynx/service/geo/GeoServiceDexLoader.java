package com.lynx.service.geo;

import android.content.Context;
import com.lynx.lib.core.dex.DexModule;
import com.lynx.lib.core.dex.ServiceLoader;
import com.lynx.lib.geo.GeoService;
import com.lynx.lib.geo.LocationListener;
import com.lynx.service.geo.impl1v1.GeoServiceImpl;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/29/13 11:16 AM
 */
public class GeoServiceDexLoader extends ServiceLoader {

    public static final String Tag = "geo";

    private List<LocationListener> listeners = null;

    private static DexModule defModule = new DexModule("geo", 1, null, null, "地理位置信息服务",
            "com.lynx.service.geo.impl1v1.GeoServiceImpl");

    public GeoServiceDexLoader() throws Exception {
        super(defModule, GeoServiceImpl.class);
    }

    @Override
    protected void beforeLoad() {
        GeoService geoService = (GeoService) service;
        listeners = geoService.listeners();
        if (geoService != null) {
            geoService.stop();
        }
    }

    @Override
    protected void loadService() {
        try {
            if (clazz != null) {
                service = (GeoService) clazz.getConstructor(Context.class).newInstance(context);
            }

            if (service == null) {
                service = new GeoServiceImpl(context);
            }

            if (service != null && listeners != null && listeners.size() > 0) {
                for (LocationListener listener : listeners) {
                    ((GeoService) service).addListener(listener);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void afterLoad() {
        service.start();
    }
}
