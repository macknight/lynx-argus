package com.lynx.argus.biz.more;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.lynx.argus.R;
import com.lynx.argus.app.BizFragment;
import com.lynx.argus.biz.more.model.GroupListAdapter;
import com.lynx.argus.biz.more.model.GroupListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris.liu
 * Created at 13-10-27-下午9:29.
 */
public class MoreFragment extends BizFragment {
	public static final String Tag = "More";
	private static List<GroupListItem> data = new ArrayList<GroupListItem>();
	private ListView listview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_more, container, false);
		listview = (ListView) v.findViewById(R.id.lv_more_idx);
		GroupListAdapter adapter = new GroupListAdapter(tabActivity, data);
		listview.setAdapter(adapter);
		return v;
	}

	public void initData() {
		GroupListItem item;
		item = new GroupListItem(-1, "通用", true);
		data.add(item);
		item = new GroupListItem(-1, "草稿箱", false);
		data.add(item);
		item = new GroupListItem(-1, "字号设置", false);
		data.add(item);
		item = new GroupListItem(-1, "自动加载", false);
		data.add(item);
		item = new GroupListItem(-1, "图片质量", false);
		data.add(item);
		item = new GroupListItem(-1, "清除缓存", false);
		data.add(item);
		item = new GroupListItem(-1, "安全", true);
		data.add(item);
		item = new GroupListItem(-1, "退出当前账号", false);
		data.add(item);
		item = new GroupListItem(-1, "账号切换", false);
		data.add(item);
		item = new GroupListItem(-1, "密码重设", false);
		data.add(item);
	}
}
