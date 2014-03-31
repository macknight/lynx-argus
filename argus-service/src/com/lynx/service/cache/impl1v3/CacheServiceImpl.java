package com.lynx.service.cache.impl1v3;

import android.content.Context;
import android.util.Log;

import com.lynx.lib.cache.CacheService;

/**
 * 
 * @author zhufeng.liu
 * @version 14-2-22 上午10:02
 */
public class CacheServiceImpl implements CacheService {
	public static final String Tag = "cache";
	private Cache cache;

	public CacheServiceImpl(Context context) {
		try {
			cache = Cache.getInstance(context);
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

	}

	@Override
	public Object get(String key, CacheType cacheType) {
		return null;
	}

	@Override
	public void remove(String key, CacheType[] cacheTypes) {

	}

	@Override
	public void clear(CacheType[] cacheTypes) {

	}
}
