package com.lynx.lib.core;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.lynx.lib.http.HttpService;
import com.lynx.lib.http.handler.HttpCallback;
import com.lynx.lib.http.impl.DefaultHttpServiceImpl;
import com.lynx.lib.util.StringUtils;
import dalvik.system.DexClassLoader;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/30/13 11:29 AM
 */
public abstract class ServiceDexLoader<T> {
    public static final String K_VERSION = "version"; // service版本
    public static final String K_URL = "url"; // service包下载地址
    public static final String K_MD5 = "md5"; // service包md5摘要
    public static final String K_CLASS = "clazz"; // service实现类名

    protected Context context;
    private static final HttpService httpService = new DefaultHttpServiceImpl();
    private final File dir;
    private final String tag;
    private JSONObject config;
    protected Class<T> clazz;
    protected T service;
    private int curVersion = -1;

    /**
     * @param context
     * @param tag          动态服务标签
     * @param minVersion   最小动态服务包版本
     * @param defaultClazz 默认服务版本
     */
    public ServiceDexLoader(Context context, String tag, int minVersion, Class<T> defaultClazz) {
        this.context = context;
        this.tag = tag;
        deleteOldDex();
        dir = new File(context.getFilesDir(), tag);
        dir.mkdir();
        try {
            config = loadLocalConfig();
        } catch (Exception e) {
            Log.e(tag, "unable to read config at " + new File(dir, "config"), e);
        }
        if (config != null) {
            try {
                curVersion = config.getInt(K_VERSION);
                if (curVersion > minVersion) {
                    clazz = loadClass(config.getInt(K_VERSION),
                            config.getString(K_MD5), config.getString(K_CLASS));
                } else {
                    curVersion = minVersion;
                    clazz = defaultClazz;
                }
                loadService();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 根据新config配置，下载并载入新版本service
     *
     * @param newConfig
     */
    public void updateService(JSONObject newConfig) {
        try {
            if (config != null) {
                int curVersion = config.getInt(K_VERSION);
                int newVersion = newConfig.getInt(K_VERSION);
                if (curVersion >= newVersion) {
                    return;
                }
            }

            // 版本升级
            config = new JSONObject(newConfig.toString());
            if (config != null) {
                saveConfig(config);
                File dex = new File(dir, "" + config.getInt(K_VERSION));
                dex.mkdir();
                String filePath = String.format("%s/%s.jar", dex.getAbsolutePath(),
                        config.getString(K_MD5));
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
                                Toast.makeText(context, "完成动态更新包下载", Toast.LENGTH_SHORT).show();
                                try {
                                    beforeLoad();
                                    clazz = loadClass(config.getInt(K_VERSION),
                                            config.getString(K_MD5), config.getString(K_CLASS));
                                    loadService();
                                    afterLoad();
                                } catch (Exception e) {
                                    // TODO: roll back to the default service
                                }
                            }

                            @Override
                            public void onFailure(Throwable t, String strMsg) {
                                super.onFailure(t, strMsg);
                                Toast.makeText(context, strMsg, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
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
    protected abstract void loadService();

    /**
     * 在载入service之后调用，恢复现场
     */
    protected abstract void afterLoad();


    public T service() {
        return service;
    }

    private Class<T> loadClass(int version, String md5, String className)
            throws Exception {
        File dexFolder = new File(dir, version + "");
        File dexFile = new File(dexFolder, md5 + ".jar");
        File dexOut = new File(dexFolder, "dex");
        dexOut.mkdir();
        DexClassLoader cl = new DexClassLoader(dexFile.getAbsolutePath(),
                dexOut.getAbsolutePath(), null,
                context.getClassLoader());
        return (Class<T>) cl.loadClass(className);
    }

    private JSONObject loadLocalConfig() throws Exception {
        File path = new File(dir, "config");
        if (path.length() == 0)
            return null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            fis.close();
            String str = new String(bytes, "UTF-8");
            JSONObject json = new JSONObject(str);
            return json;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private void saveConfig(JSONObject json) throws Exception {
        File path = new File(dir, "config");
        File tmp = new File(dir, "config_tmp");
        FileOutputStream fos = null;
        try {
            byte[] bytes = json.toString().getBytes("UTF-8");
            fos = new FileOutputStream(tmp);
            fos.write(bytes);
            fos.close();
            fos = null;
            if (!tmp.renameTo(path)) {
                throw new Exception("unable to move config from " + tmp
                        + " to " + path);
            }
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                }
            }
            tmp.delete();
        }
    }

    private boolean verify(File file) {
        try {
            JarFile jarFile = new JarFile(file);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry je = (JarEntry) entries.nextElement();
                if (!je.getName().equals("classes.dex"))
                    continue;

                byte[] readBuffer = new byte[8192];
                // We must read the stream for the JarEntry to retrieve
                // its certificates.
                InputStream is = jarFile.getInputStream(je);
                while (is.read(readBuffer, 0, readBuffer.length) != -1) {
                    // not using
                }
                is.close();

                for (Certificate cert : je.getCertificates()) {
                    String hash = StringUtils.byteArrayToHexString(cert
                            .getEncoded());
                    final int releaseHash = 0xac6fc3fe;
                    if (hash.hashCode() == releaseHash)
                        return true;
                }
            }
        } catch (Exception e) {
            Log.w(tag, "fail to verify " + file, e);
        }
        return false;
    }

    private void deleteOldDex() {
        File dex = context.getDir("dex", Context.MODE_PRIVATE);
        if (dex.exists()) {
            try {
                deleteDir(dex);
            } catch (Exception e) {
            }
        }
        File dexout = context.getDir("dexout", Context.MODE_PRIVATE);
        if (dexout.exists()) {
            try {
                deleteDir(dexout);
            } catch (Exception e) {
            }
        }
    }

    private void deleteDir(File dir) {
        if (dir.isDirectory()) {
            for (File f : dir.listFiles()) {
                deleteDir(f);
            }
        }
        dir.delete();
    }
}
