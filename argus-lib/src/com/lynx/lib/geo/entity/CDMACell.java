package com.lynx.lib.geo.entity;

/**
 * 
 * @author zhufeng.liu
 * 
 * @addtime 13-8-27 下午5:52
 */
public class CDMACell implements Cell {
	private int mcc; // mobile country code
	private int sid; // system id
	private int nid; // network id
	private int bid; // base station id
	private long lat;
	private long lng;

	public CDMACell(int mcc, int sid, int nid, int bid, long lat, long lng) {
		this.mcc = mcc;
		this.sid = sid;
		this.nid = nid;
		this.bid = bid;
		this.lat = lat;
		this.lng = lng;
	}

	public int mcc() {
		return mcc;
	}

	public int sid() {
		return sid;
	}

	public int nid() {
		return nid;
	}

	public int bid() {
		return bid;
	}

	public long lat() {
		return lat;
	}

	public long lng() {
		return lng;
	}

	@Override
	public String toString() {
		return String.format("%s,%s,%s,%s,%s,%s", mcc, sid, nid, bid, lat, lng);
	}

	@Override
	public CellType type() {
		return CellType.CDMA;
	}
}
