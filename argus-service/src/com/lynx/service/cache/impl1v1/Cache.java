package com.lynx.service.cache.impl1v1;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-9-18 上午10:32
 */
public interface Cache {

	/**
	 * 获取缓存数据
	 * 
	 * @param key
	 * @return
	 */
	Object get(String key);

	/**
	 * 添加缓存
	 * 
	 * @param key
	 * @param data
	 */
	void put(String key, Object data);

	/**
	 * 删除某项缓存
	 * 
	 * @param key
	 */
	void remove(String key);

	/**
	 * 清除缓存
	 */
	void clear();
}
