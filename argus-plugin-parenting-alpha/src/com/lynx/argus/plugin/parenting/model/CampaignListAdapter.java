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
 * @author zhufeng.liu
 * 
 * @version 13-11-22 下午5:11
 */
public class CampaignListAdapter extends BaseAdapter {
	private Context context;
	private List<CampaignListItem> data;
	private AsyncImageLoader imgLoader;

	public CampaignListAdapter(Context context, List<CampaignListItem> data) {
		this.context = context;
		this.data = data;
		this.imgLoader = AsyncImageLoader.instance();
	}

	public void setData(List<CampaignListItem> data) {
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
		CampaignListItem item = data.get(position);
		if (item == null) {
			return null;
		}

		if (convertView == null) {
			view = View.inflate(context, R.layout.layout_campaignlist_item,
					null);
			holder = new ViewHolder();
			holder.ivSnap = (ImageView) view
					.findViewById(R.id.iv_campaignlist_item_snap);
			holder.tvName = (TextView) view
					.findViewById(R.id.tv_campignlist_item_title);
			holder.tvTime = (TextView) view
					.findViewById(R.id.tv_campignlist_item_time);
			holder.tvPrice = (TextView) view
					.findViewById(R.id.tv_campignlist_item_price);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}

		holder.tvName.setText("" + item.getName());
		holder.tvTime.setText("" + item.getStartTime() + "-"
				+ item.getEndTime());
		holder.tvPrice.setText("" + item.getPrice());

		imgLoader.showAsyncImage(holder.ivSnap, item.getSnapUrl());
		return view;
	}

	static class ViewHolder {
		TextView tvName;
		ImageView ivSnap;
		TextView tvTime;
		TextView tvPrice;
	}
}
