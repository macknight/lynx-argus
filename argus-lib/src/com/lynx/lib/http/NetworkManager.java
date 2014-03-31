package com.lynx.lib.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

/**
 * 
 * @author zhufeng.liu
 * @version 2013-4-17 下午11:03:01
 */
public class NetworkManager {
	private ConnectivityManager connManager;

	public NetworkManager(Context context) {
		connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	public NetworkState state() {
		// wifi
		State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (state == State.CONNECTED || state == State.CONNECTING)
			return NetworkState.NETWORK_WIFI;

		// mobi
		state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		if (state == State.CONNECTED || state == State.CONNECTING) {
			return NetworkState.NETWORK_MOBI;
		}

		return NetworkState.NETWORK_NONE;
	}

	public static enum NetworkState {
		NETWORK_NONE, NETWORK_WIFI, NETWORK_MOBI;
	}
}
