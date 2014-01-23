package com.lynx.lib.imageloader;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * @author chris.liu
 * @version 14-1-23 下午2:33
 */
public class UpdateImageView implements Runnable {
	private final ImageView imageView;
	private final Bitmap bitmap;

	public UpdateImageView(final ImageView imageView, final Bitmap bitmap) {
		this.imageView = imageView;
		this.bitmap = bitmap;
	}

	@Override
	public void run() {
		this.imageView.setImageBitmap(this.bitmap);
	}
}
