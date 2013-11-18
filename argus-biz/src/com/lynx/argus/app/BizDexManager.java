package com.lynx.argus.app;

import com.lynx.lib.core.dex.DexManager;
import com.lynx.lib.core.dex.ServiceManager;
import com.lynx.service.cache.CacheServiceDexLoader;
import com.lynx.service.geo.GeoServiceDexLoader;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-10 下午3:38
 */
public class BizDexManager extends DexManager {

    @Override
    protected void initServiceManager() {
        serviceManager = new ServiceManager() {
            @Override
            protected void initService() {
                try {
                    GeoServiceDexLoader geoServiceDexLoader = new GeoServiceDexLoader();
                    addServiceLoader(geoServiceDexLoader);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    CacheServiceDexLoader cacheServiceDexLoader = new CacheServiceDexLoader();
                    addServiceLoader(cacheServiceDexLoader);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
