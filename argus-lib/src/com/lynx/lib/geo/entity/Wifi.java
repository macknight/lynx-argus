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

	public String ssid() {
		return this.ssid;
	}

	public String mac() {
		return this.mac;
	}

	public int dBm() {
		return this.dBm;
	}

	@Override
	public String toString() {
		return String.format("%s,%s,%s", this.ssid, this.mac, this.dBm);
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
