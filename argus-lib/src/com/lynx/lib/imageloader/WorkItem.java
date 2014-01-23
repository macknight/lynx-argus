package com.lynx.lib.imageloader;

import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.net.URL;

/**
 * @author chris.liu
 * @version 14-1-23 下午2:29
 */
public class WorkItem {
	public final String path;
	public final URL url;
	public final ImageView imageView;
	public final BitmapFactory.Options options;
	public final int imageKind;
	public final int videoKind;
	public final OnImageLoadedListener listener;

	public WorkItem(final String path, final ImageView imageView,
			final BitmapFactory.Options options, final int imageKind,
			final int videoKind, final OnImageLoadedListener listener) {
		this.path = path;
		this.url = null;
		this.imageView = imageView;
		this.options = options;
		this.imageKind = imageKind;
		this.videoKind = videoKind;
		this.listener = listener;
	}

	public WorkItem(final URL url, final ImageView imageView,
			final BitmapFactory.Options options, final int imageKind,
			final int videoKind, final OnImageLoadedListener listener) {
		this.path = null;
		this.url = url;
		this.imageView = imageView;
		this.options = options;
		this.imageKind = imageKind;
		this.videoKind = videoKind;
		this.listener = listener;
	}
}
