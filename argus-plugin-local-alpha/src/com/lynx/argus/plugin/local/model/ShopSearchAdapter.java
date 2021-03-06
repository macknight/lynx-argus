package com.lynx.argus.plugin.local.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.lynx.argus.plugin.local.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author zhufeng.liu
 * 
 * @addtime 13-11-6 下午4:11
 */
public class ShopSearchAdapter extends BaseAdapter implements Filterable {
	private ShopFilter filter;
	private List<ShopListItem> data;
	private Context context;
	private List<ShopListItem> unfilteredData;

	public ShopSearchAdapter(Context context, List<ShopListItem> data) {
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
			view = View
					.inflate(context, R.layout.layout_shop_search_item, null);
			holder = new ViewHolder();
			holder.tvName = (TextView) view
					.findViewById(R.id.tv_local_shop_search_item_name);
			holder.tvAddr = (TextView) view
					.findViewById(R.id.tv_local_shop_search_item_addr);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}

		ShopListItem item = data.get(position);
		holder.tvName.setText(item.getName());
		holder.tvAddr.setText(item.getAddr());
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
				unfilteredData = new ArrayList<ShopListItem>(data);
			}

			if (prefix == null || prefix.length() == 0) {
				List<ShopListItem> list = unfilteredData;
				results.values = list;
				results.count = list.size();
			} else {
				String prefixString = prefix.toString().toLowerCase();

				List<ShopListItem> unfilteredValues = unfilteredData;
				int count = unfilteredValues.size();

				ArrayList<ShopListItem> newValues = new ArrayList<ShopListItem>(
						count);

				for (ShopListItem item : unfilteredValues) {
					if (item.getName() != null
							&& item.getName().startsWith(prefixString)) {
						newValues.add(item);
					} else if (item.getAddr() != null
							&& item.getAddr().startsWith(prefixString)) {
						newValues.add(item);
					}
				}

				results.values = newValues;
				results.count = newValues.size();
			}

			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			// noinspection unchecked
			data = (List<ShopListItem>) results.values;
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}
	}
}
