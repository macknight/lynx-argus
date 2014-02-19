package com.lynx.argus.biz.search.model;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 2014-2-10 下午19:41:01
 */
public class SuggestionItem {
	private String name;
	private String district;

	public SuggestionItem(String name, String district) {

		this.name = name;
		this.district = district;
	}

	public String name() {
		return name;
	}

	public String district() {
		return district;
	}
}
