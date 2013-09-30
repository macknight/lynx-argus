package com.lynx.argus.biz.photo;

import android.content.Intent;
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
                Intent i = new Intent("com.lynx.argus.intent.action.LOAD_FRAGMENT");
                i.putExtra("module", "photo");
                startActivity(i);
            }
        });

        return v;
    }

}
