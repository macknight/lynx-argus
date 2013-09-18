package com.lynx.service.activity;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.lynx.lib.core.DexServiceLoader;
import com.lynx.service.R;
import com.lynx.service.cache.CacheService;
import com.lynx.service.geo.GeoService;
import com.lynx.service.test.TestService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-18 下午5:47
 */
public abstract class BasicInfoActivity extends Activity {
    protected Map<String, DexServiceLoader> loaders;
    protected List<Map<String, Object>> servicesInfos = new ArrayList<Map<String, Object>>();
    protected List<Map<String, Object>> appInfos = new ArrayList<Map<String, Object>>();
    private ListView lvAppInfo, lvServiceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sys_info);
        lvAppInfo = (ListView) findViewById(R.id.lv_sys_info_app);
        lvServiceInfo = (ListView) findViewById(R.id.lv_sys_info_service);

        getAppInfo();
        getServiceInfo();

    }

    private void getAppInfo() {
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                Map<String, Object> appInfo = new HashMap<String, Object>();
                appInfo.put("icon", packageInfo.applicationInfo.loadIcon(getPackageManager()));
                appInfo.put("name", packageInfo.applicationInfo.loadLabel(getPackageManager()).toString() + packageInfo.versionCode);
                appInfo.put("desc", packageInfo.packageName);
                appInfos.add(appInfo);
            }
        }

        SimpleAdapter adapter = new SimpleAdapter(this, appInfos,
                R.layout.layout_app_info_item,
                new String[]{"icon", "name", "desc"},
                new int[]{R.id.iv_app_info_item_icon, R.id.tv_app_info_item_name, R.id.tv_app_info_item_desc}
        );
        lvAppInfo.setAdapter(adapter);
    }

    private void getServiceInfo() {
        setDexLoaders();
        if (loaders != null && loaders.size() > 0) {
            for (String name : loaders.keySet()) {
                DexServiceLoader loader = loaders.get(name);
                Map<String, Object> serviceInfo = new HashMap<String, Object>();
                serviceInfo.put("icon", getResId(name) + "");
                serviceInfo.put("name", name);
                serviceInfo.put("clazz", loader.service().getClass().getName());
                serviceInfo.put("version", loader.curVersion() + "");
                servicesInfos.add(serviceInfo);
            }
        }

        SimpleAdapter adapter = new SimpleAdapter(this, servicesInfos,
                R.layout.layout_service_info_item,
                new String[]{"icon", "name", "clazz", "version"},
                new int[]{R.id.iv_sys_info_item_indicator, R.id.tv_sys_info_item_name,
                        R.id.tv_sys_info_item_clazz, R.id.tv_sys_info_item_version}
        );
        lvServiceInfo.setAdapter(adapter);
    }

    protected abstract void setDexLoaders();

    private int getResId(String name) {
        if (GeoService.class.getSimpleName().equals(name)) {
            return R.drawable.service_item_geo;
        } else if (TestService.class.getSimpleName().equals(name)) {
            return R.drawable.service_item_test;
        } else if (CacheService.class.getSimpleName().equals(name)) {
            return R.drawable.service_item_security;
        }
        return -1;
    }
}
