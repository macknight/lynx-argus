package com.lynx.argus.biz.widget.listview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import com.lynx.lib.widget.pulltorefresh.EmptyViewMethodAccessor;
import com.lynx.lib.widget.pulltorefresh.LoadingLayout;
import com.lynx.lib.widget.pulltorefresh.PullToRefreshAdapterViewBase;

/**
 * 可下拉刷新且有可展开的slide action的listview
 * <p/>
 * 
 * @author chris.liu
 * @version 13-11-19 下午3:55
 */
public class P2RASEListView extends PullToRefreshAdapterViewBase<ActionSlideExpandableListView> {

	private LoadingLayout mHeaderLoadingView;
	private LoadingLayout mFooterLoadingView;

	private FrameLayout mLvFooterLoadingFrame;

	public P2RASEListView(Context context) {
		super(context);
		setDisableScrollingWhileRefreshing(false);
	}

	public P2RASEListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDisableScrollingWhileRefreshing(false);
	}

	public P2RASEListView(Context context, Mode mode) {
		super(context, mode);
		setDisableScrollingWhileRefreshing(false);
	}

	@Override
	public ContextMenu.ContextMenuInfo getContextMenuInfo() {
		return null;
	}

	public void setPullLabel(String pullLabel, Mode mode) {
		super.setPullLabel(pullLabel, mode);

		if (null != mHeaderLoadingView && mode.canPullDown()) {
			mHeaderLoadingView.setPullLabel(pullLabel);
		}
		if (null != mFooterLoadingView && mode.canPullUp()) {
			mFooterLoadingView.setPullLabel(pullLabel);
		}
	}

	public void setRefreshingLabel(String refreshingLabel, Mode mode) {
		super.setRefreshingLabel(refreshingLabel, mode);

		if (null != mHeaderLoadingView && mode.canPullDown()) {
			mHeaderLoadingView.setRefreshingLabel(refreshingLabel);
		}
		if (null != mFooterLoadingView && mode.canPullUp()) {
			mFooterLoadingView.setRefreshingLabel(refreshingLabel);
		}
	}

	public void setReleaseLabel(String releaseLabel, Mode mode) {
		super.setReleaseLabel(releaseLabel, mode);

		if (null != mHeaderLoadingView && mode.canPullDown()) {
			mHeaderLoadingView.setReleaseLabel(releaseLabel);
		}
		if (null != mFooterLoadingView && mode.canPullUp()) {
			mFooterLoadingView.setReleaseLabel(releaseLabel);
		}
	}

	@Override
	protected final ActionSlideExpandableListView createRefreshableView(Context context,
			AttributeSet attrs) {
		ActionSlideExpandableListView lv = new InternalListView(context, attrs);
		// Create Loading Views ready for use later
		FrameLayout frame = new FrameLayout(context);
		mHeaderLoadingView = new LoadingLayout(context, Mode.PULL_DOWN_TO_REFRESH);
		frame.addView(mHeaderLoadingView, FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		mHeaderLoadingView.setVisibility(View.GONE);
		lv.addHeaderView(frame, null, false);

		mLvFooterLoadingFrame = new FrameLayout(context);
		mFooterLoadingView = new LoadingLayout(context, Mode.PULL_UP_TO_REFRESH);
		mLvFooterLoadingFrame.addView(mFooterLoadingView, FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		mFooterLoadingView.setVisibility(View.GONE);

		// Set it to this so it can be used in ListActivity/ListFragment
		lv.setId(android.R.id.list);
		return lv;
	}

	protected int getNumberInternalFooterViews() {
		return null != mFooterLoadingView ? 1 : 0;
	}

	protected int getNumberInternalHeaderViews() {
		return null != mHeaderLoadingView ? 1 : 0;
	}

	@Override
	protected void resetHeader() {

		// If we're not showing the Refreshing view, or the list is empty, then
		// the header/footer views won't show so we use the
		// normal method
		ListAdapter adapter = getRefreshableView().getAdapter();
		if (!getShowViewWhileRefreshing() || null == adapter || adapter.isEmpty()) {
			super.resetHeader();
			return;
		}

		LoadingLayout originalLoadingLayout;
		LoadingLayout listViewLoadingLayout;

		int scrollToHeight = getHeaderHeight();
		int selection;
		boolean scroll;

		switch (getCurrentMode()) {
		case PULL_UP_TO_REFRESH:
			originalLoadingLayout = getFooterLayout();
			listViewLoadingLayout = mFooterLoadingView;
			selection = getRefreshableView().getCount() - 1;
			scroll = getRefreshableView().getLastVisiblePosition() == selection;
			break;
		case PULL_DOWN_TO_REFRESH:
		default:
			originalLoadingLayout = getHeaderLayout();
			listViewLoadingLayout = mHeaderLoadingView;
			scrollToHeight *= -1;
			selection = 0;
			scroll = getRefreshableView().getFirstVisiblePosition() == selection;
			break;
		}

		// Set our Original View to Visible
		originalLoadingLayout.setVisibility(View.VISIBLE);

		/**
		 * Scroll so the View is at the same Y as the ListView header/footer,
		 * but only scroll if we've pulled to refresh and it's positioned
		 * correctly
		 */
		if (scroll && getState() != MANUAL_REFRESHING) {
			getRefreshableView().setSelection(selection);
			setHeaderScroll(scrollToHeight);
		}

		// Hide the ListView Header/Footer
		listViewLoadingLayout.setVisibility(View.GONE);

		super.resetHeader();
	}

	@Override
	public void setLoadingDrawable(Drawable drawable) {
		super.setLoadingDrawable(drawable);
		mHeaderLoadingView.setLoadingDrawable(drawable);
		mFooterLoadingView.setLoadingDrawable(drawable);
	}

	@Override
	public void setLoadingDrawable(Drawable drawable, Mode mode) {
		super.setLoadingDrawable(drawable, mode);
		mHeaderLoadingView.setLoadingDrawable(drawable);
		mFooterLoadingView.setLoadingDrawable(drawable);
	}

	@Override
	protected void setRefreshingInternal(boolean doScroll) {

		// If we're not showing the Refreshing view, or the list is empty, then
		// the header/footer views won't show so we use the
		// normal method
		ListAdapter adapter = getRefreshableView().getAdapter();
		if (!getShowViewWhileRefreshing() || null == adapter || adapter.isEmpty()) {
			super.setRefreshingInternal(doScroll);
			return;
		}

		super.setRefreshingInternal(false);

		final LoadingLayout originalLoadingLayout, listViewLoadingLayout;
		final int selection, scrollToY;

		switch (getCurrentMode()) {
		case PULL_UP_TO_REFRESH:
			originalLoadingLayout = getFooterLayout();
			listViewLoadingLayout = mFooterLoadingView;
			selection = getRefreshableView().getCount() - 1;
			scrollToY = getScrollY() - getHeaderHeight();
			break;
		case PULL_DOWN_TO_REFRESH:
		default:
			originalLoadingLayout = getHeaderLayout();
			listViewLoadingLayout = mHeaderLoadingView;
			selection = 0;
			scrollToY = getScrollY() + getHeaderHeight();
			break;
		}

		if (doScroll) {
			// We scroll slightly so that the ListView's header/footer is at the
			// same Y position as our normal header/footer
			setHeaderScroll(scrollToY);
		}

		// Hide our original Loading View
		originalLoadingLayout.setVisibility(View.INVISIBLE);

		// Show the ListView Loading View and set it to refresh
		listViewLoadingLayout.setVisibility(View.VISIBLE);
		listViewLoadingLayout.refreshing();

		if (doScroll) {
			// Make sure the ListView is scrolled to show the loading
			// header/footer
			getRefreshableView().setSelection(selection);

			// Smooth scroll as normal
			smoothScrollTo(0);
		}
	}

	class InternalListView extends ActionSlideExpandableListView implements EmptyViewMethodAccessor {

		private boolean mAddedLvFooter = false;

		public InternalListView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		public void draw(Canvas canvas) {
			/**
			 * This is a bit hacky, but ListView has got a bug in it when using
			 * Header/Footer Views and the list is empty. This masks the issue
			 * so that it doesn't cause an FC. See Issue #66.
			 */
			try {
				super.draw(canvas);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public ContextMenu.ContextMenuInfo getContextMenuInfo() {
			return super.getContextMenuInfo();
		}

		@Override
		public void setAdapter(ListAdapter adapter) {
			// Add the Footer View at the last possible moment
			if (!mAddedLvFooter) {
				addFooterView(mLvFooterLoadingFrame, null, false);
				mAddedLvFooter = true;
			}

			super.setAdapter(adapter);
		}

		@Override
		public void setEmptyView(View emptyView) {
			this.setEmptyView(emptyView);
		}

		@Override
		public void setEmptyViewInternal(View emptyView) {
			super.setEmptyView(emptyView);
		}
	}
}