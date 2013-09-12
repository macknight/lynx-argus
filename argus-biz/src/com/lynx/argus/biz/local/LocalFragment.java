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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.lynx.argus.R;
import com.lynx.argus.app.BasicFragment;
import com.lynx.argus.app.BizApplication;
import com.lynx.argus.biz.SysInfoActivity;
import com.lynx.service.geo.GeoService;
import com.lynx.service.geo.LocationListener;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-12 下午6:18
 */
public class LocalFragment extends BasicFragment {
    private GeoService geoService;

    private Animation animRotate;
    private AnimationDrawable adIndicator;
    private TextView tvLocAddr;
    private ImageView ivLocIndicator, ivLocRefresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_local, container, false);

        animRotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animRotate.setDuration(1500);
        animRotate.setRepeatCount(-1);
        animRotate.setRepeatMode(Animation.RESTART);

        ivLocIndicator = (ImageView) view.findViewById(R.id.iv_main_loc_indicator);
        tvLocAddr = (TextView) view.findViewById(R.id.tv_main_loc_addr);
        ivLocRefresh = (ImageView) view.findViewById(R.id.iv_main_loc_refresh);
        ivLocRefresh.setAnimation(animRotate);
        animRotate.cancel();

        Button btn = (Button) view.findViewById(R.id.btn_main_load_apk_a);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tabActivity.pushFragments("Local", new LocalMapFragment(), true, true);
            }
        });

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
