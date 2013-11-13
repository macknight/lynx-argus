package com.lynx.argus.biz.local;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.lynx.argus.R;
import com.lynx.argus.biz.local.model.PluginListAdapter;
import com.lynx.argus.biz.local.model.PluginListItem;
import com.lynx.lib.http.core.AsyncTask;
import com.lynx.lib.widget.pulltorefresh.PullToRefreshGridView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chris.liu
 * Date: 13-11-12
 * Time: 下午6:17
 */
public class PluginPanelPopWindow extends PopupWindow {
    private static final String Tag = "PluginPanelPopWindow";

    private static final String PREFIX = "pluginstore";
    private Context context;
    private List<PluginListItem> plugins = new ArrayList<PluginListItem>();
    private PullToRefreshGridView prgvPlugins;
    private PluginListAdapter pluginAdapter;

    public PluginPanelPopWindow(Context context) {
        super(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        this.context = context;
        View view = View.inflate(context, R.layout.layout_plugin_panel, null);
        this.setContentView(view);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        setHeight(dm.heightPixels * 2 / 3);

        //想要让PopupWindow中的控件能够使用，就必须设置PopupWindow为focusable
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg));

        prgvPlugins = (PullToRefreshGridView) view.findViewById(R.id.prgv_plugins);

        Drawable drawable = context.getResources().getDrawable(R.drawable.ptr_refresh);
        prgvPlugins.setLoadingDrawable(drawable);

        pluginAdapter = new PluginListAdapter(context, plugins);
        prgvPlugins.getRefreshableView().setAdapter(pluginAdapter);

        prgvPlugins.setOnRefreshListener(new PullToRefreshGridView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetDataTask().execute();
            }
        });

        //设置ListView点击事件
        prgvPlugins.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        // 显示PopupWindow有3个方法
        // popupWindow.showAsDropDown(anchor)
        // popupWindow.showAsDropDown(anchor, xoff, yoff)
        // popupWindow.showAtLocation(parent, gravity, x, y)
        // 需要注意的是以上三个方法必须在触发事件中使用，比如在点击某个按钮的时候
    }

    /**
     * 加载插件
     */
    private void loadPlugins() {
        File tmp = new File(context.getFilesDir(), PREFIX + "/config");

    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... params) {
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                ;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            prgvPlugins.onRefreshComplete();
        }
    }
}
