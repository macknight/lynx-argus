package com.lynx.service.geo;

import com.lynx.service.geo.GeoService.LocationStatus;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/27/13 5:53 PM
 */
public interface LocationListener {
    void onLocationChanged(LocationStatus status);
}
