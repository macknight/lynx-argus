package com.lynx.argus.plugin.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.lynx.argus.plugin.chat.model.Contact;
import com.lynx.argus.plugin.chat.util.XMPPService;
import com.lynx.argus.plugin.chat.util.XMPPUtil;
import com.lynx.lib.core.LFFragment;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * 
 * @author chris.liu
 * 
 * @version 13-12-15 下午12:09
 */
public class LoginFragment extends LFFragment {

	private String SERVER_HOST = "192.168.0.102";
	private int SERVER_PORT = 5222;
	private boolean ip = false;

	private Intent service;
	private ConnectionConfiguration config;
	private XMPPConnection connection;
	private Roster roster;

	private HashMap<String, String> pendingSubscriptions = new HashMap<String, String>();
	private ArrayList<String> requests = new ArrayList<String>();
	private Collection<RosterEntry> entries;
	private ArrayList<Contact> contacts = new ArrayList<Contact>();
	private String currentContact;

	private EditText etAccount, etPwd;

	public static final String XMPP_AGENT = "argus-android";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		navActivity.setPopAnimation(R.animator.slide_in_left, R.animator.slide_out_right);
		navActivity.setPushAnimation(R.animator.slide_in_right, R.animator.slide_out_left);
	}

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			throws Exception {
		View view = inflater.inflate(R.layout.layout_login, container, false);
		if (view == null) {
			throw new Exception("页面初始化错误");
		}

		etAccount = (EditText) view.findViewById(R.id.et_login_account);
		etPwd = (EditText) view.findViewById(R.id.et_login_pwd);
		Button btn = (Button) view.findViewById(R.id.btn_login_ok);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				try {
					String account = etAccount.getText().toString();
					String pwd = etPwd.getText().toString();
					XMPPUtil.getConnection().login(account, pwd, XMPP_AGENT);
					ChatListFragment chatListFragment = new ChatListFragment();
					Bundle bundle = new Bundle();
					bundle.putString("account", account);
					chatListFragment.setArguments(bundle);
					navActivity.pushFragment(chatListFragment);
				} catch (Exception e) {
					e.printStackTrace();
					XMPPUtil.closeConnection();
				}
			}
		});

		btn = (Button) view.findViewById(R.id.btn_login_register);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});

		btn = (Button) view.findViewById(R.id.btn_logint_forgot);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});
		return view;
	}

	public void setConnection(XMPPConnection connection) {
		this.connection = connection;
	}

	// sets up XMPPConnection
	public synchronized XMPPConnection setConnection() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (connection == null) {
					config = new ConnectionConfiguration(SERVER_HOST, SERVER_PORT);
					// config = new ConnectionConfiguration(SERVER_HOST,
					// SERVER_PORT, SERVICE_NAME);

					// for authentication stuffs
					config.setSASLAuthenticationEnabled(true);
					config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
					config.setDebuggerEnabled(true);
					connection = new XMPPConnection(config);

					try {
						SASLAuthentication.supportSASLMechanism("PLAIN", 0);
						// connect to server
						connection.connect();
					} catch (XMPPException e) {
						// catch block if server connection is unsuccessful
						e.printStackTrace();
						connection = null;
					}
				}
			}
		}).start();
		return connection;
	}

	public boolean login(String netID, String password) {
		if (connection.isConnected()) {
			try {
				if (ip) {
					connection.login("dwade", "dwyane", "resource");
				} else {
					// login using user's input
					connection.login("amzrobles", "izaeyogeix", "resource");
				}

				// retrieve roster
				roster = connection.getRoster();
				roster.setSubscriptionMode(Roster.SubscriptionMode.manual);
				entries = roster.getEntries();

				// start background service
				service = new Intent(navActivity, XMPPService.class);
				navActivity.startService(service);
			} catch (XMPPException e) {
				// catch block for unsuccessful login
				e.printStackTrace();
			}
		}
		return connection.isAuthenticated();
	}

	public void logout() {
		// stop background service
		navActivity.stopService(service);

		// terminate connection
		connection.disconnect();
		connection = null;
	}

	public Roster getRoster() {
		return roster;
	}

	public void setRoster(Roster roster) {
		this.roster = roster;
	}

	// stores roster to ArrayList of contacts
	public ArrayList<Contact> getContacts() {
		contacts.clear();
		if (!entries.isEmpty()) {
			for (RosterEntry r : entries) {
				Presence p = roster.getPresence(r.getUser());
				Contact c = new Contact(r);
				c.setPresence(p);

				contacts.add(c);
			}
		}
		return contacts;
	}

	// adds a contact to array list
	public void addContact(RosterEntry r, Presence p) {
		Contact c = new Contact(r);
		c.setPresence(p);
		contacts.add(c);
	}

	// adds request to array list
	public void addRequest(String requester) {
		requests.add(requester);
	}

	// deletes request in the array list
	public void removeRequest(String requester) {
		requests.remove(requester);
	}

	public ArrayList<String> getRequests() {
		return requests;
	}

	// set the contact whom the user is talking to
	public void setCurrentContact(String currentContact) {
		this.currentContact = currentContact;
	}

	public String getCurrentContact() {
		return currentContact;
	}

	public void addPendingSubscription(String netID, String name) {
		pendingSubscriptions.put(netID, name);
	}

	public void removePendingSubscription(String netID) {
		pendingSubscriptions.remove(netID);
	}

	public String getName(String netID) {
		return pendingSubscriptions.get(netID);
	}

	public boolean isPendingSubscription(String netID) {
		return pendingSubscriptions.containsKey(netID);
	}
}
