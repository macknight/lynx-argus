package com.lynx.lib.core;

import android.content.Context;
import android.widget.Toast;
import com.lynx.lib.http.HttpService;
import com.lynx.lib.http.handler.HttpCallback;
import com.lynx.lib.http.impl.DefaultHttpServiceImpl;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-10 下午2:39
 */
public abstract class ServiceManager {
    private static final String URL_SERVICE_CONFIG = "http://192.168.33.130/service_config.php";
    public static final String K_SERVICES = "services";
    public static final String K_SERVICE = "service"; // service名

    protected Context context;
    private HttpService httpService;
    private Map<String, DexServiceLoader> loaders = new HashMap<String, DexServiceLoader>();
    private JSONObject joConfig = null;

    public ServiceManager(Context context) {
        this.context = context;
        httpService = new DefaultHttpServiceImpl();
    }

    /**
     * 在ServiceManager初始化完成后加载所定制的service
     */
    public abstract void initDexLoader();

    /**
     * 更新service配置
     */
    public void updateServiceConfig() {
        httpService.get(URL_SERVICE_CONFIG, null, new HttpCallback<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                try {
                    joConfig = new JSONObject(o.toString());
                    JSONArray ja_service = joConfig.getJSONArray(K_SERVICES);
                    for (int i = 0; i < ja_service.length(); ++i) {
                        JSONObject config = ja_service.getJSONObject(i);
                        String service = config.getString(K_SERVICE);
                        DexServiceLoader dexLoader = loaders.get(service);
                        if (dexLoader != null) {
                            // service 有新的动态更新
                            dexLoader.updateService(config);
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t, String strMsg) {
                super.onFailure(t, strMsg);
                Toast.makeText(context, strMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 获取所有动态服务
     * @return
     */
    public Map<String, DexServiceLoader> dexServices() {
         return loaders;
    }

    public void addService(DexServiceLoader loader) {
        loaders.put(loader.name(), loader);
    }

    /**
     * 根据service名获取服务
     *
     * @param name
     * @return
     */
    public Object getService(String name) {
        if ("http".equals(name)) {
            return httpService;
        }
        DexServiceLoader dexLoader = loaders.get(name);
        if (dexLoader != null) {
            return dexLoader.service();
        }
        return null;
    }

}
