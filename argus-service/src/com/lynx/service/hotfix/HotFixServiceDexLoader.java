package com.lynx.service.hotfix;

import android.content.Context;
import com.lynx.lib.core.dex.DexServiceLoader;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-12 下午1:34
 */
public class HotFixServiceDexLoader extends DexServiceLoader {

	public static final String Tag = "hotfix";

	public HotFixServiceDexLoader(Context context, String moduleName, int minVersion, Class<?> defaultClazz)
			throws Exception {
		super(context, moduleName, minVersion, defaultClazz);
	}

	@Override
	protected void beforeLoad() {

	}

	@Override
	protected void loadService() throws Exception {

	}

	@Override
	protected void afterLoad() {

	}

	@Override
	public String moduleName() {
		return HotFixService.class.getSimpleName();
	}
}
