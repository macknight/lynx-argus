package com.lynx.argus.biz.plugin.test;

import android.app.Application;
import android.app.Fragment;
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
import android.widget.TextView;
import android.widget.Toast;
import com.lynx.lib.core.LFApplication;
import com.lynx.lib.http.HttpService;
import com.lynx.lib.geo.GeoService;
import com.lynx.lib.geo.LocationListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DemoFragment extends Fragment {
    private GeoService geoService;
    private HttpService httpService;

    private List<Map<String, Object>> shops = new ArrayList<Map<String, Object>>();
    private static final String BMAP_PLACE = "http://api.map.baidu.com/place/v2/search?&query=%s&location=%s,%s&radius=2000&output=json&ak=%s";

    private Animation animRotate;
    private AnimationDrawable adIndicator;
    private TextView tvLocAddr;
    private ImageView ivLocIndicator, ivLocRefresh;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            LFApplication application = (LFApplication) getActivity().getApplication();
            httpService = (HttpService) application.service("http");
        } catch (Throwable e) {
            e.printStackTrace();
        }

        animRotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animRotate.setDuration(1500);
        animRotate.setRepeatCount(-1);
        animRotate.setRepeatMode(Animation.RESTART);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main, container, false);
        initLocationModule(v);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ivLocIndicator.setBackgroundResource(R.anim.anim_indicator);
                    adIndicator = (AnimationDrawable) ivLocIndicator.getBackground();
                    adIndicator.setOneShot(false);
                    if (adIndicator.isRunning()) {
                        adIndicator.stop();
                    }
                    adIndicator.start();
                    animRotate.startNow();
                    tvLocAddr.setText("正在定位...");
                    break;
                case 1:  // 定位成功
                    if (adIndicator != null) {
                        adIndicator.stop();
                    }
                    if (animRotate != null) {
                        animRotate.cancel();
                    }
                    ivLocIndicator.setBackgroundResource(R.drawable.green_point);
                    tvLocAddr.setText(geoService.address());
                    break;
                case 2:
                    if (adIndicator != null) {
                        adIndicator.stop();
                    }
                    if (animRotate != null) {
                        animRotate.cancel();
                    }
                    ivLocIndicator.setBackgroundResource(R.drawable.gray_point);
                    tvLocAddr.setText("定位失败");
                    break;
            }
        }
    };

    /**
     * 初始化定位相关模块
     */
    private void initLocationModule(View v) {
        try {
            Application app = getActivity().getApplication();
            LFApplication application = (LFApplication) app;
            geoService = (GeoService) application.service(GeoService.class.getSimpleName());
        } catch (Throwable t) {
            t.printStackTrace();
        }

        ivLocIndicator = (ImageView) v.findViewById(R.id.iv_loc_indicator);
        tvLocAddr = (TextView) v.findViewById(R.id.tv_loc_addr);
        ivLocRefresh = (ImageView) v.findViewById(R.id.iv_loc_refresh);
        ivLocRefresh.setAnimation(animRotate);
        animRotate.cancel();


        if (geoService == null) {
            Toast.makeText(this.getActivity(), "定位模块不可用", Toast.LENGTH_SHORT).show();
            return;
        }

        if (geoService.address() != null) {
            tvLocAddr.setText(geoService.address());
            ivLocIndicator.setBackgroundResource(R.drawable.green_point);
        }

        ivLocRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
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
