package com.lynx.lib.geo.entity;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 
 * @author zhufeng.liu
 * 
 * @addtime 13-9-10 下午1:54
 */
public class GeoPoint implements Serializable {
	private double lat;
	private double lng;

	public GeoPoint(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}

	public double lat() {
		return lat;
	}

	public double lng() {
		return lng;
	}

	@Override
	public String toString() {
		try {
			JSONObject jo = new JSONObject();
			jo.put("lat", lat);
			jo.put("lng", lng);
			return jo.toString();
		} catch (Exception e) {
			return null;
		}
	}
}
