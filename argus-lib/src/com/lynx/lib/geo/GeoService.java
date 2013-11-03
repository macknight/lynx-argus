package com.lynx.lib.geo;

import com.lynx.lib.core.dex.DexService;
import com.lynx.lib.geo.entity.Address;
import com.lynx.lib.geo.entity.Coord;
import com.lynx.lib.geo.entity.Coord.CoordType;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/27/13 5:53 PM
 */
public interface GeoService extends DexService {

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

    public void removeListener(LocationListener listener);

    public List<LocationListener> listeners();

    public Coord coord();

	public Address address();

    public enum LocationStatus {
        IDLE,
        ONGOING,
        SUCCESS,
        FAIL
    }
}
