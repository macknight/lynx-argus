package com.lynx.argus.app;

import com.lynx.lib.core.Const;
import com.lynx.lib.core.LFApplication;
import com.lynx.lib.core.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/29/13 6:15 PM
 */
public class BizApplication extends LFApplication {

	@Override
	public void onCreate() {
//		Const.DOMAIN = "http://192.168.30.148/lynx-web";
//		Const.DOMAIN = "http://192.168.0.102/lynx-web";
//		Const.DOMAIN = "http://58.210.101.202:59102/lynx-web";
		Const.DOMAIN = "http://argus.maskerliu.eu.cloudbees.net/";
		Logger.setLevel(Logger.AppLevel.DEBUG);

		super.onCreate();
	}

	@Override
	protected void initDexManager() {
		dexManager = new BizDexManager();
	}
}