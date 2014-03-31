package com.lynx.lib.core.dex;

import java.util.Map;

/**
 * 负责所有动态更新模块(包括ui module和service)的更新管理
 * 
 * @author zhufeng.liu
 * @version 13-10-28 下午5:39
 */
public abstract class DexManager {
	private static final String Tag = "dexmanager";

	protected ServiceManager serviceManager;
	protected PluginManager pluginManager;

	public DexManager() {
		pluginManager = new PluginManager();

		initServiceManager();
	}

	protected abstract void initServiceManager();

	public IService service(String name) {
		return serviceManager.service(name);
	}

	public void addServiceLoader(ServiceLoader loader) {
		serviceManager.addServiceLoader(loader);
	}

	public Map<String, PluginLoader> pluginLoaders() {
		return pluginManager.pluginLoaders();
	}

	public PluginLoader pluginLoader(String name) {
		return pluginManager.getPluginLoader(name);
	}

	public void installPlugin(Plugin plugin, DexListener listener) {
		pluginManager.addPluginLoader(plugin, listener);
	}

	public void uninstallPlugin(Plugin plugin, DexListener listener) {
		pluginManager.removePluginLoader(plugin, listener);
	}
}
