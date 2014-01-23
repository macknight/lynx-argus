package com.lynx.lib.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author chris.liu
 * @version 14-1-23 下午2:28
 */
public class DiskCache {
	private static final int THUMBNAIL_QUALITY = 80;

	private static Method createImageThumbnail;

	private final String path;

	public DiskCache(final String path) {
		this.path = path;
	}

	public boolean has(final String path) {
		final File sourceFile = new File(path);

		if (!sourceFile.exists()) {
			return false;
		}

		return new File(this.path, sourceFile.getName()).exists();
	}

	public Bitmap get(final String path) {
		final File sourceFile = new File(path);

		if (!sourceFile.exists()) {
			return null;
		}

		final File thumbnailFile = new File(this.path, sourceFile.getName());

		if (thumbnailFile.exists()) {
			return BitmapFactory.decodeFile(thumbnailFile.getAbsolutePath());
		}

		return null;
	}

	public void put(final String path, final Bitmap bitmap) {
		OutputStream outputStream = null;

		try {
			outputStream = new BufferedOutputStream(new FileOutputStream(path));

			bitmap.compress(Bitmap.CompressFormat.JPEG,
					DiskCache.THUMBNAIL_QUALITY, outputStream);
		} catch (final FileNotFoundException e) {
			Log.e(this.getClass().getName(), e.getMessage(), e);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (final IOException e) {
					Log.e(this.getClass().getName(), e.getMessage(), e);
				}
			}
		}
	}

	public Bitmap get(final String path, final int imageKind) {
		final File sourceFile = new File(path);

		if (!sourceFile.exists()) {
			return null;
		}

		final File thumbnailFile = new File(this.path, sourceFile.getName());

		if (thumbnailFile.exists()) {
			return BitmapFactory.decodeFile(thumbnailFile.getAbsolutePath());
		}

		try {
			final Bitmap bitmap = DiskCache.createImageThumbnail(path,
					imageKind);

			this.put(path, bitmap);

			return bitmap;
		} catch (final IllegalArgumentException e) {
			Log.e(this.getClass().getName(), e.getMessage(), e);
		} catch (final NoSuchMethodException e) {
			Log.e(this.getClass().getName(), e.getMessage(), e);
		} catch (final IllegalAccessException e) {
			Log.e(this.getClass().getName(), e.getMessage(), e);
		} catch (final InvocationTargetException e) {
			Log.e(this.getClass().getName(), e.getMessage(), e);
		}

		return null;
	}

	private static Bitmap createImageThumbnail(final String path,
			final int imageKind) throws NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		if (DiskCache.createImageThumbnail == null) {
			DiskCache.createImageThumbnail = ThumbnailUtils.class.getMethod(
					"createImageThumbnail", String.class, int.class); //$NON-NLS-1$
		}

		return (Bitmap) DiskCache.createImageThumbnail.invoke(null, path,
				Integer.valueOf(imageKind));
	}
}
