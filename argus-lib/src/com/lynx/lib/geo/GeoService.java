package com.lynx.lib.geo;

import com.lynx.lib.core.dex.Service;
import com.lynx.lib.geo.entity.Address;
import com.lynx.lib.geo.entity.Coord;
import com.lynx.lib.geo.entity.Coord.CoordType;

import java.util.List;

/**
 * 
 * @author zhufeng.liu
 * 
 * @addtime 13-8-27 下午5:52
 */
public interface GeoService extends Service {

	LocationStatus status();

	/**
	 * get the best coordinate by server, if can be decided, return only one
	 * coordinate result, else, return a coordinate list
	 * 
	 * @param refresh
	 * @return
	 */
	void locate(boolean refresh);

	/**
	 * reverse geo-coding
	 * 
	 * @param lat
	 * @param lng
	 * @param type
	 * @return
	 */
	void rgc(double lat, double lng, CoordType type);

	void addListener(LocationListener listener);

	void removeListener(LocationListener listener);

	List<LocationListener> listeners();

	Coord coord();

	Address address();

	public enum LocationStatus {
		IDLE, ONGOING, SUCCESS, FAIL
	}
}
