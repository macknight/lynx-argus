package com.lynx.argus.biz.plugin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import com.lynx.argus.R;
import com.lynx.argus.app.BizApplication;
import com.lynx.argus.app.BizFragment;
import com.lynx.argus.biz.plugin.demo.ShopListFragment;
import com.lynx.argus.biz.plugin.model.PluginCenterAdapter;
import com.lynx.lib.core.LFApplication;
import com.lynx.lib.core.LFDexActivity;
import com.lynx.lib.core.dex.DexModuleListener;
import com.lynx.lib.core.dex.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-9-12 下午6:18
 */
public class PluginCenterFragment extends BizFragment {
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
	public View onLoadView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_plugincenter, container,
				false);

		gvPlugin = (GridView) v.findViewById(R.id.gv_plugincenter);

		gvPlugin.setOnItemClickListener(onItemClickListener);
		gvPlugin.setAdapter(adapter);

		ImageButton ibStore = (ImageButton) v
				.findViewById(R.id.ib_plugincenter_store);
		ibStore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PluginStoreFragment pluginStoreFragment = new PluginStoreFragment();
				tabActivity.pushFragment(pluginStoreFragment, true);
			}
		});

		return v;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		pluginManager.removeMsgHandler(handler);
	}

	private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Plugin plugin = plugins.get(position);
			if (position == 0) {
				ShopListFragment slf = new ShopListFragment();
				Bundle bundle = new Bundle();
				bundle.putString("query", "美食");
				slf.setArguments(bundle);
				tabActivity.pushFragment(slf, true);
			} else {
				Intent i = new Intent(getActivity(), LFDexActivity.class);
				i.putExtra("module", plugin.module());
				startActivity(i);
			}
		}
	};

	private PluginMsgHandler handler = new PluginMsgHandler() {

		@Override
		public boolean interested(int msg) {
			return DexModuleListener.DEX_INSTALL_SUCCESS == msg
					|| DexModuleListener.DEX_INSTALL_FAIL == msg
					|| DexModuleListener.DEX_UNINSTALL_SUCCESS == msg
					|| DexModuleListener.DEX_UNINSTALL_FAIL == msg;
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DexModuleListener.DEX_DOWNLOAD_SUCCESS:
			case DexModuleListener.DEX_DOWNLOAD_FAIL:
			case DexModuleListener.DEX_INSTALL_SUCCESS:
			case DexModuleListener.DEX_INSTALL_FAIL:
			case DexModuleListener.DEX_UNINSTALL_SUCCESS:
			case DexModuleListener.DEX_UNINSTALL_FAIL:
				loadMyPlugins();
				break;
			}
		}
	};

	private void loadMyPlugins() {
		plugins.clear();

		Plugin plugin = new Plugin("demo", 1, "实验", null, null, null, "试验田",
				"com.lynx.argus.biz.plugin.demo.ShopListFragment", 1);
		plugins.add(plugin);

		if (application.pluginLoaders() == null) {
			return;
		}

		for (String key : application.pluginLoaders().keySet()) {
			plugins.add((Plugin) application.pluginLoaders().get(key)
					.dexModule());
		}
	}
}
