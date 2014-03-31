package com.lynx.argus.biz.widget.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lynx.argus.R;

/**
 * 圆角ListView
 * 
 * @author chris.liu
 * @version 13-11-19 下午3:55
 */
public class CornerListView extends ListView {

	public CornerListView(Context context) {
		this(context, null);
	}

	public CornerListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 整个listview的圆角背景
		this.setBackgroundResource(R.drawable.corner_list_bg);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			int x = (int) ev.getX();
			int y = (int) ev.getY();
			int itemnum = pointToPosition(x, y);

			if (itemnum == AdapterView.INVALID_POSITION) {
				break;
			} else {
				if (itemnum == 0) {
					if (itemnum == (getAdapter().getCount() - 1)) {
						// 只有一项
						setSelector(R.drawable.corner_list_single_item);
					} else {
						// 第一项
						setSelector(R.drawable.corner_list_first_item);
					}
				} else if (itemnum == (getAdapter().getCount() - 1)) {
					// 最后一项
					setSelector(R.drawable.corner_list_last_item);
				} else {
					// 中间项
					setSelector(R.drawable.corner_list_item);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}
}
