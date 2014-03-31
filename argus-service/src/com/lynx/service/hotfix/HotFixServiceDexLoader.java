package com.lynx.service.hotfix;

import com.lynx.lib.core.dex.Service;
import com.lynx.lib.core.dex.ServiceLoader;
import com.lynx.service.hotfix.impl1v1.HotFixServiceImpl;

/**
 * 
 * @author zhufeng.liu
 * @version 13-9-12 下午1:34
 */
public class HotFixServiceDexLoader extends ServiceLoader {

	public static final String Tag = "hotfix";

	private static Service defService;

	static {
		defService = new Service();
		defService.setModule("hotfix");
		defService.setClazz("com.lynx.service.hotfix.impl1v1.HotFixServiceImpl");
		defService.setVersion(1);
		defService.setDesc("hot fix");
	}

	public HotFixServiceDexLoader() throws Exception {
		super(defService, HotFixServiceImpl.class);
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
