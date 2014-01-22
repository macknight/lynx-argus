package com.lynx.argus.biz.plugin.demo.model;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-11-6 下午4:20
 */
public class ShopListItem {
	private String uid;
	private String name;
	private String addr;
	private String tele;

	public ShopListItem(String uid, String name, String addr, String tele) {
		this.uid = uid;
		this.name = name;
		this.addr = addr;
		this.tele = tele;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getTele() {
		return tele;
	}

	public void setTele(String tele) {
		this.tele = tele;
	}
}
