package com.lynx.argus.biz.more.model;

/**
 * 
 * @author zhufeng.liu
 * 
 * @addtime 13-11-11 下午5:15
 */
public class GroupListItem {
	private int resId;
	private String title;
	private boolean isHeader;

	public GroupListItem(int resId, String title, boolean isHeader) {
		this.resId = resId;
		this.title = title;
		this.isHeader = isHeader;
	}

	public int resId() {
		return resId;
	}

	public String title() {
		return title;
	}

	public boolean isHeader() {
		return isHeader;
	}

}
