package com.lynx.lib.core;

import android.util.Log;

/**
 * Logger控制管理app日志输出level及输出，提供三种类型log输出i(info),w(waring),e(error)
 * 
 * @author zhufeng.liu
 * 
 * @addtime 13-11-25 下午2:35
 */
public class Logger {

	// app 所处模式,默认为PRODUCT
	private static AppLevel level = AppLevel.PRODUCT;

	private Logger() {
		throw new AssertionError("this class shouldn't be instanced");
	}

	public static AppLevel level() {
		return level;
	}

	public static void setLevel(AppLevel appLevel) {
		level = appLevel;
	}

	public static int i(String tag, String msg) {
		return level.ordinal() >= AppLevel.DEBUG.ordinal() ? Log.i(tag, msg)
				: -1;
	}

	public static int i(String tag, Throwable tr) {
		return level.ordinal() >= AppLevel.DEBUG.ordinal() ? Log.i(tag, "", tr)
				: -1;
	}

	public static int i(String tag, String msg, Throwable tr) {
		return level.ordinal() >= AppLevel.DEBUG.ordinal() ? Log
				.i(tag, msg, tr) : -1;
	}

	public static int w(String tag, String msg) {
		return level.ordinal() >= AppLevel.TEST.ordinal() ? Log.w(tag, msg)
				: -1;
	}

	public static int w(String tag, Throwable tr) {
		return level.ordinal() >= AppLevel.TEST.ordinal() ? Log.w(tag, tr) : -1;
	}

	public static int w(String tag, String msg, Throwable tr) {
		return level.ordinal() >= AppLevel.TEST.ordinal() ? Log.w(tag, msg, tr)
				: -1;
	}

	public static int e(String tag, String msg) {
		return level.ordinal() >= AppLevel.TEST.ordinal() ? Log.e(tag, msg)
				: -1;
	}

	public static int e(String tag, String msg, Throwable tr) {
		return level.ordinal() >= AppLevel.TEST.ordinal() ? Log.e(tag, msg, tr)
				: -1;
	}

	public enum AppLevel {
		PRODUCT, // 产品发布模式,关闭所有log输出和隐藏配置面板
		TEST, // 测试模式,开启错误、警告log输出和隐藏配置面板
		DEBUG // 调试模式,开启所有log输出和隐藏配置面板
	}
}
