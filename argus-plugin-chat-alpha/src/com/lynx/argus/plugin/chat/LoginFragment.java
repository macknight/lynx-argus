package com.lynx.argus.plugin.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.lynx.argus.plugin.chat.util.XMPPUtil;
import com.lynx.lib.core.LFFragment;

/**
 * @author chris.liu
 * @addtime 13-12-15 下午12:09
 */
public class LoginFragment extends LFFragment {

	private EditText etAccount, etPwd;

	public static final String XMPP_AGENT = "argus-android";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		navActivity.setPopAnimation(R.animator.slide_in_left,
				R.animator.slide_out_right);
		navActivity.setPushAnimation(R.animator.slide_in_right,
				R.animator.slide_out_left);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_login, container, false);
		etAccount = (EditText) v.findViewById(R.id.et_login_account);
		etPwd = (EditText) v.findViewById(R.id.et_login_pwd);
		Button btn = (Button) v.findViewById(R.id.btn_login_ok);
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

		btn = (Button) v.findViewById(R.id.btn_login_register);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});

		btn = (Button) v.findViewById(R.id.btn_logint_forgot);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});
		return v;
	}

	@Override
	public boolean shouldAdd() {
		return true;
	}

	@Override
	public boolean onBackPressed() {
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

	}
}
