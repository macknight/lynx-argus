package com.lynx.lib.imageloader;

import android.graphics.Bitmap;

/**
 *
 * @author zhufeng.liu
 *
 * @version 2014-1-22 下午5:15:48
 */
public interface ImageCache {

	public Bitmap get(String url);

	public void put(String url, Bitmap bitmap);
}
