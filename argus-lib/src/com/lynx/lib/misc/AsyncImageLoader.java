package com.lynx.lib.misc;

/**
 * Created with IntelliJ IDEA.
 * User: chris.liu
 * Date: 13-11-15 下午1:37
 */

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;

import com.lynx.lib.cache.CacheService;
import com.lynx.lib.cache.CacheService.CacheType;
import com.lynx.lib.core.LFApplication;
import com.lynx.lib.core.Logger;
import com.lynx.lib.http.HttpService;
import com.lynx.lib.util.ImageUtil;
import com.lynx.lib.util.StringUtil;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 2014-1-22 下午5:15:48
 */
public class AsyncImageLoader {
	private static final String Tag = "AsynImageLoader";
	private HttpService httpService;
	private CacheService cacheService;

	private int coreCPUNum; // CPU核心数
	private ExecutorService executorService;

	private static AsyncImageLoader instance;

	private static final int ASYNC_IMAGE_LOAD_SUCCESS = 0;
	private static final int ASYNC_IMAGE_LOAD_FAIL = 1;

	public static AsyncImageLoader instance() {
		if (instance == null) {
			instance = new AsyncImageLoader();
		}
		return instance;
	}

	private AsyncImageLoader() {
		this.httpService = (HttpService) LFApplication.instance().service("http");
		this.cacheService = (CacheService) LFApplication.instance().service("cache");
		coreCPUNum = Runtime.getRuntime().availableProcessors();
		executorService = Executors.newFixedThreadPool(coreCPUNum);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 子线程中返回的下载完成的任务
			switch (msg.what) {
			case ASYNC_IMAGE_LOAD_SUCCESS:
				Task task = (Task) msg.obj;
				// 调用callback对象的loadImage方法，并将图片路径和图片回传给adapter
				task.listener.onSuccess(task.bitmap);
				CacheType[] cacheTypes = { CacheType.Memory, CacheType.File };
				cacheService.put(StringUtil.MD5Encode(task.path), task.bitmap, cacheTypes);
				break;
			case ASYNC_IMAGE_LOAD_FAIL:

				break;
			}

		}
	};

	/**
	 * 异步载入图片
	 * 
	 * @param imageView
	 * @param url
	 */
	public void showAsyncImage(ImageView imageView, String url) {
		if (TextUtils.isEmpty(url) || imageView == null) {
			return;
		}

		imageView.setTag(url);

		String key = StringUtil.MD5Encode(url);
		Bitmap bmp = (Bitmap) cacheService.get(key, CacheType.Memory);
		if (bmp == null) {
			// get bmp from file cache
			byte[] buf = (byte[]) cacheService.get(key, CacheType.File);
			if (buf != null) {
				bmp = BitmapFactory.decodeByteArray(buf, 0, buf.length);
				if (bmp != null) {
					cacheService.put(key, bmp, new CacheType[] { CacheType.Memory });
				}
			}
		}

		if (bmp == null) {
			// load image from internet
			Task task = new Task(url, imageView);
			executorService.submit(task);
		} else {
			imageView.setImageBitmap(bmp);
		}
	}

	private class Task implements Runnable {
		String path; // 下载任务的下载路径
		Bitmap bitmap;
		ImageLoadListener listener; // 回调对象

		public Task(final String path, final ImageView imageView) {
			this.path = path;
			this.listener = new ImageLoadListener() {
				@Override
				public void onSuccess(Bitmap bitmap) {
					if (imageView.getTag().equals(path)) {
						imageView.setImageBitmap(bitmap);
					}
				}

				@Override
				public void onFail() {
					Logger.i(Tag, "download fail");
				}
			};
		}

		@Override
		public void run() {
			try {
				bitmap = ImageUtil.getbitmap(path);
				if (bitmap != null) {
					Message msg = handler.obtainMessage();
					msg.what = ASYNC_IMAGE_LOAD_SUCCESS;
					msg.obj = this;
					handler.sendMessage(msg);
				} else {
					Message msg = handler.obtainMessage();
					msg.what = ASYNC_IMAGE_LOAD_FAIL;
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				Logger.e(Tag, "async image load task error", e);
			}
		}

		@Override
		public boolean equals(Object o) {
			Task task = (Task) o;
			return path.equals(task.path);
		}
	}

	/**
	 * 图片下载完成回调接口
	 */
	public interface ImageLoadListener {
		void onSuccess(Bitmap bitmap);

		void onFail();
	}
}
