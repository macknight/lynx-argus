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

	public int getResId() {
		return resId;
	}

	public void setResId(int resId) {
		this.resId = resId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isHeader() {
		return isHeader;
	}

	public void setHeader(boolean header) {
		isHeader = header;
	}
}
