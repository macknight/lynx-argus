package com.lynx.lib.geo;

import java.util.List;

import com.lynx.lib.core.dex.IService;
import com.lynx.lib.geo.entity.Address;
import com.lynx.lib.geo.entity.Cell;
import com.lynx.lib.geo.entity.Coord;
import com.lynx.lib.geo.entity.Coord.CoordType;
import com.lynx.lib.geo.entity.Wifi;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-8-27 下午5:52
 */
public interface GeoService extends IService {

	LocationStatus status();

	/**
	 * get the best coordinate by server, if can be decided, return only one coordinate result, else, return a
	 * coordinate list
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

	/**
	 * 获取当前所扫描到的基站
	 * 
	 * @return
	 */
	List<Cell> cells();

	/**
	 * 当前所扫描到的wifi
	 * 
	 * @return
	 */
	List<Wifi> wifis();

	/**
	 * 当前定位后所拿到的位置坐标
	 * 
	 * @return
	 */
	List<Coord> coords();

	Coord coord();

	Address address();

	public enum LocationStatus {
		IDLE, ONGOING, SUCCESS, FAIL
	}
}
