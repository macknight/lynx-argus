package com.lynx.argus.plugin.chat.util;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

/**
 * Created by chris.liu Date: 13-12-12 下午9:20
 */
public class XMPPUtil {
	private static XMPPConnection conn = null;

	private static void openConnection() {
		try {
			ConnectionConfiguration connConfig = new ConnectionConfiguration(
					"192.168.0.102", 9091);
			conn = new XMPPConnection(connConfig);
			conn.connect();
		} catch (XMPPException xe) {
			xe.printStackTrace();
		}
	}

	public static XMPPConnection getConnection() {
		if (conn == null) {
			openConnection();
		}
		return conn;
	}

	public static void closeConnection() {
		conn.disconnect();
		conn = null;
	}
}
