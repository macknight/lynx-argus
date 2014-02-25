package com.lynx.lib.widget;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.lynx.lib.http.core.AsyncTask;
import com.lynx.lib.util.StringUtil;

/**
 * @author zhufeng.liu
 * @version 14-2-22 下午4:54
 */
public class AsyncImageView extends ImageView {
	private static final int INVALID_POINTER_ID = -1;

	private String uri = null;
	private Animation animation;
	private File cache;

	private float posX;
	private float posY;
	private float lstTouchX;
	private float lstTouchY;
	private float lstGestureX;
	private float lstGestureY;
	private int activePointerId = INVALID_POINTER_ID;
	private ScaleGestureDetector scaleDetector;
	private float scaleFactor = 1.f;
	private FetchNetworkImageTask task;

	public AsyncImageView(Context context) {
		this(context, null, 0);
	}

	public AsyncImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AsyncImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.cache = context.getExternalCacheDir();
		scaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		scaleDetector.onTouchEvent(ev);

		final int action = ev.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			if (!scaleDetector.isInProgress()) {
				final float x = ev.getX();
				final float y = ev.getY();

				lstTouchX = x;
				lstTouchY = y;
				activePointerId = ev.getPointerId(0);
			}
			break;
		}
		case MotionEvent.ACTION_POINTER_INDEX_MASK: {
			if (scaleDetector.isInProgress()) {
				final float gx = scaleDetector.getFocusX();
				final float gy = scaleDetector.getFocusY();
				lstGestureX = gx;
				lstGestureY = gy;
			}
			break;
		}
		case MotionEvent.ACTION_MOVE: {

			// Only move if the ScaleGestureDetector isn't processing animation gesture.
			if (!scaleDetector.isInProgress()) {
				final int pointerIndex = ev.findPointerIndex(activePointerId);
				final float x = ev.getX(pointerIndex);
				final float y = ev.getY(pointerIndex);

				final float dx = x - lstTouchX;
				final float dy = y - lstTouchY;

				posX += dx;
				posY += dy;

				invalidate();

				lstTouchX = x;
				lstTouchY = y;
			} else {
				final float gx = scaleDetector.getFocusX();
				final float gy = scaleDetector.getFocusY();

				final float gdx = gx - lstGestureX;
				final float gdy = gy - lstGestureY;

				posX += gdx;
				posY += gdy;

				invalidate();

				lstGestureX = gx;
				lstGestureY = gy;
			}
			break;
		}
		case MotionEvent.ACTION_UP: {
			activePointerId = INVALID_POINTER_ID;
			break;
		}
		case MotionEvent.ACTION_CANCEL: {
			activePointerId = INVALID_POINTER_ID;
			break;
		}
		case MotionEvent.ACTION_POINTER_UP: {
			final int pointerIdx = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			final int pointerId = ev.getPointerId(pointerIdx);
			if (pointerId == activePointerId) {
				// This was our active pointer going up. Choose animation new active pointer and adjust accordingly.
				final int newPointerIndex = pointerIdx == 0 ? 1 : 0;
				lstTouchX = ev.getX(newPointerIndex);
				lstTouchY = ev.getY(newPointerIndex);
				activePointerId = ev.getPointerId(newPointerIndex);
			} else {
				final int tempPointerIndex = ev.findPointerIndex(activePointerId);
				lstTouchX = ev.getX(tempPointerIndex);
				lstTouchY = ev.getY(tempPointerIndex);
			}

			break;
		}
		}

		return true;
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.save();

		if (posX <= -this.getWidth() / 2.0f / scaleFactor)
			posX = -this.getWidth() / 2.0f / scaleFactor;
		if (posX >= this.getWidth() / 2.0f / scaleFactor)
			posX = this.getWidth() / 2.0f / scaleFactor;

		if (posY <= -this.getHeight() / 2.0f / scaleFactor)
			posY = -this.getHeight() / 2.0f / scaleFactor;
		if (posY >= this.getHeight() / 2.0f / scaleFactor)
			posY = this.getHeight() / 2.0f / scaleFactor;

		canvas.translate(posX, posY);

		if (scaleDetector.isInProgress()) {
			canvas.scale(scaleFactor, scaleFactor, scaleDetector.getFocusX(),
					scaleDetector.getFocusY());
		} else {
			canvas.scale(scaleFactor, scaleFactor, lstGestureX, lstGestureY);
		}
		super.onDraw(canvas);
		canvas.restore();
	}

	public String getSrcURI() {
		return uri;
	}

	public void setSrcURI(String uri) {
		this.uri = uri;
		animation = new RotateAnimation(0, 360);
		super.startAnimation(animation);
		String cached_file = StringUtil.MD5Encode(uri);
		File archivo = new File(cache.getAbsolutePath() + "/" + cached_file + ".png");
		if (archivo.exists()) {
			animation.cancel();
			animation.reset();
			this.clearAnimation();
			Bitmap myBitmap = BitmapFactory.decodeFile(archivo.getAbsolutePath());
			this.setImageBitmap(myBitmap);
			this.setScaleType(ScaleType.CENTER);
		} else {
			this.setScaleType(ScaleType.CENTER_INSIDE);
			download(this.uri);
		}
	}

	public String getCacheFileName() {
		return cache.getAbsolutePath() + "/" + StringUtil.MD5Encode(getSrcURI()) + ".png";
	}

	public void download(String url) {
		if (task != null) {
			task.cancel(true);
			task = null;
		}
		task = new FetchNetworkImageTask(this);
		task.execute(url);
	}

	private void displayFile() {
		setSrcURI(getSrcURI());
	}

	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			scaleFactor *= detector.getScaleFactor();

			// Don't let the object get too small or too large.
			scaleFactor = Math.max(0.8f, Math.min(scaleFactor, 2.0f));

			invalidate();
			return true;
		}
	}

	private class FetchNetworkImageTask extends AsyncTask<String, Void, Bitmap> {

		private AsyncImageView aiv;

		public FetchNetworkImageTask(AsyncImageView aiv) {
			this.aiv = aiv;
		}

		@Override
		protected void onPostExecute(Bitmap drawable) {
			animation.cancel();
			animation.reset();
			aiv.clearAnimation();
			if (drawable != null) {
				// aiv.setImageBitmap(drawable);
				aiv.displayFile();
			}

		}

		@Override
		protected Bitmap doInBackground(String... urls) {
			URL url;
			InputStream in;
			BufferedInputStream buf;

			try {
				url = new URL(urls[0]);
				in = url.openStream();
				buf = new BufferedInputStream(in);
				Bitmap bMap = BitmapFactory.decodeStream(buf);
				if (in != null) {
					in.close();
				}
				if (buf != null) {
					buf.close();
				}
				if (isCancelled())
					return null;

				try {
					FileOutputStream out = new FileOutputStream(cache.getAbsolutePath() + "/"
							+ StringUtil.MD5Encode(urls[0]) + ".png");
					bMap.compress(Bitmap.CompressFormat.PNG, 100, out);
				} catch (Exception e) {
					e.printStackTrace();
				}

				return bMap;
			} catch (Exception e) {
				// Log.e("Error reading file", e.toString());
			}
			return null;
		}
	}

}
