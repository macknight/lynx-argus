package com.lynx.service.test;

import android.content.Context;
import com.lynx.lib.core.DexServiceLoader;
import com.lynx.service.test.impl1v1.TestServiceImpl;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-5 下午5:59
 */
public class TestServiceDexLoader extends DexServiceLoader {
	private static final String Tag = "test";

	public TestServiceDexLoader(Context context) throws Exception {
		super(context, Tag, 101, TestServiceImpl.class);
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

	@Override
	public String moduleName() {
		return com.lynx.service.test.TestService.class.getSimpleName();
	}
}
