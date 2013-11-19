package com.lynx.lib.http.handler;

/**
 * 
 * @author chris.liu
 * @name EntityCallback.java
 * @update 2013-4-17 下午10:51:41
 * 
 */
public interface EntityCallback {
	void callback(long count, long current, boolean mustNotifyUI);
}
