package com.lynx.argus.app;

import com.lynx.lib.core.LFApplication;
import com.lynx.lib.core.UIModuleManager;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/29/13 6:15 PM
 */
public class BizApplication extends LFApplication {

    public static final String BMAP_AK = "EAaacd071ed10ccc5653a49b9fbd2923";

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
        uiModuleManager = new UIModuleManager(this) {
            @Override
            public void initDexLoader() {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }
}
