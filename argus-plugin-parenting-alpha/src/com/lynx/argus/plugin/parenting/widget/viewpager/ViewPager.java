package com.lynx.argus.plugin.parenting.widget.viewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.*;
import android.widget.Scroller;

/**
 * @author zhufeng.liu
 * @version 14-2-13 下午2:20
 */
public class ViewPager extends ViewGroup {

	private Scroller scroller;
	private VelocityTracker velocityTracker;

	private int curScreen;
	private int defScreen;

	private static final int TOUCH_STATE_REST = 0;
	private static final int TOUCH_STATE_SCROLLING = 1;
	private static final int SNAP_VELOCITY = 600;

	private int touchState = TOUCH_STATE_REST;
	private int touchSlop;
	private float lstMotionX, lstMotionY;

	private int curScreenIdx = 0;

	private OnScreenChangeListener onScreenChangeListener;

	public ViewPager(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ViewPager(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		scroller = new Scroller(context);
		curScreen = defScreen;
		touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int childLeft = 0;
		final int childCount = getChildCount();
		for (int i = 0; i < childCount; ++i) {
			final View childView = getChildAt(i);
			if (childView.getVisibility() != View.GONE) {
				final int childWidth = childView.getMeasuredWidth();
				childView.layout(childLeft, 0, childLeft + childWidth,
						childView.getMeasuredHeight());
				childLeft += childWidth;
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException("viewpager can only run at EXACTLY mode");
		}

		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException("viewpager can only run at EXACTLY mode");
		}

		int count = getChildCount();
		for (int i = 0; i < count; ++i) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		scrollTo(curScreen * width, 0);
	}

	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			scrollTo(scroller.getCurrX(), scroller.getCurrY());
			postInvalidate();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (velocityTracker == null) {
			velocityTracker = VelocityTracker.obtain();
		}

		velocityTracker.addMovement(event);
		int action = event.getAction();
		float x = event.getX();
		float y = event.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!scroller.isFinished()) {
				scroller.abortAnimation();
			}
			lstMotionX = x;
			break;
		case MotionEvent.ACTION_MOVE:
			int deltaX = (int) (lstMotionX - x);
			lstMotionX = x;

			scrollBy(deltaX, 0);
			break;
		case MotionEvent.ACTION_UP:
			velocityTracker.computeCurrentVelocity(1000);
			int velocityX = (int) velocityTracker.getXVelocity();
			if (velocityX > SNAP_VELOCITY && curScreen > 0) {
				onScreenChangeListener.onScreenChange(curScreen - 1);
				snapToScreen(curScreen - 1);
			} else if (velocityX < -SNAP_VELOCITY && curScreen < getChildCount() - 1) {
				onScreenChangeListener.onScreenChange(curScreen + 1);
				snapToScreen(curScreen + 1);
			} else {
				snapToDest();
			}

			if (velocityTracker != null) {
				velocityTracker.recycle();
				velocityTracker = null;
			}

			touchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			touchState = TOUCH_STATE_REST;
			break;
		}

		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		int action = event.getAction();
		if ((action == MotionEvent.ACTION_MOVE) && (touchState != TOUCH_STATE_REST)) {
			return true;
		}

		float x = event.getX();
		float y = event.getY();
		switch (action) {
		case MotionEvent.ACTION_MOVE:
			int diffX = (int) Math.abs(lstMotionX - x);
			if (diffX > touchSlop) {
				touchState = TOUCH_STATE_SCROLLING;
			}
			break;
		case MotionEvent.ACTION_DOWN:
			lstMotionX = x;
			lstMotionY = y;
			touchState = scroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			touchState = TOUCH_STATE_REST;
			break;
		}
		return touchState != TOUCH_STATE_REST;
	}

	public void snapToDest() {
		final int screenWidth = getWidth();
		final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
		snapToScreen(destScreen);
	}

	private void snapToScreen(int whichScreen) {
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		if (getScrollX() != (whichScreen * getWidth())) {
			int delta = whichScreen * getWidth() - getScrollX();
			scroller.startScroll(getScrollX(), delta, 0, Math.abs(delta) * 2);
			curScreen = whichScreen;
			invalidate();
		}
	}

	public void setToScreen(int whichScreen) {
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		curScreen = whichScreen;
		scrollTo(whichScreen * getWidth(), 0);
	}

	public int getCurScreenIdx() {
		return curScreenIdx;
	}

	public void setCurScreenIdx(int curScreenIdx) {
		this.curScreenIdx = curScreenIdx;
	}

	// 分页监听
	public interface OnScreenChangeListener {
		void onScreenChange(int currentIndex);
	}

	public void setOnScreenChangeListener(OnScreenChangeListener onScreenChangeListener) {
		this.onScreenChangeListener = onScreenChangeListener;
	}
}
