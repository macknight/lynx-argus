package com.lynx.lib.core;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * UI模块动态加载失败提示fragment
 * <p/>
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-10-18 下午1:32
 */
public class UILoadErrorFragment extends Fragment {
    private static final String Tag = "UILoadErrorFragment";

    private String message; // 加载失败提示

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = inflater.getContext();
        AssetManager am = context.getAssets();

        ViewGroup view = new LinearLayout(context);

        ImageView iv = new ImageView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        iv.setLayoutParams(params);
        try {
            BitmapDrawable bg = new BitmapDrawable(null, am.open("err.png"));
            iv.setImageDrawable(bg);
        } catch (Exception e) {
            Logger.e(Tag, "load icon error", e);
        }
        view.addView(iv);

        TextView tv = new TextView(context);
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        tv.setLayoutParams(params);
        tv.setPadding(0, 100, 0, 0);
        tv.setTextSize(16);
        tv.setText("页面载入失败@_@");
        view.addView(tv);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
