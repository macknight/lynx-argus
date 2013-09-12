package com.lynx.argus.app;

import android.content.Context;
import android.widget.Toast;
import com.lynx.lib.core.ServiceManager;
import com.lynx.service.geo.GeoServiceDexLoader;
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
    public void initDexLoader() {
        try {
            testServiceDexLoader = new TestServiceDexLoader(context);
            addService(testServiceDexLoader);
        } catch (Exception e) {
            Toast.makeText(context, "create TestService Exception", Toast.LENGTH_SHORT).show();
        }
        try {
            geoServiceDexLoader = new GeoServiceDexLoader(context);
            addService(geoServiceDexLoader);
        } catch (Exception e) {
            Toast.makeText(context, "create GeoService Exception", Toast.LENGTH_SHORT).show();
        }
    }
}
