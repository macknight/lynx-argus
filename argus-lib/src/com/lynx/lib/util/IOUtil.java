package com.lynx.lib.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.Charset;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-17 上午11:39
 */
public class IOUtil {
    public static final Charset US_ASCII = Charset.forName("US-ASCII");
    public static final Charset UTF_8 = Charset.forName("UTF-8");


    public static JSONObject loadLocalConfig(File dir, String fileName) throws Exception {
        File path = new File(dir, fileName);
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

    public static void saveConfig(File dir, JSONObject json) throws Exception {
        File config = new File(dir, "config");
        File configTmp = new File(dir, "config_tmp");
        File configOld = new File(dir, "config_old");
        FileOutputStream fos = null;

        if (configOld.exists()) {
            configOld.delete();
        }
        if (config.exists()) {
            config.renameTo(configOld);
        }

        try {
            byte[] bytes = json.toString().getBytes("UTF-8");
            fos = new FileOutputStream(configTmp);
            fos.write(bytes);
            fos.close();
            fos = null;
            config.delete();
            if (!configTmp.renameTo(config)) {
                // revert to old config file
                if (config.exists()) {
                    config.delete();
                }
                configOld.renameTo(config);
                throw new Exception("unable to move config from " + configTmp
                        + " to " + config);
            }
        } catch (Exception e) {
            config.delete();
            configOld.renameTo(config);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                }
            }
            configTmp.delete();
            configOld.delete();
        }
    }

    public static String readFully(Reader reader) throws IOException {
        try {
            StringWriter writer = new StringWriter();
            char[] buffer = new char[1024];
            int count;
            while ((count = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, count);
            }
            return writer.toString();
        } finally {
            reader.close();
        }
    }

    /**
     * Deletes the contents of {@code dir}. Throws an IOException if any file
     * could not be deleted, or if {@code dir} is not a readable directory.
     */
    public static void deleteFile(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files == null) {
                    throw new IOException("not a readable directory: " + file);
                }
                for (File f : files) {
                    if (f.isDirectory()) {
                        deleteFile(f);
                    }
                    if (!f.delete()) {
                        throw new IOException("failed to delete file: " + f);
                    }
                }
            } else {
                file.delete();
            }
        }


    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 将数据存储到sdcard上
     *
     * @param data
     * @return
     */
    public static boolean saveFile(File file, Object data) {
        // TODO 完成对象固化
        if (data instanceof Bitmap) {
            saveBitmap(file, (Bitmap) data);
        } else if (data instanceof byte[]) {

        }

        return false;
    }

    public static boolean saveBitmap(File file, Bitmap data) {
        if (file == null || data == null)
            return false;
        try {
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            return data.compress(CompressFormat.PNG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean saveByteArray(File file, byte[] data) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            String parent = file.getParent();
            File dir = new File(parent);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            if (file.exists()) {
                file.delete();
            }

            fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
            fos = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return false;
    }


    public static boolean verifyDexFile(File file) {
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
            Log.w("IOUtil", "fail to verify " + file, e);
        }
        return false;
    }
}
