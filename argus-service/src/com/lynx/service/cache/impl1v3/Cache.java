package com.lynx.service.cache.impl1v3;

import java.util.Collection;

/**
 * @author zhufeng.liu
 * @version 14-2-24 下午4:19
 */
public interface Cache<K, V> {

	boolean put(K key, V value);

	V get(K key);

	void remove(K key);

	Collection<K> keys();

	void clear();
}
