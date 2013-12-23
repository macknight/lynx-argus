package com.lynx.argus.plugin.chat.core;

/**
 * @author: chris.liu
 * @addtime: 13-12-23 下午7:33
 */
public class ReconnectionThread extends Thread {
	private final XMPPManager xmppManager;
	private int waiting;

	public ReconnectionThread(XMPPManager xmppManager) {
		this.xmppManager = xmppManager;
		this.waiting = 0;
	}

	public void run() {
		waiting = 0;
		try {
			while (!isInterrupted()) {
				Thread.sleep((long) waiting() * 1000L);
				xmppManager.connect();
				waiting++;
			}
		} catch (final InterruptedException e) {
			xmppManager.getHandler().post(new Runnable() {
				public void run() {
					xmppManager.getConnectionListener().reconnectionFailed(e);
				}
			});
		}
	}

	private int waiting() {
		if (waiting > 20) {
			return 600;
		}
		if (waiting > 13) {
			return 300;
		}
		return waiting <= 7 ? 10 : 60;
	}
}
