package com.lynx.lib.location;

import android.location.Location;
import com.lynx.lib.core.Service;
import com.lynx.lib.location.entity.Coord.CoordType;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/27/13 5:53 PM
 */
public interface LocationService extends Service {

    LocationStatus status();

    /**
     * get the best coordinate by server, if can be decided, return only one
     * coordinate result, else, return a coordinate list
     *
     * @return
     */
    Location locate();

    /**
     * reverse geo-coding
     *
     * @param lat
     * @param lng
     * @param type
     * @return
     */
    Location rgc(double lat, double lng, CoordType type);

    public enum LocationStatus {
        IDLE,
        ONGOING,
        SUCCESS,
        FAIL
    }
}
