package com.lynx.lib.core;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.lynx.lib.http.HttpService;
import com.lynx.lib.http.handler.HttpCallback;
import com.lynx.lib.util.IOUtil;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * 动态Fragment加载器
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 9/29/13 4:12 PM
 */
public class DexUIModuleLoader {
    private static final String PREFIX = "ui/";
    public static final String K_MODULE = "module";
    public static final String K_CLAZZ = "clazz"; // 入口类名
    public static final String K_VERSION = "version"; // 版本
    public static final String K_URL = "url"; // module包下载地址
    public static final String K_MD5 = "md5"; // module包md5摘要
    public static final String K_DESC = "desc"; // module描述

    /**
     * on the disk the dex ui module file dir looks like:
     * /data/data/app.name/files/ui/test/
     * -version/
     * --test.jar
     * -config
     * -dex/
     * --test.dex
     */

    private HttpService httpService;
    private Context context;
    private File basicPath; // /data/data/app.name/files/ui/module
    private String dexPath; // data/data/app.name/files/ui/module/dex
    private String apkPath; // data/data/app.name/files/ui/module/version/
    private String moduleName;
    private String clazzName = null;
    private int curVersion = -1;
    private String md5 = null;
    private String desc;

    public DexUIModuleLoader(Context context, String tag) {
        this(context, tag, 0);
    }

    public DexUIModuleLoader(Context context, String moduleName, int minVersion) {
        this.context = context;
        this.moduleName = moduleName;
        this.curVersion = minVersion;
        this.httpService = (HttpService) ((LFApplication) context).service("http");

        basicPath = new File(context.getFilesDir(), PREFIX + moduleName);
        if (!basicPath.exists()) {
            basicPath.mkdirs();
        }

        File dexDir = new File(basicPath, "dex");
        if (!dexDir.exists()) {
            dexDir.mkdirs();
        }
        dexPath = dexDir.getAbsolutePath();

        try {
            JSONObject config = IOUtil.loadLocalConfig(basicPath, "config");
            if (config != null) {
                int version = config.getInt(K_VERSION);
                if (version > minVersion) {
                    curVersion = version;
                    clazzName = config.getString(K_CLAZZ);
                    md5 = config.getString(K_MD5);
                    desc = config.getString(K_DESC);
                    apkPath = new File(basicPath, "" +
                            config.getInt(K_VERSION)).getAbsolutePath() +
                            String.format("/%s.apk", config.getString(K_MD5));
                }
            }
        } catch (Exception e) {
            Log.e(moduleName, "unable to read config at " + new File(basicPath, "config"), e);
        }
    }

    /**
     * 读取UI更新配置，下载模块更新
     *
     * @param config
     */
    public void update(JSONObject config) {
        try {
            int newVersion = config.getInt(K_VERSION);
            if (curVersion >= newVersion) {
                return;
            }


            // 版本升级
            IOUtil.saveConfig(basicPath, config);
            File path = new File(basicPath, "" + config.getInt(K_VERSION));
            path.mkdir();

            try {
                clazzName = config.getString(K_CLAZZ);
                curVersion = config.getInt(K_VERSION);
                md5 = config.getString(K_MD5);
                desc = config.getString(K_DESC);
            } catch (Exception e) {
                return;
            }

            apkPath = String.format("%s/%s.apk", path.getAbsolutePath(), md5);
            httpService.download(config.getString(K_URL), apkPath, true,
                    new HttpCallback<File>() {
                        @Override
                        public void onStart() {
                            super.onStart();
                            Toast.makeText(context, "开始下载动态更新包", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(File file) {
                            super.onSuccess(file);
                            // TODO: md5校验
                            Toast.makeText(context, "完成动态更新包下载", Toast.LENGTH_SHORT).show();

                            // 删除老的dex文件
                            try {
                                deleteOldDexFile();
                            } catch (Exception e) {
                                Log.d("module loader", "删除老的Dex文件错误" + e.getMessage());
                            }
                        }

                        @Override
                        public void onFailure(Throwable t, String strMsg) {
                            super.onFailure(t, strMsg);
                            Toast.makeText(context, strMsg, Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(moduleName, e.getLocalizedMessage());
        }
    }

    public String name() {
        return moduleName;
    }

    /**
     * 动态模块类名
     *
     * @return
     */
    public String clazzName() {
        return clazzName;
    }

    /**
     * 动态模块路径
     *
     * @return
     */
    public String modulePath() {
        return basicPath.getAbsolutePath();
    }

    /**
     * 动态模块包路径
     *
     * @return
     */
    public String apkPath() {
        return apkPath;
    }


    public String dexPath() {
        return dexPath;
    }

    /**
     * 动态模块描述
     *
     * @return
     */
    public String desc() {
        return desc;
    }

    private void deleteOldDexFile() throws IOException {
        File dexDir = new File(basicPath, "dex");
        IOUtil.deleteFile(dexDir);
        dexDir = new File(basicPath, "dex");
        dexDir.mkdirs();
    }
}
