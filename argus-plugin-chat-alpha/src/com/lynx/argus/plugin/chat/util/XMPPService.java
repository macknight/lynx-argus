package com.lynx.argus.plugin.chat.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.lynx.argus.plugin.chat.ChatFragment;
import com.lynx.argus.plugin.chat.LoginFragment;
import com.lynx.argus.plugin.chat.R;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collection;

/**
 *
 * @author zhufeng.liu
 *
 * @addtime 13-12-23 下午1:20
 */
public class XMPPService extends Service {
	private static final String TAG = "XMPPService";

	private LoginFragment loginFragment = null;
	private XMPPConnection connection;
	private Roster roster;
	private PacketFilter presenceHandler;
	private PacketFilter messageHandler;

	public XMPPService() {
		super();
		Log.i(TAG, "XMPPService constructed");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		connection = loginFragment.setConnection();
		if (connection != null) {
			// set connection handlers and listeners
			roster = loginFragment.getRoster();
			setPresenceHandler();
			setRosterListener();
			addMessageHandler();
		}

		Log.i(TAG, "onCreated");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.i(TAG, "onStartCommand");

		// get the connection
		loginFragment.setConnection(connection);

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroyed");
	}

	// notification for a contact request
	@SuppressWarnings("deprecation")
	public void onSubscribe(String from) {
		int icon = R.drawable.add_menu;
		CharSequence text = "Contact Notification";
		long when = System.currentTimeMillis();

		CharSequence info = from + " adds you to his/her contacts";

		NotificationManager notifier = (NotificationManager) XMPPService.this
				.getSystemService(Context.NOTIFICATION_SERVICE);

//		Notification notification = new Notification(icon, text, when);
//
//		Intent pending = new Intent(this, RequestActivity.class);
//		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//				pending, 0);
//
//		notification.setLatestEventInfo(this, "Contact Request", info,
//				contentIntent);
//
//		notification.flags |= Notification.FLAG_AUTO_CANCEL;
//		notification.flags |= Notification.DEFAULT_SOUND;
//
//		notifier.notify(0x007, notification);
	}

	// notification for a received message
	public void onMessage(String from, String body) {
		int icon = R.drawable.chat_menu;
		CharSequence text = "Message Received";
		long when = System.currentTimeMillis();

		CharSequence info = from + " sends you a message.";

		NotificationManager notifier = (NotificationManager) XMPPService.this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = new Notification(icon, text, when);

		Intent pending = new Intent(this, ChatFragment.class);
		pending.putExtra("target", from);
		pending.putExtra("msg", body);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				pending, 0);

		notification.setLatestEventInfo(this, "Message Received", info,
				contentIntent);

		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.DEFAULT_SOUND;

		notifier.notify(0x007, notification);
	}

	public void setPresenceHandler() {
		presenceHandler = new PacketTypeFilter(Presence.class);
		connection.addPacketListener(new PacketListener() {

			@Override
			public void processPacket(Packet packet) {
				Presence subscription = (Presence) packet;
				Presence.Type type = subscription.getType();
				String from = subscription.getFrom();

				if (type.equals(Presence.Type.subscribe)) {
					RosterEntry e = roster.getEntry(from);

					if (loginFragment.isPendingSubscription(from)) {
						connection.sendPacket(new Presence(
								Presence.Type.subscribed));
					} else {
						// show notification
						Log.i(TAG,
								"Subscription from " + from + "\nType: "
										+ e.getType());
						loginFragment.addRequest(from);
						onSubscribe(subscription.getFrom());
					}
				} else if (type.equals(Presence.Type.subscribed)) {
					if (loginFragment.isPendingSubscription(from)) {
						try {
							roster.createEntry(from,
									loginFragment.getName(from), null);
							loginFragment.removePendingSubscription(from);
						} catch (XMPPException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}, presenceHandler);
	}

	public void setRosterListener() {
		if (roster != null) {
			roster.addRosterListener(new RosterListener() {
				@Override
				public void entriesAdded(Collection<String> names) {
					for (String s : names) {
						Log.i(TAG, s);
						RosterEntry r = roster.getEntry(s);
						if (r != null) {
							Presence p = roster.getPresence(s);
							loginFragment.addContact(r, p);
						}
					}
				}

				@Override
				public void entriesDeleted(Collection<String> arg0) {

				}

				@Override
				public void entriesUpdated(Collection<String> arg0) {

				}

				@Override
				public void presenceChanged(Presence p) {
					String from = p.getFrom();
					boolean cont = loginFragment.getContacts().contains(from);
					Log.i(TAG, from + " is now " + p.getType() + " cont: "
							+ cont);

				}

			});
		} else
			Log.i(TAG, "roster is null! OH WHY?!");
	}

	public void addMessageHandler() {
		// add a packet listener to handle messages
		messageHandler = new MessageTypeFilter(Message.Type.chat);
		connection.addPacketListener(new PacketListener() {
			public void processPacket(Packet packet) {
				Message message = (Message) packet;

				// get sender's bare JID
				String from = message.getFrom();
				if (!from.equals(loginFragment.getCurrentContact()))
					onMessage(from, message.getBody());
			}
		}, messageHandler);
	}
}
