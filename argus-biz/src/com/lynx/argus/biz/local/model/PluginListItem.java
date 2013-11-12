package com.lynx.argus.biz.local.model;

/**
 * Created with IntelliJ IDEA.
 * User: chris.liu
 * Date: 13-11-12
 * Time: 下午6:24
 */
public class PluginListItem {
	private String name; // 插件名
	private String desc; //插件介绍
	private String img; // 插件图标

	public PluginListItem(String name, String desc, String img) {
		this.name = name;
		this.desc = desc;
		this.img = img;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}
}
