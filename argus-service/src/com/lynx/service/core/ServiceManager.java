package com.lynx.service.core;

import android.content.Context;
import android.widget.Toast;
import com.lynx.lib.dataservice.HttpService;
import com.lynx.lib.dataservice.handler.HttpCallback;
import com.lynx.lib.dataservice.impl.DefaultHttpServiceImpl;
import com.lynx.lib.location.LocationService;
import com.lynx.service.location.LocationServiceDexLoader;
import com.lynx.service.test.TestService;
import com.lynx.service.test.TestServiceDexLoader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-5 下午5:48
 */
public class ServiceManager {
    private static final String URL_SERVICE_CONFIG = "http://192.168.33.130/service_config.php";
    public static final String K_SERVICES = "services";
    public static final String K_SERVICE = "service"; // service名

    private Context context;
    private HttpService httpService;

    private TestServiceDexLoader testServiceDexLoader;
    private LocationServiceDexLoader locationServiceDexLoader;


    private Map<String, ServiceDexLoader> SERVICE_DEX_LOADERS = new HashMap<String, ServiceDexLoader>();

    private JSONObject JO_CONFIG = null;

    public ServiceManager(Context context) {
        this.context = context;
        httpService = new DefaultHttpServiceImpl();
        testServiceDexLoader = new TestServiceDexLoader(context);
        locationServiceDexLoader = new LocationServiceDexLoader(context);
        SERVICE_DEX_LOADERS.put(TestService.class.getSimpleName(), testServiceDexLoader);
        SERVICE_DEX_LOADERS.put(LocationService.class.getSimpleName(), locationServiceDexLoader);
        updateServiceConfig();
    }

    /**
     * 更新service配置
     */
    private void updateServiceConfig() {
        httpService.get(URL_SERVICE_CONFIG, null, new HttpCallback<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                try {
                    JO_CONFIG = new JSONObject(o.toString());
                    JSONArray ja_service = JO_CONFIG.getJSONArray(K_SERVICES);
                    for (int i = 0; i < ja_service.length(); ++i) {
                        JSONObject config = ja_service.getJSONObject(i);
                        String service = config.getString(K_SERVICE);
                        ServiceDexLoader dexLoader = SERVICE_DEX_LOADERS.get(service);
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
     * 根据service名获取对应service
     *
     * @param name
     * @return
     */
    public Object getService(String name) {

        ServiceDexLoader dexLoader = SERVICE_DEX_LOADERS.get(name);
        if (dexLoader != null) {
            return dexLoader.service();
        }
        return null;
    }
}
