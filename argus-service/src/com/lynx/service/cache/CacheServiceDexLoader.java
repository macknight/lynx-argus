package com.lynx.service.cache;

import android.content.Context;

import com.lynx.lib.core.LFApplication;
import com.lynx.lib.core.dex.DexModule;
import com.lynx.lib.core.dex.IService;
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

	private static Service defService;

	static {
		defService = new Service();
		defService.setModule("cache");
		defService.setClazz("com.lynx.service.cache.impl1v1.CacheServiceImpl");
		defService.setVersion(1);
		defService.setDesc("缓存服务");
	}

	public CacheServiceDexLoader() throws Exception {
		super(defService, CacheServiceImpl.class);
	}

	@Override
	protected void beforeLoad() {

	}

	@Override
	protected void loadService() throws Exception {
		try {
			Context context = LFApplication.instance();
			if (clazz != null) {
				service = (IService) clazz.getConstructor(Context.class).newInstance(context);
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
