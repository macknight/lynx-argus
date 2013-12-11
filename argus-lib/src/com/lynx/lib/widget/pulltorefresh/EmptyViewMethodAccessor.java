package com.lynx.lib.widget.pulltorefresh;

import android.view.View;

/**
 * Interface that allows PullToRefreshBase to hijack the call to
 * AdapterView.setEmptyView()
 * 
 * @author zhufeng.liu
 * 
 * @addtime 13-11-15 下午1:38
 */
public interface EmptyViewMethodAccessor {

	/**
	 * Calls upto AdapterView.setEmptyView()
	 * 
	 * @param emptyView
	 *            to set as Empty View
	 */
	public void setEmptyViewInternal(View emptyView);

	/**
	 * Should call PullToRefreshBase.setEmptyView() which will then
	 * automatically call through to setEmptyViewInternal()
	 * 
	 * @param emptyView
	 *            to set as Empty View
	 */
	public void setEmptyView(View emptyView);

}
