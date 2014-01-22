package com.lynx.argus.plugin.chat.model;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;

/**
 * @author chris.liu
 * 
 * @version 13-12-22 下午10:09
 */
public class Contact {
	private String netID;
	private String name;
	private String presence;

	public Contact(RosterEntry r) {
		netID = r.getUser();
		name = r.getName();
	}

	public void setPresence(Presence p) {
		presence = p.getType().toString();
	}

	public String getNetID() {
		return netID;
	}

	public String getName() {
		return name;
	}

	public String getPresence() {
		return presence;
	}
}
