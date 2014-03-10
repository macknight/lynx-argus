package com.lynx.argus.plugin.parenting;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lynx.lib.core.LFFragment;
import com.lynx.lib.widget.photoview.PhotoView;

/**
 * @author zhufeng.liu
 * @version 14-2-24 下午5:07
 */
public class GuideFragment extends LFFragment {

	private ViewPager vpGuide;

	@Override
	protected View onLoadView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) throws Exception {
		View view = inflater.inflate(R.layout.layout_guide, container, false);
		vpGuide = (ViewPager) view.findViewById(R.id.vp_guide);
		vpGuide.setAdapter(new SamplePagerAdapter());

		return view;
	}

	static class SamplePagerAdapter extends PagerAdapter {

		private static int[] sDrawables = { R.drawable.beginner_guide_01,
				R.drawable.beginner_guide_02, R.drawable.beginner_guide_03,
				R.drawable.beginner_guide_04 };

		@Override
		public int getCount() {
			return sDrawables.length;
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			PhotoView photoView = new PhotoView(container.getContext());
			photoView.setImageResource(sDrawables[position]);

			// Now just add PhotoView to ViewPager and return it
			container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT);

			return photoView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	}
}
