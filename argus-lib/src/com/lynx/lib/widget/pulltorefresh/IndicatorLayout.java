package com.lynx.lib.widget.pulltorefresh;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.*;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.lynx.lib.R;

public class IndicatorLayout extends FrameLayout
        implements AnimationListener {

    static final int DEFAULT_ROTATION_ANIMATION_DURATION = 150;

    private Animation mInAnim, mOutAnim;
    private ImageView mArrowImageView;

    private final Animation mRotateAnimation, mResetRotateAnimation;

    public IndicatorLayout(Context context, PullToRefreshBase.Mode mode) {
        super(context);

        mArrowImageView = new ImageView(context);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        lp.topMargin = lp.bottomMargin = lp.leftMargin = lp.rightMargin = getResources().getDimensionPixelSize(
                R.dimen.indicator_internal_padding);
        addView(mArrowImageView, lp);

        int inAnimResId, outAnimResId;
        switch (mode) {
            case PULL_UP_TO_REFRESH:
                inAnimResId = R.anim.slide_in_left;
                outAnimResId = R.anim.slide_out_left;
                setBackgroundResource(R.drawable.indicator_bg_bottom);
                mArrowImageView.setImageResource(R.drawable.arrow_up);
                break;
            default:
            case PULL_DOWN_TO_REFRESH:
                inAnimResId = R.anim.slide_in_right;
                outAnimResId = R.anim.slide_out_right;
                setBackgroundResource(R.drawable.indicator_bg_top);
                mArrowImageView.setImageResource(R.drawable.arrow_down);
                break;
        }

        mInAnim = AnimationUtils.loadAnimation(context, inAnimResId);
        mInAnim.setAnimationListener(this);

        mOutAnim = AnimationUtils.loadAnimation(context, outAnimResId);
        mOutAnim.setAnimationListener(this);

        final Interpolator interpolator = new LinearInterpolator();
        mRotateAnimation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateAnimation.setInterpolator(interpolator);
        mRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
        mRotateAnimation.setFillAfter(true);

        mResetRotateAnimation = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mResetRotateAnimation.setInterpolator(interpolator);
        mResetRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
        mResetRotateAnimation.setFillAfter(true);

    }

    public final boolean isVisible() {
        Animation currentAnim = getAnimation();
        if (null != currentAnim) {
            return mInAnim == currentAnim;
        }

        return getVisibility() == View.VISIBLE;
    }

    public void hide() {
        startAnimation(mOutAnim);
    }

    public void show() {
        startAnimation(mInAnim);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == mOutAnim) {
            mArrowImageView.clearAnimation();
            setVisibility(View.GONE);
        } else if (animation == mInAnim) {
            setVisibility(View.VISIBLE);
        }

        clearAnimation();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // NO-OP
    }

    @Override
    public void onAnimationStart(Animation animation) {
        setVisibility(View.VISIBLE);
    }

    public void releaseToRefresh() {
        mArrowImageView.startAnimation(mRotateAnimation);
    }

    public void pullToRefresh() {
        mArrowImageView.startAnimation(mResetRotateAnimation);
    }

}
