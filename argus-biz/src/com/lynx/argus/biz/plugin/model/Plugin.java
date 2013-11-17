package com.lynx.argus.biz.plugin.model;

import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: chris.liu
 * Date: 13-11-12
 * Time: 下午6:24
 */
public class Plugin {
	public static final String K_MODULE = "module";
	public static final String K_VERSION = "version";
	public static final String K_NAME = "name";
	public static final String K_ICON = "icon";
	public static final String K_DESC = "desc";
	public static final String K_URL = "url";
	public static final String K_MD5 = "md5";
	public static final String K_CLAZZ = "clazz";
	public static final String K_CATEGORY = "category";

	private String module; // 模块名
	private int version; // 版本号
	private String name; // 插件名
	private String icon; // 插件图标
	private String desc; // 插件介绍
	private String url; // 下载包地址
	private String md5; // md5摘要
	private String clazz; // 入口类名
	private int category; // 分类

	public Plugin(String module, int version, String name, String icon,
	              String desc, String url, String md5, String clazz, int category) {
		this.module = module;
		this.version = version;
		this.name = name;
		this.icon = icon;
		this.desc = desc;
		this.url = url;
		this.md5 = md5;
		this.clazz = clazz;
		this.category = category;
	}

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

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
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

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public JSONObject obj2json() {
		try {
			JSONObject jo = new JSONObject();
			jo.put("name", name);
			jo.put("icon", icon);
			jo.put("desc", desc);
			jo.put("url", url);
			jo.put("md5", md5);
			jo.put("clazz", clazz);
			jo.put("category", category);
			return jo;
		} catch (Exception e) {

		}
		return null;
	}
}
