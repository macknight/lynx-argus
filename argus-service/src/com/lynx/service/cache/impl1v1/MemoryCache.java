package com.lynx.service.cache.impl1v1;

import android.text.TextUtils;
import android.util.Log;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-17 下午3:36
 */
public class MemoryCache {
    private static final String TAG = "MemoryCache";

    //WeakReference Map: key=string, value=Bitmap
    private Map<String, Object> cache = new WeakHashMap<String, Object>();

    /**
     * Search the memory cache by a unique key.
     *
     * @param key Should be unique.
     * @return The Bitmap object in memory cache corresponding to specific key.
     */
    public Object get(String key) {
        if (key != null)
            return cache.get(key);
        return null;
    }

    /**
     * Put a bitmap into cache with a unique key.
     *
     * @param key   Should be unique.
     * @param value A bitmap.
     */
    public void put(String key, Object value) {
        if (key != null && !"".equals(key) && value != null) {
            cache.put(key, value);
            Log.d(TAG, "size of memory cache: " + cache.size());
        }
    }

    public void remove(String key) {
        if (key != null && !TextUtils.isEmpty(key)) {
            cache.remove(key);
        }
    }

    /**
     * clear the memory cache.
     */
    public void clear() {
        cache.clear();
    }
}