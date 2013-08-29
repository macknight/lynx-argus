package com.lynx.peleus.location.impl;

import android.location.Location;
import com.lynx.lib.location.LocationService;
import com.lynx.lib.location.entity.Coord.CoordType;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/29/13 11:27 AM
 */
public class LocationServiceImpl implements LocationService {

    private LocationStatus status;


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
