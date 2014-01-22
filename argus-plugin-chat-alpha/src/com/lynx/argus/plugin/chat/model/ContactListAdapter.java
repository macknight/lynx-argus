package com.lynx.argus.plugin.chat.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lynx.argus.plugin.chat.R;

import java.util.List;

/**
 * @author chris.liu
 * 
 * @version 13-12-22 下午10:09
 */
public class ContactListAdapter extends BaseAdapter {
	private static List<Contact> contactList;
	private LayoutInflater mInflater;

	public ContactListAdapter(Context context, List<Contact> contacts) {
		contactList = contacts;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return contactList.size();
	}

	@Override
	public Object getItem(int position) {
		return contactList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.layout_contract, null);

			viewHolder = new ViewHolder();
			viewHolder.contactName = (TextView) convertView
					.findViewById(R.id.tv_contact_name);
			viewHolder.contactNetID = (TextView) convertView
					.findViewById(R.id.tv_contact_id);
			viewHolder.presenceMode = (ImageView) convertView
					.findViewById(R.id.presence_mode);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.contactName.setText(contactList.get(position).getName());
		viewHolder.contactNetID.setText(contactList.get(position).getNetID());

		// set image for presence mode
		String p = contactList.get(position).getPresence();
		if (p.equals("unavailable")) {
			viewHolder.presenceMode
					.setImageResource(R.drawable.unavailable_mode);
		} else {
			viewHolder.presenceMode.setImageResource(R.drawable.available_mode);
		}
		return convertView;
	}

	static class ViewHolder {
		TextView contactName;
		TextView contactNetID;
		ImageView presenceMode;
	}
}
