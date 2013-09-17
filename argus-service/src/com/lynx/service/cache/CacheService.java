package com.lynx.service.cache;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-17 上午10:02
 */
public interface CacheService {

    /**
     * 以指定方式将数据放入CacheService中
     *
     * @param key
     * @param value
     * @param cacheType
     */
    void put(String key, Object value, CacheType cacheType);

    /**
     * 获取缓存数据
     *
     * @param key
     * @return
     */
    Object get(String key);

    /**
     * 根据key删除缓存在指定缓存中的数据
     *
     * @param key
     */
    void remove(String key, CacheType cacheType);


    /**
     * 清空指定缓存
     *
     * @param cacheType
     */
    void clear(CacheType cacheType);


    public enum CacheType {
        Memory, // 只在内存加入/删除数据缓存
        File, // 只在外存加入/删除数据缓存
        BOTH // 内存和外存都加入/删除数据缓存
    }
}
