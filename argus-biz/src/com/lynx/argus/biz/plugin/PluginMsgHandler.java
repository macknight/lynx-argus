package com.lynx.argus.biz.plugin;

import android.os.Handler;

/**
 * Created with IntelliJ IDEA.
 * User: chris.liu
 * Date: 13-11-16 下午11:10
 */
public abstract class PluginMsgHandler extends Handler {

	/**
	 * 是否对某种消息感兴趣
	 *
	 * @param msg
	 * @return
	 */
	public abstract boolean interested(int msg);

}
