package com.lynx.argus.biz.pluginstore.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lynx.argus.R;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chris.liu
 * Date: 13-11-12
 * Time: 下午6:27
 */
public class PluginListAdapter extends BaseAdapter {
	private Context context;
	private List<PluginItem> data;

	public PluginListAdapter(Context context, List<PluginItem> data) {
		this.context = context;
		this.data = data;
	}

	public void setData(List<PluginItem> data) {
		this.data = data;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
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
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}

		PluginItem item = data.get(position);
		holder.tvName.setText("" + item.getName());
		holder.tvDesc.setText("" + item.getDesc());
//		holder.ivIcon.setImageBitmap(null);
		return view;
	}

	static class ViewHolder {
		public TextView tvName;
		public TextView tvDesc;
		public ImageView ivIcon;
	}
}
