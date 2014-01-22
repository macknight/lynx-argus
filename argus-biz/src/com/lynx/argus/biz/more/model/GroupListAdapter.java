package com.lynx.argus.biz.more.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.lynx.argus.R;

import java.util.List;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-11-11 下午5:11
 */
public class GroupListAdapter extends BaseAdapter {
	private Context context;
	private List<GroupListItem> data;

	public GroupListAdapter(Context context, List<GroupListItem> data) {
		this.context = context;
		this.data = data;
	}

	public void setData(List<GroupListItem> data) {
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
			holder = new ViewHolder();
			if (data.get(position).isHeader()) {
				view = View.inflate(context, R.layout.layout_more_groupheader,
						null);
			} else {
				view = View.inflate(context, R.layout.layout_more_item, null);
			}
			holder.tvTitle = (TextView) view
					.findViewById(R.id.tv_more_item_title);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}

		holder.tvTitle.setText(data.get(position).title());
		return view;
	}

	private class ViewHolder {
		public TextView tvTitle;
	}
}
