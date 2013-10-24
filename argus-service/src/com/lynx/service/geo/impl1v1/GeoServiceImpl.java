package com.lynx.service.geo.impl1v1;

import android.content.Context;
import com.lynx.lib.geo.GeoService;
import com.lynx.lib.geo.LocationListener;
import com.lynx.lib.geo.entity.Coord;
import com.lynx.lib.geo.entity.Coord.CoordType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/29/13 11:27 AM
 */
public class GeoServiceImpl implements GeoService, LocationListener {

    private Context context;
    private LocationCenter locationCenter;
    private LocationStatus status;
    private final ArrayList<LocationListener> listeners = new ArrayList<LocationListener>();

    public GeoServiceImpl(Context context) {
        this.context = context;
        locationCenter = new LocationCenter(this);
        status = LocationStatus.IDLE;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        if (locationCenter != null) {
            locationCenter.stop();
        }
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
    public void rgc(double lat, double lng, CoordType type) {

    }

    @Override
    public void addListener(LocationListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(LocationListener listener) {
        listeners.remove(listener);
    }

    @Override
    public List<LocationListener> listeners() {
        return listeners;
    }

    @Override
    public Coord coord() {
        return locationCenter.coord();
    }

    @Override
    public String address() {
        if (locationCenter.address() != null) {
            return locationCenter.address().getStreet() + "(impl1v1)";
        } else {
            return null;
        }
    }

    @Override
    public void onLocationChanged(LocationStatus status) {
        this.status = status;
        for (LocationListener l : listeners) {
            l.onLocationChanged(status);
        }
    }

    public Context context() {
        return context;
    }
}
