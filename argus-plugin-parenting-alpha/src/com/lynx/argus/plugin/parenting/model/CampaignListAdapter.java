package com.lynx.argus.plugin.parenting.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lynx.argus.plugin.parenting.R;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chris.liu
 * Date: 13-11-22
 * Time: 下午5:11
 */
public class CampaignListAdapter extends BaseAdapter {
	private Context context;
	private List<CampaignListItem> data;

	public CampaignListAdapter(Context context, List<CampaignListItem> data) {
		this.context = context;
		this.data = data;
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
		if (convertView == null) {
			view = View.inflate(context, R.layout.layout_campaignlist_item, null);
			holder = new ViewHolder();
			holder.tvName = (TextView) view.findViewById(R.id.tv_campignlist_item_title);
			holder.tvShop = (TextView) view.findViewById(R.id.tv_campignlist_item_shop);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		CampaignListItem item = data.get(position);
		holder.tvName.setText("" + item.getName());
		holder.tvShop.setText("" + item.getShopName());
		holder.tvTime.setText("" + item.getStartTime() + "-" + item.getEndTime());
		holder.tvPrice.setText("" + item.getPrice());
		return view;
	}

	static class ViewHolder {
		TextView tvName;
		TextView tvShop;
		ImageView ivSnap;
		TextView tvTime;
		TextView tvPrice;
	}
}
