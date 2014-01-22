package com.lynx.lib.geo.entity;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-8-27 下午5:53
 */
public class GSMCell implements Cell {
	private int mcc; // mobile country code
	private int mnc; // mobile network code
	private int lac; // com.lynx.argus.biz.plugin.plugin area code
	private int cid; // cell id
	private int asu; // single strength

	public GSMCell(int mcc, int mnc, int lac, int cid, int asu) {
		this.mcc = mcc;
		this.mnc = mnc;
		this.lac = lac;
		this.cid = cid;
		this.asu = asu;
	}

	public int mcc() {
		return mcc;
	}

	public int mnc() {
		return mnc;
	}

	public int lac() {
		return lac;
	}

	public int cid() {
		return cid;
	}

	public int asu() {
		return asu;
	}

	@Override
	public String toString() {
		return String.format("%s,%s,%s,%s,%s", mcc, mnc, lac, cid, asu);
	}

	@Override
	public CellType type() {
		return CellType.GSM;
	}
}
