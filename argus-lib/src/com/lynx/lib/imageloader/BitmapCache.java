package com.lynx.lib.imageloader;

import android.graphics.Bitmap;
import android.util.LruCache;

import java.lang.ref.SoftReference;

/**
 * 图片内存缓存
 * 
 * @author chris.liu
 * 
 * @version 14-1-23 下午2:26
 */
public class BitmapCache extends LruCache<String, SoftReference<Bitmap>> {

	public BitmapCache(final int maxSize) {
		super(maxSize);
	}

	@Override
	protected void entryRemoved(final boolean evicted, final String key,
			final SoftReference<Bitmap> oldValue,
			final SoftReference<Bitmap> newValue) {
		if (oldValue != null) {
			final Bitmap bitmap = oldValue.get();

			if (bitmap != null) {
				if (!oldValue.get().isRecycled()) {
					oldValue.get().recycle();
				}
			}
		}
	}
}
