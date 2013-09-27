package com.lynx.lib.core;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lynx.lib.R;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 9/27/13 4:32 PM
 */
public class ErrorFragment extends LFFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_error, null, false);
        return v;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
