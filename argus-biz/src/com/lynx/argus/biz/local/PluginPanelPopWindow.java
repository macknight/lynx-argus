package com.lynx.argus.biz.local;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import com.lynx.argus.R;
import com.lynx.argus.biz.local.model.PluginListAdapter;
import com.lynx.argus.biz.local.model.PluginListItem;
import com.lynx.lib.http.core.AsyncTask;
import com.lynx.lib.widget.pulltorefresh.PullToRefreshGridView;

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
	private List<PluginListItem> plugins = new ArrayList<PluginListItem>();
	private PullToRefreshGridView prgvPlugin;
	private PluginListAdapter pluginAdapter;

	public PluginPanelPopWindow() {
		super();
		pluginAdapter = new PluginListAdapter(getContentView().getContext(), plugins);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_plugin_panel, container, false);
		prgvPlugin = (PullToRefreshGridView) v.findViewById(R.id.prgv_local_idx);
		prgvPlugin.getRefreshableView().setAdapter(pluginAdapter);
		return v;
	}

	public boolean onBackPressed() {
		return false;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {
		@Override
		protected String[] doInBackground(Void... params) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				;
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			prgvPlugin.onRefreshComplete();
		}
	}
}
