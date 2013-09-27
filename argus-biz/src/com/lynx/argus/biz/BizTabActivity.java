package com.lynx.argus.biz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import com.lynx.argus.R;
import com.lynx.argus.biz.local.LocalFragment;
import com.lynx.argus.biz.photo.PhotoFragment;
import com.lynx.lib.core.LFTabActivity;

import java.util.HashMap;
import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-12 下午5:19
 */
public class BizTabActivity extends LFTabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // do custom init here
    }

    @Override
    protected void initUI() {
        setContentView(R.layout.layout_main_tab);
    }

    @Override
    protected void initTabHost() {
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
    }

    @Override
    protected void initTabContents() {
        resContent = R.id.realtabcontent;

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
}
