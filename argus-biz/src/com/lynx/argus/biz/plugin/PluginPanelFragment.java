package com.lynx.argus.biz.plugin;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import com.lynx.argus.R;
import com.lynx.argus.app.BizFragment;
import com.lynx.argus.biz.plugin.demo.ShopListFragment;
import com.lynx.lib.http.core.AsyncTask;
import com.lynx.lib.widget.pulltorefresh.PullToRefreshGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chris.liu
 * Date: 13-9-12 下午6:18
 */
public class PluginPanelFragment extends BizFragment {
    public static final String Tag = "plugin";

    private List<Map<String, Object>> idxInfos = new ArrayList<Map<String, Object>>();
    private PullToRefreshGridView prgvIdx;
    private PluginStorePopWindow pluginStorePopWindow;
    private SimpleAdapter idxAdapter;

    public PluginPanelFragment() {
        initIdx();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idxAdapter = new SimpleAdapter(tabActivity, idxInfos,
                R.layout.layout_plugin_panel_item,
                new String[]{"icon", "name"},
                new int[]{R.id.iv_plugin_panel_item_icon, R.id.tv_plugin_panel_item_name}
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_plugin_panel, container, false);

        prgvIdx = (PullToRefreshGridView) v.findViewById(R.id.prgv_local_idx);
        prgvIdx.getRefreshableView().setAdapter(idxAdapter);

        Drawable drawable = getResources().getDrawable(R.drawable.ptr_refresh);
        prgvIdx.setLoadingDrawable(drawable);

        prgvIdx.setOnRefreshListener(new PullToRefreshGridView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetDataTask().execute();
            }
        });

        prgvIdx.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Map<String, Object> idxInfo = idxInfos.get(position);
                    ShopListFragment slf = new ShopListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("query", "美食");
                    slf.setArguments(bundle);
                    tabActivity.pushFragment(Tag, slf, true, true);
                }
            }
        });


        pluginStorePopWindow = new PluginStorePopWindow(tabActivity);

        ImageButton ibAdd = (ImageButton) v.findViewById(R.id.ib_local_add);
        ibAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pluginStorePopWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            }
        });

        return v;
    }

    private void initIdx() {
        idxInfos.clear();

        Map<String, Object> idxInfo = new HashMap<String, Object>();
        idxInfo.put("icon", R.drawable.plugin_location + "");
        idxInfo.put("name", "附近");
        idxInfos.add(idxInfo);

        idxInfo = new HashMap<String, Object>();
        idxInfo.put("icon", R.drawable.plugin_def + "");
        idxInfo.put("name", "待续");
        idxInfos.add(idxInfo);

        idxInfo = new HashMap<String, Object>();
        idxInfo.put("icon", R.drawable.plugin_def + "");
        idxInfo.put("name", "待续");
        idxInfos.add(idxInfo);

        idxInfo = new HashMap<String, Object>();
        idxInfo.put("icon", R.drawable.plugin_def + "");
        idxInfo.put("name", "待续");
        idxInfos.add(idxInfo);
    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                ;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            prgvIdx.onRefreshComplete();
        }
    }
}
