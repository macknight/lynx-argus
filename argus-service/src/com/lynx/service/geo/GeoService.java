package com.lynx.service.geo;

import com.lynx.service.geo.entity.Address;
import com.lynx.service.geo.entity.Coord;
import com.lynx.service.geo.entity.Coord.CoordType;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/27/13 5:53 PM
 */
public interface GeoService {

    public LocationStatus status();

    /**
     * get the best coordinate by server, if can be decided, return only one
     * coordinate result, else, return a coordinate list
     *
     * @param refresh
     * @return
     */
    public void locate(boolean refresh);

    /**
     * reverse geo-coding
     *
     * @param lat
     * @param lng
     * @param type
     * @return
     */
    public void rgc(double lat, double lng, CoordType type);

    public void addListener(LocationListener listener);

    public Coord coord();

    public Address address();

    public enum LocationStatus {
        IDLE,
        ONGOING,
        SUCCESS,
        FAIL
    }
}
