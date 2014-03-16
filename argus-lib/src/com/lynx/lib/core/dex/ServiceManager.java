package com.lynx.lib.core.dex;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.lynx.lib.core.Const;
import com.lynx.lib.core.LFApplication;
import com.lynx.lib.core.Logger;
import com.lynx.lib.http.HttpCallback;
import com.lynx.lib.http.HttpService;
import com.lynx.lib.http.core.HttpParam;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-11-17 上午1:32
 */
public abstract class ServiceManager {
	private static final String Tag = "ServiceManager";
	private static final String LM_API_SERVICE_CONFIG = "/dex/service";
	public static final String PREFIX = "service";

	private LFApplication context;
	private HttpService httpService;

	private Map<String, ServiceLoader> serviceLoaders = new HashMap<String, ServiceLoader>();

	public ServiceManager() {
		this.context = LFApplication.instance();
		this.httpService = (HttpService) context.service("http");

		File tmp = new File(context.getFilesDir(), PREFIX);
		if (!tmp.exists()) {
			tmp.mkdirs();
		}

		updateCheck();

		initService();
	}

	protected abstract void initService();

	private HttpCallback<Object> callback = new HttpCallback<Object>() {
		@Override
		public void onSuccess(Object o) {
			super.onSuccess(o);
			try {
				JSONObject joResult = new JSONObject(o.toString());
				if (joResult.getInt("status") != 200) {
					Logger.w(Tag, "获取服务更新配置服务器返回错误");
					return;
				}
				JSONArray jaPlugin = joResult.getJSONArray("data");
				// 获取插件更新配置
				for (int i = 0; i < jaPlugin.length(); ++i) {
					try {
						Service service = context.gson().fromJson(
								jaPlugin.getJSONObject(i).toString(), Service.class);
						if (service != null) {
							// 无需通知上层，直接更新
							ServiceLoader loader = serviceLoaders.get(service.getModule());
							if (loader != null) {
								// service 有新的动态更新
                                loader.update(service);
							}
						}
					} catch (Exception e) {
						Logger.e(Tag, "服务更新配置数据解析异常", e);
					}
				}
			} catch (Exception e) {
				Logger.e(Tag, "服务更新配置数据解析异常", e);
			}
		}

		@Override
		public void onFailure(Throwable t, String strMsg) {
			Logger.w(Tag, "获取服务更新配置失败", t);
		}
	};

	/**
	 * 更新配置
	 */
	public void updateCheck() {
		HttpParam param = new HttpParam();
		param.put("ua", "android");
		httpService.post(String.format("%s%s", Const.LM_API_DOMAIN, LM_API_SERVICE_CONFIG), param,
				callback);
	}

	public IService service(String name) {
		ServiceLoader loader = serviceLoaders.get(name);
		if (loader != null) {
			return loader.service();
		}
		return null;
	}

	public void addServiceLoader(ServiceLoader loader) {
		serviceLoaders.put(loader.module(), loader);
	}
}
