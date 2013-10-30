package com.lynx.lib.core;

import android.app.Application;
import android.content.res.Configuration;
import com.lynx.lib.core.dex.DexManager;
import com.lynx.lib.core.dex.DexServiceLoader;
import com.lynx.lib.core.dex.DexUILoader;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-10 下午2:32
 */
public abstract class LFApplication extends Application {

	protected static LFApplication instance;
	protected DexManager dexManager;

	public static LFApplication instance() {
		if (instance == null) {
			throw new IllegalStateException("Application has not been created");
		}
		return instance;
	}

	public LFApplication() {
		instance = this;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		initDexManager();
		dexManager.initDexServiceLoader();
		dexManager.initDexUIModuleLoader();
		dexManager.updateConfig();
	}


	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
	}

	protected abstract void initDexManager();

	/**
	 * 列举所有服务
	 *
	 * @return
	 */
	public Map<String, DexServiceLoader> services() {
		return dexManager.dexServices();
	}

	/**
	 * 根据服务获取对应服务
	 *
	 * @param name
	 * @return
	 */
	public Object service(String name) {
		return dexManager.getService(name);
	}

	public DexUILoader moduleLoader(String name) {
		return dexManager.getDexUILoader(name);
	}
}
