package com.lynx.argus.biz.plugin;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.lynx.argus.R;
import com.lynx.argus.biz.plugin.model.PluginStoreAdapter;
import com.lynx.argus.biz.widget.listview.ActionSlideExpandableListView;
import com.lynx.argus.biz.widget.listview.ActionSlideExpandableListView.OnActionClickListener;
import com.lynx.argus.biz.widget.listview.P2RASEListView;
import com.lynx.lib.core.LFFragment;
import com.lynx.lib.core.dex.DexModuleListener;
import com.lynx.lib.core.dex.Plugin;
import com.lynx.lib.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-11-16 下午10:30
 */
public class PluginStoreFragment extends LFFragment {
	private P2RASEListView p2raselv;
	private PluginStoreAdapter adapter;
	private BizPluginManager pluginManager;
	private InstallPopWindow optPopWindow;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		pluginManager = BizPluginManager.instance();
		pluginManager.addMsgHandler(handler);

		optPopWindow = new InstallPopWindow();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) throws Exception {
		View view = inflater.inflate(R.layout.layout_pluginstore, container,
				false);
		if (view == null) {
			throw new Exception("页面初始化错误");
		}

		p2raselv = (P2RASEListView) view
				.findViewById(R.id.p2raselv_pluginstore);

		Drawable drawable = getResources().getDrawable(R.drawable.ptr_refresh);
		p2raselv.setLoadingDrawable(drawable);

		ActionSlideExpandableListView aselv = (ActionSlideExpandableListView) p2raselv
				.getRefreshableView();
		adapter = new PluginStoreAdapter(tabActivity,
				pluginManager.pluginsAtStore());
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
					optPopWindow.show(view);
					break;
				}
				optPopWindow.show(parent);
			}
		}, R.id.btn_pluginstore_install, R.id.btn_pluginstore_uninstall);

		ImageButton ibDownload = (ImageButton) view
				.findViewById(R.id.ib_pluginstore_download);
		ibDownload.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DownloadCenterFragment downloadFragment = new DownloadCenterFragment();
				tabActivity.pushFragment(downloadFragment);
			}
		});

		ImageButton ibBack = (ImageButton) view
				.findViewById(R.id.ib_pluginstore_back);
		ibBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tabActivity.onBackPressed();
			}
		});

		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		pluginManager.removeMsgHandler(handler);
	}

	private PluginMsgHandler handler = new PluginMsgHandler() {
		@Override
		public boolean interested(int msg) {
			return BizPluginManager.MSG_STORE_UPDATE_FIN == msg
					|| DexModuleListener.DEX_DOWNLOAD_SUCCESS == msg
					|| DexModuleListener.DEX_DOWNLOAD_FAIL == msg
					|| DexModuleListener.DEX_INSTALL_SUCCESS == msg
					|| DexModuleListener.DEX_INSTALL_FAIL == msg
					|| DexModuleListener.DEX_UNINSTALL_SUCCESS == msg
					|| DexModuleListener.DEX_UNINSTALL_FAIL == msg;
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BizPluginManager.MSG_STORE_UPDATE_FIN:
				p2raselv.onRefreshComplete();
				adapter.setData(pluginManager.pluginsAtStore());
				break;
			case DexModuleListener.DEX_DOWNLOAD_SUCCESS:
			case DexModuleListener.DEX_INSTALL_SUCCESS:
			case DexModuleListener.DEX_UNINSTALL_SUCCESS:
				optPopWindow.onSuccess();
				adapter.notifyDataSetChanged();
				break;
			case DexModuleListener.DEX_INSTALL_FAIL:

			case DexModuleListener.DEX_DOWNLOAD_FAIL:

			case DexModuleListener.DEX_UNINSTALL_FAIL:
				optPopWindow.onFail();
				adapter.notifyDataSetChanged();
				break;
			}
		}
	};

	private class InstallPopWindow extends PopupWindow {
		private Animation animInstalling;
		private ImageView ivOpt;

		public InstallPopWindow() {
			super(LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);

			View view = View.inflate(tabActivity,
					R.layout.layout_plugin_install, null);
			this.setContentView(view);

			ivOpt = (ImageView) view.findViewById(R.id.iv_pluginstore_opt);

			DisplayMetrics dm = tabActivity.getResources().getDisplayMetrics();
			setWidth(dm.widthPixels * 9 / 10);
			setHeight(dm.heightPixels * 1 / 3);

			setFocusable(true);
			setOutsideTouchable(false);
			setBackgroundDrawable(getResources().getDrawable(
					R.drawable.shape_round_bg));

			animInstalling = new RotateAnimation(-80, 0,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			LinearInterpolator lin = new LinearInterpolator();
			animInstalling.setInterpolator(lin);
			animInstalling.setDuration(500);
			animInstalling.setRepeatCount(Animation.INFINITE);
			animInstalling.setRepeatMode(Animation.REVERSE);

			setAnimationStyle(R.style.AnimationFade);
		}

		@Override
		public void dismiss() {
			ivOpt.clearAnimation();
			super.dismiss();
		}

		public void onSuccess() {
			ivOpt.clearAnimation();
			ivOpt.setImageResource(R.drawable.plugin_opt_success);
		}

		public void onFail() {
			ivOpt.clearAnimation();
			ivOpt.setImageResource(R.drawable.plugin_opt_fail);
		}

		public void show(View parent) {
			ivOpt.setImageResource(R.drawable.plugin_opt_ongoing);
			ivOpt.startAnimation(animInstalling);
			showAtLocation(parent, Gravity.CENTER, 0, 0);
		}
	}
}
