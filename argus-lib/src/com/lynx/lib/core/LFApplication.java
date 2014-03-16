package com.lynx.lib.core;

import android.app.Application;
import android.content.res.Configuration;
import com.google.gson.Gson;
import com.lynx.lib.core.dex.DexManager;
import com.lynx.lib.core.dex.DexModuleListener;
import com.lynx.lib.core.dex.Plugin;
import com.lynx.lib.core.dex.PluginLoader;
import com.lynx.lib.db.DBService;
import com.lynx.lib.http.HttpService;
import com.lynx.lib.http.impl.DefaultHttpServiceImpl;

import java.util.Map;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-9-10 下午2:32
 */
public abstract class LFApplication extends Application {

	protected static LFApplication instance;

	private static HttpService httpService;
	private static DBService dbService;
	protected DexManager dexManager;
	private static Gson gson;

	public static LFApplication instance() {
		if (instance == null) {
			throw new IllegalStateException("Application has not been created");
		}
		return instance;
	}

	protected LFApplication() {
		instance = this;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		httpService = new DefaultHttpServiceImpl();
		dbService = DBService.create(this, "argus");
        gson = new Gson();

		initDexManager();
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
	 * 根据服务名获取对应服务
	 * 
	 * @param name
	 * @return
	 */
	public Object service(String name) {
		if ("http".equals(name)) {
			return httpService;
		}
		return dexManager.service(name);
	}

	public DBService dbService() {
		return dbService;
	}

    public Gson gson() {
        return gson;
    }

	public Map<String, PluginLoader> pluginLoaders() {
		return dexManager.pluginLoaders();
	}

	/**
	 * 根据模块名获取对应插件加载器
	 * 
	 * @param name
	 * @return
	 */
	public PluginLoader pluginLoader(String name) {
		return dexManager.pluginLoader(name);
	}

	/**
	 * 插件是否存在
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasPlugin(String name) {
		return pluginLoader(name) != null;
	}

	/**
	 * 安装插件
	 * 
	 * @param plugin
	 */
	public void installPlugin(Plugin plugin, DexModuleListener listener) {
		dexManager.installPlugin(plugin, listener);
	}

	/**
	 * 卸载插件
	 * 
	 * @param plugin
	 */
	public void uninstallPlugin(Plugin plugin, DexModuleListener listener) {
		dexManager.uninstallPlugin(plugin, listener);
	}
}
