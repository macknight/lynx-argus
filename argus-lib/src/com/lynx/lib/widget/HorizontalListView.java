package com.lynx.lib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

/**
 * @author zhufeng.liu
 * @version 14-2-13 下午4:55
 */
public class HorizontalListView extends HorizontalScrollView {
	private ViewGroup mContainer = null;
	private ListAdapter mAdapter = null;
	private OnItemClickListener mListItemClickListener = null;

	private int downX;
	private static final int SCROLL_GAP = 200;

	/**
	 * OnItemClickListener interface Interface definition for a callback to be invoked when a list item is clicked.
	 */
	public interface OnItemClickListener {
		/**
		 * Called when a view has been clicked.
		 * 
		 * @param v
		 *            The view that was clicked.
		 * @param position
		 *            The position if the item that was clicked
		 */
		void onClick(View v, int position);
	}

	/**
	 * Register a listener for list item click.
	 * 
	 * @param listItemClickListener
	 */
	public void setOnItemClickListener(OnItemClickListener listItemClickListener) {
		mListItemClickListener = listItemClickListener;
	}

	/**
	 * Custom OnClickListener for list item
	 */
	public class CustomOnClickListener implements OnClickListener {
		private int mPosition;

		public CustomOnClickListener(int position) {
			mPosition = position;
		}

		@Override
		public void onClick(View v) {
			if (mListItemClickListener != null) {
				mListItemClickListener.onClick(v, mPosition);
			}
		}
	}

	/**
	 * HorizontalListView constructor.
	 * 
	 * @param context
	 * @param attributeSet
	 */
	public HorizontalListView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		// Init child view
		LinearLayout container = new LinearLayout(context);
		container.setOrientation(LinearLayout.HORIZONTAL);
		container.setLayoutParams(new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		mContainer = container;
		addView(mContainer);

		// Remove horizontal scrollbar
		setHorizontalScrollBarEnabled(false);
	}

	/**
	 * Set the adapter which will be used to build the list item views.
	 * 
	 * @param adapter
	 */
	public void setAdapter(ListAdapter adapter) {
		mAdapter = adapter;

		if (getChildCount() == 0 || adapter == null)
			return;

		mContainer.removeAllViews();

		for (int i = 0; i < adapter.getCount(); i++) {
			View v = adapter.getView(i, null, mContainer);
			if (v != null) {
				v.setOnClickListener(new CustomOnClickListener(i));
				mContainer.addView(v);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = (int) event.getRawX();
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			int distanceX = (int) event.getRawX() - downX;// 移动的距离
			if (distanceX > 0) {
				pageScroll(View.FOCUS_LEFT);
			} else {
				pageScroll(View.FOCUS_RIGHT);
			}
			break;
		default:
			break;
		}

		return true;
	}

}
