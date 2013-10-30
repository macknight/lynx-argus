package com.lynx.lib.core;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.lynx.lib.core.dex.DexUILoader;
import dalvik.system.DexClassLoader;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 9/30/13 10:10 AM
 */
public class LFDexActivity extends LFActivity {
    private static final String Tag = "LFDexActivity";

    protected AssetManager dexAssetManager;
    protected Resources dexResources;
    protected Resources.Theme dexTheme;
    protected ClassLoader dexClassLoader;

    private DexUILoader moduleLoader;

    private Resources.Theme defTheme; // 系统原有主题

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        defTheme = this.getTheme();

        loadModule(savedInstanceState);
    }

    protected void loadModule(Bundle savedInstanceState) {
        FrameLayout rootView = new FrameLayout(this);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        rootView.setId(android.R.id.primary);
        setContentView(rootView);

        try {
            AssetManager am = getAssets();
            BitmapDrawable bg = new BitmapDrawable(null, am.open("bg.png"));
            rootView.setBackground(bg);
        } catch (Exception e) {
            Logger.e(Tag, "load icon error", e);
        }

        try {
            String module = getIntent().getStringExtra("module");

            moduleLoader = LFApplication.instance().moduleLoader(module);

            if (moduleLoader == null) {
                Toast.makeText(this, "模块加载失败鸟 @_@", Toast.LENGTH_SHORT).show();
                throw new Exception("not such module exist:" + module);
            }

            DexClassLoader dcl = new DexClassLoader(moduleLoader.srcPath(),
                    moduleLoader.dexDir(), null, super.getClassLoader());
            dexClassLoader = dcl;

            AssetManager am = AssetManager.class.newInstance();
            am.getClass().getMethod("addAssetPath", String.class)
                    .invoke(am, moduleLoader.srcPath());
            dexAssetManager = am;

            Resources superRes = super.getResources();
            dexResources = new Resources(getAssets(), superRes.getDisplayMetrics(),
                    superRes.getConfiguration());

            dexTheme = dexResources.newTheme();
            dexTheme.setTo(super.getTheme());

            super.onCreate(savedInstanceState);

            if (savedInstanceState != null)
                return;

            Fragment f = (Fragment) getClassLoader().loadClass(
                    moduleLoader.clazzName()).newInstance();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(android.R.id.primary, f);
            ft.commit();
        } catch (Throwable e) {
            e.printStackTrace();

            rollback();

            super.onCreate(savedInstanceState);

            Fragment f = new UILoadErrorFragment();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(android.R.id.primary, f);
            ft.commit();
        }
    }

    private void rollback() {
        dexAssetManager = super.getAssets();
        dexResources = super.getResources();
        dexClassLoader = super.getClassLoader();
        dexTheme = defTheme;
    }

    @Override
    public Resources getResources() {
        return dexResources == null ? super.getResources() : dexResources;
    }

    @Override
    public Resources.Theme getTheme() {
        return dexTheme == null ? super.getTheme() : dexTheme;
    }

    @Override
    public AssetManager getAssets() {
        return dexAssetManager == null ? super.getAssets() : dexAssetManager;
    }

    @Override
    public ClassLoader getClassLoader() {
        return dexClassLoader == null ? super.getClassLoader() : dexClassLoader;
    }
}
