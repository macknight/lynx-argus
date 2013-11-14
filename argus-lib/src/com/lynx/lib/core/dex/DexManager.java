package com.lynx.lib.core.dex;

import android.content.Context;
import android.widget.Toast;
import com.lynx.lib.core.Const;
import com.lynx.lib.core.Logger;
import com.lynx.lib.http.HttpService;
import com.lynx.lib.http.core.HttpParam;
import com.lynx.lib.http.handler.HttpCallback;
import com.lynx.lib.http.impl.DefaultHttpServiceImpl;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
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

	private static final String LM_API_SERVICE_CONFIG = "/config/framework";
	public static final String K_SERVICE = "service";
	public static final String K_UI = "ui";

	protected Context context;
	private static HttpService httpService;

	private Map<String, DexServiceLoader> serviceLoaders = new HashMap<String, DexServiceLoader>();
	private Map<String, DexUILoader> uiModuleLoaders = new HashMap<String, DexUILoader>();

	public DexManager(Context context) {
		this.context = context;
		httpService = new DefaultHttpServiceImpl();
	}

	/**
	 * 在ServiceManager初始化完成后加载所定制的service
	 */
	public abstract void initDexServiceLoader();

	/**
	 * 在UIModuleManager初始化完成后加载所定制的模块
	 */
	public abstract void initDexUIModuleLoader();

	/**
	 * 更新配置
	 */
	public void updateConfig() {
		HttpParam param = new HttpParam();
		param.put("ua", "android");
		httpService.post(String.format("%s%s", Const.DOMAIN, LM_API_SERVICE_CONFIG), param,
				new HttpCallback<Object>() {
					@Override
					public void onSuccess(Object o) {
						super.onSuccess(o);
						try {
							JSONObject joResult = new JSONObject(o.toString());
							if (joResult.getInt("status") != 200) {
								Toast.makeText(context, "获取框架配置失败", Toast.LENGTH_SHORT).show();
								return;
							}
							JSONObject joConfig = joResult.getJSONObject("data");
							// 获取service动态更新相关配置
							try {
								JSONArray jaService = joConfig.getJSONArray(K_SERVICE);
								for (int i = 0; i < jaService.length(); ++i) {
									updateService(jaService.getJSONObject(i));
								}
							} catch (Exception e) {

							}
							// 获取ui模块动态更新配置
							try {
								JSONArray jaModule = joConfig.getJSONArray(K_UI);
								for (int i = 0; i < jaModule.length(); ++i) {
									updateUI(jaModule.getJSONObject(i));
								}
							} catch (Exception e) {

							}
						} catch (Exception e) {
							Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onFailure(Throwable t, String strMsg) {
						super.onFailure(t, strMsg);
						Toast.makeText(context, "获取框架配置失败", Toast.LENGTH_SHORT).show();
					}
				});
	}

	public HttpService httpService() {
		return httpService;
	}

	public Map<String, DexServiceLoader> allServiceLoader() {
		return serviceLoaders;
	}

	public DexServiceLoader getDexServiceLoader(String name) {
		return serviceLoaders.get(name);
	}

	public void addService(DexServiceLoader loader) {
		serviceLoaders.put(loader.moduleName(), loader);
	}

	private void updateService(JSONObject config) {
		try {
			String moduleName = config.getString(DexModuleLoader.K_MODULE);
			DexServiceLoader dexLoader = serviceLoaders.get(moduleName);
			if (dexLoader != null) {
				// service 有新的动态更新
				dexLoader.update(config);
			}
		} catch (Exception e) {
			Logger.e(Tag, "update service error", e);
		}
	}

	public Map<String, DexUILoader> allUIModuleLoader() {
		return uiModuleLoaders;
	}

	public DexUILoader getDexUILoader(String module) {
		return uiModuleLoaders.get(module);
	}

	public void addUIModule(DexUILoader loader) {
		uiModuleLoaders.put(loader.moduleName(), loader);
	}

	private void updateUI(JSONObject config) {
		try {
			String module = config.getString(DexModuleLoader.K_MODULE);
			DexUILoader dexLoader = getDexUILoader(module);
			if (dexLoader != null) {
				// UI module有新的动态更新
				dexLoader.update(config);
			} else {
				dexLoader = new DexUILoader(context, module);
				dexLoader.update(config);
				addUIModule(dexLoader);
			}
		} catch (Exception e) {
			Logger.e(Tag, "update ui module error", e);
		}
	}
}
