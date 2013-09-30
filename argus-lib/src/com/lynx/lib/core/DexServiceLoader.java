package com.lynx.lib.core;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.lynx.lib.http.HttpService;
import com.lynx.lib.http.handler.HttpCallback;
import com.lynx.lib.util.IOUtil;
import dalvik.system.DexClassLoader;
import org.json.JSONObject;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/30/13 11:29 AM
 */
public abstract class DexServiceLoader {
    private static final String PREFIX = "service/";
    public static final String K_VERSION = "version"; // service版本
    public static final String K_URL = "url"; // service包下载地址
    public static final String K_MD5 = "md5"; // service包md5摘要
    public static final String K_CLAZZ = "clazz"; // service实现类名

    protected Context context;
    private final HttpService httpService;
    private final File dir;
    private final String tag;
    private String clazzName = null;
    private String md5 = null;
    private int curVersion = -1;

    protected Class<?> clazz;
    protected DexService service;


    /**
     * @param context
     * @param tag          动态服务标签
     * @param minVersion   最小动态服务包版本
     * @param defaultClazz 默认服务版本
     */
    public DexServiceLoader(Context context, String tag, int minVersion, Class<?> defaultClazz)
            throws Exception {
        this.context = context;
        this.tag = tag;
        this.curVersion = minVersion;
        this.httpService = (HttpService) ((LFApplication) context).service("http");

        deleteOldDex();
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
                    loadClass(curVersion, md5, clazzName);
                }
            }
        } catch (Exception e) {
            Log.e(tag, "unable to read config at " + new File(dir, "config"), e);
        }

        if (clazz == null) {
            clazz = defaultClazz;
            clazzName = defaultClazz.getName();
        }

        try {
            loadService();
        } catch (Exception e) {
            throw new Exception("loading service exception");
        }
    }


    /**
     * 根据新config配置，下载并载入新版本service
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

                            replaceService();
                        }

                        @Override
                        public void onFailure(Throwable t, String strMsg) {
                            super.onFailure(t, strMsg);
                            Toast.makeText(context, strMsg, Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 在载入service之前调用，预处理
     */
    protected abstract void beforeLoad();

    /**
     * 读取本地config，载入service
     */
    protected abstract void loadService() throws Exception;

    /**
     * 在载入service之后调用，恢复现场
     */
    protected abstract void afterLoad();


    /**
     * 服务名
     *
     * @return
     */
    public abstract String name();

    /**
     * 当前服务版本
     *
     * @return
     */
    public int curVersion() {
        return curVersion;
    }

    public DexService service() {
        return service;
    }

    private void replaceService() {
        beforeLoad();
        try {
            loadClass(curVersion, md5, clazzName);
        } catch (Exception e) {
            // TODO: roll back to the default service
        }
        try {
            loadService();
        } catch (Exception e) {

        }
        afterLoad();
    }

    private void loadClass(int version, String md5, String className)
            throws Exception {
        File dexFolder = new File(dir, version + "");
        File dexFile = new File(dexFolder, md5 + ".jar");
        File dexOut = new File(dir, "dex");
        dexOut.mkdir();
        DexClassLoader cl = new DexClassLoader(dexFile.getAbsolutePath(),
                dexOut.getAbsolutePath(), null, context.getClassLoader());
        clazz = (Class<DexService>) cl.loadClass(className);
    }

    private void deleteOldDex() {
        File dex = context.getDir("dex", Context.MODE_PRIVATE);
        if (dex.exists()) {
            try {
                IOUtil.deleteFile(dex);
            } catch (Exception e) {
            }
        }
        File dexout = context.getDir("dexout", Context.MODE_PRIVATE);
        if (dexout.exists()) {
            try {
                IOUtil.deleteFile(dexout);
            } catch (Exception e) {
            }
        }
    }
}
