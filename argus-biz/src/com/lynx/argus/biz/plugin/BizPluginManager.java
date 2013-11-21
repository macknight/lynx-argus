package com.lynx.argus.biz.plugin;

import android.widget.Toast;
import com.lynx.argus.app.BizApplication;
import com.lynx.lib.core.Const;
import com.lynx.lib.core.LFApplication;
import com.lynx.lib.core.dex.DexModuleListener;
import com.lynx.lib.core.dex.DexModuleLoader.DexType;
import com.lynx.lib.core.dex.DexUtil;
import com.lynx.lib.core.dex.Plugin;
import com.lynx.lib.http.HttpCallback;
import com.lynx.lib.http.HttpService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chris.liu
 * Date: 13-11-14 下午2:37
 */
public class BizPluginManager {
	private static final String K_LST_UPDATE = "lst_update";

	private static final String LM_API_ALL_PLUGIN = "/dex/pluginstore";

	private static final String PREFIX = "plugincenter";
	private static final String PLUGIN_STORE_CONFIG = "pluginstore_config";

	public static final int MSG_STORE_UPDATE_FIN = 0; // 插件商店更新完成
	public static final int MSG_PLUGIN_INSTALL_SUCCESS = 1; // 插件下载成功
	public static final int MSG_PLUGIN_INSTALL_FAIL = 2; // 插件安装失败
	public static final int MSG_PLUGIN_UNINSTALL_SUCCESS = 3; // 插件卸载成功
	public static final int MSG_PLUGIN_UNINSTALL_FAIL = 4; // 插件卸载失败

	private LFApplication application;
	private String basicDir;
	protected HttpService httpService;

	private JSONObject joPluginAtStore;
	private List<Plugin> pluginsAtStore = new ArrayList<Plugin>();

	private List<PluginMsgHandler> handlers = new ArrayList<PluginMsgHandler>();

	private static BizPluginManager instance;

	public static BizPluginManager instance() {
		if (instance == null) {
			instance = new BizPluginManager();
		}
		return instance;
	}

	private BizPluginManager() {
		this.httpService = (HttpService) LFApplication.instance().service("http");
		this.application = BizApplication.instance();
		File tmp = new File(application.getFilesDir(), PREFIX);
		if (!tmp.exists()) {
			tmp.mkdirs();
		}
		basicDir = tmp.getAbsolutePath();

		loadLocalPluginStore();
		loadPluginAtStore();
		if (pluginsAtStore.size() <= 0) {
			updatePluginStore();
		}
	}

	private DexModuleListener listener = new DexModuleListener() {
		@Override
		public void onStatusChanged(int status) {

		}
	};

	private HttpCallback<Object> storeUpdateCallback = new HttpCallback<Object>() {
		@Override
		public void onSuccess(Object o) {
			try {
				JSONObject joPlugins = new JSONObject(o.toString());
				if (joPlugins != null) {
					joPlugins.put(K_LST_UPDATE, System.currentTimeMillis());
					saveConfig(PLUGIN_STORE_CONFIG, joPlugins);
					joPluginAtStore = joPlugins;
					loadPluginAtStore();
				} else {
					Toast.makeText(application, "插件市场更新失败", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				Toast.makeText(application, "插件市场更新失败", Toast.LENGTH_SHORT).show();
			}
			dispatchMessage(MSG_STORE_UPDATE_FIN);
		}

		@Override
		public void onFailure(Throwable t, String strMsg) {
			Toast.makeText(application, "插件市场更新失败", Toast.LENGTH_SHORT).show();
			dispatchMessage(MSG_STORE_UPDATE_FIN);
		}
	};

	/**
	 * 更新插件商店
	 */
	public void updatePluginStore() {
		try {
			if (joPluginAtStore != null &&
					System.currentTimeMillis() - joPluginAtStore.getLong(K_LST_UPDATE) > 100 * 60 * 60 * 1000) {
				dispatchMessage(MSG_STORE_UPDATE_FIN);
				return;
			}
			String url = String.format("%s%s", Const.DOMAIN, LM_API_ALL_PLUGIN);
			httpService.post(url, storeUpdateCallback);
		} catch (Exception e) {
			dispatchMessage(MSG_STORE_UPDATE_FIN);
		}
	}

	/**
	 * 插件商店中的插件
	 *
	 * @return
	 */
	public List<Plugin> pluginsAtStore() {
		return pluginsAtStore;
	}


	public void installPlugin(Plugin plugin) {
		int msg = application.installPlugin(plugin, listener) ? MSG_PLUGIN_INSTALL_SUCCESS : MSG_PLUGIN_INSTALL_FAIL;
		dispatchMessage(msg);
	}

	/**
	 * 卸载插件
	 *
	 * @param plugin
	 */
	public void uninstallPlugin(Plugin plugin) {
		int msg = application.uninstallPlugin(plugin) ? MSG_PLUGIN_UNINSTALL_SUCCESS: MSG_PLUGIN_UNINSTALL_FAIL;
		dispatchMessage(msg);
	}

	public void addMsgHandler(PluginMsgHandler handler) {
		handlers.add(handler);
	}

	public void removeMsgHandler(PluginMsgHandler handler) {
		handlers.remove(handler);
	}

	private void dispatchMessage(int msg) {
		for (PluginMsgHandler handler : handlers) {
			if (handler.interested(msg)) {
				handler.sendEmptyMessage(msg);
			}
		}
	}

	/**
	 * 读取本地插件商店配置文件
	 *
	 * @return
	 * @throws Exception
	 */
	private void loadLocalPluginStore() {
		File path = new File(basicDir + File.separator + PLUGIN_STORE_CONFIG);
		if (path.length() == 0)
			return;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(path);
			byte[] bytes = new byte[fis.available()];
			fis.read(bytes);
			fis.close();
			String str = new String(bytes, "UTF-8");
			joPluginAtStore = new JSONObject(str);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
				}
			}
		}
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

	/**
	 * load plugins at store
	 */
	private void loadPluginAtStore() {
		JSONArray jaPlugin;
		try {
			jaPlugin = joPluginAtStore.getJSONArray("data");
		} catch (Exception e) {
			return;
		}

		if (jaPlugin == null || jaPlugin.length() <= 0) {
			return;
		}
		pluginsAtStore.clear();
		for (int i = 0; i < jaPlugin.length(); ++i) {
			try {
				Plugin plugin = (Plugin) DexUtil.json2dexModule(DexType.PLUGIN, jaPlugin.getJSONObject(i));
				if (plugin != null) {
					pluginsAtStore.add(plugin);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
