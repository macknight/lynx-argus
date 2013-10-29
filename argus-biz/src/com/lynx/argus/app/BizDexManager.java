package com.lynx.argus.app;

import android.content.Context;
import android.widget.Toast;
import com.lynx.lib.core.DexManager;
import com.lynx.lib.core.DexUIModuleLoader;
import com.lynx.service.cache.CacheServiceDexLoader;
import com.lynx.service.geo.GeoServiceDexLoader;
import com.lynx.service.test.TestServiceDexLoader;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-10 下午3:38
 */
public class BizDexManager extends DexManager {

	public BizDexManager(Context context) {
		super(context);
	}

	@Override
	public void initDexServiceLoader() {
		// 根据项目需求定制不同的service管理器
		try {
			TestServiceDexLoader testServiceDexLoader = new TestServiceDexLoader(context);
			addService(testServiceDexLoader);
		} catch (Exception e) {
			Toast.makeText(context, "create TestService Exception", Toast.LENGTH_SHORT).show();
		}
		try {
			GeoServiceDexLoader geoServiceDexLoader = new GeoServiceDexLoader(context);
			addService(geoServiceDexLoader);
		} catch (Exception e) {
			Toast.makeText(context, "create GeoService Exception", Toast.LENGTH_SHORT).show();
		}

		try {
			CacheServiceDexLoader cacheServiceDexLoader = new CacheServiceDexLoader(context);
			addService(cacheServiceDexLoader);
		} catch (Exception e) {
			Toast.makeText(context, "create CacheService Exception", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void initDexUIModuleLoader() {
		addUIModule(new DexUIModuleLoader(context, "local"));
	}
}
