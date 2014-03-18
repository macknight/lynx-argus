package com.lynx.lib.core.dex;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

import com.lynx.lib.core.Const;
import com.lynx.lib.core.LFApplication;
import com.lynx.lib.core.Logger;
import com.lynx.lib.core.dex.DexLoader.DexStatus;
import com.lynx.lib.db.DBService;
import com.lynx.lib.http.HttpCallback;
import com.lynx.lib.http.HttpService;
import com.lynx.lib.http.core.HttpParam;
import com.lynx.lib.util.FileUtil;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-11-17 上午1:33
 */
public class PluginManager {
	private static final String Tag = "pluginManager";
	private static final String LM_API_PLUGIN_CONFIG = "/dex/myplugin";

	private LFApplication context;
	private HttpService httpService;
	private DBService dbService;

	private Map<String, PluginLoader> pluginLoaders = new HashMap<String, PluginLoader>();

	public PluginManager() {
		this.context = LFApplication.instance();
		this.httpService = (HttpService) LFApplication.instance().service("http");
		this.dbService = context.dbService();

		loadLocalPlugins();
		deleteNoUseFolder();
		updateCheck();
	}

	private HttpCallback<Object> callback = new HttpCallback<Object>() {
		@Override
		public void onSuccess(Object o) {
			super.onSuccess(o);
			try {
				JSONObject joResult = new JSONObject(o.toString());
				if (joResult.getInt("status") != 200) {
					Logger.w(Tag, "获取动态模块更新配置服务器返回错误");
					return;
				}
				JSONArray jaPlugin = joResult.getJSONArray("data");
				// 获取插件更新配置
				for (int i = 0; i < jaPlugin.length(); ++i) {
					try {
						Plugin plugin = context.gson().fromJson(
								jaPlugin.getJSONObject(i).toString(), Plugin.class);
						PluginLoader loader = pluginLoaders.get(plugin.getModule());
						// TODO: 提示用户有插件更新
						loader.update(plugin);
					} catch (Exception e) {
						Logger.e(Tag, "获取动态模块更新配置内容错误", e);
					}
				}
			} catch (Exception e) {
				Logger.e(Tag, "获取动态模块更新配置异常", e);
			}
		}

		@Override
		public void onFailure(Throwable t, String strMsg) {
			super.onFailure(t, strMsg);
			Logger.e(Tag, "获取插件更新配置失败", t);
		}
	};

	private DexListener installListener = new DexListener() {
		@Override
		public void onStatusChanged(DexLoader dexLoader, int status) {
			switch (status) {
			case DexListener.DEX_INSTALL_FAIL:
				removePluginLoader((Plugin) dexLoader.dexModule(), null);
			}
		}
	};

	/**
	 * 插件更新检查
	 */
	public void updateCheck() {
		if (TextUtils.isEmpty(localPlugins())) {
			return;
		}

		HttpParam param = new HttpParam();
		param.put("ua", "android");
		param.put("plugins", localPlugins());
		httpService.post(String.format("%s%s", Const.LM_API_DOMAIN, LM_API_PLUGIN_CONFIG), param,
				callback);
	}

	public Map<String, PluginLoader> pluginLoaders() {
		return pluginLoaders;
	}

	public PluginLoader getPluginLoader(String module) {
		return pluginLoaders.get(module);
	}

	/**
	 * 加入UI插件
	 */
	public void addPluginLoader(Plugin plugin, DexListener listener) {
		try {
			PluginLoader loader = new PluginLoader(plugin, DexStatus.UPDATE);

			loader.addListener(listener);
			loader.addListener(installListener);

			dbService.save(loader.dexModule());
			pluginLoaders.put(loader.module(), loader);
		} catch (Exception e) {
			Logger.e(Tag, "add pluginloader error", e);
			removePluginLoader(plugin, null);
		}
	}

	public void removePluginLoader(Plugin plugin, DexListener listener) {
		PluginLoader loader = pluginLoaders.remove(plugin.getModule());
		if (loader == null) {
			Logger.e(Tag, String.format("no such pluginloader[%s] find", plugin.getModule()));
			return;
		}

		try {
			loader.addListener(listener);
			loader.delete();

			String where = String.format("module='%s'", loader.module());
			dbService.deleteByWhere(Plugin.class, where);
			pluginLoaders.remove(loader.module());

			if (listener != null) {
				listener.onStatusChanged(loader, DexListener.DEX_UNINSTALL_SUCCESS);
			}
		} catch (Exception e) {
			Logger.e(Tag, "remove pluginloader error", e);
			if (listener != null) {
				listener.onStatusChanged(loader, DexListener.DEX_UNINSTALL_FAIL);
			}
		}
	}

	/**
	 * 载入本地插件
	 */
	private void loadLocalPlugins() {
		try {
			List<Plugin> myPlugins = dbService.findAll(Plugin.class);
			for (Plugin plugin : myPlugins) {
				PluginLoader loader = new PluginLoader(plugin, DexStatus.LOAD);
				pluginLoaders.put(loader.module(), loader);
			}
		} catch (Exception e) {
			Logger.e(Tag, "load local plugins error", e);
		}
	}

	private String localPlugins() {
		String plugins = "";
		for (String key : pluginLoaders.keySet()) {
			plugins = String.format("%s|%s,%s", plugins, key, pluginLoaders.get(key).version());
		}

		return plugins.length() > 1 ? plugins.substring(1) : null;
	}

	/**
	 * 删除卸载插件的残余文件
	 */
	private void deleteNoUseFolder() {
		String pluginBaseDir = String.format("%s/plugin", context.getFilesDir().getAbsolutePath());
		File pluginBase = new File(pluginBaseDir);
		if (pluginBase.exists()) {
			File[] files = pluginBase.listFiles();
			for (File file : files) {
				try {
					if (!pluginLoaders.keySet().contains(file.getName())) {
						FileUtil.deleteFile(file);
					}
				} catch (Exception e) {
					Logger.w(Tag, "删除卸载插件的残余文件异常", e);
				}
			}
		}
	}

}
