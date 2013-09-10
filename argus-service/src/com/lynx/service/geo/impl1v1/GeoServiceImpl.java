package com.lynx.service.geo.impl1v1;

import android.content.Context;
import com.lynx.service.geo.GeoService;
import com.lynx.service.geo.LocationListener;
import com.lynx.service.geo.entity.Address;
import com.lynx.service.geo.entity.Coord;
import com.lynx.service.geo.entity.Coord.CoordType;

import java.util.ArrayList;

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
    public Coord coord() {
        return locationCenter.coord();
    }

    @Override
    public Address address() {
        return locationCenter.address();
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
