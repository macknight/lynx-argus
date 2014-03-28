package com.lynx.service.cache.impl1v1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.lynx.lib.util.FileUtil;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-9-17 下午3:36
 */
public class FileCache implements Cache {
	private static final String Tag = "FileCache";
	private static final String CACHE_DIR = "Argus";
	private File cacheDir; // the directory to save images

	public FileCache(Context context) {
		// Find the directory to save cached images
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			cacheDir = new File(Environment.getExternalStorageDirectory(), CACHE_DIR);
		} else {
			cacheDir = context.getCacheDir();
		}
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
	}

	/**
	 * Search the specific file with a unique key.
	 * 
	 * @param key
	 *            Should be unique.
	 * @return Returns the file corresponding to the key.
	 */
	@Override
	public Object get(String key) {
		File f = new File(cacheDir, key);

		if (!f.exists()) {
			return null;
		}

		InputStream instream = null;
		try {
			instream = new FileInputStream(f);
			byte[] bytes = new byte[instream.available()];
			instream.read(bytes);
			instream.close();
			return bytes;
		} catch (Exception e) {

		} finally {
			if (instream != null) {
				try {
					instream.close();
				} catch (Exception e) {

				}
			}
		}
		return null;
	}

	/**
	 * Put a bitmap into cache with a unique key.
	 * 
	 * @param key
	 *            Should be unique.
	 * @param value
	 *            A bitmap.
	 */
	@Override
	public void put(String key, Object value) {
		File f = new File(cacheDir, key);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		FileUtil.saveFile(f, value);
	}

	@Override
	public void remove(String key) {
		if (key != null && !TextUtils.isEmpty(key)) {
			try {
				FileUtil.deleteFile(new File(cacheDir, key));
			} catch (Exception e) {
				Log.e(Tag, "remove disk cache error", e);
			}
		}
	}

	/**
	 * Clear the cache directory on sdcard.
	 */
	public void clear() {
		try {
			FileUtil.deleteFile(cacheDir);
		} catch (Exception e) {
			Log.e(Tag, "remove disk cache error", e);
		}
	}
}
