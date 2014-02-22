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
	private String uri = null;
	private Animation animation;
	private File cache;
	private boolean zoom = false;

	// ///
	private static final int INVALID_POINTER_ID = -1;

	private float mPosX;
	private float mPosY;

	private float mLastTouchX;
	private float mLastTouchY;
	private float mLastGestureX;
	private float mLastGestureY;
	private int mActivePointerId = INVALID_POINTER_ID;

	private ScaleGestureDetector mScaleDetector;
	private float mScaleFactor = 1.f;

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
		mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// Let the ScaleGestureDetector inspect all events.
		mScaleDetector.onTouchEvent(ev);

		final int action = ev.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			if (!mScaleDetector.isInProgress()) {
				final float x = ev.getX();
				final float y = ev.getY();

				mLastTouchX = x;
				mLastTouchY = y;
				mActivePointerId = ev.getPointerId(0);
			}
			break;
		}
		case MotionEvent.ACTION_POINTER_INDEX_MASK: {
			if (mScaleDetector.isInProgress()) {
				final float gx = mScaleDetector.getFocusX();
				final float gy = mScaleDetector.getFocusY();
				mLastGestureX = gx;
				mLastGestureY = gy;
			}
			break;
		}
		case MotionEvent.ACTION_MOVE: {

			// Only move if the ScaleGestureDetector isn't processing animation gesture.
			if (!mScaleDetector.isInProgress()) {
				final int pointerIndex = ev.findPointerIndex(mActivePointerId);
				final float x = ev.getX(pointerIndex);
				final float y = ev.getY(pointerIndex);

				final float dx = x - mLastTouchX;
				final float dy = y - mLastTouchY;

				mPosX += dx;
				mPosY += dy;

				invalidate();

				mLastTouchX = x;
				mLastTouchY = y;
			} else {
				final float gx = mScaleDetector.getFocusX();
				final float gy = mScaleDetector.getFocusY();

				final float gdx = gx - mLastGestureX;
				final float gdy = gy - mLastGestureY;

				mPosX += gdx;
				mPosY += gdy;

				invalidate();

				mLastGestureX = gx;
				mLastGestureY = gy;
			}

			break;
		}
		case MotionEvent.ACTION_UP: {
			mActivePointerId = INVALID_POINTER_ID;
			break;
		}
		case MotionEvent.ACTION_CANCEL: {
			mActivePointerId = INVALID_POINTER_ID;
			break;
		}
		case MotionEvent.ACTION_POINTER_UP: {

			final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			final int pointerId = ev.getPointerId(pointerIndex);
			if (pointerId == mActivePointerId) {
				// This was our active pointer going up. Choose animation new
				// active pointer and adjust accordingly.
				final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
				mLastTouchX = ev.getX(newPointerIndex);
				mLastTouchY = ev.getY(newPointerIndex);
				mActivePointerId = ev.getPointerId(newPointerIndex);
			} else {
				final int tempPointerIndex = ev.findPointerIndex(mActivePointerId);
				mLastTouchX = ev.getX(tempPointerIndex);
				mLastTouchY = ev.getY(tempPointerIndex);
			}

			break;
		}
		}

		return true;
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.save();

		if (mPosX <= -this.getWidth() / 2.0f / mScaleFactor)
			mPosX = -this.getWidth() / 2.0f / mScaleFactor;
		if (mPosX >= this.getWidth() / 2.0f / mScaleFactor)
			mPosX = this.getWidth() / 2.0f / mScaleFactor;

		if (mPosY <= -this.getHeight() / 2.0f / mScaleFactor)
			mPosY = -this.getHeight() / 2.0f / mScaleFactor;
		if (mPosY >= this.getHeight() / 2.0f / mScaleFactor)
			mPosY = this.getHeight() / 2.0f / mScaleFactor;

		canvas.translate(mPosX, mPosY);

		if (mScaleDetector.isInProgress()) {
			canvas.scale(mScaleFactor, mScaleFactor, mScaleDetector.getFocusX(),
					mScaleDetector.getFocusY());
		} else {
			canvas.scale(mScaleFactor, mScaleFactor, mLastGestureX, mLastGestureY);
		}
		super.onDraw(canvas);
		canvas.restore();
	}

	public void setZoom(boolean z) {
		this.zoom = z;
	}

	public String getFile() {
		return uri;
	}

	public void setFile(String s) {
		uri = s;
		animation = new RotateAnimation(0, 360);
		super.startAnimation(animation);
		String cached_file = StringUtil.MD5Encode(s);
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
			download(uri);
		}
	}

	public String getCacheFileName() {
		return cache.getAbsolutePath() + "/" + StringUtil.MD5Encode(getFile()) + ".png";
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
		setFile(getFile());
	}

	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mScaleFactor *= detector.getScaleFactor();

			// Don't let the object get too small or too large.
			mScaleFactor = Math.max(0.8f, Math.min(mScaleFactor, 2.0f));

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
