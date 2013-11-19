package com.lynx.service.cache.impl1v1;

import android.content.Context;
import android.util.Log;
import com.lynx.lib.cache.CacheService;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-17 上午10:02
 */
public class CacheServiceImpl implements CacheService {
    public static final String Tag = "cache";
    private MemoryCache memoryCache;
    private FileCache fileCache;
    private DBCache dbCache;


    public CacheServiceImpl(Context context) {
        try {
            memoryCache = new MemoryCache();
            fileCache = new FileCache(context);
            dbCache = new DBCache(context);
        } catch (Exception e) {
            Log.e(Tag, "init cache service error", e);
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void put(String key, Object value, CacheType[] cacheTypes) {

        for (CacheType cacheType : cacheTypes) {
            switch (cacheType) {
                case Memory:
                    memoryCache.put(key, value);
                    break;
                case File:
                    fileCache.put(key, value);
                    break;
                case DB:
                    dbCache.put(key, value);
                    break;
            }
        }
    }

    @Override
    public Object get(String key, CacheType cacheType) {
        switch (cacheType) {
            case Memory:
                return memoryCache.get(key);
            case File:
                return fileCache.get(key);
            case DB:
                return dbCache.get(key);
            default:
                return null;
        }
    }

    @Override
    public void remove(String key, CacheType[] cacheTypes) {
        for (CacheType cacheType : cacheTypes) {
            switch (cacheType) {
                case Memory:
                    memoryCache.remove(key);
                    break;
                case File:
                    fileCache.remove(key);
                    break;
                case DB:
                    dbCache.remove(key);
                    break;
            }
        }
    }

    @Override
    public void clear(CacheType[] cacheTypes) {
        for (CacheType cacheType : cacheTypes) {
            switch (cacheType) {
                case Memory:
                    memoryCache.clear();
                    break;
                case File:
                    fileCache.clear();
                    break;
                case DB:
                    dbCache.clear();
                    break;
            }
        }
    }
}
