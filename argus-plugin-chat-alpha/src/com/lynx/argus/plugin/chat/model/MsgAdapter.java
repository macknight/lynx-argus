package com.lynx.argus.plugin.chat.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.lynx.argus.plugin.chat.R;

import java.util.List;

/**
 * Created by chris.liu
 * Time 13-12-7 下午8:06
 */
public class MsgAdapter extends BaseAdapter {
	private Context context;
	private List<Msg> data;

	public MsgAdapter(Context context, List<Msg> data) {
		this.context = context;
		this.data = data;
	}

	@Override
	public int getCount() {
		return data == null ? 0 : data.size();
	}

	@Override
	public Object getItem(int position) {
		return data == null ? null : data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder holder;
		Msg msg = data.get(position);
		if (convertView == null) {
			if (msg.from()) {
				view = View.inflate(context, R.layout.layout_msg_item_from, null);
			} else {
				view = View.inflate(context, R.layout.layout_msg_item_to, null);
			}
			holder = new ViewHolder();
			holder.tvContent = (TextView) view.findViewById(R.id.tv_msg_item_content);
			holder.tvDate = (TextView) view.findViewById(R.id.tv_msg_item_date);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}

		holder.tvContent.setText("" + msg.content());
		holder.tvDate.setText("" + msg.date());
		return view;
	}

	class ViewHolder {
		TextView tvContent;
		TextView tvDate;
	}
}