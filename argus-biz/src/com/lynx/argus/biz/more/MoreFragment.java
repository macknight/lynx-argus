package com.lynx.argus.biz.more;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
 * 
 * @author zhufeng.liu
 * 
 * @addtime 13-10-27 下午9:29
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

		Log.d("chris", "onCreate");
		adapter = new GroupListAdapter(tabActivity, data);
	}

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("chris", "onCreateView");
		View v = inflater.inflate(R.layout.layout_more, container, false);
		listview = (ListView) v.findViewById(R.id.lv_more_idx);
		listview.setAdapter(adapter);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("chris", "onActivityCreated");
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d("chris", "onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("chris", "onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d("chris", "onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d("chris", "onStop");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d("chris", "onDestroyView");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("chris", "onDestroy");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.d("chris", "onDetach");
	}

	public void initData() {
		Log.d("chris", "init more");
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
