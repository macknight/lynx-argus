package com.lynx.argus.biz.pluginstore;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;
import com.lynx.argus.biz.pluginstore.model.PluginItem;
import com.lynx.lib.core.Const;
import com.lynx.lib.core.LFApplication;
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
public class PluginStoreManager {
    private static final String K_NAME = "name";
    private static final String K_ICON = "icon";
    private static final String K_DESC = "desc";
    private static final String K_URL = "url";
    private static final String K_MD5 = "md5";
    private static final String K_CLAZZ = "clazz";
    private static final String K_CATEGORY = "category";

    private static final String PREFIX = "pluginstore";
    private static final String LM_API_ALL_PLUGIN = "/pluginstore";

    public static final int MSG_UPDATE_FIN = 0;
    public static final int MSG_DOWNLOAD_FIN = 1;

    private Context context;
    private Handler handler;
    private String basicDir;
    private long lstUpdate; // 插件中心更新时间
    protected HttpService httpService;

    private List<PluginItem> plugins = new ArrayList<PluginItem>();

    public PluginStoreManager(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
        File tmp = new File(context.getFilesDir(), PREFIX);
        if (!tmp.exists()) {
            tmp.mkdirs();
        }
        basicDir = tmp.getAbsolutePath();

        this.httpService = (HttpService) LFApplication.instance().service("http");

        try {
            JSONObject joPlugins = loadLocalConfig();
            lstUpdate = joPlugins.getLong("lstUpdate");
            if (System.currentTimeMillis() - lstUpdate > 60 * 60 * 1000) {
                // plugin store超过一个小时没有更新，自动更新
                updatePluginStore();
            } else {
                loadPlugins(joPlugins);
            }
        } catch (Exception e) {

        }
    }

    private HttpCallback callback = new HttpCallback() {
        @Override
        public void onSuccess(Object o) {
            try {
                JSONObject joPlugins = new JSONObject(o.toString());
                if (joPlugins != null) {
                    saveConfig(joPlugins);
                    loadPlugins(joPlugins);
                } else {
                    Toast.makeText(context, "update plugin stroe fail", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(context, "update plugin stroe fail", Toast.LENGTH_SHORT).show();
            }
            handler.sendEmptyMessage(MSG_UPDATE_FIN);
        }

        @Override
        public void onFailure(Throwable t, String strMsg) {
            Toast.makeText(context, "update plugin stroe fail", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(MSG_UPDATE_FIN);
        }
    };

    /**
     * 更新插件商店
     */
    public void updatePluginStore() {
        String url = String.format("%s%s", Const.DOMAIN, LM_API_ALL_PLUGIN);
        httpService.post(url, callback);
    }

    public List<PluginItem> plugins() {
        return plugins;
    }

    /**
     * 插件下载
     *
     * @param pluginItem
     */
    private void downloadPlugin(PluginItem pluginItem) {

    }

    private void loadPlugins(JSONObject config) {
        try {
            JSONArray jaPlugin = config.getJSONArray("data");
            if (jaPlugin != null && jaPlugin.length() > 0) {
                plugins.clear();
                for (int i = 0; i < jaPlugin.length(); ++i) {
                    try {
                        JSONObject joPlugin = jaPlugin.getJSONObject(i);
                        String name = joPlugin.getString(K_NAME);
                        String icon = joPlugin.getString(K_ICON);
                        String desc = joPlugin.getString(K_DESC);
                        String url = joPlugin.getString(K_URL);
                        String md5 = joPlugin.getString(K_MD5);
                        String clazz = joPlugin.getString(K_CLAZZ);
                        int category = joPlugin.getInt(K_CATEGORY);
                        plugins.add(new PluginItem(name, icon, desc, url, md5, clazz, category));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取本地配置文件
     *
     * @return
     * @throws Exception
     */
    private JSONObject loadLocalConfig() throws Exception {
        File path = new File(basicDir + "/config");
        if (path.length() == 0)
            return null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            fis.close();
            String str = new String(bytes, "UTF-8");
            JSONObject json = new JSONObject(str);
            return json;
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
     * @param json
     * @throws Exception
     */
    private void saveConfig(JSONObject json) throws Exception {
        File config = new File(basicDir, "config");
        File configTmp = new File(basicDir, "config_tmp");
        File configOld = new File(basicDir, "config_old");
        FileOutputStream fos = null;

        if (configOld.exists()) {
            configOld.delete();
        }
        if (config.exists()) {
            config.renameTo(configOld);
        }

        try {
            json.put("lstUpdate", System.currentTimeMillis());
            byte[] bytes = json.toString().getBytes("UTF-8");
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
