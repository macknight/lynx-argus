package com.lynx.argus.plugin.parenting.model;

import com.lynx.lib.geo.entity.GeoPoint;

/**
 * Created with IntelliJ IDEA.
 * User: chris.liu
 * Date: 13-11-22
 * Time: 下午4:59
 */
public class ShopListItem {
	private String shopId;
	private String name;
	private String snapUrl;
	private GeoPoint latlng;
	private String region;
	private int reviewNum;

	public ShopListItem(String shopId, String name, String snapUrl,
	                    GeoPoint latlng, String region, int reviewNum) {
		this.shopId = shopId;
		this.name = name;
		this.snapUrl = snapUrl;
		this.latlng = latlng;
		this.region = region;
		this.reviewNum = reviewNum;
	}

	public String getName() {
		return name;
	}

	public String getShopId() {
		return shopId;
	}

	public String getSnapUrl() {
		return snapUrl;
	}

	public GeoPoint getLatlng() {
		return latlng;
	}

	public String getRegion() {
		return region;
	}

	public int getReviewNum() {
		return reviewNum;
	}
}
