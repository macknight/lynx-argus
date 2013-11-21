package com.lynx.lib.core.dex;

import java.util.Map;

/**
 * 负责所有动态更新模块(包括ui module和service)的更新管理
 * <p/>
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-10-28 下午5:39
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

	public Service service(String name) {
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

	public boolean installPlugin(Plugin plugin, DexModuleListener listener) {
		return pluginManager.addPluginLoader(new PluginLoader(plugin, listener));
	}

	public boolean uninstallPlugin(Plugin plugin) {
		return pluginManager.removePluginLoader(plugin);
	}
}