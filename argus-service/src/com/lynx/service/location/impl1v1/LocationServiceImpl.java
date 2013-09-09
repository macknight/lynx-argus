package com.lynx.service.location.impl1v1;

import android.content.Context;
import android.location.Location;
import com.lynx.lib.location.LocationListener;
import com.lynx.lib.location.LocationService;
import com.lynx.lib.location.entity.Coord.CoordType;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/29/13 11:27 AM
 */
public class LocationServiceImpl implements LocationService, LocationListener {

    private Context context;
    private LocationCenter locationCenter;
    private LocationStatus status;
    private final ArrayList<LocationListener> listeners = new ArrayList<LocationListener>();

    public LocationServiceImpl(Context context) {
        this.context = context;
        locationCenter = new LocationCenter(context);
    }

    @Override
    public LocationStatus status() {
        return status;
    }

    @Override
    public void locate(boolean refresh) {
        locationCenter.start();
    }

    @Override
    public Location rgc(double lat, double lng, CoordType type) {
        return null;
    }

    @Override
    public void onLocationChanged(int status) {
        for (LocationListener l : listeners) {
            l.onLocationChanged(status);
        }
    }

    public void addLocListener(LocationListener listener) {
        listeners.add(listener);
    }
}
