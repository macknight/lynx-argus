package com.lynx.argus.biz.misc;

/**
 * @author zhufeng.liu
 * @version 14-2-10 下午6:20
 */
public class EncryptUtil {

	static {
		// 加载库文件
		System.loadLibrary("encrypt.so");
	}

	private native String decrypt(String input);

    private native String encrypt(String input);
}
