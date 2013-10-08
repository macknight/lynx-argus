package com.lynx.argus.app;

import com.lynx.lib.core.LFApplication;

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
    protected void initUIMoudleManager() {
        uiModuleManager = new BizUIModuleManager(this);
    }
}
