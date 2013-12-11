package com.lynx.argus.plugin.chat.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lynx.argus.plugin.chat.R;

import java.util.List;

/**
 * 
 * @author zhufeng.liu
 * 
 * @addtime 13-12-8 下午7:43
 */
public class ChatAdapter extends BaseAdapter {
	private Context context;
	private List<ChatListItem> data;

	public ChatAdapter(Context context, List<ChatListItem> data) {
		this.context = context;
		this.data = data;
	}

	@Override
	public int getCount() {
		return data == null ? 0 : data.size();
	}

	@Override
	public Object getItem(int i) {
		return data == null ? null : data.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder holder;
		ChatListItem item = data.get(position);
		if (convertView == null) {
			view = View.inflate(context, R.layout.layout_chatlist_item, null);
			holder = new ViewHolder();
			holder.ivAvatar = (ImageView) view
					.findViewById(R.id.iv_chatlist_item_avatar);
			holder.tvFrom = (TextView) view
					.findViewById(R.id.tv_chatlist_item_from);
			holder.tvSummary = (TextView) view
					.findViewById(R.id.tv_chatList_item_summary);
			holder.tvDate = (TextView) view
					.findViewById(R.id.tv_chatlist_item_date);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}

		holder.tvFrom.setText("" + item.from());
		holder.tvSummary.setText("" + item.summary());
		holder.tvDate.setText("" + item.date());
		return view;
	}

	class ViewHolder {
		ImageView ivAvatar;
		TextView tvFrom;
		TextView tvSummary;
		TextView tvDate;
	}
}
