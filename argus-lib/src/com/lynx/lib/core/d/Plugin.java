package com.lynx.lib.core.d;

import com.google.gson.annotations.Expose;
import com.lynx.lib.db.anonation.Table;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-11-17 上午12:39
 */
@Table(name = "my_plugin")
public class Plugin extends DexModule {
	@Expose
	private String name; // 插件名

	@Expose
	private String icon; // 图标

	@Expose
	private int category; // 分类

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}
}
