package com.lynx.lib.core;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;

/**
 * 支持UI动态加载
 * <p/>
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 9/26/13 3:06 PM
 */
public abstract class LFActivity extends Activity {
    private AssetManager dexAssetManager;
    private Resources dexResources;
    private Resources.Theme dexTheme;
    private ClassLoader dexClassLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initDexContext();
        super.onCreate(savedInstanceState);
    }

    public abstract void initDexContext();

    public AssetManager getDexAssetManager() {
        return dexAssetManager == null ? super.getAssets() : dexAssetManager;
    }

    public void setDexAssetManager(AssetManager assetManager) {
        dexAssetManager = assetManager;
    }

    public Resources getDexResources() {
        return dexResources == null ? super.getResources() : dexResources;
    }

    public void setDexResources(Resources resources) {
        dexResources = resources;
    }

    public Resources.Theme getDexTheme() {
        return dexTheme == null ? super.getTheme() : dexTheme;
    }

    public void setDexTheme(Resources.Theme theme) {
        dexTheme = theme;
    }

    public ClassLoader getDexClassLoader() {
        return dexClassLoader == null ? super.getClassLoader() : dexClassLoader;
    }

    public void setDexClassLoader(ClassLoader classLoader) {
        dexClassLoader = classLoader;
    }
}
