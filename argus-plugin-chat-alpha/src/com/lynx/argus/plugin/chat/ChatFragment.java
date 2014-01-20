package com.lynx.argus.plugin.chat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.lynx.argus.plugin.chat.model.Msg;
import com.lynx.argus.plugin.chat.model.MsgAdapter;
import com.lynx.argus.plugin.chat.util.XMPPUtil;
import com.lynx.lib.core.LFFragment;
import com.lynx.lib.core.Logger;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author zhufeng.liu
 * 
 * @addtime 13-12-7 下午6:58
 */
public class ChatFragment extends LFFragment {
	private static final String LM_API_MSG = "/chat";

	public static final int MSG_RECEIVED_FAIL = 0;
	public static final int MSG_TEXT_RECEIVED_SUCCESS = 1;
	public static final int MSG_FILE_RECEIVED_SUCCESS = 2;
	public static final int MSG_FILE_RECEIVED_FAIL = 3;
	public static final int MSG_SEND_SUCCESS = 4;
	public static final int MSG_SEND_FAIL = 5;

	private static final String SERVER_NAME = "@lynx";
	private static final String XMPP_RESOURCES = "android";

	private String fromUser = "chris";
	private String account;

	private ListView lvMsg;
	private EditText etMsg;
	private MsgAdapter msgAdapter;
	private List<Msg> msgs = new ArrayList<Msg>();

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) throws Exception {

		account = getArguments().getString("account");

		ChatManager cm = XMPPUtil.getConnection().getChatManager();
		// 从会话管理实例中创建一个会话
		final Chat chat = cm.createChat(fromUser + SERVER_NAME, msgListener);
		// 添加一个聊天的监听
		cm.addChatListener(new ChatManagerListener() {
			@Override
			public void chatCreated(Chat chat, boolean able) {
				chat.addMessageListener(msgListener);
			}
		});

		msgAdapter = new MsgAdapter(navActivity, msgs);

		View v = inflater.inflate(R.layout.layout_chat, container, false);
		TextView tvTitle = (TextView) v.findViewById(R.id.tv_chat_title);
		tvTitle.setText(fromUser);
		lvMsg = (ListView) v.findViewById(R.id.lv_msg);
		lvMsg.setAdapter(msgAdapter);

		etMsg = (EditText) v.findViewById(R.id.et_msg);

		Button btSend = (Button) v.findViewById(R.id.btn_send);
		btSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String content = etMsg.getText().toString();
				if (content.length() > 0) {
					try {
						Msg chatMsg = new Msg(account, content, new Date()
								.getTime(), false);
						chat.sendMessage(content);
						android.os.Message msg = handler.obtainMessage();
						msg.what = MSG_SEND_SUCCESS;
						msg.obj = chatMsg;
						msg.sendToTarget();
					} catch (Exception e) {

					}
				}
			}
		});
		return v;
	}

	private MessageListener msgListener = new MessageListener() {
		@Override
		public void processMessage(Chat chat, Message message) {
			try {
				Msg chatMsg = new Msg(message.getFrom(), message.getBody(),
						new Date().getTime(), true);
				android.os.Message msg = handler.obtainMessage();
				msg.what = MSG_TEXT_RECEIVED_SUCCESS;
				msg.obj = chatMsg;
				msg.sendToTarget();
			} catch (Exception e) {
				Logger.w("chat", e);
			}
		}
	};

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_TEXT_RECEIVED_SUCCESS:
				msgAdapter.addMsg((Msg) msg.obj);
				break;
			case MSG_SEND_SUCCESS:
				msgAdapter.addMsg((Msg) msg.obj);
				break;
			}
		}
	};

	@Override
	public boolean onBackPressed() {
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

	}
}
