package com.lynx.argus.plugin.chat.util;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

/**
 * 
 * @author zhufeng.liu
 * 
 * @addtime 13-12-12 下午9:20
 */
public class XMPPUtil {
	private static XMPPConnection conn = null;

	private static void openConnection() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ConnectionConfiguration connConfig = new ConnectionConfiguration(
							"192.168.0.100", 5222);
					conn = new XMPPConnection(connConfig);
					conn.connect();
				} catch (XMPPException e) {
					closeConnection();
				}
			}
		}).start();
	}

	public static XMPPConnection getConnection() {
		if (conn == null) {
			openConnection();
		}
		return conn;
	}

	public static void closeConnection() {
		if (conn != null) {
			conn.disconnect();
			conn = null;
		}
	}
}
