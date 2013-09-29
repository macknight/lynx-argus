package com.lynx.lib.core;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.lynx.lib.http.HttpService;
import dalvik.system.DexClassLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 动态Fragment加载器
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 9/29/13 4:12 PM
 */
public class DexUILoader extends Activity {
    private static final String PREFIX = "ui/";
    public static final String K_NAME = "module";
    public static final String K_CLAZZ = "clazz"; // 入口类名
    public static final String K_DESC = "desc"; // module描述
    public static final String K_VERSION = "version"; // 版本
    public static final String K_URL = "url"; // module包下载地址
    public static final String K_MD5 = "md5"; // module包md5摘要

    private HttpService httpService;

    private File dir;
    private String tag;
    private String clazzName = null;
    private String md5 = null;
    private int curVersion = -1;

    protected Class<?> clazz;

    private AssetManager asm;
    private Resources res;
    private Resources.Theme thm;
    private ClassLoader cl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        httpService = (HttpService) (LFApplication.instance().service("http"));

        loadModule(savedInstanceState);
    }

    /**
     * 读取UI更新配置，下载模块更新
     */
    private void updateModule() {

    }

    protected void beforeLoad() {

    }

    protected void loadModule(Bundle savedInstanceState) {
        try {
            if ("com.lynx.argus.intent.action.LOAD_FRAGMENT".equals(getIntent()
                    .getAction())) {
                // we need to setup environment before super.onCreate
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

            super.onCreate(savedInstanceState);

            FrameLayout rootView = new FrameLayout(this);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            rootView.setId(android.R.id.primary);
            rootView.setBackgroundColor(android.R.color.white);
            setContentView(rootView);

            if (savedInstanceState != null)
                return;

            if ("com.lynx.argus.intent.action.LOAD_FRAGMENT".equals(getIntent()
                    .getAction())) {
                try {
                    String fragmentClass = getIntent().getStringExtra("class");
                    Fragment f = (Fragment) getClassLoader().loadClass(
                            fragmentClass).newInstance();
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.add(android.R.id.primary, f);
                    ft.commit();
                } catch (Throwable e) {
                    throw e;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            super.onCreate(savedInstanceState);
            FrameLayout rootView = new FrameLayout(this);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            rootView.setId(android.R.id.primary);
            setContentView(rootView);
        }
    }

    protected void afterLoad() {

    }


    @Override
    public AssetManager getAssets() {
        return asm == null ? super.getAssets() : asm;
    }

    @Override
    public Resources getResources() {
        return res == null ? super.getResources() : res;
    }

    @Override
    public Resources.Theme getTheme() {
        return thm == null ? super.getTheme() : thm;
    }

    @Override
    public ClassLoader getClassLoader() {
        return cl == null ? super.getClassLoader() : cl;
    }
}
