package com.lynx.argus.app;

import com.lynx.lib.core.dex.DexManager;
import com.lynx.service.cache.CacheServiceDexLoader;
import com.lynx.service.geo.GeoServiceDexLoader;
import com.lynx.service.test.TestServiceDexLoader;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-10 下午3:38
 */
public class BizDexManager extends DexManager {

	public BizDexManager() {
		super();
		initDexServiceLoader();
	}

	private void initDexServiceLoader() {
		// 根据项目需求定制不同的service管理器
		try {
			TestServiceDexLoader testServiceDexLoader = new TestServiceDexLoader();
			addServiceLoader(testServiceDexLoader);
		} catch (Exception e) {

		}
		try {
			GeoServiceDexLoader geoServiceDexLoader = new GeoServiceDexLoader();
			addServiceLoader(geoServiceDexLoader);
		} catch (Exception e) {

		}

		try {
			CacheServiceDexLoader cacheServiceDexLoader = new CacheServiceDexLoader();
			addServiceLoader(cacheServiceDexLoader);
		} catch (Exception e) {

		}
	}
}
