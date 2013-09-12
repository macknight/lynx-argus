package com.lynx.argus.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.lynx.argus.biz.MainTabActivity;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-12 下午6:18
 */
public class BasicFragment extends Fragment {
    protected MainTabActivity tabActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tabActivity = (MainTabActivity) this.getActivity();
    }

    public boolean onBackPressed() {
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

}
