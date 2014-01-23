package com.lynx.lib.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 
 * @author chris.liu
 * 
 * @version 14-1-23 上午9:44
 */
public class NetworkUtil {
	private static final String TAG = "NetworkUtil";

	private NetworkUtil() {
		throw new AssertionError("this class shouldn't be instanced");
	}

	public String getNetworkType(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null) {
			// 接入点名称: 此名称可被用户任意更改 如: cmwap,
			return activeNetInfo.getExtraInfo();
		} else {
			// cmnet,
			// internet ...
			return null;
		}
	}

}
