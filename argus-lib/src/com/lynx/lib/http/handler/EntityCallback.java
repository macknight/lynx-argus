package com.lynx.lib.http.handler;

/**
 * 
 * @author zhufeng.liu
 * 
 * @addtime 2013-4-17 下午10:51:41
 */
public interface EntityCallback {
	void callback(long count, long current, boolean mustNotifyUI);
}
