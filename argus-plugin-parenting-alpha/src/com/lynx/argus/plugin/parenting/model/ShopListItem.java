package com.lynx.argus.plugin.parenting.model;

import com.lynx.lib.geo.entity.GeoPoint;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-11-22 下午4:59
 */
public class ShopListItem {

	private String storeName;
	private String storeId;
	private String shopName;
	private String shopId;
	private String snapUrl;
	private GeoPoint latlng;
	private String region;
	private int reviewNum;

	public ShopListItem(String storeId, String storeName, String shopId,
			String shopName, String snapUrl, GeoPoint latlng, String region,
			int reviewNum) {
		this.storeId = storeId;
		this.storeName = storeName;
		this.shopId = shopId;
		this.shopName = shopName;
		this.snapUrl = snapUrl;
		this.latlng = latlng;
		this.region = region;
		this.reviewNum = reviewNum;
	}

	public String storeId() {
		return storeId;
	}

	public String storeName() {
		return storeName;
	}

	public String shopId() {
		return shopId;
	}

	public String shopName() {
		return storeName;
	}

	public String snapUrl() {
		return snapUrl;
	}

	public GeoPoint latlng() {
		return latlng;
	}

	public String region() {
		return region;
	}

	public int reviewNum() {
		return reviewNum;
	}
}
