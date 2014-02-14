package com.lynx.argus.plugin.parenting.widget.viewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.lynx.argus.plugin.parenting.R;

/**
 * @author zhufeng.liu
 * @version 14-2-13 下午2:17
 */
public class PageIndicator extends LinearLayout {

	private int count;

	public PageIndicator(Context context) {
		super(context);
	}

	public PageIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PageIndicator(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void bindScrollViewGroup(ViewPager viewPager) {
		this.count = viewPager.getChildCount();
		System.out.println("count=" + count);
		generatePageIndicator(viewPager.getCurScreenIdx());

		viewPager.setOnScreenChangeListener(new ViewPager.OnScreenChangeListener() {
			@Override
			public void onScreenChange(int currentIndex) {
				generatePageIndicator(currentIndex);
			}
		});
	}

	private void generatePageIndicator(int curIdx) {
		removeAllViews();

		int pageNum = 6;
		int pageNo = curIdx + 1;
		int pageSum = count;

		if (pageSum > 1) {
			int curNum = (pageNo % pageNum == 0 ? (pageNo / pageNum) - 1 : (pageNo / pageNum))
					* pageNum;
			if (curNum < 0) {
				curNum = 0;
			}
			ImageView iv;
			if (pageNo > pageNum) {
				iv = new ImageView(getContext());
				iv.setImageResource(R.drawable.indicator_left);
				addView(iv);
			}

			for (int i = 0; i < pageNum; ++i) {
				if (curNum + i + 1 > pageSum || pageNum < 2) {
					break;
				}

				iv = new ImageView(getContext());
				if (curNum + i + 1 == pageNo) {
					iv.setImageResource(R.drawable.indicator_focused);
				} else {
					iv.setImageResource(R.drawable.indicator_def);
				}
				addView(iv);
			}

			if (pageSum > (curNum + pageNum)) {
				iv = new ImageView(getContext());
				iv.setImageResource(R.drawable.indicator_right);
				addView(iv);
			}
		}
	}
}
