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
        try {
            this.serviceManager = new BizServiceManager(this);
        }   catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object service(String name) {
        return serviceManager.getService(name);
    }
}
