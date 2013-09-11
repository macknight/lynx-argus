package com.lynx.argus.app;

import com.lynx.lib.core.DexServiceLoader;
import com.lynx.lib.core.LFApplication;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/29/13 6:15 PM
 */
public class BizApplication extends LFApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void initServiceManager() {
        serviceManager = new BizServiceManager(this);
    }

    @Override
    public Object service(String name) {
        return serviceManager.getService(name);
    }

    @Override
    public Map<String, DexServiceLoader> services() {
        return serviceManager.dexServices();
    }
}
