package com.lynx.argus.plugin.chat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.lynx.argus.plugin.chat.model.Contact;
import com.lynx.argus.plugin.chat.model.ContactListAdapter;
import com.lynx.lib.core.LFFragment;
import org.jivesoftware.smack.packet.Presence;

import java.util.List;

/**
 * @author chris.liu
 * 
 * @version 13-12-22 下午10:09
 */
public class ContractListFragment extends LFFragment {
	private LoginFragment loginFragment = null;
	private List<Contact> contacts;
	private Button btnAddEmpty;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			throws Exception {
		View view = inflater.inflate(R.layout.layout_contractlist, container, false);
		if (view == null) {
			throw new Exception("页面初始化错误");
		}

		setContacts();

		btnAddEmpty = (Button) view.findViewById(R.id.btn_add_empty);
		btnAddEmpty.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (view.getId() == R.id.btn_add_empty) {
					AddContactDialog addDialog = new AddContactDialog(navActivity);
					addDialog.show();
				}
			}
		});

		final ListView listViewContacts = (ListView) view.findViewById(R.id.lv_contacts);
		listViewContacts.setAdapter(new ContactListAdapter(navActivity, contacts));
		listViewContacts.setEmptyView(view.findViewById(R.id.emptyView));
		listViewContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				Object o = listViewContacts.getItemAtPosition(position);

				Contact fullObject = (Contact) o;

				String target = fullObject.getNetID();
				Intent chat = new Intent(navActivity, ChatFragment.class);
				chat.putExtra("target", target);
				loginFragment.setCurrentContact(target);
				startActivity(chat);

				contacts.clear();
			}
		});

		return view;
	}

	private void setContacts() {
		contacts = loginFragment.getContacts();

	}

	public class AddContactDialog extends Dialog {
		private EditText etAddContractId;
		private EditText etAddName;
		private Button btnAdd;

		public AddContactDialog(Context context) {
			super(context);
		}

		protected void onStart() {
			super.onStart();
			setContentView(R.layout.layout_add_contact);
			getWindow().setFlags(4, 4);
			setTitle("Add a Contact");

			etAddContractId = (EditText) findViewById(R.id.et_contract_id);
			etAddName = (EditText) findViewById(R.id.et_add_name);

			btnAdd = (Button) findViewById(R.id.btn_add);
			btnAdd.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					String to = etAddContractId.getText().toString();
					String name = etAddName.getText().toString();

					loginFragment.addPendingSubscription(to, name);
					Presence subscribe = new Presence(Presence.Type.subscribe);
					subscribe.setTo(to);

					loginFragment.setConnection().sendPacket(subscribe);
					dismiss();
				}
			});
		}
	}
}
