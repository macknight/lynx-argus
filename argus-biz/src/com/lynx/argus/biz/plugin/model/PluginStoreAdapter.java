package com.lynx.argus.biz.plugin.model;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lynx.argus.R;
import com.lynx.argus.app.BizApplication;
import com.lynx.lib.core.LFApplication;
import com.lynx.lib.core.dex.Plugin;
import com.lynx.lib.misc.AsyncImageLoader;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-11-12 下午6:27
 */
public class PluginStoreAdapter extends BaseAdapter {
	private Context context;
	private List<Plugin> data;
	private AsyncImageLoader imgLoader;
	private LFApplication application;

	public PluginStoreAdapter(Context context, List<Plugin> data) {
		this.context = context;
		this.data = data;
		this.imgLoader = AsyncImageLoader.instance();
		this.application = BizApplication.instance();
	}

	public void setData(List<Plugin> data) {
		this.data = data;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Plugin getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder holder;
		if (convertView == null) {
			view = View.inflate(context, R.layout.layout_pluginstore_item, null);
			holder = new ViewHolder();
			holder.tvName = (TextView) view.findViewById(R.id.tv_pluginstore_item_name);
			holder.tvDesc = (TextView) view.findViewById(R.id.tv_pluginstore_item_desc);
			holder.ivIcon = (ImageView) view.findViewById(R.id.iv_pluginstore_item_icon);
			holder.ivStatus = (ImageView) view.findViewById(R.id.iv_pluginstore_item_status);
			holder.btnInstall = (Button) view.findViewById(R.id.btn_pluginstore_install);
			holder.btnUninstall = (Button) view.findViewById(R.id.btn_pluginstore_uninstall);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}

		Plugin item = data.get(position);
		holder.tvName.setText(item.getName());
		holder.tvDesc.setText(item.getDesc());
		if (application.hasPlugin(item.getModule())) {
			holder.ivStatus.setImageResource(R.drawable.plugin_installed);
			holder.btnInstall.setEnabled(false);
			holder.btnUninstall.setEnabled(true);
		} else {
			holder.ivStatus.setImageResource(R.drawable.plugin_install);
			holder.btnInstall.setEnabled(true);
			holder.btnUninstall.setEnabled(false);
		}
		imgLoader.showAsyncImage(holder.ivIcon, item.getIcon());
		return view;
	}

	static class ViewHolder {
		TextView tvName;
		TextView tvDesc;
		ImageView ivIcon;
		ImageView ivStatus;
		Button btnInstall, btnUninstall;
	}
}
