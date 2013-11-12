package com.lynx.argus.biz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import com.lynx.argus.R;
import com.lynx.argus.biz.local.LocalFragment;
import com.lynx.argus.biz.more.MoreFragment;
import com.lynx.argus.biz.my.MyFragment;
import com.lynx.argus.biz.search.SearchFragment;
import com.lynx.argus.biz.shopping.ShoppingFragment;
import com.lynx.lib.core.LFFragment;
import com.lynx.lib.core.LFTabActivity;

import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-12 下午5:19
 */
public class BizTabActivity extends LFTabActivity {

	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// do custom init here
		sp = getSharedPreferences("biz", MODE_PRIVATE);
		curTab = sp.getString("cur_tab", LocalFragment.Tag);
		tabHost.setCurrentTabByTag(curTab);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}


	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("cur_tab", curTab);
		editor.commit();
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d("chris", "onStop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("chris", "onDestroy");
	}

	@Override
	protected void initUI() {
		setContentView(R.layout.layout_main_tab);

		setPopAnimation(R.animator.slide_in_left, R.animator.slide_out_right);
		setPushAnimation(R.animator.slide_in_right, R.animator.slide_out_left);
	}

	@Override
	protected void initTabHost() {
		stacks.put(LocalFragment.Tag, new Stack<LFFragment>());
		stacks.put(ShoppingFragment.Tag, new Stack<LFFragment>());
		stacks.put(MyFragment.Tag, new Stack<LFFragment>());
		stacks.put(SearchFragment.Tag, new Stack<LFFragment>());
		stacks.put(MoreFragment.Tag, new Stack<LFFragment>());

		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				curTab = tabId;
				if (stacks.get(tabId).size() == 0) {
					if (tabId.equals(LocalFragment.Tag)) {
						pushFragment(tabId, new LocalFragment(), false, true);
					} else if (tabId.equals(ShoppingFragment.Tag)) {
						pushFragment(tabId, new ShoppingFragment(), false, true);
					} else if (tabId.equals(MyFragment.Tag)) {
						pushFragment(tabId, new MyFragment(), false, true);
					} else if (tabId.equals(SearchFragment.Tag)) {
						pushFragment(tabId, new SearchFragment(), false, true);
					} else if (tabId.equals(MoreFragment.Tag)) {
						pushFragment(tabId, new MoreFragment(), false, true);
					}
				} else {
					pushFragment(tabId, stacks.get(tabId).lastElement(), false, false);
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
		spec.setIndicator(createTabView(R.drawable.main_idx_local));
		tabHost.addTab(spec);

		spec = tabHost.newTabSpec(ShoppingFragment.Tag);
		spec.setContent(new TabHost.TabContentFactory() {
			@Override
			public View createTabContent(String tag) {
				return findViewById(R.id.realtabcontent);
			}
		});
		spec.setIndicator(createTabView(R.drawable.main_idx_shop));
		tabHost.addTab(spec);

		spec = tabHost.newTabSpec(MyFragment.Tag);
		spec.setContent(new TabHost.TabContentFactory() {
			@Override
			public View createTabContent(String tag) {
				return findViewById(R.id.realtabcontent);
			}
		});
		spec.setIndicator(createTabView(R.drawable.main_idx_my));
		tabHost.addTab(spec);

		spec = tabHost.newTabSpec(SearchFragment.Tag);
		spec.setContent(new TabHost.TabContentFactory() {
			@Override
			public View createTabContent(String tag) {
				return findViewById(R.id.realtabcontent);
			}
		});
		spec.setIndicator(createTabView(R.drawable.main_idx_search));
		tabHost.addTab(spec);

		spec = tabHost.newTabSpec(MoreFragment.Tag);
		spec.setContent(new TabHost.TabContentFactory() {
			@Override
			public View createTabContent(String tag) {
				return findViewById(R.id.realtabcontent);
			}
		});
		spec.setIndicator(createTabView(R.drawable.main_idx_more));
		tabHost.addTab(spec);
	}

	private View createTabView(final int id) {
		View view = LayoutInflater.from(this).inflate(R.layout.layout_tab_item, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.tab_icon);
		imageView.setImageDrawable(getResources().getDrawable(id));
		return view;
	}
}
