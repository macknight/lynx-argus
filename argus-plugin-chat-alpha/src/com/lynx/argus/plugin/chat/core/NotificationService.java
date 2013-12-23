package com.lynx.argus.plugin.chat.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import android.os.SystemClock;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Packet;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.*;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.provider.SyncStateContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * @author: chris.liu
 * @addtime: 13-12-23 下午5:15
 */
public class NotificationService extends Service {
	private static final int HEARTBEAT_INTERVAL = 30000;
	public static final String SERVICE_NAME = "com.lynx.argus.plugin.chat.core.NotificationService";
	private TelephonyManager teleManager;
	private BroadcastReceiver connectivityReceiver;
	private PhoneStateListener phoneStateListener;
	private ExecutorService executorService;
	private TaskSubmitter taskSubmitter;
	private TaskTracker taskTracker;
	private XMPPManager xmppManager;
	private SharedPreferences sharedPreferences;
	private static Context context;
	private AlarmManager alarmManager;
	private XMPPHeartbeat heartbeatAlarm;

	@Override
	public void onCreate() {
		context = this;
		teleManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		sharedPreferences = getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);

		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(Constants.DEVICE_ID, deviceId);
		editor.commit();

		xmppManager = new XMPPManager(this);
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public XMPPManager getXMPPManager() {
		return xmppManager;
	}

    public TaskSubmitter getTaskSubmitter() {
        return taskSubmitter;
    }

	public TaskTracker getTaskTracker() {
		return taskTracker;
	}

	public static void sendConnectionStatus(boolean isConnected) {
		Intent it = new Intent();
		it.setAction("android.intent.xmppconnection.isconnected");
		it.putExtra("isConnected", isConnected);
		context.sendBroadcast(it);
	}

	public void connect() {
		taskSubmitter.submit(new Runnable() {
			@Override
			public void run() {
				NotificationService.this.getXMPPManager().connect();
				startHeartbeat();
			}
		});
	}

	public void disconnect() {
		taskSubmitter.submit(new Runnable() {
			@Override
			public void run() {
				NotificationService.this.getXMPPManager().disconnect();
				stopHeartbeat();
			}
		});
	}

	public boolean isConnected() {
		return getXMPPManager().getConnection().isConnected();
	}

	private void startHeartbeat() {
		long time = SystemClock.elapsedRealtime() + HEARTBEAT_INTERVAL;
		Intent i = new Intent(SyncStateContract.Constants.ACCOUNT_NAME);
		PendingIntent pi = PendingIntent.getBroadcast(NotificationService.this,
				0, i, 0);
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, time,
				HEARTBEAT_INTERVAL, pi);
	}

	private void stopHeartbeat() {
		Intent i = new Intent(CONNECTIVITY_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcast(NotificationService.this,
				0, i, 0);
		alarmManager.cancel(pi);
	}

	private void registerHeartbeatAlarmReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(SyncStateContract.Constants.ACCOUNT_NAME);
		registerReceiver(heartbeatAlarm, filter);
	}

	private void unregisterHeartbeatAlarmReceiver() {
		unregisterReceiver(heartbeatAlarm);
	}

	private void registerConnectivityReceiver() {
		teleManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(connectivityReceiver, filter);
	}

	private void unregisterConnectivityReceiver() {
		teleManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
		unregisterReceiver(connectivityReceiver);
	}

	private void start() {
		xmppManager.connect();
	}

	private void stop() {
		xmppManager.disconnect();
		executorService.shutdown();
	}

	public void sendXMPPHeartbeatPacket() {
		XMPPConnection conn = xmppManager.getConnection();
		if (conn != null && conn.isConnected()) {
			xmppManager.getConnection().sendPacket(new HeartbeatPacket());
		}
		sendConnectionStatus(conn.isConnected());
	}

	public class HeartbeatPacket extends Packet {
		@Override
		public String toXML() {
			return " ";
		}
	}

	public class TaskSubmitter {
		final NotificationService notificationService;

		public TaskSubmitter(NotificationService notificationService) {
			this.notificationService = notificationService;
		}

		public Future<?> submit(Runnable task) {
			Future<?> result = null;
			if (!notificationService.getExecutorService().isTerminated()
					&& !notificationService.getExecutorService().isShutdown()
					&& task != null) {
				result = notificationService.getExecutorService().submit(task);
			}
			return result;
		}
	}

	public class TaskTracker {
		final NotificationService notificationService;

		public int count;

		public TaskTracker(NotificationService notificationService) {
			this.notificationService = notificationService;
			this.count = 0;
		}

		public void increase() {
			synchronized (notificationService) {
				notificationService.getTaskTracker().count++;
			}
		}

		public void decrease() {
			synchronized (notificationService) {
				notificationService.getTaskTracker().count--;
			}
		}
	}
}
