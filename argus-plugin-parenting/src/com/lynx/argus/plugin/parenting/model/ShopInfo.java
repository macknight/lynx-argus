package com.lynx.argus.plugin.parenting.model;

import com.lynx.lib.geo.entity.GeoPoint;

import java.io.Serializable;

/**
 * 商户信息
 * 
 * @author zhufeng.liu
 * 
 * @version 14-2-11 下午4:45
 */
public class ShopInfo implements Serializable {
	private String storeName; // 商户名
	private String storeId; // 商户Id
	private String shopName; // 门店名
	private String shopId; // 门店Id
	private String snapUrl; // 截图
	private double rate; // 星级
	private double avgConsume; // 人均
	private GeoPoint latlng;
	private String tele;
	private String region; // 商区
	private int reviewNum; // 点评数
	private String address; // 地址
	private String workTime; // 工作时间
	private String transport; // 交通路线
	private GoodsInfo[] goods; // 产品
	private int imageNum; // 图片数
	private String[] imageUrls; // 图片地址

	public ShopInfo() {

	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public String getSnapUrl() {
		return snapUrl;
	}

	public void setSnapUrl(String snapUrl) {
		this.snapUrl = snapUrl;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public double getAvgConsume() {
		return avgConsume;
	}

	public void setAvgConsume(double avgConsume) {
		this.avgConsume = avgConsume;
	}

	public GeoPoint getLatlng() {
		return latlng;
	}

	public void setLatlng(GeoPoint latlng) {
		this.latlng = latlng;
	}

	public String getTele() {
		return tele;
	}

	public void setTele(String tele) {
		this.tele = tele;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public int getReviewNum() {
		return reviewNum;
	}

	public void setReviewNum(int reviewNum) {
		this.reviewNum = reviewNum;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getWorkTime() {
		return workTime;
	}

	public void setWorkTime(String workTime) {
		this.workTime = workTime;
	}

	public String getTransport() {
		return transport;
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}

	public GoodsInfo[] getGoods() {
		return goods;
	}

	public void setGoods(GoodsInfo[] goods) {
		this.goods = goods;
	}

	public int getImageNum() {
		return imageNum;
	}

	public void setImageNum(int imageNum) {
		this.imageNum = imageNum;
	}

	public String[] getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(String[] imageUrls) {
		this.imageUrls = imageUrls;
	}
}
