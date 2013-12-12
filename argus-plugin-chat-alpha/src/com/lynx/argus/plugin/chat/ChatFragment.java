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
 * @author zhufeng.liu
 * @addtime 13-12-7 下午6:58
 */
public class ChatFragment extends LFFragment {
	private static final String LM_API_MSG = "/chat";

	public static final int MSG_TEXT_RECEIVED_SUCCESS = 0;

	private static final String SERVER_NAME = "@xcjxmppserver";
	private static final String XMPP_RESOURCES = "android";

	private String fromUser = "lily";
	private String uid = "chris";

	private ListView lvMsg;
	private EditText etMsg;
	private MsgAdapter msgAdapter;
	private List<Msg> msgs = new ArrayList<Msg>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ChatManager cm = XMPPUtil.getConnection().getChatManager();
		// 从会话管理实例中创建一个会话
		final Chat chat = cm.createChat(fromUser + SERVER_NAME, null);
		// 添加一个聊天的监听
		cm.addChatListener(new ChatManagerListener() {
			@Override
			public void chatCreated(Chat chat, boolean able) {
				chat.addMessageListener(msgListener);
			}
		});

		msgAdapter = new MsgAdapter(navActivity, msgs);

		View v = inflater.inflate(R.layout.layout_chat, container, false);
		lvMsg = (ListView) v.findViewById(R.id.lv_msg);
		lvMsg.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		lvMsg.setAdapter(msgAdapter);

		etMsg = (EditText) v.findViewById(R.id.et_msg);

		Button btSend = (Button) v.findViewById(R.id.btn_send);
		btSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String content = etMsg.getText().toString();
				if (content.length() > 0) {
					Msg msg = new Msg(uid, content, new Date().getTime(), false);
					msgAdapter.addMsg(msg);
				}
			}
		});
		return v;
	}

	private MessageListener msgListener = new MessageListener() {
		@Override
		public void processMessage(Chat chat, Message message) {
			if (message.getFrom().contains(fromUser)) {
				try {
					Msg chatMsg = new Msg(fromUser, message.getBody(),
							new Date().getTime(), true);
					android.os.Message msg = handler.obtainMessage();
					msg.what = MSG_TEXT_RECEIVED_SUCCESS;
					msg.obj = chatMsg;
					msg.sendToTarget();
				} catch (Exception e) {
					Logger.w("chat", e);
				}
			} else {

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
