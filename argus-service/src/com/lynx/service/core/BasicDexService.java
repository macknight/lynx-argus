package com.lynx.service.core;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.lynx.lib.core.Service;
import com.lynx.lib.util.StringUtils;
import com.lynx.lib.location.LocationService;
import com.lynx.service.location.impl.LocationServiceImpl;
import dalvik.system.DexClassLoader;
import org.json.JSONObject;

import java.io.*;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/30/13 11:29 AM
 */
public class BasicDexService implements Service {
    protected static String DEX_CONFIG_URL = "http://192.168.33.95/argus/locationconfig";
    private static final String K_VERSION = "dexVersion";
    private static final String K_URL = "dexUrl";
    private static final String K_MD5 = "dexMD5";
    private static final String K_CLASS = "dexClass";

    private static final int MIN_DEX_VERSION = 142;

    private String TAG = this.getClass().getSimpleName();
    private Context context;

    private final File dir;
    // ./location/config
    // ./location/config_tmp
    private JSONObject config;
    private int version;
    private Service service;

    public BasicDexService(Context context) {
        this.context = context;
        deleteOldDex();
        dir = new File(context.getFilesDir(), "location");
        dir.mkdir();
        try {
            config = readConfig();
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "unable to read config at " + new File(dir, "config"), e);
        }
        if (config == null) {
            this.config = new JSONObject();
        }
    }

    private Class<?> loadClass(int version, String md5, String className)
            throws Exception {
        File d = new File(dir, "v" + version);
        File file = new File(d, TextUtils.isEmpty(md5) ? "1.apk" : md5 + ".apk");
        if (file.length() == 0) {
            throw new FileNotFoundException(file + " not found");
        }
        File dexout = new File(d, "dexout");
        dexout.mkdir();
        DexClassLoader loader = new DexClassLoader(file.getAbsolutePath(),
                dexout.getAbsolutePath(), null, context.getClassLoader());
        Class<?> c = loader.loadClass(className);
        Log.i(this.getClass().getSimpleName(), className + " loaded from " + file + ", version=" + version);
        return c;
    }

    @Override
    public boolean start() {
        int ver = config.optInt(K_VERSION, 0);
        if (ver <= MIN_DEX_VERSION)
            ver = 0;

        if (version != ver) {
            try {
                String md5 = config.optString(K_MD5);
                String className = config.getString(K_CLASS);
                Class<?> c = loadClass(ver, md5, className);
                LocationService newService = (LocationService) c
                        .getConstructor(Context.class).newInstance(context);
                if (service != null) {
                    service.stop();
                }
                service = newService;
                version = ver;
            } catch (FileNotFoundException e) {
                Log.w(TAG, e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "dex class load fail", e);
            }
        }

        if (service == null) {
            service = new LocationServiceImpl(context);
            version = ver;
            Log.i(TAG, "DefaultLocationService loaded, version=" + ver);
        }

        return service.start();
    }

    @Override
    public boolean stop() {
        if (service != null)
            return service.stop();
        return false;
    }

    private JSONObject readConfig() throws Exception {
        File path = new File(dir, "config");
        if (path.length() == 0)
            return new JSONObject();
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
            Log.w(TAG, "fail to verify " + file, e);
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
