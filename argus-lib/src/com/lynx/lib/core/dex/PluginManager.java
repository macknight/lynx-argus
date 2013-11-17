package com.lynx.lib.core.dex;

import android.content.Context;
import android.widget.Toast;
import com.lynx.lib.core.Const;
import com.lynx.lib.core.LFApplication;
import com.lynx.lib.core.Logger;
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
 * Created with IntelliJ IDEA.
 * User: chris.liu
 * Date: 13-11-17 上午1:33
 */
public class PluginManager {
	private static final String Tag = "pluginManager";
	public static final String PREFIX = "plugincenter";
	public static final String MY_PLUGIN_CONFIG = "myplugin_config";

	private static final String LM_API_PLUGIN_CONFIG = "/dex/plugin";

	private Map<String, PluginLoader> pluginLoaders = new HashMap<String, PluginLoader>();

	private Context context;
	private HttpService httpService;

	private JSONArray jaMyPlugins;
	private String basicDir;

	public PluginManager() {
		this.context = LFApplication.instance();
		this.httpService = (HttpService) LFApplication.instance().service("http");

		File tmp = new File(context.getFilesDir(), PREFIX);
		if (!tmp.exists()) {
			tmp.mkdirs();
		}
		basicDir = tmp.getAbsolutePath();
		loadLocalPlugins();
	}


	private HttpCallback<Object> callback = new HttpCallback<Object>() {
		@Override
		public void onSuccess(Object o) {
			super.onSuccess(o);
			try {
				JSONObject joResult = new JSONObject(o.toString());
				if (joResult.getInt("status") != 200) {
					Toast.makeText(context, "获取插件更新配置失败", Toast.LENGTH_SHORT).show();
					return;
				}
				JSONArray jaPlugin = joResult.getJSONArray("data");
				// 获取插件更新配置
				for (int i = 0; i < jaPlugin.length(); ++i) {
					try {
						// TODO: 提示用户有插件更新
						update(jaPlugin.getJSONObject(i));
					} catch (Exception e) {

					}
				}
			} catch (Exception e) {
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onFailure(Throwable t, String strMsg) {
			super.onFailure(t, strMsg);
			Toast.makeText(context, "获取插件更新配置失败", Toast.LENGTH_SHORT).show();
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
		httpService.post(String.format("%s%s", Const.DOMAIN, LM_API_PLUGIN_CONFIG),
				param, callback);
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
	public void addPluginLoader(PluginLoader loader) {
		try {
			JSONObject jo = DexUtil.dexModule2json(DexType.PLUGIN, loader.dexModule());
			jaMyPlugins.put(jo);
			saveConfig(MY_PLUGIN_CONFIG, jaMyPlugins);
			pluginLoaders.put(loader.module(), loader);
		} catch (Exception e) {

		}
	}

	public void removePluginLoader(Plugin plugin) {
		pluginLoaders.remove(plugin.module());
		JSONArray ja = new JSONArray();
		for (String key : pluginLoaders.keySet()) {
			if (key.equals(plugin.module())) {
				continue;
			}
			try {
				PluginLoader loader = pluginLoaders.get(key);
				loader.delete();
				JSONObject jo = DexUtil.dexModule2json(DexType.PLUGIN, loader.dexModule());
				ja.put(jo);
			} catch (Exception e) {

			}
		}
		try {
			jaMyPlugins = ja;
			saveConfig(MY_PLUGIN_CONFIG, jaMyPlugins);
		} catch (Exception e) {

		}
	}

	private void update(JSONObject config) {
		try {
			Plugin plugin = (Plugin) DexUtil.json2dexModule(DexType.PLUGIN, config);
			PluginLoader dexLoader = getPluginLoader(plugin.module());
			if (dexLoader != null) {
				// UI module有新的动态更新
				dexLoader.update(plugin);
			} else {
				dexLoader = new PluginLoader(plugin);
				dexLoader.update(plugin);
				addPluginLoader(dexLoader);
			}
		} catch (Exception e) {
			Logger.e(Tag, "update ui module error", e);
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
				Plugin plugin = (Plugin) DexUtil.json2dexModule(DexType.PLUGIN, jaMyPlugins.getJSONObject(i));
				if (plugin != null) {
					PluginLoader loader = new PluginLoader(plugin);
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
