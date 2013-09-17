package com.lynx.service.geo;

import android.content.Context;
import com.lynx.lib.core.DexServiceLoader;
import com.lynx.service.geo.impl1v1.GeoServiceImpl;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/29/13 11:16 AM
 */
public class GeoServiceDexLoader extends DexServiceLoader {
    public static final String Tag = "geo";

    private List<LocationListener> listeners = null;
    private static final int minVersion = 102;

    public GeoServiceDexLoader(Context context) throws Exception {
        super(context, Tag, minVersion, GeoServiceImpl.class);
    }

    @Override
    protected void beforeLoad() {
        GeoService geoService = (GeoService) service;
        if (geoService != null) {
            geoService.stop();
        }

        listeners = geoService.listeners();
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

    @Override
    public String name() {
        return GeoService.class.getSimpleName();
    }
}
