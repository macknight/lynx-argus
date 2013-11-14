package com.lynx.argus.app.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import com.lynx.lib.cache.CacheService;
import com.lynx.lib.cache.CacheService.CacheType;
import com.lynx.lib.core.LFApplication;
import com.lynx.lib.http.HttpCallback;
import com.lynx.lib.http.HttpService;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: chris.liu
 * Date: 13-11-14 下午5:21
 */
public class ImageLoader {
	private HttpService httpService;
	private CacheService cacheService;
	private String url;
	private ImageView imageView;


	public ImageLoader(Context context, String url, ImageView imageView) {
		this.httpService = (HttpService) LFApplication.instance().service("http");
		this.cacheService = (CacheService) LFApplication.instance().service("cache");

		this.url = url;
		this.imageView = imageView;
	}

	// 最主要的方法
	public void loadImage() {
		Bitmap bmp = null;
		try {
			bmp = (Bitmap) cacheService.get(url, CacheType.Memory);
			if (bmp == null) {
				InputStream data = (InputStream) cacheService.get(url, CacheType.File);
				if (data != null) {
					bmp = BitmapFactory.decodeStream(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (bmp != null) {
			imageView.setImageBitmap(bmp);
		} else { // 若没有的话则从网上加载图片
			httpService.get(url, callback);
		}
	}

	private HttpCallback callback = new HttpCallback() {
		@Override
		public void onSuccess(Object o) {
			try {
				Bitmap bmp = BitmapFactory.decodeStream((InputStream) o);
				if (bmp != null) {
					CacheType[] cacheTypes = {CacheType.File};
					cacheService.put(url, bmp, cacheTypes);
					imageView.setImageBitmap(bmp);
				}
			} catch (Exception e) {

			}
		}

		@Override
		public void onFailure(Throwable t, String strMsg) {
			super.onFailure(t, strMsg);
		}
	};

	// decode这个图片并且按比例缩放以减少内存消耗，虚拟机对每张图片的缓存大小也是有限制的
	private Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 100;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	public static void copyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (; ; ) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
			Log.e("", "CopyStream catch Exception...");
		}
	}

	public static byte[] stream2byte(InputStream instream) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buff = new byte[100];
		int rc = 0;
		while ((rc = instream.read(buff, 0, 100)) > 0) {
			baos.write(buff, 0, rc);
		}
		return baos.toByteArray();
	}
}
