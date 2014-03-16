package com.lynx.lib.core.d;

import com.google.gson.annotations.Expose;
import com.lynx.lib.db.anonation.Id;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-11-17 上午12:37
 */
public class DexModule {
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

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
}
