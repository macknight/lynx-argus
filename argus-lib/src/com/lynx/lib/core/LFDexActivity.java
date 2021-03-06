package com.lynx.lib.core;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.lynx.lib.core.dex.DexModule;
import com.lynx.lib.core.dex.PluginLoader;
import dalvik.system.DexClassLoader;

/**
 * 
 * @author zhufeng.liu
 * 
 * @addtime 13-9-30 上午10:10
 */
public class LFDexActivity extends LFNavigationActivity {
	private static final String Tag = "LFDexActivity";

	protected AssetManager dexAssetManager;
	protected Resources dexResources;
	protected Resources.Theme dexTheme;
	protected ClassLoader dexClassLoader;

	private PluginLoader moduleLoader;

	private Resources.Theme defTheme; // 系统原有主题

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		defTheme = this.getTheme();

		loadModule(savedInstanceState);
	}

	protected void loadModule(Bundle savedInstanceState) {
		FrameLayout rootView = new FrameLayout(this);
		rootView.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		rootView.setId(android.R.id.primary);
		setContentView(rootView);

		try {
			AssetManager am = getAssets();
			BitmapDrawable bg = new BitmapDrawable(null, am.open("bg.png"));
			rootView.setBackground(bg);
		} catch (Exception e) {
			Logger.e(Tag, "load background error", e);
		}

		try {
			String module = getIntent().getStringExtra("module");

			moduleLoader = LFApplication.instance().pluginLoader(module);

			if (moduleLoader == null || moduleLoader.dexModule() == null) {
				throw new Exception("not such module exist:" + module);
			}

			DexClassLoader dcl = new DexClassLoader(moduleLoader.srcPath(),
					moduleLoader.dexDir(), null, super.getClassLoader());
			dexClassLoader = dcl;

			AssetManager am = AssetManager.class.newInstance();
			am.getClass().getMethod("addAssetPath", String.class)
					.invoke(am, moduleLoader.srcPath());
			dexAssetManager = am;

			Resources superRes = super.getResources();
			dexResources = new Resources(getAssets(),
					superRes.getDisplayMetrics(), superRes.getConfiguration());

			dexTheme = dexResources.newTheme();
			dexTheme.setTo(super.getTheme());

			super.onCreate(savedInstanceState);

			if (savedInstanceState != null)
				return;

			DexModule dexModule = moduleLoader.dexModule();
			if (dexModule != null && !TextUtils.isEmpty(dexModule.clazz())) {
				LFFragment f = (LFFragment) getClassLoader().loadClass(
						dexModule.clazz()).newInstance();
				pushFragment(f);
			}
		} catch (Throwable e) {
			e.printStackTrace();

			rollback();

			super.onCreate(savedInstanceState);

			LFFragment f = new PluginLoadErrorFragment();
			pushFragment(f);
		}
	}

	private void rollback() {
		dexAssetManager = super.getAssets();
		dexResources = super.getResources();
		dexClassLoader = super.getClassLoader();
		dexTheme = defTheme;
	}

	@Override
	public Resources getResources() {
		return dexResources == null ? super.getResources() : dexResources;
	}

	@Override
	public Resources.Theme getTheme() {
		return dexTheme == null ? super.getTheme() : dexTheme;
	}

	@Override
	public AssetManager getAssets() {
		return dexAssetManager == null ? super.getAssets() : dexAssetManager;
	}

	@Override
	public ClassLoader getClassLoader() {
		return dexClassLoader == null ? super.getClassLoader() : dexClassLoader;
	}
}
