package com.lynx.lib.geo.entity;

import java.io.Serializable;

/**
 * 
 * @author zhufeng.liu
 * 
 * @addtime 13-8-27 下午5:53
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
		return String.format("{coord:%s,addr:%s}", this.coord.toString(),
				this.addr.toString());
	}
}
