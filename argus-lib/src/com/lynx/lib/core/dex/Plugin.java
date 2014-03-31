package com.lynx.lib.core.dex;

import com.google.gson.annotations.Expose;
import com.lynx.lib.db.anonation.Id;
import com.lynx.lib.db.anonation.Table;

/**
 * 
 * @author zhufeng.liu
 * @version 13-11-17 上午12:39
 */
@Table(name = "my_plugin")
public class Plugin extends DexModule {
	@Id
	@Expose
	private String module; // 模块名
	@Expose
	private int version = -1; // 版本号
	@Expose
	private String url;
	@Expose
	private String md5; // md5摘要
	@Expose
	private String desc; // 动态模块描述
	@Expose
	private String clazz; // 入口类名
	@Expose
	private String name; // 插件名
	@Expose
	private String icon; // 图标
	@Expose
	private int category; // 分类

	@Override
	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	@Override
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	@Override
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

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
