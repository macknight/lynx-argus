package com.lynx.argus.biz.photo;

import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.lynx.argus.R;
import com.lynx.argus.app.BizFragment;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-12 下午6:20
 */
public class PhotoFragment extends BizFragment {

    public static final String Tag = "Photo";

    private AssetManager asm;
    private Resources res;
    private Resources.Theme thm;
    private ClassLoader cl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_photo, container, false);

        Button btn = (Button) v.findViewById(R.id.btn_photo_dex_laoder);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AssetManager asset = tabActivity.getAssets();
                    Intent i = new Intent("com.dianping.intent.action.LOAD_FRAGMENT");
                    i.putExtra("path", "apk/" + asset.list("apk")[0]);
                    i.putExtra("class", "com.lynx.argus.biz.plugin.test.DemoFragment");
                    startActivity(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return v;
    }
}
