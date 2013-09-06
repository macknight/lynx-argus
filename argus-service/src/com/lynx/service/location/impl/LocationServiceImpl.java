package com.lynx.service.location.impl;

import android.content.Context;
import android.location.Location;
import com.lynx.lib.location.LocationService;
import com.lynx.lib.location.entity.Coord.CoordType;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/29/13 11:27 AM
 */
public class LocationServiceImpl implements LocationService {

    private Context context;
    private LocationStatus status;


    public LocationServiceImpl(Context context) {
        this.context = context;
    }

    @Override
    public LocationStatus status() {
        return status;
    }

    @Override
    public Location locate() {
        return null;
    }

    @Override
    public Location rgc(double lat, double lng, CoordType type) {
        return null;
    }
}
