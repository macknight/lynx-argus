package com.lynx.service.hotfix;

import com.lynx.lib.core.dex.DexModule;
import com.lynx.lib.core.dex.ServiceLoader;
import com.lynx.service.hotfix.impl1v1.HotFixServiceImpl;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-9-12 下午1:34
 */
public class HotFixServiceDexLoader extends ServiceLoader {

	public static final String Tag = "hotfix";

	private static DexModule defModule = new DexModule("hotfix", 1, null, null, "hot fix",
			"com.lynx.service.hotfix.impl1v1.HotFixServiceImpl");

	public HotFixServiceDexLoader() throws Exception {
		super(defModule, HotFixServiceImpl.class);
	}

	@Override
	protected void beforeLoad() {

	}

	@Override
	protected void loadService() throws Exception {

	}

	@Override
	protected void afterLoad() {

	}
}
