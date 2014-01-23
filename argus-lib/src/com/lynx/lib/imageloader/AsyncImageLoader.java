package com.lynx.lib.imageloader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.widget.ImageView;
import com.lynx.lib.core.LFApplication;
import com.lynx.lib.http.HttpService;
import com.lynx.lib.util.FileUtil;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.LinkedList;

/**
 * 图片异步loader
 * 
 * @author chris.liu
 * 
 * @version 14-1-23 上午11:33
 */
public class AsyncImageLoader {

	private static BitmapCache bitmapCache;

	private final LinkedList<WorkItem> workItems = new LinkedList<WorkItem>();
	private final DiskCache diskCache;
	private final Activity activity;
	private static HttpService httpService;

	private boolean paused;
	private boolean closed;
	private Thread thread;

	/**
	 * Creates a new instance of AsyncImageLoader object and specifies the
	 * maximum number of Bitmap objects to cache in memory, and the path to
	 * store generated thumbnails.
	 * 
	 * @param activity
	 *            the Activity that hosts the ImageView to display loaded
	 *            Bitmaps.
	 * @param numberOfCachedBitmaps
	 *            the maximum number of Bitmap objects to cache in memory.
	 * @param thumbnailPath
	 *            the path to store generated thumbnail files.
	 */
	public AsyncImageLoader(final Activity activity,
			final int numberOfCachedBitmaps, final String thumbnailPath) {
		if (AsyncImageLoader.bitmapCache == null) {
			AsyncImageLoader.bitmapCache = new BitmapCache(
					numberOfCachedBitmaps);
		}

		httpService = (HttpService) LFApplication.instance().service("http");

		this.diskCache = new DiskCache(thumbnailPath);
		this.activity = activity;
	}

	/**
	 * Loads and displays a Bitmap at the given path for an ImageView.
	 * 
	 * @param path
	 *            the path to load a Bitmap.
	 * @param imageView
	 *            the ImageView to display the loaded Bitmap.
	 */
	public void loadImage(final String path, final ImageView imageView) {
		this.loadImage(path, imageView, null, null);
	}

	/**
	 * Loads and displays a Bitmap from the given url for ImageView.
	 * 
	 * @param url
	 *            the URL to retrieve the Bitmap data.
	 * @param imageView
	 *            the ImageView to display the loaded ink Bitmap.
	 */
	public void loadImage(final URL url, final ImageView imageView) {
		this.loadImage(url, imageView, null);
	}

	/**
	 * Loads and displays a Bitmap at the given path for an ImageView.
	 * 
	 * @param path
	 *            the path to load a Bitmap.
	 * @param imageView
	 *            the ImageView to display the loaded Bitmap.
	 * @param options
	 *            the bitmap options to use, if any, when loading the Bitmap.
	 */
	public void loadImage(final String path, final ImageView imageView,
			final BitmapFactory.Options options) {
		this.loadImage(path, imageView, options, null);
	}

	/**
	 * Loads and displays a {@link Bitmap} at the given <code>path</code> for an
	 * {@link ImageView}, and notifies the given <code>listener</code> when the
	 * process completes.
	 * 
	 * @param path
	 *            the path to load a {@link Bitmap}.
	 * @param imageView
	 *            the {@link ImageView} to display the loaded {@link Bitmap}.
	 * @param listener
	 *            a callback when the process completes.
	 */
	public void loadImage(final String path, final ImageView imageView,
			final OnImageLoadedListener listener) {
		this.loadImage(path, imageView, null, listener);
	}

	/**
	 * Loads and displays a Bitmap at the given path for an ImageView, and
	 * notifies the given listener when the process completes.
	 * 
	 * @param path
	 *            the path to load a Bitmap.
	 * @param imageView
	 *            the ImageView to display the loaded Bitmap.
	 * @param options
	 *            the bitmap options to use, if any, when loading the Bitmap.
	 * @param listener
	 *            a callback when the process completes.
	 */
	public void loadImage(final String path, final ImageView imageView,
			final BitmapFactory.Options options,
			final OnImageLoadedListener listener) {
		this.checkState();

		if (this.thread == null) {
			this.thread = new Thread(new WorkerThread());

			this.thread.start();
		}

		this.cancel(imageView);

		synchronized (this.workItems) {
			this.workItems.add(new WorkItem(path, imageView, options, -1, -1,
					listener));
			this.workItems.notifyAll();
		}
	}

