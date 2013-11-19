package com.lynx.argus.biz.plugin;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.lynx.argus.R;
import com.lynx.argus.app.BizFragment;
import com.lynx.argus.biz.plugin.model.PluginStoreAdapter;
import com.lynx.argus.biz.widget.listview.ActionSlideExpandableListView;
import com.lynx.argus.biz.widget.listview.ActionSlideExpandableListView.OnActionClickListener;
import com.lynx.argus.biz.widget.listview.P2RASEListView;
import com.lynx.lib.core.dex.Plugin;
import com.lynx.lib.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener;

/**
 * Created with IntelliJ IDEA.
 * User: chris.liu
 * Date: 13-11-16 下午10:30
 */
public class PluginStoreFragment extends BizFragment {
    private P2RASEListView p2raselv;
    private PluginStoreAdapter adapter;
    private BizPluginManager pluginManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pluginManager = BizPluginManager.instance();
        pluginManager.addMsgHandler(handler);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_pluginstore, container, false);

        p2raselv = (P2RASEListView) v.findViewById(R.id.p2raselv_pluginstore);

        Drawable drawable = getResources().getDrawable(R.drawable.ptr_refresh);
        p2raselv.setLoadingDrawable(drawable);

        ActionSlideExpandableListView aselv = (ActionSlideExpandableListView) p2raselv.getRefreshableView();
        adapter = new PluginStoreAdapter(tabActivity, pluginManager.pluginsAtStore());
        aselv.setAdapter(adapter);

        p2raselv.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                pluginManager.updatePluginStore();
            }
        });

        aselv.setItemActionListener(new OnActionClickListener() {
            @Override
            public void onClick(View parent, View view, int position) {
                Plugin plugin = adapter.getItem(position);
                switch (view.getId()) {
                    case R.id.btn_pluginstore_install:
                        pluginManager.installPlugin(plugin);
                        break;
                    case R.id.btn_pluginstore_uninstall:
                        pluginManager.uninstallPlugin(plugin);
                        break;
                }
            }
        }, R.id.btn_pluginstore_install, R.id.btn_pluginstore_uninstall);

        ImageButton ibDownload = (ImageButton) v.findViewById(R.id.ib_pluginstore_download);
        ibDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadCenterFragment downloadFragment = new DownloadCenterFragment();
                tabActivity.pushFragment(PluginCenterFragment.Tag, downloadFragment, true, true);
            }
        });

        ImageButton ibBack = (ImageButton)v.findViewById(R.id.ib_pluginstore_back);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabActivity.onBackPressed();
            }
        });

        return v;
    }

    private PluginMsgHandler handler = new PluginMsgHandler() {
        @Override
        public boolean interested(int msg) {
            return BizPluginManager.MSG_STORE_UPDATE_FIN == msg ||
                    BizPluginManager.MSG_PLUGIN_INSTALL_FIN == msg ||
                    BizPluginManager.MSG_PLUGIN_UNINSTALL_FIN == msg;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BizPluginManager.MSG_STORE_UPDATE_FIN:
                    p2raselv.onRefreshComplete();
                    adapter.setData(pluginManager.pluginsAtStore());
                    break;
                case BizPluginManager.MSG_PLUGIN_INSTALL_FIN:
                case BizPluginManager.MSG_PLUGIN_UNINSTALL_FIN:
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };
}
