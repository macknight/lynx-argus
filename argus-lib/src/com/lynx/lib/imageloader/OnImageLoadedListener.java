package com.lynx.lib.imageloader;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * A callback when the loader completes a loading task.
 * 
 * @author chris.liu
 * 
 * @version 14-1-23 下午2:28
 */
public interface OnImageLoadedListener {
	/**
	 * Called when a Bitmap loads successfully.
	 * 
	 * @param imageView
	 *            the ImageView to display the loaded Bitmap.
	 * @param bitmap
	 *            the loaded Bitmap.
	 * @return the bitmap to be displayed.
	 *         <p>
	 *         The original Bitmap could be returned, or a modified one.
	 *         </p>
	 */
	Bitmap onImageLoaded(ImageView imageView, Bitmap bitmap);

	/**
	 * Called when there was a error in the loading process.
	 * 
	 * @param imageView
	 *            the ImageView supposed to display any loaded Bitmaps.
	 * @param exception
	 *            the Exception thrown during the loading process.
	 */
	void onError(ImageView imageView, Exception exception);
}
