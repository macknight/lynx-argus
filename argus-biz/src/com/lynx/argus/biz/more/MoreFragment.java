package com.lynx.argus.biz.more;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.LinearLayout.LayoutParams;
import com.lynx.argus.R;
import com.lynx.argus.biz.widget.listview.CornerListView;
import com.lynx.lib.core.LFFragment;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-10-27 下午9:29
 */
public class MoreFragment extends LFFragment implements AdapterView.OnItemClickListener {
	public static final String Tag = "More";
	private List<List<Map<String, String>>> data = new ArrayList<List<Map<String, String>>>();
	private String[] mSettingItems = { "离线下载", "", "分享给朋友", "评分", "", "检查新版本", "意见反馈", "联系我们",
			"关于", "", "捐赠" };
	private String[] mSettingItemMethods = { "offlineSetting", "", "shareApp", "jmupToMarket", "",
			"newVersionCheck", "feedback", "contract", "about", "", "donate" };
	private HashMap<String, String> mSettingItemMethodMap = new HashMap<String, String>();

	public MoreFragment() {
		initData();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			throws Exception {
		View view = inflater.inflate(R.layout.layout_more, container, false);
		if (view == null) {
			throw new Exception("页面初始化错误");
		}

		LinearLayout cornerContainer = (LinearLayout) view.findViewById(R.id.ll_more_container);

		for (int i = 0; i < mSettingItems.length; i++) {
			mSettingItemMethodMap.put(mSettingItems[i], mSettingItemMethods[i]);
		}

		int size = data.size();
		CornerListView cornerListView;
		LinearLayout.LayoutParams lp;
		SimpleAdapter adapter;
		for (int i = 0; i < size; i++) {
			cornerListView = new CornerListView(tabActivity);
			lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			if (i == 0 && i == (size - 1)) {
				lp.setMargins(15, 20, 15, 20);
			} else if (i == 0) {
				lp.setMargins(15, 20, 15, 10);
			} else if (i == (size - 1)) {
				lp.setMargins(15, 10, 15, 20);
			} else {
				lp.setMargins(15, 10, 15, 10);
			}
			cornerListView.setLayoutParams(lp);
			cornerListView.setCacheColorHint(0);
			cornerListView.setDivider(getResources().getDrawable(R.drawable.divider_h_gray));
			cornerListView.setSelector(android.R.color.transparent);
			cornerListView.setScrollbarFadingEnabled(false);
			cornerContainer.addView(cornerListView);

			adapter = new SimpleAdapter(tabActivity, data.get(i), R.layout.layout_more_item,
					new String[] { "text" }, new int[] { R.id.tv_more_item_title });
			cornerListView.setAdapter(adapter);
			cornerListView.setOnItemClickListener(this);
			int height = data.get(i).size()
					* (int) getResources().getDimension(R.dimen.more_item_height);
			if (data.get(i).size() > 1) {
				height -= data.get(i).size();
			} else {
				height += 1;
			}
			cornerListView.getLayoutParams().height = height;
		}
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TextView textView = (TextView) view.findViewById(R.id.tv_more_item_title);
		String key = textView.getText().toString();
		Class<? extends MoreFragment> clazz = this.getClass();
		try {
			Method method = clazz.getMethod(mSettingItemMethodMap.get(key));
			method.invoke(MoreFragment.this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initData() {
		data.clear();
		List<Map<String, String>> listData = new ArrayList<Map<String, String>>();

		Map<String, String> map;

		for (int i = 0; i < mSettingItems.length; i++) {
			if ("".equals(mSettingItems[i])) {
				data.add(listData);
				listData = new ArrayList<Map<String, String>>();
			} else {
				map = new HashMap<String, String>();
				map.put("text", mSettingItems[i]);
				listData.add(map);
			}
		}

		data.add(listData);
	}

	public void offlineSetting() {

	}

	public void shareApp() {

	}

	public void jmupToMarket() {

	}

	public void newVersionCheck() {

	}

	public void feedback() {

	}

	public void contract() {

	}

	public void about() {
		AboutFragment aboutFragment = new AboutFragment();
		tabActivity.pushFragment(aboutFragment);
	}

	public void donate() {
		GuideFragment guideFragment = new GuideFragment();
		tabActivity.pushFragment(guideFragment);
	}
}
