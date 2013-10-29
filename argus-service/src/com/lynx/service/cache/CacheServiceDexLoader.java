package com.lynx.service.cache;

import android.content.Context;
import com.lynx.lib.cache.CacheService;
import com.lynx.lib.core.DexService;
import com.lynx.lib.core.DexServiceLoader;
import com.lynx.service.cache.impl1v1.CacheServiceImpl;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-17 上午10:01
 */
public class CacheServiceDexLoader extends DexServiceLoader {

	public static final String Tag = "cache";

	private static final int minVersion = 101;

	public CacheServiceDexLoader(Context context)
			throws Exception {
		super(context, Tag, minVersion, CacheServiceImpl.class);
	}

	@Override
	protected void beforeLoad() {

	}

	@Override
	protected void loadService() throws Exception {
		try {
			if (clazz != null) {
				service = (DexService) clazz.getConstructor(Context.class).newInstance(context);
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

	@Override
	public String moduleName() {
		return CacheService.class.getSimpleName();
	}
}
