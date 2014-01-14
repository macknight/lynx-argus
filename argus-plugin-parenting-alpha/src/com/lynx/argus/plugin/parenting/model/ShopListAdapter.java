package com.lynx.argus.plugin.parenting.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lynx.argus.plugin.parenting.R;
import com.lynx.lib.util.AsyncImageLoader;

import java.util.List;

/**
 * @author: chris.liu
 * @addtime: 14-1-13 上午11:25
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
            view = View.inflate(context, R.layout.layout_campaignlist_item,
                    null);
            holder = new ViewHolder();
            holder.ivSnap = (ImageView) view
                    .findViewById(R.id.iv_campaignlist_item_snap);
            holder.tvName = (TextView) view
                    .findViewById(R.id.tv_campignlist_item_title);
            holder.tvShop = (TextView) view
                    .findViewById(R.id.tv_campignlist_item_shop);
            holder.tvTime = (TextView) view
                    .findViewById(R.id.tv_campignlist_item_time);
            holder.tvPrice = (TextView) view
                    .findViewById(R.id.tv_campignlist_item_price);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        ShopListItem item = data.get(position);
        holder.tvName.setText(String.format("%s%s", item.storeName() ,item.shopName()));
        holder.tvShop.setText("" + item.region());
        holder.tvTime.setText(item.latlng().toString());
        holder.tvPrice.setText("" + item.reviewNum());

        imgLoader.showAsyncImage(holder.ivSnap, item.snapUrl(),
                R.drawable.gallery_view);
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
