package com.lynx.argus.app;

import android.content.Context;
import com.lynx.lib.core.ServiceManager;
import com.lynx.service.geo.GeoService;
import com.lynx.service.geo.GeoServiceDexLoader;
import com.lynx.service.test.TestService;
import com.lynx.service.test.TestServiceDexLoader;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-10 下午3:38
 */
public class BizServiceManager extends ServiceManager {
    // 根据项目需求定制不同的service管理器
    private TestServiceDexLoader testServiceDexLoader;
    private GeoServiceDexLoader geoServiceDexLoader;

    public BizServiceManager(Context context) {
        super(context);
    }

    @Override
    protected void initDexLoader() {
        testServiceDexLoader = new TestServiceDexLoader(context);
        geoServiceDexLoader = new GeoServiceDexLoader(context);
        SERVICE_DEX_LOADERS.put(TestService.class.getSimpleName(), testServiceDexLoader);
        SERVICE_DEX_LOADERS.put(GeoService.class.getSimpleName(), geoServiceDexLoader);
    }
}
