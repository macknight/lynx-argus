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

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chris.liu
 * Date: 13-11-17 上午1:32
 */
public class ServiceManager {
	private static final String Tag = "serviceManager";
	private static final String LM_API_SERVICE_CONFIG = "/dex/service";

	private Context context;
	private HttpService httpService;
	private Map<String, ServiceLoader> serviceLoaders = new HashMap<String, ServiceLoader>();


	public ServiceManager() {
		this.context = LFApplication.instance();
		this.httpService = (HttpService) LFApplication.instance().service("http");
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
	 * 更新配置
	 */
	public void updateConfig() {
		HttpParam param = new HttpParam();
		param.put("ua", "android");
		httpService.post(String.format("%s%s", Const.DOMAIN, LM_API_SERVICE_CONFIG),
				param, callback);
	}

	private void update(JSONObject config) {
		try {
			DexModule module = DexUtil.json2dexModule(DexType.SERVICE, config);
			ServiceLoader dexLoader = serviceLoaders.get(module.module());
			if (dexLoader != null) {
				// service 有新的动态更新
				dexLoader.update(module);
			}
		} catch (Exception e) {
			Logger.e(Tag, "update service error", e);
		}
	}

	public Service service(String name) {
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
