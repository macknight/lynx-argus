package com.lynx.lib.core;

import android.content.Context;

/**
 * 动态Fragment加载器
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 9/29/13 4:12 PM
 */
public class DexUILoader extends DexModuleLoader {

	public DexUILoader(Context context, String moduleName) {
		super(context, "ui", moduleName, 0);
	}

}
