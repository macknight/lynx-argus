package com.lynx.argus.biz.plugin;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;

import com.lynx.argus.R;
import com.lynx.argus.app.BizApplication;
import com.lynx.argus.biz.plugin.demo.DemoFragment;
import com.lynx.argus.biz.plugin.model.PluginCenterAdapter;
import com.lynx.lib.core.LFApplication;
import com.lynx.lib.core.LFDexActivity;
import com.lynx.lib.core.LFFragment;
import com.lynx.lib.core.dex.DexListener;
import com.lynx.lib.core.dex.Plugin;

/**
 * 
 * @author zhufeng.liu
 * @version 13-9-12 下午6:18
 */
public class PluginCenterFragment extends LFFragment {
	public static final String Tag = "plugin";

	private LFApplication application;
	private List<Plugin> plugins = new ArrayList<Plugin>();
	private GridView gvPlugin;
	private BizPluginManager pluginManager;
	private PluginCenterAdapter adapter;

	public PluginCenterFragment() {
		application = BizApplication.instance();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pluginManager = BizPluginManager.instance();
		pluginManager.addMsgHandler(handler);

		adapter = new PluginCenterAdapter(tabActivity, plugins);
		loadMyPlugins();
	}

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			throws Exception {
		View view = inflater.inflate(R.layout.layout_plugincenter, container, false);
		if (view == null) {
			throw new Exception("页面初始化错误");
		}
		gvPlugin = (GridView) view.findViewById(R.id.gv_plugincenter);

		gvPlugin.setOnItemClickListener(onItemClickListener);
		gvPlugin.setAdapter(adapter);

		ImageButton ibStore = (ImageButton) view.findViewById(R.id.ib_plugincenter_store);
		ibStore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PluginStoreFragment pluginStoreFragment = new PluginStoreFragment();
				tabActivity.pushFragment(pluginStoreFragment);
			}
		});

		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		pluginManager.removeMsgHandler(handler);
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Plugin plugin = plugins.get(position);
			if (position == 0) {
				DemoFragment demoFragment = new DemoFragment();
				tabActivity.pushFragment(demoFragment);
			} else {
				Intent i = new Intent(getActivity(), LFDexActivity.class);
				i.putExtra("module", plugin.getModule());
				startActivity(i);
			}
		}
	};

	private PluginMsgHandler handler = new PluginMsgHandler() {

		@Override
		public boolean interested(int msg) {
			return DexListener.DEX_INSTALL_SUCCESS == msg || DexListener.DEX_INSTALL_FAIL == msg
					|| DexListener.DEX_UNINSTALL_SUCCESS == msg
					|| DexListener.DEX_UNINSTALL_FAIL == msg;
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DexListener.DEX_DOWNLOAD_SUCCESS:
			case DexListener.DEX_DOWNLOAD_FAIL:
			case DexListener.DEX_INSTALL_SUCCESS:
			case DexListener.DEX_INSTALL_FAIL:
			case DexListener.DEX_UNINSTALL_SUCCESS:
			case DexListener.DEX_UNINSTALL_FAIL:
				loadMyPlugins();
				break;
			}
		}
	};

	private void loadMyPlugins() {
		plugins.clear();

		Plugin plugin = new Plugin();
		plugin.setModule("demo");
		plugin.setName("科学实验");
		plugin.setClazz("com.lynx.argus.biz.plugin.demo.DemoFragment");
		plugin.setVersion(1);
		plugin.setDesc("试验田");
		plugin.setCategory(1);
		plugins.add(plugin);

		if (application.pluginLoaders() == null) {
			return;
		}

		for (String key : application.pluginLoaders().keySet()) {
			plugins.add((Plugin) application.pluginLoaders().get(key).dexModule());
		}
	}
}
