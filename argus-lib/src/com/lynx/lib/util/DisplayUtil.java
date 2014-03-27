package com.lynx.lib.util;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 
 * @author zhufeng.liu
 * @version 13-9-8 上午11:43
 */
public class DisplayUtil {

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	public static Point getScreenSize(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		dm = context.getResources().getDisplayMetrics();
		// this is another way to get the screen size
		// wm.getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels; // 屏幕宽（像素，如：480px）
		int screenHeight = dm.heightPixels;
		return new Point(screenWidth, screenHeight);

	}
}
