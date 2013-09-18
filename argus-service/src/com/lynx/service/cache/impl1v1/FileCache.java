package com.lynx.service.cache.impl1v1;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import com.lynx.lib.util.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-17 下午3:36
 */
public class FileCache implements Cache {
    private static final String Tag = "MemoryCache";
    private static final String CACHE_DIR = "Argus";
    private File cacheDir;    //the directory to save images

    public FileCache(Context context) {
        // Find the directory to save cached images
        if (android.os.Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(Environment.getExternalStorageDirectory(), CACHE_DIR);
        } else {
            cacheDir = context.getCacheDir();
        }
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        Log.d(Tag, "cache dir: " + cacheDir.getAbsolutePath());
    }

    /**
     * Search the specific image file with a unique key.
     *
     * @param key Should be unique.
     * @return Returns the image file corresponding to the key.
     */
    @Override
    public Object get(String key) {
        File f = new File(cacheDir, key);

        if (!f.exists()) {
            return null;
        }

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            fis.close();
            return bytes;
        } catch (Exception e) {

        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    /**
     * Put a bitmap into cache with a unique key.
     *
     * @param key   Should be unique.
     * @param value A bitmap.
     */
    @Override
    public void put(String key, Object value) {
        File f = new File(cacheDir, key);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        IOUtil.saveFile(f, value);
    }

    @Override
    public void remove(String key) {
        if (key != null && !TextUtils.isEmpty(key)) {
            try {
                IOUtil.deleteFile(new File(cacheDir, key));
            } catch (Exception e) {
                Log.e(Tag, "remove disk cache error", e);
            }
        }
    }

    /**
     * Clear the cache directory on sdcard.
     */
    public void clear() {
        try {
            IOUtil.deleteFile(cacheDir);
        } catch (Exception e) {
            Log.e(Tag, "remove disk cache error", e);
        }
    }
}