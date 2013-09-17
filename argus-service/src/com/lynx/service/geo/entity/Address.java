package com.lynx.service.geo.entity;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/27/13 5:53 PM
 */
public class Address implements Serializable {
    private String province;
    private String city;
    private String region;
    private String street;
    private String number;

    public Address(String province, String city, String region, String street, String number) {
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
}
