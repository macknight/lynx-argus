package com.lynx.lib.watchdog;

import com.lynx.lib.core.dex.Service;

/**
 * 线上bug fix入口
 * 
 * @author zhufeng.liu
 * 
 * @version 13-9-12 下午1:34
 */
public interface WatchdogService extends Service {

	/**
	 * 启动后只执行一次的修复逻辑
	 */
	public void executeOnce();

	/**
	 * 每次启动都执行的修复逻辑
	 */
	public void execute();
}