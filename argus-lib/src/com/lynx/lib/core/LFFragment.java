package com.lynx.lib.core;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 9/26/13 3:23 PM
 */
public abstract class LFFragment extends Fragment {

    protected LFTabActivity tabActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tabActivity = (LFTabActivity) this.getActivity();
    }

    public abstract boolean onBackPressed();

    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);
}
