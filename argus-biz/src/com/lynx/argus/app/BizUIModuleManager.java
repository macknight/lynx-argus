package com.lynx.argus.app;

import android.content.Context;
import com.lynx.lib.core.DexUIModuleLoader;
import com.lynx.lib.core.UIModuleManager;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-10-8 下午1:52
 */
public class BizUIModuleManager extends UIModuleManager {

    public BizUIModuleManager(Context context) {
        super(context);
    }

    @Override
    public void initDexLoader() {
        addUIModule(new DexUIModuleLoader(context, "test"));
    }
}
