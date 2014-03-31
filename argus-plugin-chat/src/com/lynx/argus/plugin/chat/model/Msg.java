package com.lynx.argus.plugin.chat.model;

/**
 * 
 * @author zhufeng.liu
 * @version 13-12-7 下午8:06
 */
public class Msg {
	private String uid;
	private String content;
	private long date;
	private boolean from;

	public Msg(String uid, String content, long date, boolean from) {
		this.uid = uid;
		this.content = content;
		this.date = date;
		this.from = from;
	}

	public String uid() {
		return uid;
	}

	public String content() {
		return content;
	}

	public long date() {
		return date;
	}

	public boolean from() {
		return from;
	}
}
