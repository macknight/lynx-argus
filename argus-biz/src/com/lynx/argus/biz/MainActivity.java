package com.lynx.argus.biz;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.lynx.argus.R;
import com.lynx.argus.app.BizApplication;
import com.lynx.service.geo.GeoService;
import com.lynx.service.geo.LocationListener;
import com.lynx.service.test.TestService;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/29/13 11:27 AM
 */
public class MainActivity extends Activity {
    private TextView tvConsole = null;

    private TestService testService;
    private GeoService geoService;

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(GeoService.LocationStatus status) {
            Toast.makeText(MainActivity.this, geoService.coord().toString(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        testService = (TestService) BizApplication.instance().service(TestService.class.getSimpleName());
        geoService = (GeoService) BizApplication.instance().service(GeoService.class.getSimpleName());

        if (geoService != null) {
            geoService.addListener(locationListener);
        }

        tvConsole = (TextView) findViewById(R.id.tv_main_console);

        Button btn = (Button) findViewById(R.id.btn_main_load_apk_a);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvConsole.setText(testService.hello("chris"));
            }
        });

        btn = (Button) findViewById(R.id.btn_main_load_apk_b);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    geoService.locate(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }
}
