package com.lynx.argus.biz;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.lynx.argus.R;
import com.lynx.argus.app.BizApplication;
import com.lynx.lib.core.DexServiceLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * APP系统基本信息，包括android系统信息以及service描述信息
 * <p/>
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-11 下午1:35
 */
public class SysInfoActivity extends Activity {

    private List<Map<String, Object>> servicesInfo = new ArrayList<Map<String, Object>>();
    private ListView lvServiceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sys_info);
        lvServiceInfo = (ListView) findViewById(R.id.lv_sys_info_service);

        init();

        SimpleAdapter adapter = new SimpleAdapter(this, servicesInfo,
                R.layout.layout_sys_info_item,
                new String[]{"name", "clazz", "version"},
                new int[]{R.id.tv_sys_info_item_name, R.id.tv_sys_info_item_clazz, R.id.tv_sys_info_item_version}
        );
        lvServiceInfo.setAdapter(adapter);
    }


    private void init() {
        Map<String, DexServiceLoader> loaders = BizApplication.instance().services();
        if (loaders != null && loaders.size() > 0) {
            for (String name : loaders.keySet()) {
                DexServiceLoader loader = loaders.get(name);
                Map<String, Object> serviceInfo = new HashMap<String, Object>();
                serviceInfo.put("name", name);
                serviceInfo.put("clazz", loader.service().getClass().getName());
                serviceInfo.put("version", loader.curVersion() + "");
                servicesInfo.add(serviceInfo);
            }
        }
    }
}
