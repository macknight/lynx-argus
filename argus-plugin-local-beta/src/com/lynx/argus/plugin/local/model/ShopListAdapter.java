package com.lynx.argus.plugin.local.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.lynx.argus.plugin.local.R;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chris.liu
 * Date: 13-11-6
 * Time: 下午4:24
 */
public class ShopListAdapter extends BaseAdapter {
	private Context context;
	private List<ShopListItem> data;

	public ShopListAdapter(Context context, List<ShopListItem> data) {
		this.context = context;
		this.data = data;
	}

	public void setData(List<ShopListItem> data) {
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
			view = View.inflate(context, R.layout.layout_local_shoplist_item, null);
			holder = new ViewHolder();
			holder.tvName = (TextView) view.findViewById(R.id.tv_local_shoplist_item_name);
			holder.tvAddr = (TextView) view.findViewById(R.id.tv_local_shoplist_item_addr);
			holder.tvTele = (TextView) view.findViewById(R.id.tv_local_shoplist_item_tele);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}

		ShopListItem item = data.get(position);
		holder.tvName.setText("" + item.getName());
		holder.tvAddr.setText("" + item.getAddr());
		holder.tvTele.setText("" + item.getTele());
		return view;
	}

	static class ViewHolder {
		public TextView tvName;
		public TextView tvAddr;
		public TextView tvTele;
	}
}
