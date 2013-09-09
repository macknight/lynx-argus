package com.lynx.argus.biz;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.lynx.lib.location.LocationService;
import com.lynx.service.core.ServiceManager;
import com.lynx.service.test.TestService;
import com.lynx.argus.R;

public class MainActivity extends Activity {
    private TextView tvConsole = null;

    private ServiceManager serviceManager;
    private TestService testService;
    private LocationService locationService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        serviceManager = new ServiceManager(this);
        testService = (TestService) serviceManager.getService(TestService.class.getSimpleName());
        locationService = (LocationService) serviceManager.getService(LocationService.class.getSimpleName());
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
                    locationService.locate(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
