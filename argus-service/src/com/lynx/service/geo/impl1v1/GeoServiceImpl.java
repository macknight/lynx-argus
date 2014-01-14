package com.lynx.service.geo.impl1v1;

import android.content.Context;
import com.lynx.lib.geo.GeoService;
import com.lynx.lib.geo.LocationListener;
import com.lynx.lib.geo.entity.Address;
import com.lynx.lib.geo.entity.Coord;
import com.lynx.lib.geo.entity.Coord.CoordType;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author zhufeng.liu
 * 
 * @addtime 13-8-29 下午11:27
 */
public class GeoServiceImpl implements GeoService, LocationListener {
	public static final String Tag = "geo";
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
