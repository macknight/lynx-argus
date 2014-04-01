package com.lynx.lib.geo.entity;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * 
 * @author zhufeng.liu
 * @version 13-8-27 下午5:53
 */
public class Address implements Serializable {
    @Expose
	private String province;
    @Expose
    private String city;
    @Expose
    private String region;
    @Expose
    private String street;
    @Expose
    private String more;

	public Address(String province, String city, String region, String street, String more) {
		this.province = province;
		this.city = city;
		this.region = region;
		this.street = street;
		this.more = more;
	}

	public String province() {
		return province;
	}

	public String city() {
		return city;
	}

	public String region() {
		return region;
	}

	public String street() {
		return street;
	}

	public String more() {
		return more;
	}

	@Override
	public String toString() {
		return String.format("%s%s", region, street);
	}
}
