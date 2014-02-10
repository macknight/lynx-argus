package com.lynx.lib.core.dex;

import org.json.JSONObject;

import com.lynx.lib.core.dex.DexModuleLoader.DexType;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-11-17 下午11:40
 */
public class DexUtil {

	private DexUtil() {
		throw new AssertionError("this class shouldn't be instanced");
	}

	public static JSONObject dexModule2json(DexType type, DexModule module) {
		switch (type) {
		case PLUGIN:
			return plugin2json((Plugin) module);
		case SERVICE:
			return dexModule2json(module);
		}
		return null;
	}

	public static DexModule json2dexModule(DexType type, JSONObject json) {
		switch (type) {
		case PLUGIN:
			return json2plugin(json);
		case SERVICE:
			return json2dexModule(json);
		}
		return null;
	}

	/**
	 * convert DexModule object to json object
	 * 
	 * @param module
	 * @return
	 */
	private static JSONObject dexModule2json(DexModule module) {
		try {
			JSONObject jo = new JSONObject();
			jo.put(DexModule.K_MODULE, module.module());
			jo.put(DexModule.K_VERSION, module.version());
			jo.put(DexModule.K_URL, module.url());
			jo.put(DexModule.K_MD5, module.md5());
			jo.put(DexModule.K_CLAZZ, module.clazz());
			jo.put(DexModule.K_DESC, module.desc());
			return jo;
		} catch (Exception e) {

		}
		return null;
	}

	/**
	 * convert json object to plugin object
	 * 
	 * @param json
	 * @return
	 */
	private static DexModule json2dexModule(JSONObject json) {
		try {
			String module = json.getString(DexModule.K_MODULE);
			int version = json.getInt(DexModule.K_VERSION);
			String url = json.getString(DexModule.K_URL);
			String md5 = json.getString(DexModule.K_MD5);
			String desc = json.getString(DexModule.K_DESC);
			String clazz = json.getString(DexModule.K_CLAZZ);
			return new DexModule(module, version, url, md5, desc, clazz);
		} catch (Exception e) {

		}
		return null;
	}

	/**
	 * convert Plugin object to json object
	 * 
	 * @param plugin
	 * @return
	 */
	private static JSONObject plugin2json(Plugin plugin) {
		try {
			JSONObject jo = dexModule2json(plugin);
			jo.put(Plugin.K_NAME, plugin.name());
			jo.put(Plugin.K_CATEGORY, plugin.category());
			jo.put(Plugin.K_ICON, plugin.icon());
			return jo;
		} catch (Exception e) {

		}
		return null;
	}

	/**
	 * convert json object to plugin object
	 * 
	 * @param json
	 * @return
	 */
	private static Plugin json2plugin(JSONObject json) {
		try {
			String module = json.getString(Plugin.K_MODULE);
			int version = json.getInt(Plugin.K_VERSION);
			String name = json.getString(Plugin.K_NAME);
			String icon = json.getString(Plugin.K_ICON);
			String desc = json.getString(Plugin.K_DESC);
			String url = json.getString(Plugin.K_URL);
			String md5 = json.getString(Plugin.K_MD5);
			String clazz = json.getString(Plugin.K_CLAZZ);
			int category = json.getInt(Plugin.K_CATEGORY);
			return new Plugin(module, version, name, icon, url, md5, desc, clazz, category);
		} catch (Exception e) {

		}
		return null;
	}
}
