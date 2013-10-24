package com.lynx.lib.geo.entity;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-10 下午1:54
 */
public class GeoPoint implements Serializable {
    private double lat;
    private double lng;

    public GeoPoint(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
