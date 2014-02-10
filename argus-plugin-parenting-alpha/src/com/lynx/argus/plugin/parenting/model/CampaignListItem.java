package com.lynx.argus.plugin.parenting.model;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-11-22 下午5:00
 */
public class CampaignListItem {
	private String id;
	private String name;
	private String shopId;
	private String shopName;
	private String price;
	private String snapUrl;
	private String startTime;
	private String endTime;
	private String place;
	private String region;

	public CampaignListItem(String id, String name, String shopId, String shopName, String price,
			String snapUrl, String startTime, String endTime, String place, String region) {
		this.id = id;
		this.name = name;
		this.shopId = shopId;
		this.shopName = shopName;
		this.price = price;
		this.snapUrl = snapUrl;
		this.startTime = startTime;
		this.endTime = endTime;
		this.place = place;
		this.region = region;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getShopName() {
		return shopName;
	}

	public String getShopId() {
		return shopId;
	}

	public String getPrice() {
		return price;
	}

	public String getSnapUrl() {
		return snapUrl;
	}

	public String getStartTime() {
		return startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public String getPlace() {
		return place;
	}

	public String getRegion() {
		return region;
	}
}