	/**
	 * Loads and displays a Bitmap from the given url for an ImageView, and
	 * notifies the given listener when the process completes.
	 * 
	 * @param url
	 *            the URL to retrieve the Bitmap data.
	 * @param imageView
	 *            the ImageView to display the loaded Bitmap.
	 * @param listener
	 *            a callback when the process completes.
	 */
	public void loadImage(final URL url, final ImageView imageView,
			final OnImageLoadedListener listener) {
		this.checkState();

		if (this.thread == null) {
			this.thread = new Thread(new WorkerThread());

			this.thread.start();
		}

		this.cancel(imageView);

		synchronized (this.workItems) {
			this.workItems.add(new WorkItem(url, imageView, null, -1, -1,
					listener));
			this.workItems.notifyAll();
		}
	}

	/**
	 * Loads and displays the thumbnail version of the Bitmap at the given path
	 * for an ImageView.
	 * 
	 * @param path
	 *            the path to create a thumbnail from.
	 * @param imageView
	 *            the ImageView to display the generated thumbnail.
	 * @param kind
	 *            either
	 */
	public void loadImageThumbnail(final String path,
			final ImageView imageView, final int kind) {
		this.loadImageThumbnail(path, imageView, kind, null);
	}

	/**
	 * Loads and displays the thumbnail version of the Bitmap at the given path
	 * for an ImageView.
	 * 
	 * @param path
	 *            the path to create a thumbnail from.
	 * @param imageView
	 *            the ImageView to display the generated thumbnail.
	 * @param kind
	 *            either
	 * @param listener
	 *            a callback when the process completes.
	 */
	public void loadImageThumbnail(final String path,
			final ImageView imageView, final int kind,
			final OnImageLoadedListener listener) {
		this.checkState();

		if (this.thread == null) {
			this.thread = new Thread(new WorkerThread());

			this.thread.start();
		}

		this.cancel(imageView);

		synchronized (this.workItems) {
			this.workItems.add(new WorkItem(path, imageView, null, kind, -1,
					listener));
			this.workItems.notifyAll();
		}
	}

	/**
	 * Loads and displays the thumbnail of a video at the given path for an
	 * ImageView.
	 * 
	 * @param path
	 *            the path to create a thumbnail from.
	 * @param imageView
	 *            the ImageView to display the generated thumbnail.
	 * @param kind
	 *            either android.provider.MediaStore.Video.Thumbnails#MINI_KIND
	 *            or android.provider.MediaStore.Video.Thumbnails#MICRO_KIND ,
	 *            or -1 if this parameter is irrelevant.
	 */
	public void loadVideoThumbnail(final String path,
			final ImageView imageView, final int kind) {
		this.loadVideoThumbnail(path, imageView, kind, null);
	}

	/**
	 * Loads and displays the thumbnail of a video at the given path for an
	 * ImageView}.
	 * 
	 * @param path
	 *            the path to create a thumbnail from.
	 * @param imageView
	 *            the ImageView to display the generated thumbnail.
	 * @param kind
	 *            either android.provider.MediaStore.Video.Thumbnails#MINI_KIND
	 *            or android.provider.MediaStore.Video.Thumbnails#MICRO_KIND ,
	 *            or -1 if this parameter is irrelevant.
	 * @param listener
	 *            a callback when the process completes.
	 */
	public void loadVideoThumbnail(final String path,
			final ImageView imageView, final int kind,
			final OnImageLoadedListener listener) {
		this.checkState();

		if (this.thread == null) {
			this.thread = new Thread(new WorkerThread());

			this.thread.start();
		}

		this.cancel(imageView);

		synchronized (this.workItems) {
			this.workItems.add(new WorkItem(path, imageView, null, -1, kind,
					listener));
			this.workItems.notifyAll();
		}
	}

	/**
	 * Pauses the thread that loads and displays images.
	 */
	public void pause() {
		synchronized (this.workItems) {
			this.paused = true;

			this.workItems.notifyAll();
		}
	}

	/**
	 * Resumes any paused thread that loads and displays images.
	 */
	public void resume() {
		synchronized (this.workItems) {
			this.paused = false;

			this.workItems.notifyAll();
		}
	}

