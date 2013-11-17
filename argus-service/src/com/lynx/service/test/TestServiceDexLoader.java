package com.lynx.service.test;

import com.lynx.lib.core.dex.DexModule;
import com.lynx.lib.core.dex.ServiceLoader;
import com.lynx.service.test.impl1v1.TestServiceImpl;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-5 下午5:59
 */
public class TestServiceDexLoader extends ServiceLoader {

	public static final String Tag = "test";

	private static DexModule defModule = new DexModule("test", 1, null, null, "测试",
			"com.lynx.service.test.impl1v1.TestServiceImpl");

	public TestServiceDexLoader() throws Exception {
		super(defModule, TestServiceImpl.class);
	}

	@Override
	protected void beforeLoad() {

	}

	@Override
	protected void loadService() {
		try {
			if (clazz != null) {
				service = (TestService) clazz.newInstance();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void afterLoad() {

	}
}
