package com.lynx.service.cache;

import android.content.Context;

import com.lynx.lib.core.dex.DexModule;
import com.lynx.lib.core.dex.Service;
import com.lynx.lib.core.dex.ServiceLoader;
import com.lynx.service.cache.impl1v1.CacheServiceImpl;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-9-17 上午10:01
 */
public class CacheServiceDexLoader extends ServiceLoader {

	public static final String Tag = "cache";

	private static final int minVersion = 101;
	private static DexModule defModule = new DexModule("cache", 1, null, null,
			"缓存服务", "com.lynx.service.cache.impl1v1.CacheServiceImpl");

	public CacheServiceDexLoader() throws Exception {
		super(defModule, CacheServiceImpl.class);
	}

	@Override
	protected void beforeLoad() {

	}

	@Override
	protected void loadService() throws Exception {
		try {
			if (clazz != null) {
				service = (Service) clazz.getConstructor(Context.class)
						.newInstance(context);
			}

			if (service == null) {
				service = new CacheServiceImpl(context);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void afterLoad() {

	}
}
