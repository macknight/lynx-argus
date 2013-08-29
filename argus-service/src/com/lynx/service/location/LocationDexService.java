package com.lynx.service.location;

import android.location.Location;
import com.lynx.lib.location.LocationService;
import com.lynx.lib.location.entity.Coord;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/29/13 11:16 AM
 */
public class LocationDexService implements LocationService {

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
    public Location rgc(double lat, double lng, Coord.CoordType type) {
        return null;
    }
}
