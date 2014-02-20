package com.lynx.argus.biz.widget.satellitemenu;

import android.content.Context;
import android.view.animation.*;
import com.lynx.argus.R;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 14-2-20 下午6:40
 */
public class SatelliteAnimationUtil {

	public static Animation createItemInAnimation(Context context, int idx, long expandDuration,
			int x, int y) {
		RotateAnimation animRotate = new RotateAnimation(720, 0, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();
		animRotate.setInterpolator(accelerateInterpolator);
		animRotate.setDuration(expandDuration);

		TranslateAnimation animTrasnlate = new TranslateAnimation(x, 0, y, 0);
		long delay = 250;
		if (expandDuration <= 250) {
			delay = expandDuration / 3;
		}

		long durationTranslate = 400;
		if ((expandDuration - delay) > durationTranslate) {
			durationTranslate = expandDuration - delay;
		}
		animTrasnlate.setDuration(durationTranslate);
		animTrasnlate.setStartOffset(delay);
		AnticipateInterpolator anticipateInterpolator = new AnticipateInterpolator();
		animTrasnlate.setInterpolator(anticipateInterpolator);

		AlphaAnimation animAlpha = new AlphaAnimation(1.0f, 0.0f);
		long durationAlpha = 10;
		if (expandDuration < 10) {
			durationAlpha = expandDuration / 10;
		}
		animAlpha.setDuration(durationAlpha);
		animAlpha.setStartOffset((delay + durationTranslate) - durationAlpha);

		AnimationSet animationSet = new AnimationSet(false);
		animationSet.setFillAfter(false);
		animationSet.setFillBefore(false);
		animationSet.setFillEnabled(true);
		animationSet.addAnimation(animAlpha);
		animationSet.addAnimation(animRotate);
		animationSet.addAnimation(animTrasnlate);

		animationSet.setStartOffset(30 * idx);
		animationSet.start();
		animationSet.startNow();
		return animationSet;
	}

	public static Animation createItemOutAnimation(Context context, int index, long expandDuration,
			int x, int y) {

		AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
		long alphaDuration = 60;
		if (expandDuration < 60) {
			alphaDuration = expandDuration / 4;
		}
		alphaAnimation.setDuration(alphaDuration);
		alphaAnimation.setStartOffset(0);

		TranslateAnimation translate = new TranslateAnimation(0, x, 0, y);

		translate.setStartOffset(0);
		translate.setDuration(expandDuration);
		OvershootInterpolator overshootInterpolator = new OvershootInterpolator();
		translate.setInterpolator(overshootInterpolator);

		RotateAnimation rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
		rotate.setInterpolator(decelerateInterpolator);

		long duration = 100;
		if (expandDuration <= 150) {
			duration = expandDuration / 3;
		}

		rotate.setDuration(expandDuration - duration);
		rotate.setStartOffset(duration);

		AnimationSet animationSet = new AnimationSet(false);
		animationSet.setFillAfter(false);
		animationSet.setFillBefore(true);
		animationSet.setFillEnabled(true);

		// animationSet.addAnimation(alphaAnimation);
		// animationSet.addAnimation(rotate);
		animationSet.addAnimation(translate);

		animationSet.setStartOffset(30 * index);

		return animationSet;
	}

	public static Animation createMainButtonAnimation(Context context) {
		return AnimationUtils.loadAnimation(context, R.anim.sat_main_rotate_left);
	}

	public static Animation createMainButtonInverseAnimation(Context context) {
		return AnimationUtils.loadAnimation(context, R.anim.sat_main_rotate_right);
	}

	public static Animation createItemClickAnimation(Context context) {
		return AnimationUtils.loadAnimation(context, R.anim.sat_item_anim_click);
	}

	public static int getTranslateX(float degree, int distance) {
		return Double.valueOf(distance * Math.cos(Math.toRadians(degree))).intValue();
	}

	public static int getTranslateY(float degree, int distance) {
		return Double.valueOf(-1 * distance * Math.sin(Math.toRadians(degree))).intValue();
	}
}
