package com.lynx.argus.app;

import com.lynx.lib.core.LFApplication;
import com.lynx.lib.core.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/29/13 6:15 PM
 */
public class BizApplication extends LFApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.setLevel(Logger.AppLevel.PRODUCT);
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
