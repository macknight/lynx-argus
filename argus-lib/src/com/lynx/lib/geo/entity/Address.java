package com.lynx.lib.geo.entity;

import java.io.Serializable;

/**
 * 
 * @author zhufeng.liu
 * 
 * @addtime 13-8-27 下午5:53
 */
public class Address implements Serializable {
	private String province;
	private String city;
	private String region;
	private String street;
	private String number;

	public Address(String province, String city, String region, String street,
			String number) {
		this.province = province;
		this.city = city;
		this.region = region;
		this.street = street;
		this.number = number;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	@Override
	public String toString() {
		return null;
	}
}
