package com.lynx.lib.core.dex;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-11-17 上午12:39
 */
public class Plugin extends DexModule {
	public static final String K_NAME = "name";
	public static final String K_ICON = "icon";
	public static final String K_CATEGORY = "category";

	private String name;
	private String icon;
	private int category;

	public Plugin(String module, int version, String name, String icon,
			String url, String md5, String desc, String clazz, int category) {
		super(module, version, url, md5, desc, clazz);
		this.name = name;
		this.icon = icon;
		this.category = category;
	}

	public String name() {
		return name;
	}

	public String icon() {
		return icon;
	}

	public int category() {
		return category;
	}

}
