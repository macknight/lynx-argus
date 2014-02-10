package com.lynx.lib.geo.entity;

import com.lynx.lib.core.Logger;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-8-27 下午5:53
 */
public class Location implements Serializable {
	private Coord coord;
	private Address addr;

	public Location() {
	}

	public Location(Coord coord, Address addr) {
		this.coord = coord;
		this.addr = addr;
	}

	public Coord coord() {
		return coord;
	}

	public Address addr() {
		return addr;
	}

	@Override
	public String toString() {
		try {
			JSONObject jo = new JSONObject();
			jo.put("coord", coord.toString());
			jo.put("addr", addr.toString());
			return jo.toString();
		} catch (Exception e) {
			Logger.e("Location", "location object serialized error", e);
			return null;
		}
	}
}
