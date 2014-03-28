package com.lynx.lib.core;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import com.lynx.lib.util.FileUtil;
import com.lynx.lib.util.ImageUtil;

/**
 * 日志输出
 * 
 * @author chris.liu
 * @version 3/28/14 11:46 AM
 */
public class LFLogStackActivity extends LFActivity {

	private TextView tvLogConsole;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FrameLayout rootView = new FrameLayout(this);
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		rootView.setId(android.R.id.primary);
		setContentView(rootView);
		Drawable drawable = ImageUtil.getImageDrawableFromAssets(this, "bg.9.png");
		rootView.setBackgroundDrawable(drawable);

		tvLogConsole = new TextView(this);
		tvLogConsole.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		tvLogConsole.setTextSize(10);
		tvLogConsole.setTextColor(0xff005876);
        loadlstLogStack();
	}

	private void loadlstLogStack() {
		try {
			byte[] buf = FileUtil.readExternallStoragePublic(this, "argus.log");
			tvLogConsole.setText(new String(buf, "UTF-8"));
		} catch (Exception e) {

		}
	}
}
