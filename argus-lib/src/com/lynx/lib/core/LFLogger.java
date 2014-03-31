package com.lynx.lib.core;

import android.util.Log;

import com.lynx.lib.util.FileUtil;

import java.util.Date;

/**
 * Logger控制管理app日志输出level及输出，提供三种类型log输出i(info),w(waring),e(error)
 * 
 * @author zhufeng.liu
 * @version 13-11-25 下午2:35
 */
public class LFLogger {

	// app 所处模式,默认为PRODUCT
	private static AppLevel level = AppLevel.PRODUCT;

	private LFLogger() {
		throw new AssertionError("this class shouldn't be instanced");
	}

	public static AppLevel level() {
		return level;
	}

	public static void setLevel(AppLevel appLevel) {
		level = appLevel;
	}

	public static int i(String tag, String msg) {
		return level.ordinal() >= AppLevel.DEBUG.ordinal() ? Log.i(tag, msg) : -1;
	}

	public static int i(String tag, String msg, Throwable tr) {
		return level.ordinal() >= AppLevel.DEBUG.ordinal() ? Log.i(tag, msg, tr) : -1;
	}

	public static int w(String tag, String msg) {
		writeLog2File(tag, msg);
		return level.ordinal() >= AppLevel.TEST.ordinal() ? Log.w(tag, msg) : -1;
	}

	public static int w(String tag, String msg, Throwable tr) {
		writeLog2File(tag, msg, tr);
		return level.ordinal() >= AppLevel.TEST.ordinal() ? Log.w(tag, msg, tr) : -1;
	}

	public static int e(String tag, String msg) {
		writeLog2File(tag, msg);
		return level.ordinal() >= AppLevel.TEST.ordinal() ? Log.e(tag, msg) : -1;
	}

	public static int e(String tag, String msg, Throwable tr) {
		writeLog2File(tag, msg, tr);
		return level.ordinal() >= AppLevel.TEST.ordinal() ? Log.e(tag, msg, tr) : -1;
	}

	private static void writeLog2File(String tag, String msg) {
		writeLog2File(tag, msg, null);
	}

	private static void writeLog2File(String tag, String msg, Throwable tr) {
		try {
			StackTraceElement[] elements = tr.getStackTrace();
			String stacks = String.format("[%s:%s:%s]", new Date(), tag, msg);
			for (StackTraceElement element : elements) {
				stacks = String.format("%s\n%s", stacks, element);
			}
			FileUtil.writeToExternalStoragePublic("argus.log", stacks.getBytes(LFConst.DEF_CHARSET));
		} catch (Throwable t) {
			LFLogger.i("logger", "logger write to file error");
		}
	}

	public enum AppLevel {
		PRODUCT, // 产品发布模式,关闭所有log输出和隐藏配置面板
		TEST, // 测试模式,开启错误、警告log输出和隐藏配置面板
		DEBUG // 调试模式,开启所有log输出和隐藏配置面板
	}
}
