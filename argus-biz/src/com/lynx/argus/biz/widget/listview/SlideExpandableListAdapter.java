package com.lynx.argus.biz.widget.listview;

import android.view.View;
import android.widget.ListAdapter;
import com.lynx.argus.R;

/**
 * ListAdapter that adds sliding functionality to a list. Uses
 * R.id.expandalbe_toggle_button and R.id.expandable id's if no ids are given in
 * the contructor.
 * 
 * @author tjerk
 * @date 6/13/12 8:04 AM
 */
public class SlideExpandableListAdapter extends
		AbstractSlideExpandableListAdapter {
	private int toggleViewId;
	private int expandableViewId;

	public SlideExpandableListAdapter(ListAdapter wrapped, int toggleViewId,
			int expandableViewId) {
		super(wrapped);
		this.toggleViewId = toggleViewId;
		this.expandableViewId = expandableViewId;
	}

	public SlideExpandableListAdapter(ListAdapter wrapped) {
		this(wrapped, R.id.rl_selv_item, R.id.ll_selv_expandable);
	}

	@Override
	public View getExpandToggleView(View parent) {
		return parent.findViewById(toggleViewId);
	}

	@Override
	public View getExpandableView(View parent) {
		return parent.findViewById(expandableViewId);
	}
}
