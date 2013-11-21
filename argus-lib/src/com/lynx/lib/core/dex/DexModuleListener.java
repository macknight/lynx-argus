package com.lynx.lib.core.dex;

/**
 * 插件安装、更新回调
 * <p/>
 * Created with IntelliJ IDEA.
 * User: chris.liu
 * Date: 13-11-21
 * Time: 下午8:30
 */
public interface DexModuleListener {
	public static final int DEX_HAS_UPDATE = 0; // 有更新
	public static final int DEX_DOWNLOAD_SUCCESS = 1; // 动态模块下载成功
	public static final int DEX_DOWNLOAD_FAIL = 2; // 动态模块下载失败
	public static final int DEX_INSTALL_SUCCESS = 3; // 动态模块安装成功
	public static final int DEX_INSTALL_FAIL = 4; // 动态模块安装失败
	public static final int DEX_UNINSTALL_SUCCESS = 5; // 动态模块卸载成功
	public static final int DEX_UNINSTALL_FAIL = 6; // 动态模块卸载失败

	void onStatusChanged(int status);

}
