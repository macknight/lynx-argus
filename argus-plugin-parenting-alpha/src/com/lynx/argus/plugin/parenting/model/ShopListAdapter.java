package com.lynx.argus.plugin.parenting.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lynx.argus.plugin.parenting.R;
import com.lynx.lib.misc.AsyncImageLoader;

import java.util.List;

/**
 * 
 * @author chris.liu
 * 
 * @version 14-1-13 上午11:25
 */
public class ShopListAdapter extends BaseAdapter {

	private Context context;
	private List<ShopListItem> data;
	private AsyncImageLoader imgLoader;

	public ShopListAdapter(Context context, List<ShopListItem> data) {
		this.context = context;
		this.data = data;
		this.imgLoader = AsyncImageLoader.instance();
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
			view = View.inflate(context, R.layout.layout_shoplist_item,
					null);
			holder = new ViewHolder();
			holder.ivSnap = (ImageView) view
					.findViewById(R.id.iv_shoplist_item_snap);
			holder.tvName = (TextView) view
					.findViewById(R.id.tv_shoplist_item_title);
			holder.tvShop = (TextView) view
					.findViewById(R.id.tv_shoplist_item_shop);
			holder.tvTime = (TextView) view
					.findViewById(R.id.tv_shoplist_item_time);
			holder.tvReview = (TextView) view
					.findViewById(R.id.tv_shoplist_item_review);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		ShopListItem item = data.get(position);
		holder.tvName.setText(String.format("%s%s", item.storeName(),
				item.shopName()));
		holder.tvShop.setText("" + item.region());
		holder.tvTime.setText(item.latlng().toString());
		holder.tvReview.setText("" + item.reviewNum());

		imgLoader.showAsyncImage(holder.ivSnap, item.snapUrl());
		return view;
	}

	static class ViewHolder {
		TextView tvName;
		TextView tvShop;
		ImageView ivSnap;
		TextView tvTime;
		TextView tvReview;
	}
}
