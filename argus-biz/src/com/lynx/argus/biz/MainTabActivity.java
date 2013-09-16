package com.lynx.argus.biz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import com.lynx.argus.R;
import com.lynx.argus.app.BasicFragment;
import com.lynx.argus.biz.local.LocalFragment;
import com.lynx.argus.biz.photo.PhotoFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-12 下午5:19
 */
public class MainTabActivity extends FragmentActivity {

    private TabHost tabHost;
    private Map<String, Stack<Fragment>> stacks;
    private String curTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_main_tab);

        stacks = new HashMap<String, Stack<Fragment>>();
        stacks.put("Local", new Stack<Fragment>());
        stacks.put("Photo", new Stack<Fragment>());
        stacks.put("Message", new Stack<Fragment>());
        stacks.put("Friends", new Stack<Fragment>());
        stacks.put("Share", new Stack<Fragment>());

        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                curTab = tabId;
                if (stacks.get(tabId).size() == 0) {
                    if (tabId.equals(LocalFragment.Tag)) {
                        pushFragments(tabId, new LocalFragment(), false, true);
                    } else if (tabId.equals(PhotoFragment.Tag)) {
                        pushFragments(tabId, new PhotoFragment(), false, true);
                    } else if (tabId.equals("Message")) {

                    } else if (tabId.equals("Friends")) {

                    } else if (tabId.equals("Share")) {

                    }
                } else {
                    pushFragments(tabId, stacks.get(tabId).lastElement(), false, false);
                }
            }
        });
        tabHost.setup();
        initTabs();
    }

    private void initTabs() {
        TabHost.TabSpec spec = tabHost.newTabSpec(LocalFragment.Tag);
        tabHost.setCurrentTab(-3);
        spec.setContent(new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });
        spec.setIndicator(createTabView(R.drawable.tab_local_def));
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec(PhotoFragment.Tag);
        spec.setContent(new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });
        spec.setIndicator(createTabView(R.drawable.tab_photo_def));
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Message");
        spec.setContent(new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });
        spec.setIndicator(createTabView(R.drawable.tab_msg_def));
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Friends");
        spec.setContent(new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });
        spec.setIndicator(createTabView(R.drawable.tab_friends_def));
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Share");
        spec.setContent(new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });
        spec.setIndicator(createTabView(R.drawable.tab_share_def));
        tabHost.addTab(spec);
    }

    private View createTabView(final int id) {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_tab_item, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.tab_icon);
        imageView.setImageDrawable(getResources().getDrawable(id));
        return view;
    }

    public void pushFragments(String tag, Fragment fragment, boolean shouldAnimate, boolean shouldAdd) {
        if (shouldAdd) {
            stacks.get(tag).push(fragment);
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (shouldAnimate) {
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        ft.replace(R.id.realtabcontent, fragment);
        ft.commit();
    }

    private void popFragments() {
        Fragment fragment = stacks.get(curTab).elementAt(stacks.get(curTab).size() - 2);

        stacks.get(curTab).pop();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        ft.replace(R.id.realtabcontent, fragment);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (((BasicFragment) stacks.get(curTab).lastElement()).onBackPressed() == false) {
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
