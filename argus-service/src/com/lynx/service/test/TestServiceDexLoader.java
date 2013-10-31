package com.lynx.service.test;

import android.content.Context;
import com.lynx.lib.core.dex.DexServiceLoader;
import com.lynx.service.test.impl1v1.TestServiceImpl;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-5 下午5:59
 */
public class TestServiceDexLoader extends DexServiceLoader {

	public TestServiceDexLoader(Context context) throws Exception {
		super(context, "test", 101, TestServiceImpl.class);
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
