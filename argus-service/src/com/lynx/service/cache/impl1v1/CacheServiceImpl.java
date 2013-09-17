package com.lynx.service.cache.impl1v1;

import android.content.Context;
import android.util.Log;
import com.lynx.service.cache.CacheService;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-17 上午10:02
 */
public class CacheServiceImpl implements CacheService {
    private static final String Tag = "cache";
    private MemoryCache memoryCache;
    private FileCache fileCache;


    public CacheServiceImpl(Context context) {
        try {
            memoryCache = new MemoryCache();
            fileCache = new FileCache(context);
        } catch (Exception e) {
            Log.e(Tag, "init cache service error", e);
        }
    }

    @Override
    public void put(String key, Object value, CacheType cacheType) {


    }

    @Override
    public Object get(String key) {
        Object obj = memoryCache.get(key);
        if (obj == null) {
            fileCache.getFile(key);
        }

        return null;
    }

    @Override
    public void remove(String key, CacheType cacheType) {
         if (cacheType == CacheType.Memory) {

         }

        switch (cacheType) {
            case Memory:
                memoryCache.remove(key);
                break;
            case File:
                fileCache.remove(key);
                break;
        }
    }

    @Override
    public void clear(CacheType cacheType) {
        switch (cacheType) {
            case Memory:
                memoryCache.clear();
                break;
            case File:
                fileCache.clear();
                break;
            case BOTH:
                memoryCache.clear();
                fileCache.clear();
                break;
        }
    }
}
