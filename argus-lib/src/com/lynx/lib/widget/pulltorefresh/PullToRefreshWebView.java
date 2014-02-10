package com.lynx.lib.widget.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-8-30 下午1:33
 */
public class PullToRefreshWebView extends PullToRefreshBase<WebView> {

	private final OnRefreshListener defaultOnRefreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			mRefreshableView.reload();
		}
	};

	private final WebChromeClient defaultWebChromeClient = new WebChromeClient() {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress == 100) {
				onRefreshComplete();
			}
		}
	};

	public PullToRefreshWebView(Context context) {
		super(context);

		/**
		 * Added so that by default, Pull-to-Refresh refreshes the page
		 */
		setOnRefreshListener(defaultOnRefreshListener);
		mRefreshableView.setWebChromeClient(defaultWebChromeClient);
	}

	public PullToRefreshWebView(Context context, AttributeSet attrs) {
		super(context, attrs);

		/**
		 * Added so that by default, Pull-to-Refresh refreshes the page
		 */
		setOnRefreshListener(defaultOnRefreshListener);
		mRefreshableView.setWebChromeClient(defaultWebChromeClient);
	}

	public PullToRefreshWebView(Context context, Mode mode) {
		super(context, mode);

		/**
		 * Added so that by default, Pull-to-Refresh refreshes the page
		 */
		setOnRefreshListener(defaultOnRefreshListener);
		mRefreshableView.setWebChromeClient(defaultWebChromeClient);
	}

	@Override
	protected WebView createRefreshableView(Context context, AttributeSet attrs) {
		WebView webView = new WebView(context, attrs);
		return webView;
	}

	@Override
	protected boolean isReadyForPullDown() {
		return mRefreshableView.getScrollY() == 0;
	}

	@Override
	protected boolean isReadyForPullUp() {
		float exactContentHeight = FloatMath.floor(mRefreshableView.getContentHeight()
				* mRefreshableView.getScale());
		return mRefreshableView.getScrollY() >= (exactContentHeight - mRefreshableView.getHeight());
	}
}
