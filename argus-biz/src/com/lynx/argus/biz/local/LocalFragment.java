package com.lynx.argus.biz.local;

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
import com.lynx.lib.http.core.AsyncTask;
import com.lynx.lib.widget.pulltorefresh.PullToRefreshGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-12 下午6:18
 */
public class LocalFragment extends BizFragment {
    public static final String Tag = "local";

    private List<Map<String, Object>> idxInfos = new ArrayList<Map<String, Object>>();
    private PullToRefreshGridView prgvIdx;
    private PluginStorePopWindow pluginStorePopWindow;
    private SimpleAdapter idxAdapter;

    public LocalFragment() {
        initIdx();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idxAdapter = new SimpleAdapter(tabActivity, idxInfos,
                R.layout.layout_local_idx_item,
                new String[]{"icon"},
                new int[]{R.id.iv_local_idx_item_icon}
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_local, container, false);

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
                Map<String, Object> idxInfo = idxInfos.get(position);
                ShopListFragment slf = new ShopListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("query", idxInfo.get("title").toString());
                slf.setArguments(bundle);
                tabActivity.pushFragment(Tag, slf, true, true);
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
        idxInfo.put("icon", R.drawable.as_alias + "");
        idxInfo.put("title", "美食");
        idxInfos.add(idxInfo);

        idxInfo = new HashMap<String, Object>();
        idxInfo.put("icon", R.drawable.as_favorite + "");
        idxInfo.put("title", "商场");
        idxInfos.add(idxInfo);

        idxInfo = new HashMap<String, Object>();
        idxInfo.put("icon", R.drawable.as_browser + "");
        idxInfo.put("title", "酒店");
        idxInfos.add(idxInfo);

        idxInfo = new HashMap<String, Object>();
        idxInfo.put("icon", R.drawable.as_clear_location + "");
        idxInfo.put("title", "娱乐");
        idxInfos.add(idxInfo);

        idxInfo = new HashMap<String, Object>();
        idxInfo.put("icon", R.drawable.as_delete + "");
        idxInfo.put("title", "学校");
        idxInfos.add(idxInfo);

        idxInfo = new HashMap<String, Object>();
        idxInfo.put("icon", R.drawable.as_copy + "");
        idxInfo.put("title", "银行");
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
