package com.lynx.lib.geo.entity;

import java.io.Serializable;

/**
 * 
 * @author zhufeng.liu
 * 
 * @addtime 13-8-27 下午5:52
 */
public class Wifi implements Serializable {
	private String ssid;
	private String mac;
	private int dBm;

	public Wifi(String ssid, String mac, int dBm) {
		this.ssid = ssid;
		this.mac = mac;
		this.dBm = dBm;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getSsid() {
		return this.ssid;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getMac() {
		return this.mac;
	}

	public void setDBm(int dBm) {
		this.dBm = dBm;
	}

	public int getDBm() {
		return this.dBm;
	}

	@Override
	public String toString() {
		return String.format("%s,%s,%d", this.ssid, this.mac, this.dBm);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Wifi) {
			Wifi wifi = (Wifi) obj;
			if (this.mac.equals(wifi.mac)) {
				return true;
			}
		}
		return false;
	}
}
