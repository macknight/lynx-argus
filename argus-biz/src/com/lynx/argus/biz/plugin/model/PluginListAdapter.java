package com.lynx.argus.biz.plugin.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lynx.argus.R;
import com.lynx.argus.util.AsyncImageLoader;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chris.liu
 * Date: 13-11-12
 * Time: 下午6:27
 */
public class PluginListAdapter extends BaseAdapter {
    private Context context;
    private List<PluginItem> data;
    AsyncImageLoader imgLoader;

    public PluginListAdapter(Context context, List<PluginItem> data) {
        this.context = context;
        this.data = data;

        this.imgLoader = new AsyncImageLoader(context);
    }

    public void setData(List<PluginItem> data) {
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
            view = View.inflate(context, R.layout.layout_pluginstore_item, null);
            holder = new ViewHolder();
            holder.tvName = (TextView) view.findViewById(R.id.tv_pluginstore_item_name);
            holder.tvDesc = (TextView) view.findViewById(R.id.tv_pluginstore_item_desc);
            holder.ivIcon = (ImageView) view.findViewById(R.id.iv_pluginstore_item_icon);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        PluginItem item = data.get(position);
        holder.tvName.setText(item.getName());
        holder.tvDesc.setText(item.getDesc());

        imgLoader.showAsyncImage(holder.ivIcon, item.getIcon(), R.drawable.plugin_def);
        return view;
    }

    static class ViewHolder {
        public TextView tvName;
        public TextView tvDesc;
        public ImageView ivIcon;
    }
}
