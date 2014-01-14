package com.lynx.lib.geo.entity;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 
 * @author zhufeng.liu
 * 
 * @addtime 13-8-27 下午5:53
 */
public class Coord implements Serializable {
	private CoordSource source;
	private double lat;
	private double lng;
	private int acc;
	private long elapse;

	public Coord() {

	}

	public Coord(CoordSource source, double lat, double lng, int acc,
			long elapse) {
		this.source = source;
		this.lat = lat;
		this.lng = lng;
		this.acc = acc;
		this.elapse = elapse;
	}

	public CoordSource source() {
		return source;
	}

	public double lat() {
		return lat;
	}

	public double lng() {
		return lng;
	}

	public int acc() {
		return acc;
	}

	public long elapse() {
		return elapse;
	}

	@Override
	public String toString() {
		try {
			JSONObject jo = new JSONObject();
			jo.put("source", source);
			jo.put("lat", lat);
			jo.put("lng", lng);
			jo.put("acc", acc);
			jo.put("elapse", elapse);
			return jo.toString();
		} catch (Exception e) {
			return null;
		}
	}

	public enum CoordType {
		UNKNOWN, GPS, AMAP, BMAP, GMAP
	}

	public enum CoordSource {
		UNKNOWN, GPS, // 手机GPS芯片定位结果
		NETWORK, // android系统定位结果
		GMAP, // 谷歌地图定位结果
		BMAP, // 百度地图定位结果
		AMAP, // 高德地图定位结果
		LMAP; // lynx定位结果
	}
}
