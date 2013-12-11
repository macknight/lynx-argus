package com.lynx.lib.core.dex;

import android.content.Context;
import com.lynx.lib.core.Const;
import com.lynx.lib.core.LFApplication;
import com.lynx.lib.core.Logger;
import com.lynx.lib.core.dex.DexModuleLoader.DexStatus;
import com.lynx.lib.core.dex.DexModuleLoader.DexType;
import com.lynx.lib.http.HttpCallback;
import com.lynx.lib.http.HttpService;
import com.lynx.lib.http.core.HttpParam;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author zhufeng.liu
 * 
 * @addtime 13-11-17 上午1:33
 */
public class PluginManager {
	private static final String Tag = "pluginManager";
	public static final String PREFIX = "plugincenter";
	public static final String MY_PLUGIN_CONFIG = "myplugin_config";

	private static final String LM_API_PLUGIN_CONFIG = "/dex/myplugin";

	private Context context;
	private HttpService httpService;
	private Map<String, PluginLoader> pluginLoaders = new HashMap<String, PluginLoader>();
	private JSONArray jaMyPlugins;
	private String basicDir;

	public PluginManager() {
		this.context = LFApplication.instance();
		this.httpService = (HttpService) LFApplication.instance().service(
				"http");

		File tmp = new File(context.getFilesDir(), PREFIX);
		if (!tmp.exists()) {
			tmp.mkdirs();
		}
		basicDir = tmp.getAbsolutePath();
		loadLocalPlugins();
		updateCheck();
	}

	private HttpCallback<Object> callback = new HttpCallback<Object>() {
		@Override
		public void onSuccess(Object o) {
			super.onSuccess(o);
			try {
				JSONObject joResult = new JSONObject(o.toString());
				if (joResult.getInt("status") != 200) {
					Logger.w(Tag, "获取动态模块更新配置失败");
					return;
				}
				JSONArray jaPlugin = joResult.getJSONArray("data");
				saveConfig(MY_PLUGIN_CONFIG, jaPlugin);

				// 获取插件更新配置
				for (int i = 0; i < jaPlugin.length(); ++i) {
					try {
						Plugin plugin = (Plugin) DexUtil.json2dexModule(
								DexType.PLUGIN, jaPlugin.getJSONObject(i));
						update(plugin);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				Logger.w(Tag, "获取动态模块更新配置异常", e);
			}
		}

		@Override
		public void onFailure(Throwable t, String strMsg) {
			super.onFailure(t, strMsg);
			Logger.w(Tag, "获取插件更新配置失败", t);
		}
	};

	private DexModuleListener installListener = new DexModuleListener() {
		@Override
		public void onStatusChanged(DexModule dexModule, int status) {
			switch (status) {
			case DexModuleListener.DEX_INSTALL_FAIL:
				removePluginLoader((Plugin) dexModule, null);
			}
		}
	};

	/**
	 * 插件更新检查
	 */
	public void updateCheck() {
		if (localPlugins() == null) {
			return;
		}

		HttpParam param = new HttpParam();
		param.put("ua", "android");
		param.put("apps", localPlugins());
		httpService.post(String.format("%s%s", Const.LM_API_DOMAIN,
				LM_API_PLUGIN_CONFIG), param, callback);
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
	public void addPluginLoader(PluginLoader loader, DexModuleListener listener) {
		try {
			loader.addListener(listener);
			loader.addListener(installListener);
			if (jaMyPlugins == null) {
				jaMyPlugins = new JSONArray();
			}
			JSONObject jo = DexUtil.dexModule2json(DexType.PLUGIN,
					loader.dexModule());
			jaMyPlugins.put(jo);
			saveConfig(MY_PLUGIN_CONFIG, jaMyPlugins);
			pluginLoaders.put(loader.module(), loader);
		} catch (Exception e) {
			removePluginLoader((Plugin) loader.dexModule(), null);
		}
	}

	public void removePluginLoader(Plugin plugin, DexModuleListener listener) {
		try {
			JSONArray ja = new JSONArray();
			PluginLoader loader = pluginLoaders.get(plugin.module());
			loader.addListener(listener);
			loader.delete();
			for (String key : pluginLoaders.keySet()) {
				if (key.equals(plugin.module())) {
					continue;
				}
				loader = pluginLoaders.get(key);
				JSONObject jo = DexUtil.dexModule2json(DexType.PLUGIN,
						loader.dexModule());
				ja.put(jo);
			}

			jaMyPlugins = ja;
			saveConfig(MY_PLUGIN_CONFIG, jaMyPlugins);
			pluginLoaders.remove(plugin.module());
			if (listener != null) {
				listener.onStatusChanged(plugin,
						DexModuleListener.DEX_UNINSTALL_SUCCESS);
			}
		} catch (Exception e) {
			if (listener != null) {
				listener.onStatusChanged(plugin,
						DexModuleListener.DEX_UNINSTALL_FAIL);
			}
		}
	}

	// TODO: 提示用户有插件更新
	private void update(Plugin plugin) {
		PluginLoader dexLoader = getPluginLoader(plugin.module());
		if (dexLoader != null) {
			// UI module有新的动态更新
			dexLoader.hasUpdate(plugin);
		}
	}

	/**
	 * 载入本地插件
	 */
	private void loadLocalPlugins() {
		File path = new File(basicDir + File.separator + MY_PLUGIN_CONFIG);
		if (path.length() == 0)
			return;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(path);
			byte[] bytes = new byte[fis.available()];
			fis.read(bytes);
			fis.close();
			String str = new String(bytes, "UTF-8");
			JSONArray jaMyPlugins = new JSONArray(str);
			if (jaMyPlugins == null || jaMyPlugins.length() <= 0) {
				return;
			}

			for (int i = 0; i < jaMyPlugins.length(); ++i) {
				Plugin plugin = (Plugin) DexUtil.json2dexModule(DexType.PLUGIN,
						jaMyPlugins.getJSONObject(i));
				if (plugin != null) {
					PluginLoader loader = new PluginLoader(plugin,
							DexStatus.UPDATE);
					pluginLoaders.put(loader.module(), loader);
				}
			}
		} catch (Exception e) {

		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {

				}
			}
		}
	}

	private String localPlugins() {
		String plugins = "";
		for (String key : pluginLoaders.keySet()) {
			plugins = String.format("%s|%s", plugins, key);
		}
		return plugins.length() > 1 ? plugins.substring(1) : null;
		// return "local";
	}

	/**
	 * 将配置文件保存到本地
	 * 
	 * @param data
	 * @throws Exception
	 */
	private void saveConfig(String fileName, Object data) throws Exception {
		File config = new File(basicDir, fileName);
		File configTmp = new File(basicDir, "tmp");
		File configOld = new File(basicDir, "old");
		FileOutputStream fos = null;

		if (configOld.exists()) {
			configOld.delete();
		}
		if (config.exists()) {
			config.renameTo(configOld);
		}

		try {
			byte[] bytes = data.toString().getBytes("UTF-8");
			fos = new FileOutputStream(configTmp);
			fos.write(bytes);
			fos.close();
			fos = null;
			config.delete();
			if (!configTmp.renameTo(config)) {
				// revert to old config file
				if (config.exists()) {
					config.delete();
				}
				configOld.renameTo(config);
				throw new Exception("unable to move config from " + configTmp
						+ " to " + config);
			}
		} catch (Exception e) {
			config.delete();
			configOld.renameTo(config);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
				}
			}
			configTmp.delete();
			configOld.delete();
		}
	}
}
