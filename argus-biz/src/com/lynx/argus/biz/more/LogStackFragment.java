package com.lynx.argus.biz.more;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lynx.argus.R;
import com.lynx.lib.core.LFConst;
import com.lynx.lib.core.LFFragment;
import com.lynx.lib.util.FileUtil;

/**
 * 日志输出
 * 
 * @author chris.liu
 * @version 3/28/14 11:46 AM
 */
public class LogStackFragment extends LFFragment {

	private TextView tvLogConsole;

	@Override
	protected View onLoadView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) throws Exception {
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.layout_log_stack, container,
				false);
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		tvLogConsole = (TextView) view.findViewById(R.id.tv_logstack_content);
		tvLogConsole.setMovementMethod(ScrollingMovementMethod.getInstance());
		loadlstLogStack();
		return view;
	}

	private void loadlstLogStack() {
		try {
			byte[] buf = FileUtil.readExternallStoragePublic("argus.log");
			String log = new String(buf, LFConst.DEF_CHARSET);
			tvLogConsole.setText(log);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
