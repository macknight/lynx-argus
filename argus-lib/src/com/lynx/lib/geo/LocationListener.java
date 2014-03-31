package com.lynx.lib.geo;

import com.lynx.lib.geo.GeoService.LocationStatus;

/**
 * 
 * @author zhufeng.liu
 * @version 13-8-27 下午5:52
 */
public interface LocationListener {
	void onLocationChanged(LocationStatus status);
}
