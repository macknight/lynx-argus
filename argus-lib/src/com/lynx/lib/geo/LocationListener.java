package com.lynx.lib.geo;

import com.lynx.lib.geo.GeoService.LocationStatus;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/27/13 5:53 PM
 */
public interface LocationListener {
    void onLocationChanged(LocationStatus status);
}
