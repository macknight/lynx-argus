package com.lynx.lib.widget.photoview;

import android.annotation.TargetApi;
import android.view.View;

/**
 * 
 * @author zhufeng.liu
 * @version 14-2-24 下午4:55
 */
@TargetApi(16)
public class SDK16 {

	public static void postOnAnimation(View view, Runnable r) {
		view.postOnAnimation(r);
	}

}
