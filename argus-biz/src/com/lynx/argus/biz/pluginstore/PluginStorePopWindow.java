package com.lynx.argus.biz.pluginstore;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.lynx.argus.R;
import com.lynx.argus.biz.pluginstore.model.PluginListAdapter;
import com.lynx.lib.widget.pulltorefresh.PullToRefreshGridView;
import com.lynx.lib.widget.pulltorefresh.PullToRefreshListView;

/**
 * Created with IntelliJ IDEA.
 * User: chris.liu
 * Date: 13-11-12
 * Time: 下午6:17
 */
public class PluginStorePopWindow extends PopupWindow {
    private static final String Tag = "PluginStorePopWindow";

    private Context context;
    private PullToRefreshListView prlvPlugins;
    private PluginListAdapter pluginAdapter;
    private PluginStoreManager pluginStoreManager;


    public PluginStorePopWindow(Context context) {
        super(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        this.context = context;
        this.pluginStoreManager = new PluginStoreManager(context, handler);

        View view = View.inflate(context, R.layout.layout_pluginstore, null);
        this.setContentView(view);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
	    setWidth(dm.widthPixels * 9 / 10);
        setHeight(dm.heightPixels * 2 / 3);

        //想要让PopupWindow中的控件能够使用，就必须设置PopupWindow为focusable
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg));

        prlvPlugins = (PullToRefreshListView) view.findViewById(R.id.prlv_pluginstore);

        Drawable drawable = context.getResources().getDrawable(R.drawable.ptr_refresh);
        prlvPlugins.setLoadingDrawable(drawable);

        pluginAdapter = new PluginListAdapter(context, pluginStoreManager.plugins());
        prlvPlugins.getRefreshableView().setAdapter(pluginAdapter);

        prlvPlugins.setOnRefreshListener(new PullToRefreshGridView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pluginStoreManager.updatePluginStore();
            }
        });

        //设置ListView点击事件
        prlvPlugins.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: add download plugin task
                dismiss();
            }
        });

        ImageButton ibClose = (ImageButton) view.findViewById(R.id.ib_plugin_panel_close);
        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        setAnimationStyle(R.style.AnimationFade);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PluginStoreManager.MSG_UPDATE_FIN:
                    prlvPlugins.onRefreshComplete();
                    pluginAdapter.setData(pluginStoreManager.plugins());
                    break;
                case PluginStoreManager.MSG_DOWNLOAD_FIN:
                    break;
            }
        }
    };
}
