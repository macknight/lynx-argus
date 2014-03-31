package com.lynx.lib.widget.pulltorefresh;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @author zhufeng.liu
 * @version 13-8-30 下午1:33
 */
public class LoadingLayout extends RelativeLayout {

	static final int DEFAULT_ROTATION_ANIMATION_DURATION = 600;

	private final ImageView mHeaderImage;
	private final Matrix mHeaderImageMatrix;

	private final TextView mHeaderText;
	private final TextView mSubHeaderText;

	private String mPullLabel;
	private String mRefreshingLabel;
	private String mReleaseLabel;

	private float mRotationPivotX, mRotationPivotY;

	private final Animation mRotateAnimation;

	public LoadingLayout(Context context, final PullToRefreshBase.Mode mode) {
		super(context);

		setPadding(0, 25, 0, 25);

		mHeaderText = new TextView(context);
		mHeaderText.setGravity(Gravity.CENTER);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		mHeaderText.setLayoutParams(params);
		mHeaderText.setTextColor(0xFF000000);
		mHeaderText.setTextSize(16);

		addView(mHeaderText);

		mSubHeaderText = new TextView(context);
		params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		mSubHeaderText.setLayoutParams(params);
		mSubHeaderText.setTextColor(0xFF000000);
		mSubHeaderText.setTextSize(16);
		addView(mSubHeaderText);

		mHeaderImage = new ImageView(context);
		params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		params.setMargins(70, 0, 0, 0);
		mHeaderImage.setLayoutParams(params);
		addView(mHeaderImage);

		mHeaderImage.setScaleType(ScaleType.MATRIX);
		mHeaderImageMatrix = new Matrix();
		mHeaderImage.setImageMatrix(mHeaderImageMatrix);

		final Interpolator interpolator = new LinearInterpolator();
		mRotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		mRotateAnimation.setInterpolator(interpolator);
		mRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
		mRotateAnimation.setRepeatCount(Animation.INFINITE);
		mRotateAnimation.setRepeatMode(Animation.RESTART);

		switch (mode) {
		case PULL_UP_TO_REFRESH:
			// Load in labels
			mPullLabel = "上滑并释放以刷新…";
			mRefreshingLabel = "载入中…";
			mReleaseLabel = "释放以刷新…";
			break;

		case PULL_DOWN_TO_REFRESH:
		default:
			// Load in labels
			mPullLabel = "下拉并释放以刷新…";
			mRefreshingLabel = "载入中…";
			mReleaseLabel = "释放以刷新…";
			break;
		}

		reset();
	}

	public void reset() {
		mHeaderText.setText(Html.fromHtml(mPullLabel));
		mHeaderImage.setVisibility(View.VISIBLE);
		mHeaderImage.clearAnimation();

		resetImageRotation();

		if (TextUtils.isEmpty(mSubHeaderText.getText())) {
			mSubHeaderText.setVisibility(View.GONE);
		} else {
			mSubHeaderText.setVisibility(View.VISIBLE);
		}
	}

	public void releaseToRefresh() {
		mHeaderText.setText(Html.fromHtml(mReleaseLabel));
	}

	public void setPullLabel(String pullLabel) {
		mPullLabel = pullLabel;
	}

	public void refreshing() {
		mHeaderText.setText(Html.fromHtml(mRefreshingLabel));
		mHeaderImage.startAnimation(mRotateAnimation);

		mSubHeaderText.setVisibility(View.GONE);
	}

	public void setRefreshingLabel(String refreshingLabel) {
		mRefreshingLabel = refreshingLabel;
	}

	public void setReleaseLabel(String releaseLabel) {
		mReleaseLabel = releaseLabel;
	}

	public void pullToRefresh() {
		mHeaderText.setText(Html.fromHtml(mPullLabel));
	}

	public void setTextColor(ColorStateList color) {
		mHeaderText.setTextColor(color);
		mSubHeaderText.setTextColor(color);
	}

	public void setSubTextColor(ColorStateList color) {
		mSubHeaderText.setTextColor(color);
	}

	public void setTextColor(int color) {
		setTextColor(ColorStateList.valueOf(color));
	}

	public void setLoadingDrawable(Drawable imageDrawable) {
		// Set Drawable, and save width/height
		mHeaderImage.setImageDrawable(imageDrawable);
		mRotationPivotX = imageDrawable.getIntrinsicWidth() / 2f;
		mRotationPivotY = imageDrawable.getIntrinsicHeight() / 2f;
	}

	public void setSubTextColor(int color) {
		setSubTextColor(ColorStateList.valueOf(color));
	}

	public void setSubHeaderText(CharSequence label) {
		if (TextUtils.isEmpty(label)) {
			mSubHeaderText.setVisibility(View.GONE);
		} else {
			mSubHeaderText.setText(label);
			mSubHeaderText.setVisibility(View.VISIBLE);
		}
	}

	public void onPullY(float scaleOfHeight) {
		mHeaderImageMatrix.setRotate(scaleOfHeight * 90, mRotationPivotX, mRotationPivotY);
		mHeaderImage.setImageMatrix(mHeaderImageMatrix);
	}

	private void resetImageRotation() {
		mHeaderImageMatrix.reset();
		mHeaderImage.setImageMatrix(mHeaderImageMatrix);
	}
}
