package com.lynx.argus.biz.local;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.lynx.argus.R;
import com.lynx.argus.app.BasicFragment;
import com.lynx.argus.app.BizApplication;
import com.lynx.argus.biz.SysInfoActivity;
import com.lynx.lib.http.core.AsyncTask;
import com.lynx.service.geo.GeoService;
import com.lynx.service.geo.LocationListener;

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
    private GeoService geoService;
    private List<Map<String, Object>> idxInfos = new ArrayList<Map<String, Object>>();

    private Animation animRotate;
    private AnimationDrawable adIndicator;
    private TextView tvLocAddr;
    private ImageView ivLocIndicator, ivLocRefresh;
    private PullToRefreshListView prlvIdx;

    private static final String BMAP_PLACE = "http://api.map.baidu.com/place/v2/search?&query=%s&location=%s,%s&radius=2000&output=json&ak=%s";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_local, container, false);

        animRotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animRotate.setDuration(1500);
        animRotate.setRepeatCount(-1);
        animRotate.setRepeatMode(Animation.RESTART);

        prlvIdx = (PullToRefreshListView) view.findViewById(R.id.prlv_local_idx);
        ivLocIndicator = (ImageView) view.findViewById(R.id.iv_main_loc_indicator);
        tvLocAddr = (TextView) view.findViewById(R.id.tv_main_loc_addr);
        ivLocRefresh = (ImageView) view.findViewById(R.id.iv_main_loc_refresh);
        ivLocRefresh.setAnimation(animRotate);
        animRotate.cancel();

        initIdx();
        SimpleAdapter adapter = new SimpleAdapter(this.getActivity(), idxInfos,
                R.layout.layout_local_idx_item,
                new String[]{"icon", "title", "desc"},
                new int[]{R.id.iv_local_idx_item_icon, R.id.tv_local_idx_item_title, R.id.tv_local_idx_item_desc}
        );
        prlvIdx.setAdapter(adapter);
        prlvIdx.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Do work to refresh the list here.
                new GetDataTask().execute();
            }
        });
//      tabActivity.pushFragments("Local", new LocalMapFragment(), true, true);


        ImageView ivIndicator = (ImageView) view.findViewById(R.id.iv_main_loc_indicator);
        ivIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(LocalFragment.this.getActivity(), SysInfoActivity.class);
                startActivity(intent);
            }
        });

        initLocationModule();
        return view;


    }

    private void initIdx() {
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


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    tvLocAddr.setText("正在定位...");
                    break;
                case 1:  // 定位成功
                    adIndicator.stop();
                    animRotate.cancel();
                    ivLocIndicator.setBackgroundResource(R.drawable.green_point);
                    tvLocAddr.setText(geoService.address());
                    break;
                case 2:
                    adIndicator.stop();
                    animRotate.cancel();
                    ivLocIndicator.setBackgroundResource(R.drawable.gray_point);
                    tvLocAddr.setText("定位失败");
                    break;
            }
        }
    };

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


    /**
     * 初始化定位相关模块
     */
    private void initLocationModule() {
        geoService = (GeoService) BizApplication.instance().service(GeoService.class.getSimpleName());

        if (geoService == null) {
            Toast.makeText(this.getActivity(), "定位模块不可用", Toast.LENGTH_SHORT).show();
            return;
        }

        ivLocRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                animRotate.startNow();
                ivLocIndicator.setBackgroundResource(R.anim.anim_indicator);
                adIndicator = (AnimationDrawable) ivLocIndicator.getBackground();
                adIndicator.setOneShot(false);
                if (adIndicator.isRunning()) {
                    adIndicator.stop();
                }
                adIndicator.start();
                geoService.locate(false);
            }
        });

        geoService.addListener(new LocationListener() {
            @Override
            public void onLocationChanged(GeoService.LocationStatus status) {
                switch (status) {
                    case ONGOING:
                        handler.sendEmptyMessage(0);
                        break;
                    case SUCCESS:
                        handler.sendEmptyMessage(1);
                        break;
                    case FAIL:
                        handler.sendEmptyMessage(2);
                        break;
                }
            }
        });
    }
}
