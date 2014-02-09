package com.lynx.argus.biz.more;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import com.lynx.argus.R;
import com.lynx.lib.core.LFEnvironment;
import com.lynx.lib.core.LFFragment;
import com.lynx.lib.core.Logger;

/**
 * 
 * @author chris.liu
 * 
 * @version 14-1-20 上午11:53
 */
public class AboutFragment extends LFFragment {
	private static final String Tag = "about";

	private TextView tvVersion;
	private WebView wvAbout;

	@Override
	protected View onLoadView(LayoutInflater inflater, ViewGroup container,
			Bundle bundle) throws Exception {
		View v = inflater.inflate(R.layout.layout_about, container, false);

		tvVersion = (TextView) v.findViewById(R.id.tv_more_about_version);
		String version = "unknown version";
		try {
			PackageManager packageManager = tabActivity.getPackageManager();
			if (packageManager != null) {
				PackageInfo pinfo = LFEnvironment.pkgInfo();
				version = pinfo.versionName;
			}
		} catch (Exception e) {
			Logger.e(Tag, "load resource from assert error", e);
		}

		tvVersion.setText(version);
		tvVersion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (LFEnvironment.isDebug()) {
					SysInfoFragment sysInfoFragment = new SysInfoFragment();
					tabActivity.pushFragment(sysInfoFragment);
				}
			}
		});

		wvAbout = (WebView) v.findViewById(R.id.more_about_link);
		wvAbout.setBackgroundColor(0); // 设置背景色
		wvAbout.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		wvAbout.loadUrl("file:///android_asset/more_about.html");

		return v;
	}
}
