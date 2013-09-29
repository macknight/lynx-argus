package com.lynx.lib.core;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import com.lynx.lib.R;

import java.util.Map;
import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 9/26/13 3:08 PM
 */
public abstract class LFTabActivity extends LFActivity {
    protected TabHost tabHost;
    protected Map<String, Stack<Fragment>> stacks;
    protected String curTab;
    protected int resContent = -1; // fragment根容器ID
    private int animResPushIn = R.animator.slide_in_right, animResPushOut = R.animator.slide_out_left;
    private int animResPopIn = R.animator.slide_in_left, animResPopOut = R.animator.slide_out_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();
        initTabHost();
        initTabContents();
    }

    protected abstract void initUI();

    protected abstract void initTabHost();

    protected abstract void initTabContents();

    protected void setPushAnimation(int resIn, int resOut) {
        animResPushIn = resIn;
        animResPushOut = resOut;
    }

    protected void setPopAnimation(int resIn, int resOut) {
        animResPopIn = resIn;
        animResPopOut = resOut;
    }

    public void pushFragments(String tag, Fragment fragment, boolean shouldAnimate, boolean shouldAdd) {
        if (shouldAdd) {
            stacks.get(tag).push(fragment);
        }
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (shouldAnimate) {
            ft.setCustomAnimations(animResPushIn, animResPushOut);
        }
        ft.replace(resContent, fragment);
        ft.commit();
    }

    private void popFragments() {
        Fragment fragment = stacks.get(curTab).elementAt(stacks.get(curTab).size() - 2);

        stacks.get(curTab).pop();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(animResPopIn, animResPopOut);
        ft.replace(resContent, fragment);

        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (((LFFragment) stacks.get(curTab).lastElement()).onBackPressed() == false) {
            if (stacks.get(curTab).size() == 1) {
                super.onBackPressed();
            } else {
                popFragments();
            }
        } else {
            // do nothing
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (stacks.get(curTab).size() == 0) {
            return;
        }
        stacks.get(curTab).lastElement().onActivityResult(requestCode, requestCode, data);
    }
}
