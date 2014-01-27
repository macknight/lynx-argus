package com.lynx.lib.misc;

/**
 * Created with IntelliJ IDEA.
 * User: chris.liu
 * Date: 13-11-15 下午1:37
 */

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

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 2014-1-22 下午5:15:48
 */
public class AsyncImageLoader {
	private static final String TAG = "AsynImageLoader";
	// 缓存下载过的图片的Map
	private HttpService httpService;
	private CacheService cacheService;

	private int coreCPUNum; // CPU核心数
	private ExecutorService executorService;

	private static AsyncImageLoader instance;

	public static AsyncImageLoader instance() {
		if (instance == null) {
			instance = new AsyncImageLoader();
		}
		return instance;
	}

	private AsyncImageLoader() {
		this.httpService = (HttpService) LFApplication.instance().service(
				"http");
		this.cacheService = (CacheService) LFApplication.instance().service(
				"cache");
		coreCPUNum = Runtime.getRuntime().availableProcessors();
		executorService = Executors.newFixedThreadPool(coreCPUNum);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 子线程中返回的下载完成的任务
			Task task = (Task) msg.obj;
			// 调用callback对象的loadImage方法，并将图片路径和图片回传给adapter
			task.listener.onSuccess(task.bitmap);
			CacheType[] cacheTypes = { CacheType.Memory, CacheType.File };
			cacheService.put(StringUtil.MD5Encode(task.path), task.bitmap,
					cacheTypes);
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
					cacheService.put(key, bmp,
							new CacheType[] { CacheType.Memory });
				}
			}
		}

		if (bmp == null) {
			// load image from internet
            Logger.i("Tag", "down image from " + url);
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

				}
			};
		}

		@Override
		public void run() {
			try {
				InputStream instream = (InputStream) httpService.getSync(path);
                bitmap = ImageUtil.stream2bitmap(instream);
				Message msg = handler.obtainMessage();
				msg.obj = this;
				handler.sendMessage(msg);
			} catch (Exception e) {

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