	/**
	 * Cleans up any resources used by the loader.
	 * <p>
	 * Further use of the loader beyond this point may cause unexpected errors.
	 * </p>
	 */
	public void close() {
		if (!this.closed) {
			synchronized (this.workItems) {
				this.closed = true;

				this.workItems.notifyAll();
			}

			this.workItems.clear();

			try {
				this.thread.join();
			} catch (final InterruptedException e) {

			}
		}
	}

	private void cancel(final ImageView imageView) {
		synchronized (this.workItems) {
			WorkItem workItemToRemove = null;

			for (final WorkItem workItem : this.workItems) {
				if (workItem.imageView == imageView) {
					workItemToRemove = workItem;

					break;
				}
			}

			if (workItemToRemove != null) {
				this.workItems.remove(workItemToRemove);
			}
		}
	}

	private void checkState() {
		if (this.closed) {
			throw new IllegalStateException(
					"AsyncImageLoaded has already been closed"); //$NON-NLS-1$
		}
	}

	/**
	 * 异步图片加载工作线程
	 */
	private final class WorkerThread implements Runnable {
		public WorkerThread() {

		}

		@Override
		public void run() {
			while (true) {
				WorkItem workItem = null;

				synchronized (AsyncImageLoader.this.workItems) {
					if (AsyncImageLoader.this.closed) {
						break;
					}

					if (AsyncImageLoader.this.paused) {
						try {
							AsyncImageLoader.this.workItems.wait();
						} catch (final InterruptedException e) {

						}
					}

					if (AsyncImageLoader.this.workItems.isEmpty()) {
						try {
							AsyncImageLoader.this.workItems.wait();
						} catch (final InterruptedException e) {

						}

						continue;
					}

					workItem = AsyncImageLoader.this.workItems.removeFirst();
				}

				if (workItem != null) {
					Bitmap bitmap = null;

					final SoftReference<Bitmap> reference = AsyncImageLoader.bitmapCache
							.get(workItem.path == null ? workItem.url
									.toString() : workItem.path);

					if (reference != null) {
						bitmap = reference.get();
					}

					if (bitmap == null) {
						try {
							if (workItem.path == null) {
								InputStream instream = (InputStream) httpService
										.getSync(workItem.url.toString());

								final byte[] data = FileUtil
										.stream2byte(instream);

								bitmap = BitmapFactory.decodeByteArray(data, 0,
										data.length);
							} else {
								if (workItem.imageKind > 0) {
									bitmap = AsyncImageLoader.this.diskCache
											.get(workItem.path,
													workItem.imageKind);
								} else if (workItem.videoKind > 0) {
									bitmap = ThumbnailUtils
											.createVideoThumbnail(
													workItem.path,
													workItem.videoKind);
								} else {
									bitmap = AsyncImageLoader.this.diskCache
											.get(workItem.path);

									if (bitmap == null) {
										bitmap = BitmapFactory
												.decodeFile(workItem.path,
														workItem.options);
									}
								}
							}

							if (bitmap != null) {
								AsyncImageLoader.bitmapCache.put(
										workItem.path == null ? workItem.url
												.toString() : workItem.path,
										new SoftReference<Bitmap>(bitmap));

								if (workItem.path != null
										&& AsyncImageLoader.this.diskCache
												.has(workItem.path)) {
									AsyncImageLoader.this.diskCache.put(
											workItem.path, bitmap);
								}

								this.onImageLoaded(workItem, bitmap);
							}
						} catch (final Exception e) {
							if (workItem.listener != null) {
								workItem.listener
										.onError(workItem.imageView, e);
							}
						}
					} else {
						this.onImageLoaded(workItem, bitmap);
					}
				}
			}
		}

		private void onImageLoaded(final WorkItem workItem, final Bitmap bitmap) {
			if (workItem.listener != null) {
				final Bitmap finalBitmap = workItem.listener.onImageLoaded(
						workItem.imageView, bitmap);

				if (workItem.imageView != null) {
					AsyncImageLoader.this.activity
							.runOnUiThread(new UpdateImageView(
									workItem.imageView, finalBitmap));
				}
			} else {
				if (workItem.imageView != null) {
					AsyncImageLoader.this.activity
							.runOnUiThread(new UpdateImageView(
									workItem.imageView, bitmap));
				}
			}
		}
	}
}
