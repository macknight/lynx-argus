package com.lynx.lib.core;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import dalvik.system.DexClassLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 支持UI动态加载
 * <p/>
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 9/26/13 3:06 PM
 */
public class LFActivity extends FragmentActivity {
    private AssetManager asm;
    private Resources res;
    private Resources.Theme thm;
    private ClassLoader cl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        contextInit();
        super.onCreate(savedInstanceState);
    }

    @Override
    public AssetManager getAssets() {
        return asm == null ? super.getAssets() : asm;
    }

    public void setAssetManager(AssetManager asm) {
        this.asm = asm;
    }

    @Override
    public Resources getResources() {
        return res == null ? super.getResources() : res;
    }

    public void setResources(Resources res) {
        this.res = res;
    }

    @Override
    public Resources.Theme getTheme() {
        return thm == null ? super.getTheme() : thm;
    }

    public void setTheme(Resources.Theme thm) {
        this.thm = thm;
    }

    @Override
    public ClassLoader getClassLoader() {
        return cl == null ? super.getClassLoader() : cl;
    }

    public void setClassLoader(ClassLoader cl) {
        this.cl = cl;
    }


    private void contextInit() {
        if ("com.dianping.intent.action.LOAD_FRAGMENT".equals(getIntent().getAction())) {
            try {
                String path = getIntent().getStringExtra("path");
                InputStream ins = LFApplication.instance().getAssets()
                        .open(path);
                byte[] bytes = new byte[ins.available()];
                ins.read(bytes);
                ins.close();

                File f = new File(LFApplication.instance().getFilesDir(), "ui/");
                if (!f.exists()) {
                    f.mkdirs();
                }
                f = new File(f, "FL_" + Integer.toHexString(path.hashCode()) + ".apk");
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bytes);
                fos.close();

                File fo = new File(LFApplication.instance().getFilesDir(), "ui/dexout");
                if (!fo.exists()) {
                    fo.mkdirs();
                }

                DexClassLoader dcl = new DexClassLoader(f.getAbsolutePath(),
                        fo.getAbsolutePath(), null, super.getClassLoader());
                cl = dcl;

                try {
                    AssetManager am = (AssetManager) AssetManager.class
                            .newInstance();

                    am.getClass().getMethod("addAssetPath", String.class)
                            .invoke(am, f.getAbsolutePath());
                    asm = am;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                Resources superRes = super.getResources();

                res = new Resources(asm, superRes.getDisplayMetrics(),
                        superRes.getConfiguration());

                thm = res.newTheme();
                thm.setTo(super.getTheme());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
