package com.lynx.argus.biz.local;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import com.lynx.argus.R;
import com.lynx.argus.app.BasicFragment;
import com.lynx.lib.http.core.AsyncTask;
import com.lynx.lib.widget.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-12 下午6:18
 */
public class LocalFragment extends BasicFragment {
    public static final String Tag = "Local";

    private List<Map<String, Object>> idxInfos = new ArrayList<Map<String, Object>>();

    private PullToRefreshListView prlvIdx;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_local, container, false);

        prlvIdx = (PullToRefreshListView) view.findViewById(R.id.prlv_local_idx);

        initIdx();
        ListAdapter adapter = new SimpleAdapter(this.getActivity(), idxInfos,
                R.layout.layout_local_idx_item,
                new String[]{"icon", "title", "desc"},
                new int[]{R.id.iv_local_idx_item_icon, R.id.tv_local_idx_item_title, R.id.tv_local_idx_item_desc}
        );
        prlvIdx.getRefreshableView().setAdapter(adapter);

        prlvIdx.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetDataTask().execute();
            }
        });

        prlvIdx.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> idxInfo = idxInfos.get(position - 1);
                ShopListFragment slf = new ShopListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("query", idxInfo.get("title").toString());
                slf.setArguments(bundle);
                tabActivity.pushFragments(Tag, slf, true, true);
            }
        });

        return view;
    }

    private void initIdx() {
        idxInfos.clear();

        Map<String, Object> idxInfo = new HashMap<String, Object>();
        idxInfo.put("icon", R.drawable.food_u + "");
        idxInfo.put("title", "小吃快餐");
        idxInfo.put("desc", "asdfasd.fadsf");
        idxInfos.add(idxInfo);

        idxInfo = new HashMap<String, Object>();
        idxInfo.put("icon", R.drawable.coffee_u + "");
        idxInfo.put("title", "咖啡屋");
        idxInfo.put("desc", "asdfasd.fadsf");
        idxInfos.add(idxInfo);

        idxInfo = new HashMap<String, Object>();
        idxInfo.put("icon", R.drawable.bread_u + "");
        idxInfo.put("title", "西点");
        idxInfo.put("desc", "asdfasd.fadsf");
        idxInfos.add(idxInfo);

        idxInfo = new HashMap<String, Object>();
        idxInfo.put("icon", R.drawable.pot_u + "");
        idxInfo.put("title", "火锅");
        idxInfo.put("desc", "asdfasd.fadsf");
        idxInfos.add(idxInfo);

        idxInfo = new HashMap<String, Object>();
        idxInfo.put("icon", R.drawable.ktv_u + "");
        idxInfo.put("title", "KTV");
        idxInfo.put("desc", "asdfasd.fadsf");
        idxInfos.add(idxInfo);

        idxInfo = new HashMap<String, Object>();
        idxInfo.put("icon", R.drawable.film_u + "");
        idxInfo.put("title", "电影院");
        idxInfo.put("desc", "asdfasd.fadsf");
        idxInfos.add(idxInfo);

        idxInfo = new HashMap<String, Object>();
        idxInfo.put("icon", R.drawable.hotel_u + "");
        idxInfo.put("title", "酒店");
        idxInfo.put("desc", "asdfasd.fadsf");
        idxInfos.add(idxInfo);

        idxInfo = new HashMap<String, Object>();
        idxInfo.put("icon", R.drawable.bank_u + "");
        idxInfo.put("title", "银行");
        idxInfo.put("desc", "asdfasd.fadsf");
        idxInfos.add(idxInfo);

        idxInfo = new HashMap<String, Object>();
        idxInfo.put("icon", R.drawable.hair_u + "");
        idxInfo.put("title", "美容美发");
        idxInfo.put("desc", "asdfasd.fadsf");
        idxInfos.add(idxInfo);

        idxInfo = new HashMap<String, Object>();
        idxInfo.put("icon", R.drawable.park_u + "");
        idxInfo.put("title", "停车场");
        idxInfo.put("desc", "asdfasd.fadsf");
        idxInfos.add(idxInfo);
    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                ;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            // Call onRefreshComplete when the list has been refreshed.
            prlvIdx.onRefreshComplete();
            super.onPostExecute(result);
        }
    }


}
