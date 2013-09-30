package com.lynx.lib.core;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.lynx.lib.http.HttpService;
import com.lynx.lib.http.handler.HttpCallback;
import com.lynx.lib.util.IOUtil;
import org.json.JSONObject;

import java.io.File;

/**
 * 动态Fragment加载器
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 9/29/13 4:12 PM
 */
public abstract class DexUIModuleLoader {
    private static final String PREFIX = "ui/";
    public static final String K_CLAZZ = "clazz"; // 入口类名
    public static final String K_VERSION = "version"; // 版本
    public static final String K_URL = "url"; // module包下载地址
    public static final String K_MD5 = "md5"; // module包md5摘要
    public static final String K_DESC = "desc"; // module描述

    private HttpService httpService;
    private Context context;
    private File dir;
    private String tag;
    private String clazzName = null;
    private int curVersion = -1;
    private String md5 = null;
    private String desc;

    public DexUIModuleLoader(Context context, String tag, int minVersion) {
        this.httpService = (HttpService) ((LFApplication) context).service("http");
        this.context = context;
        this.tag = tag;
        this.curVersion = minVersion;

        dir = new File(context.getFilesDir(), PREFIX + tag);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            JSONObject config = IOUtil.loadLocalConfig(dir);
            if (config != null) {
                int version = config.getInt(K_VERSION);
                if (version > minVersion) {
                    curVersion = version;
                    clazzName = config.getString(K_CLAZZ);
                    md5 = config.getString(K_MD5);
                    desc = config.getString(K_DESC);
                }
            }
        } catch (Exception e) {
            Log.e(tag, "unable to read config at " + new File(dir, "config"), e);
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
            IOUtil.saveConfig(dir, config);
            File dex = new File(dir, "" + config.getInt(K_VERSION));
            dex.mkdir();

            try {
                clazzName = config.getString(K_CLAZZ);
                curVersion = config.getInt(K_VERSION);
                md5 = config.getString(K_MD5);
                desc = config.getString(K_DESC);
            } catch (Exception e) {
                return;
            }

            String filePath = String.format("%s/%s.jar", dex.getAbsolutePath(), md5);
            httpService.download(config.getString(K_URL), filePath, true,
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

                        }

                        @Override
                        public void onFailure(Throwable t, String strMsg) {
                            super.onFailure(t, strMsg);
                            Toast.makeText(context, strMsg, Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(tag, e.getLocalizedMessage());
        }
    }

    public abstract String name();

    /**
     * 动态模块类名
     *
     * @return
     */
    public String clazzName() {
        return clazzName;
    }

    /**
     * 动态模块包路径
     *
     * @return
     */
    public File dexPath() {
        return dir;
    }

    /**
     * 动态模块路径
     *
     * @return
     */
    public File moduleDir() {
        return dir;
    }

    /**
     * 动态模块描述
     *
     * @return
     */
    public String desc() {
        return desc;
    }
}
