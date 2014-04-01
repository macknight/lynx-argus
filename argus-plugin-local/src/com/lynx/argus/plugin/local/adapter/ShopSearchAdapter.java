package com.lynx.argus.plugin.local.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.lynx.argus.plugin.local.R;
import com.lynx.argus.plugin.local.model.ShopInfo;

/**
 * 
 * @author zhufeng.liu
 * @version 13-11-6 下午4:11
 */
public class ShopSearchAdapter extends BaseAdapter implements Filterable {
	private ShopFilter filter;
	private List<ShopInfo> data;
	private Context context;
	private List<ShopInfo> unfilteredData;

	public ShopSearchAdapter(Context context, List<ShopInfo> data) {
		this.context = context;
		this.data = data;
	}

	@Override
	public int getCount() {
		return data == null ? 0 : data.size();
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
			view = View.inflate(context, R.layout.layout_shop_search_item, null);
			holder = new ViewHolder();
			holder.tvName = (TextView) view.findViewById(R.id.tv_local_shop_search_item_name);
			holder.tvAddr = (TextView) view.findViewById(R.id.tv_local_shop_search_item_addr);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}

		ShopInfo item = data.get(position);
		holder.tvName.setText(item.name);
		holder.tvAddr.setText(item.address);
		return view;
	}

	static class ViewHolder {
		public TextView tvName;
		public TextView tvAddr;
	}

	@Override
	public Filter getFilter() {
		if (filter == null) {
			filter = new ShopFilter();
		}
		return filter;
	}

	private class ShopFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();

			if (unfilteredData == null) {
				unfilteredData = new ArrayList<ShopInfo>(data);
			}

			if (prefix == null || prefix.length() == 0) {
				List<ShopInfo> list = unfilteredData;
				results.values = list;
				results.count = list.size();
			} else {
				String prefixString = prefix.toString().toLowerCase();

				List<ShopInfo> unfilteredValues = unfilteredData;
				int count = unfilteredValues.size();

				ArrayList<ShopInfo> newValues = new ArrayList<ShopInfo>(count);

				for (ShopInfo item : unfilteredValues) {
					if (item.name != null && item.name.startsWith(prefixString)) {
						newValues.add(item);
					} else if (item.address != null && item.address.startsWith(prefixString)) {
						newValues.add(item);
					}
				}

				results.values = newValues;
				results.count = newValues.size();
			}

			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			// noinspection unchecked
			data = (List<ShopInfo>) results.values;
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}
	}
}
