package com.lynx.lib.core.dex;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-11-17 上午12:37
 */
public class DexModule {
	public static final String K_MODULE = "module"; // 模块名
	public static final String K_VERSION = "version"; // 版本
	public static final String K_URL = "url"; // module包下载地址
	public static final String K_MD5 = "md5"; // module包md5摘要
	public static final String K_DESC = "desc"; // module描述
	public static final String K_CLAZZ = "clazz"; // 入口类名

	private String module; // 模块名
	private int version = -1; // 版本号
	private String url;
	private String md5; // md5摘要
	private String desc; // 动态模块描述
	private String clazz; // 入口类名

	public DexModule(String module, int version, String url, String md5,
			String desc, String clazz) {
		this.module = module;
		this.version = version;
		this.url = url;
		this.md5 = md5;
		this.desc = desc;
		this.clazz = clazz;
	}

	public String module() {
		return module;
	}

	public int version() {
		return version;
	}

	public String url() {
		return url;
	}

	public String md5() {
		return md5;
	}

	public String desc() {
		return desc;
	}

	public String clazz() {
		return clazz;
	}
}
