package com.lynx.argus.plugin.local.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lynx.argus.plugin.local.R;
import com.lynx.argus.plugin.local.adapter.ShopListAdapter.ViewHolder;
import com.lynx.argus.plugin.local.model.ShopInfo;
import com.lynx.argus.plugin.local.model.TuanEvent;

import java.util.List;

/**
 * @author chris.liu
 * @version 4/3/14 11:48 AM
 */
public class ShopTuanListAdapter extends BaseAdapter {
	private Context context;
	private List<TuanEvent> data;

	public ShopTuanListAdapter(Context context, List<TuanEvent> data) {
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
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		View view;
		ViewHolder holder;
		if (convertView == null) {
			view = View.inflate(context, R.layout.layout_shop_tuanlist_item, null);
			holder = new ViewHolder();
			holder.tvName = (TextView) view.findViewById(R.id.tv_shoplist_item_name);
			holder.tvStartTime = (TextView) view.findViewById(R.id.tv_shoplist_item_addr);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		TuanEvent item = data.get(position);
		holder.tvName.setText("" + item.name);
        holder.tvRegPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		holder.tvRegPrice.setText("" + item.regularPrice);
		return view;
	}

	private class ViewHolder {
		TextView tvName;
		ImageView ivSnap;
		TextView tvDesc;
		TextView tvPrice;
		TextView tvRegPrice;
		TextView tvRebate;
		TextView tvStartTime;
		TextView tvEndTime;
	}
}
