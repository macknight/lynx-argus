package com.lynx.service.geo;

import android.content.Context;
import com.lynx.lib.core.ServiceDexLoader;
import com.lynx.service.geo.impl1v1.GeoServiceImpl;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/29/13 11:16 AM
 */
public class GeoServiceDexLoader<LocationService> extends ServiceDexLoader {
    public static final String Tag = "geo";

    private static final int minVersion = 102;

    public GeoServiceDexLoader(Context context) {
        super(context, Tag, minVersion, GeoServiceImpl.class);
    }

    @Override
    protected void beforeLoad() {

    }

    @Override
    protected void loadService() {
        try {
            if (clazz != null) {
                service = (LocationService) clazz.getConstructor(Context.class).newInstance(context);
            }

            if (service == null) {
                service = new GeoServiceImpl(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void afterLoad() {

    }
}
