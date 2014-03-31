package com.lynx.lib.core.dex;

/**
 * 
 * @author zhufeng.liu
 * @version 13-11-17 上午12:37
 */
public abstract class DexModule {

	public abstract String getModule();

	public abstract int getVersion();

	public abstract String getUrl();

	public abstract String getMd5();

	public abstract String getDesc();

	public abstract String getClazz();
}
