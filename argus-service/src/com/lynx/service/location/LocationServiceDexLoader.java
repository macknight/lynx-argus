package com.lynx.service.location;

import android.content.Context;
import com.lynx.service.core.ServiceDexLoader;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/29/13 11:16 AM
 */
public class LocationServiceDexLoader<LocationService> extends ServiceDexLoader {
    public static final String Tag = "location";

    public LocationServiceDexLoader(Context context) {
        super(context, Tag);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void afterLoad() {

    }
}
