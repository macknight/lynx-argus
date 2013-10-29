package com.lynx.argus.biz;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import com.lynx.argus.R;
import com.lynx.argus.biz.local.LocalFragment;
import com.lynx.argus.biz.more.MoreFragment;
import com.lynx.argus.biz.msg.MsgFragment;
import com.lynx.argus.biz.photo.PhotoFragment;
import com.lynx.argus.biz.social.SocialFragment;
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
        //
    }

    @Override
    protected void initUI() {
        setContentView(R.layout.layout_main_tab);

        setPopAnimation(R.animator.slide_in_left, R.animator.slide_out_right);
        setPushAnimation(R.animator.slide_in_right, R.animator.slide_out_left);
    }

    @Override
    protected void initTabHost() {
        stacks = new HashMap<String, Stack<Fragment>>();
        stacks.put("Local", new Stack<Fragment>());
        stacks.put("Photo", new Stack<Fragment>());
        stacks.put("Message", new Stack<Fragment>());
        stacks.put("Social", new Stack<Fragment>());
        stacks.put("More", new Stack<Fragment>());

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
                        pushFragments(tabId, new MsgFragment(), false, true);
                    } else if (tabId.equals("Social")) {
                        pushFragments(tabId, new SocialFragment(), false, true);
                    } else if (tabId.equals("More")) {
                        pushFragments(tabId, new MoreFragment(), false, true);
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
        spec.setIndicator(createTabView(R.drawable.tab_local));
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec(PhotoFragment.Tag);
        spec.setContent(new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });
        spec.setIndicator(createTabView(R.drawable.tab_photo));
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Message");
        spec.setContent(new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });
        spec.setIndicator(createTabView(R.drawable.tab_msg));
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Social");
        spec.setContent(new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });
        spec.setIndicator(createTabView(R.drawable.tab_social));
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("More");
        spec.setContent(new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });
        spec.setIndicator(createTabView(R.drawable.tab_more));
        tabHost.addTab(spec);
    }

    private View createTabView(final int id) {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_tab_item, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.tab_icon);
        imageView.setImageDrawable(getResources().getDrawable(id));
        return view;
    }
}
