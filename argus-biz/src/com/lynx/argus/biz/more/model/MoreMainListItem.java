package com.lynx.argus.biz.more.model;

/**
 * 
 * @author zhufeng.liu
 * @version 14-1-29 上午11:22
 */
public class MoreMainListItem {
	private String title;
	private String desc;
	private String methodName;

	public MoreMainListItem(String title, String desc, String methodName) {
		this.title = title;
		this.desc = desc;
		this.methodName = methodName;
	}

	public String title() {
		return title;
	}

	public String desc() {
		return desc;
	}

	public String methodName() {
		return methodName;
	}

}
