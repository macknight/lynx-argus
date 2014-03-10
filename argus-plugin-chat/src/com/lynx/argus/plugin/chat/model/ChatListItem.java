package com.lynx.argus.plugin.chat.model;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-12-7 下午11:41
 */
public class ChatListItem {
	private String from;
	private String summary;
	private long date;

	public ChatListItem(String from, String summary, long date) {
		this.from = from;
		this.summary = summary;
		this.date = date;
	}

	public String from() {
		return from;
	}

	public String summary() {
		return summary;
	}

	public long date() {
		return date;
	}
}
