package com.lynx.argus.biz.plugin.model;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lynx.argus.R;
import com.lynx.lib.core.dex.Plugin;
import com.lynx.lib.misc.AsyncImageLoader;

/**
 * 
 * @author zhufeng.liu
 * @version 13-11-16 下午5:19
 */
public class PluginCenterAdapter extends BaseAdapter {
	private Context context;
	private List<Plugin> plugins;
	private AsyncImageLoader imgLoader;

	public PluginCenterAdapter(Context context, List<Plugin> plugins) {
		this.context = context;
		this.plugins = plugins;

		this.imgLoader = AsyncImageLoader.instance();
	}

	public void addPlugin(Plugin plugin) {
		plugins.add(plugin);
		notifyDataSetChanged();
	}

	public void setPlugins(List<Plugin> plugins) {
		this.plugins = plugins;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return plugins.size();
	}

	@Override
	public Object getItem(int position) {
		return plugins.get(position);
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
			view = View.inflate(context, R.layout.layout_plugincenter_item, null);
			holder = new ViewHolder();
			holder.tvName = (TextView) view.findViewById(R.id.tv_plugincenter_item_name);
			holder.ivIcon = (ImageView) view.findViewById(R.id.iv_plugincenter_item_icon);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}

		Plugin item = plugins.get(position);
		holder.tvName.setText(item.getName());
		imgLoader.showAsyncImage(holder.ivIcon, item.getIcon());
		return view;
	}

	static class ViewHolder {
		public TextView tvName;
		public ImageView ivIcon;
	}
}
