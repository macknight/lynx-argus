package com.lynx.lib.core;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.lynx.lib.http.HttpService;
import com.lynx.lib.http.handler.HttpCallback;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 9/30/13 10:25 AM
 */
public abstract class UIModuleManager {
    private static final String URL_SERVICE_CONFIG = "/ui_config.php";
    public static final String K_MODULES = "modules";

    protected Context context;
    private HttpService httpService;
    private Map<String, DexUIModuleLoader> loaders = new HashMap<String, DexUIModuleLoader>();
    private JSONObject joConfig = null;

    public UIModuleManager(Context context) {
        this.context = context;
        httpService = (HttpService) LFApplication.instance.service("http");
    }

    /**
     * 在UIModuleManager初始化完成后加载所定制的模块
     */
    public abstract void initDexLoader();

    /**
     * 更新service配置
     */
    public void updateConfig() {
        httpService.get(String.format("%s%s", Const.PRODUCT_DOMAIN, URL_SERVICE_CONFIG), null,
                new HttpCallback<Object>() {
                    @Override
                    public void onSuccess(Object o) {
                        super.onSuccess(o);
                        Log.d("chris", o.toString());
                        try {
                            joConfig = new JSONObject(o.toString());
                            JSONArray jaModule = joConfig.getJSONArray(K_MODULES);
                            for (int i = 0; i < jaModule.length(); ++i) {
                                JSONObject config = jaModule.getJSONObject(i);
                                String module = config.getString(DexUIModuleLoader.K_MODULE);
                                DexUIModuleLoader dexLoader = getDexUIModuleLoader(module);
                                if (dexLoader != null) {
                                    // UI module有新的动态更新
                                    dexLoader.update(config);
                                } else {
                                    dexLoader = new DexUIModuleLoader(context, module);
                                    dexLoader.update(config);
                                    addUIModule(dexLoader);
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
     * 获取所有动态模块
     *
     * @return
     */
    public Map<String, DexUIModuleLoader> getUIModuleLoaders() {
        return loaders;
    }

    public DexUIModuleLoader getDexUIModuleLoader(String module) {
        return loaders.get(module);
    }

    public void addUIModule(DexUIModuleLoader loader) {
        loaders.put(loader.name(), loader);
    }

}
