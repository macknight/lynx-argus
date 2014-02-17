package com.lynx.argus.plugin.parenting.model;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.lynx.argus.plugin.parenting.ParentingFragment;
import com.lynx.argus.plugin.parenting.R;
import com.lynx.lib.misc.AsyncImageLoader;

/**
 * @author zhufeng.liu
 * @version 14-2-13 下午5:08
 */
public class HotShopListAdapter extends BaseAdapter implements ListAdapter {

	private Context context;
	private List<ShopInfo> data;
	private DisplayMetrics displayMetrics;
	private AsyncImageLoader imgLoader;

	public HotShopListAdapter(Activity context, List<ShopInfo> data) {
		this.context = context;
		this.data = data;
		this.imgLoader = AsyncImageLoader.instance();

		displayMetrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
	}

	public void setData(List<ShopInfo> data) {
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
			view = View.inflate(context, R.layout.layout_shoplist_item, null);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT);
			params.width = displayMetrics.widthPixels;
			view.setLayoutParams(params);

			holder = new ViewHolder();
			holder.ivSnap = (ImageView) view.findViewById(R.id.iv_shoplist_item_snap);
			holder.tvName = (TextView) view.findViewById(R.id.tv_shoplist_item_name);
			holder.tvShop = (TextView) view.findViewById(R.id.tv_shoplist_item_shop);
			holder.tvTime = (TextView) view.findViewById(R.id.tv_shoplist_item_time);
			holder.tvReview = (TextView) view.findViewById(R.id.tv_shoplist_item_review);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		ShopInfo item = data.get(position);
		holder.tvName.setText(String.format("%s%s", item.getStoreName(), item.getShopName()));
		holder.tvShop.setText("" + item.getRegion());
		holder.tvTime.setText(item.getLatlng().toString());
		holder.tvReview.setText("" + item.getReviewNum());

		imgLoader.showAsyncImage(holder.ivSnap,
				String.format("%s/%s", ParentingFragment.LM_API_PARENT_DOMAIN, item.getSnapUrl()));
		return view;
	}

	class ViewHolder {
		TextView tvName;
		TextView tvShop;
		ImageView ivSnap;
		TextView tvTime;
		TextView tvReview;
	}
}
