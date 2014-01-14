package com.lynx.lib.widget.curve;

import android.content.Context;
import android.view.MotionEvent;
import android.view.VelocityTracker;

/**
 * @author: chris.liu
 * @addtime: 14-1-6 下午4:21
 */
public class MotionDetector {
	private static MotionEvent latestEvent;
	private static MotionEvent startPoint;
	private final int maxMovementInClickingPx;
	private final float scale;
	private VelocityTracker velocityTracker;

	public MotionDetector(Context paramContext) {
		this.scale = paramContext.getResources().getDisplayMetrics().density;
		this.maxMovementInClickingPx = (int) (0.5F + 10.0F * this.scale);
	}

	public void addMotionEvent(MotionEvent motionEvent) {
		latestEvent = MotionEvent.obtain(motionEvent);
		this.velocityTracker.addMovement(motionEvent);
	}

	public MotionIntent detect() {
		MotionIntent localMotionIntent = MotionIntent.UNKNOWN;
		if ((startPoint == null) && (latestEvent == null))
			localMotionIntent = MotionIntent.UNKNOWN;
		float f1 = Math.abs(startPoint.getX() - latestEvent.getX());
		float f2 = Math.abs(startPoint.getY() - latestEvent.getY());
		if ((f1 < this.maxMovementInClickingPx)
				&& (f2 < this.maxMovementInClickingPx)) {
			localMotionIntent = MotionIntent.UNKNOWN;
		}
		if (1.5D * f1 > f2) {
			localMotionIntent = MotionIntent.HORIZONTAL_SCROLL;
		}
		localMotionIntent = MotionIntent.VIRTICAL_SCROLL;
		return localMotionIntent;
	}

	public VelocityTracker getVelocityTracker() {
		return this.velocityTracker;
	}

	public void reset() {
		if (this.velocityTracker != null) {
			this.velocityTracker.recycle();
			this.velocityTracker = null;
		}
	}

	public void start(MotionEvent paramMotionEvent) {
		if (this.velocityTracker != null)
			reset();
		startPoint = MotionEvent.obtain(paramMotionEvent);
		this.velocityTracker = VelocityTracker.obtain();
	}

	public static enum MotionIntent {
		UNKNOWN, VIRTICAL_SCROLL, HORIZONTAL_SCROLL
	}
}
