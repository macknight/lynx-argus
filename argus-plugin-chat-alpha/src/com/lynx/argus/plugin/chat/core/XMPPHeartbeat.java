package com.lynx.argus.plugin.chat.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author: chris.liu
 * @addtime: 13-12-23 下午7:23
 */
public class XMPPHeartbeat extends BroadcastReceiver {
	NotificationService notificationService;

	public XMPPHeartbeat(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (notificationService != null) {
			notificationService.sendXMPPHeartbeatPacket();
		}
	}
}
