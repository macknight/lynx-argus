package com.lynx.service.location;

import android.content.Context;
import android.location.Location;
import com.lynx.lib.location.LocationService;
import com.lynx.lib.location.entity.Coord;
import com.lynx.service.core.BasicDexService;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/29/13 11:16 AM
 */
public class LocationDexService extends BasicDexService implements LocationService {

    private static final String URL_LOC_CONFIG = "http://192.168.33.95/argus/locationconfig";
    private LocationStatus status;

    public LocationDexService(Context context) {
        super(context);
    }

    @Override
    public boolean start() {
        return false;
    }

    @Override
    public boolean stop() {
        return false;
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
    public Location rgc(double lat, double lng, Coord.CoordType type) {
        return null;
    }


}
