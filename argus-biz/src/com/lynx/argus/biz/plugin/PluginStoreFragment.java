package com.lynx.argus.biz.plugin;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import com.lynx.argus.R;
import com.lynx.argus.app.BizFragment;
import com.lynx.argus.biz.plugin.model.PluginStoreAdapter;
import com.lynx.lib.widget.pulltorefresh.PullToRefreshGridView;
import com.lynx.lib.widget.pulltorefresh.PullToRefreshListView;

/**
 * Created with IntelliJ IDEA.
 * User: chris.liu
 * Date: 13-11-16 下午10:30
 */
public class PluginStoreFragment extends BizFragment {
	private PullToRefreshListView prlvPlugins;
	private PluginStoreAdapter adapter;
	private BizPluginManager pluginManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pluginManager = BizPluginManager.instance();
		pluginManager.addMsgHandler(handler);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_pluginstore, container, false);
		prlvPlugins = (PullToRefreshListView) v.findViewById(R.id.prlv_pluginstore);

		Drawable drawable = getResources().getDrawable(R.drawable.ptr_refresh);
		prlvPlugins.setLoadingDrawable(drawable);

		adapter = new PluginStoreAdapter(tabActivity, pluginManager.pluginsAtStore());
		prlvPlugins.getRefreshableView().setAdapter(adapter);

		prlvPlugins.setOnRefreshListener(new PullToRefreshGridView.OnRefreshListener() {
			@Override
			public void onRefresh() {
				pluginManager.updatePluginStore();
			}
		});

		//设置ListView点击事件
		prlvPlugins.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO: add download plugin task
				ImageView ivOpt = (ImageView) view.findViewById(R.id.iv_pluginstore_item_opt);
				ivOpt.setImageResource(R.drawable.plugin_install);
			}
		});

		prlvPlugins.getRefreshableView().setLongClickable(true);
		prlvPlugins.getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				ImageView ivOpt = (ImageView) view.findViewById(R.id.iv_pluginstore_item_opt);
				ivOpt.setImageResource(R.drawable.plugin_uninstall);
				return false;
			}
		});
		return v;
	}

	private PluginMsgHandler handler = new PluginMsgHandler() {
		@Override
		public boolean interested(int msg) {
			return BizPluginManager.MSG_STORE_UPDATE_FIN == msg ||
					BizPluginManager.MSG_PLUGIN_DOWNLOAD_FIN == msg;
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case BizPluginManager.MSG_STORE_UPDATE_FIN:
					prlvPlugins.onRefreshComplete();
					adapter.setData(pluginManager.pluginsAtStore());
					break;
				case BizPluginManager.MSG_PLUGIN_DOWNLOAD_FIN:
					break;
			}
		}
	};
}
