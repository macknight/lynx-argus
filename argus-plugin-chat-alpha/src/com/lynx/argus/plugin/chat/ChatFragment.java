package com.lynx.argus.plugin.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lynx.argus.plugin.chat.model.MsgAdapter;
import com.lynx.argus.plugin.chat.model.Msg;
import com.lynx.lib.core.LFFragment;

import java.util.List;

/**
 * 
 * @author zhufeng.liu
 * 
 * @addtime 13-12-7 下午6:58
 */
public class ChatFragment extends LFFragment {
	private static final String LM_API_MSG = "/chat";

	private static final String SERVER_NAME = "192.168.0.2";
	private static final String XMPP_RESOURCES = "android";

	private String UserNAME = "chris";

	private MsgAdapter chatAdapter;
	private List<Msg> msgs;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_chat, container, false);

		return v;
	}

	@Override
	public boolean onBackPressed() {
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

	}
}
