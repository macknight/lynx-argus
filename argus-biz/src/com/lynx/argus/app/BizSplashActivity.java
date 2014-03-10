package com.lynx.argus.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.lynx.argus.R;
import com.lynx.lib.core.LFActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author chris
 * @version 3/7/14 3:40 PM
 */
public class BizSplashActivity extends LFActivity {

	private Timer timer;
	private long startTime;

	private final TimerTask task = new TimerTask() {
		@Override
		public void run() {
			if (System.currentTimeMillis() - startTime > 1000) {
				Message message = new Message();
				message.what = 0;
				timerHandler.sendMessage(message);
				timer.cancel();
				this.cancel();
			}

		}
	};

	private final Handler timerHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
                BizTabActivity bizTabActivity = new BizTabActivity();
                Intent intent = new Intent();
                intent.setClass(BizSplashActivity.this, bizTabActivity.getClass());
                startActivity(intent);
                overridePendingTransition(R.anim.anim_splash_in, R.anim.anim_splash_out);
                BizSplashActivity.this.finish();
                break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.layout_splash);

		timer = new Timer(true);
		startTime = System.currentTimeMillis();
		timer.schedule(task, 0, 1);
	}
}
