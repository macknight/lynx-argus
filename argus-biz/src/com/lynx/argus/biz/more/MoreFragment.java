package com.lynx.argus.biz.more;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.lynx.argus.R;
import com.lynx.argus.app.BizFragment;
import com.lynx.argus.biz.more.model.GroupListAdapter;
import com.lynx.argus.biz.more.model.GroupListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-10-27 下午9:29
 */
public class MoreFragment extends BizFragment {
	public static final String Tag = "More";
	private List<GroupListItem> data = new ArrayList<GroupListItem>();
	private GroupListAdapter adapter;
	private ListView listview;

	public MoreFragment() {
		initData();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d("chris", "onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		adapter = new GroupListAdapter(tabActivity, data);
	}

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) throws Exception {
		View v = inflater.inflate(R.layout.layout_more, container, false);
		listview = (ListView) v.findViewById(R.id.lv_more_idx);
		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				if (position == 10) {
					tabActivity.pushFragment(new AboutFragment(), true);
				}
			}
		});
		return v;
	}

	public void initData() {
		GroupListItem item;
		item = new GroupListItem(-1, "", true);
		data.add(item);
		item = new GroupListItem(-1, "草稿箱", false);
		data.add(item);
		item = new GroupListItem(-1, "主题", false);
		data.add(item);
		item = new GroupListItem(-1, "图片质量", false);
		data.add(item);
		item = new GroupListItem(-1, "缓存设置", false);
		data.add(item);

		item = new GroupListItem(-1, "", true);
		data.add(item);
		item = new GroupListItem(-1, "账号管理", false);
		data.add(item);
		item = new GroupListItem(-1, "隐私与安全", false);
		data.add(item);
		item = new GroupListItem(-1, "密码重设", false);
		data.add(item);

		item = new GroupListItem(-1, "", true);
		data.add(item);
		item = new GroupListItem(-1, "关于我们", false);
		data.add(item);
		item = new GroupListItem(-1, "意见反馈", false);
		data.add(item);
	}
}
