package com.lynx.lib.imageloader;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 2014-1-22 下午5:15:48
 */
public class LazyImageLoader {
	private static final String TAG = "ProfileImageCacheManager";
	public static final int HANDLER_MESSAGE_ID = 1;
	public static final String EXTRA_BITMAP = "extra_bitmap";
	public static final String EXTRA_IMAGE_URL = "extra_image_url";

	private ImageManager imageManager = new ImageManager();
	private BlockingQueue<String> urlList = new ArrayBlockingQueue<String>(50);
	private CallbackManager callbackManager = new CallbackManager();

	private GetImageTask mTask = new GetImageTask();

	/**
	 * 取图片, 可能直接从cache中返回, 或下载图片后返回
	 * 
	 * @param url
	 * @param callback
	 * @return
	 */
	public Bitmap get(String url, ImageLoaderCallback callback) {
		Bitmap bitmap = ImageManager.defaultBmp;
		if (imageManager.isContains(url)) {
			bitmap = imageManager.get(url);
		} else {
			// bitmap不存在，启动Task进行下载
			callbackManager.put(url, callback);
			startDownloadThread(url);
		}
		return bitmap;
	}

	private void startDownloadThread(String url) {
		if (url != null) {
			addUrlToDownloadQueue(url);
		}

		// Start Thread
		State state = mTask.getState();
		if (Thread.State.NEW == state) {
			mTask.start(); // first start
		} else if (Thread.State.TERMINATED == state) {
			mTask = new GetImageTask(); // restart
			mTask.start();
		}
	}

	private void addUrlToDownloadQueue(String url) {
		if (!urlList.contains(url)) {
			try {
				urlList.put(url);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// Low-level interface to get ImageManager
	public ImageManager getImageManager() {
		return imageManager;
	}

	private class GetImageTask extends Thread {
		private volatile boolean mTaskTerminated = false;
		private static final int TIMEOUT = 3 * 60;
		private boolean isPermanent = true;

		@Override
		public void run() {
			try {
				while (!mTaskTerminated) {
					String url;
					if (isPermanent) {
						url = urlList.take();
					} else {
						url = urlList.poll(TIMEOUT, TimeUnit.SECONDS); // waiting
						if (null == url) {
							break;
						} // no more, shutdown
					}

					// Bitmap bitmap = ImageCache.defaultBmp;
					final Bitmap bitmap = imageManager.safeGet(url);

					// use handler to process callback
					final Message m = handler.obtainMessage(HANDLER_MESSAGE_ID);
					Bundle bundle = m.getData();
					bundle.putString(EXTRA_IMAGE_URL, url);
					bundle.putParcelable(EXTRA_BITMAP, bitmap);
					handler.sendMessage(m);
				}
			} catch (Exception ioe) {
				Log.e(TAG, "Get Image failed, " + ioe.getMessage());
			} finally {
				Log.v(TAG, "Get image task terminated.");
				mTaskTerminated = true;
			}
		}

		@SuppressWarnings("unused")
		public boolean isPermanent() {
			return isPermanent;
		}

		@SuppressWarnings("unused")
		public void setPermanent(boolean isPermanent) {
			this.isPermanent = isPermanent;
		}

		@SuppressWarnings("unused")
		public void shutDown() throws InterruptedException {
			mTaskTerminated = true;
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_MESSAGE_ID:
				final Bundle bundle = msg.getData();
				String url = bundle.getString(EXTRA_IMAGE_URL);
				Bitmap bitmap = (Bitmap) (bundle.get(EXTRA_BITMAP));

				// callback
				callbackManager.call(url, bitmap);
				break;
			default:
				// do nothing.
			}
		}
	};

	public interface ImageLoaderCallback {
		void refresh(String url, Bitmap bitmap);
	}

	public static class CallbackManager {
		private static final String TAG = "CallbackManager";
		private ConcurrentHashMap<String, List<ImageLoaderCallback>> mCallbackMap;

		public CallbackManager() {
			mCallbackMap = new ConcurrentHashMap<String, List<ImageLoaderCallback>>();
		}

		public void put(String url, ImageLoaderCallback callback) {
			Log.v(TAG, "url=" + url);
			if (!mCallbackMap.containsKey(url)) {
				Log.v(TAG, "url does not exist, add list to map");
				mCallbackMap.put(url, new ArrayList<ImageLoaderCallback>());
				// mCallbackMap.put(url, Collections.synchronizedList(new
				// ArrayList<ImageLoaderCallback>()));
			}

			mCallbackMap.get(url).add(callback);
			Log.v(TAG,
					"Add callback to list, count(url)="
							+ mCallbackMap.get(url).size());
		}

		public void call(String url, Bitmap bitmap) {
			Log.v(TAG, "call url=" + url);
			List<ImageLoaderCallback> callbackList = mCallbackMap.get(url);
			if (callbackList == null) {
				// FIXME: 有时会到达这里，原因我还没想明白
				Log.e(TAG, "callbackList=null");
				return;
			}
			for (ImageLoaderCallback callback : callbackList) {
				if (callback != null) {
					callback.refresh(url, bitmap);
				}
			}

			callbackList.clear();
			mCallbackMap.remove(url);
		}

	}
}
