package com.lynx.argus.app;

import com.lynx.lib.core.Const;
import com.lynx.lib.core.LFApplication;
import com.lynx.lib.core.Logger;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-8-29 下午6:15
 */
public class BizApplication extends LFApplication {

	@Override
	public void onCreate() {
		Const.LM_API_DOMAIN = "http://192.168.30.148/lynx-web";
		// Const.LM_API_DOMAIN = "http://192.168.0.102/lynx-web";
		// Const.LM_API_DOMAIN = "http://58.210.101.202:59102/lynx-web";
		// Const.LM_API_DOMAIN = "http://192.168.1.163/lynx-web";
		// Const.LM_API_DOMAIN = "http://argus.maskerliu.eu.cloudbees.net/";
		Logger.setLevel(Logger.AppLevel.DEBUG);

		super.onCreate();
	}

	@Override
	protected void initDexManager() {
		dexManager = new BizDexManager();
	}
}