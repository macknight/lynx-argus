package com.lynx.argus.plugin.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.lynx.argus.plugin.chat.model.ChatAdapter;
import com.lynx.argus.plugin.chat.model.ChatListItem;
import com.lynx.lib.core.LFFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-12-7 下午6:58
 */
public class ChatListFragment extends LFFragment {
	private static final String LM_API_MSG = "/chat";

	private List<ChatListItem> chatlist = new ArrayList<ChatListItem>();
	private ChatAdapter chatAdapter;

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) throws Exception {

		final String account = getArguments().getString("account");

		chatlist.clear();
		chatAdapter = new ChatAdapter(navActivity, chatlist);
		View v = inflater.inflate(R.layout.layout_chatlist, container, false);
		ListView lvChat = (ListView) v.findViewById(R.id.lv_chats);
		lvChat.setAdapter(chatAdapter);
		lvChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				ChatFragment chatFragment = new ChatFragment();
				Bundle bundle = new Bundle();
				bundle.putString("account", account);
				chatFragment.setArguments(bundle);
				navActivity.pushFragment(chatFragment);
			}
		});
		initData();
		return v;
	}

	private void initData() {
		ChatListItem chat = new ChatListItem("珍妮花", "上海人民已阵亡",
				new Date().getTime());
		chatAdapter.addChat(chat);
		chat = new ChatListItem("lily", "南京人民已阵亡", new Date().getTime());
		chatAdapter.addChat(chat);
		chat = new ChatListItem("chris", "北京人民已阵亡", new Date().getTime());
		chatAdapter.addChat(chat);
		chat = new ChatListItem("tom", "杭州人民已阵亡", new Date().getTime());
		chatAdapter.addChat(chat);
	}
}
