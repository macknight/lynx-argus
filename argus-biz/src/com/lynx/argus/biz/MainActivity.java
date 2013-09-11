package com.lynx.argus.biz;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.lynx.argus.R;
import com.lynx.argus.app.BizApplication;
import com.lynx.service.geo.GeoService;
import com.lynx.service.geo.GeoService.LocationStatus;
import com.lynx.service.geo.LocationListener;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/29/13 11:27 AM
 */
public class MainActivity extends Activity {
    private GeoService geoService;

    private Animation animRotate;
    private AnimationDrawable adIndicator;
    private TextView tvLocAddr;
    private ImageView ivLocIndicator, ivLocRefresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        animRotate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
        animRotate.setInterpolator(new LinearInterpolator());
        animRotate.setRepeatMode(Animation.RESTART);

        geoService = (GeoService) BizApplication.instance().service(GeoService.class.getSimpleName());

        ivLocIndicator = (ImageView) findViewById(R.id.iv_main_loc_indicator);
        tvLocAddr = (TextView) findViewById(R.id.tv_main_loc_addr);
        ivLocRefresh = (ImageView) findViewById(R.id.iv_main_loc_refresh);

        Button btn = (Button) findViewById(R.id.btn_main_load_apk_a);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ImageView ivIndicator = (ImageView) findViewById(R.id.iv_main_loc_indicator);
        ivIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SysInfoActivity.class);
                startActivity(intent);
            }
        });

        initLocationModule();
    }

    /**
     * 初始化定位相关模块
     */
    private void initLocationModule() {
        if (geoService == null) {
            Toast.makeText(this, "定位模块不可用", Toast.LENGTH_SHORT).show();
            return;
        }

        ivLocRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ivLocRefresh.startAnimation(animRotate);
                tvLocAddr.setText("正在定位ing");
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
            public void onLocationChanged(LocationStatus status) {
                adIndicator.stop();
                switch (status) {
                    case FAIL:
                        ivLocIndicator.setBackgroundResource(R.drawable.gray_point);
                        tvLocAddr.setText("定位失败");
                        break;
                    case SUCCESS:
                        ivLocIndicator.setBackgroundResource(R.drawable.green_point);
                        tvLocAddr.setText(geoService.address());
                        break;
                }
            }
        });
    }

}
