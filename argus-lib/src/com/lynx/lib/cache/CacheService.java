package com.lynx.lib.cache;

import com.lynx.lib.core.dex.Service;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-17 上午10:02
 */
public interface CacheService extends Service {

    /**
     * 以指定方式将数据放入CacheService中,默认放入内存缓存
     *
     * @param key
     * @param value
     * @param cacheTypes
     */
    void put(String key, Object value, CacheType[] cacheTypes);

    /**
     * 从指定缓存中获取缓存数据
     *
     * @param key
     * @param cacheType
     * @return
     */
    Object get(String key, CacheType cacheType);

    /**
     * 根据key删除指定缓存中的数据
     *
     * @param key
     * @param cacheTypes
     */
    void remove(String key, CacheType[] cacheTypes);


    /**
     * 清空指定缓存
     *
     * @param cacheTypes
     */
    void clear(CacheType[] cacheTypes);


    public enum CacheType {
        Memory, // 只在内存加入/删除数据缓存
        File, // 只在外存加入/删除数据缓存
        DB; // 只在SQLite中加入/删除数据缓存
    }
}
