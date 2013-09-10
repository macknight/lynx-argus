package com.lynx.lib.core;

import android.app.Application;
import android.content.res.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-10 下午2:32
 */
public abstract class LFApplication extends Application {

    protected static LFApplication instance;
    protected ServiceManager serviceManager;

    public static LFApplication instance() {
        if (instance == null) {
            throw new IllegalStateException("Application has not been created");
        }
        return instance;
    }

    public LFApplication() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    public abstract Object service(String name);
}
