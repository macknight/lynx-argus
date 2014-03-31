package com.lynx.lib.core.dex;

import com.google.gson.annotations.Expose;

/**
 * @author chris.liu
 * @version 3/15/14 10:22 PM
 */
public class Service extends DexModule {
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
}
