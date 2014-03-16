package com.lynx.lib.core;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import com.lynx.lib.core.dex.DexModule;
import com.lynx.lib.core.dex.PluginLoader;
import com.lynx.lib.util.ImageUtil;
import dalvik.system.DexClassLoader;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-9-30 上午10:10
 */
public class LFDexActivity extends LFNavigationActivity {
	private static final String Tag = "LFDexActivity";

	protected AssetManager dexAssetManager;
	protected Resources dexResources;
	protected Resources.Theme dexTheme;
	protected ClassLoader dexClassLoader;

	private PluginLoader moduleLoader;

	private static Theme defTheme; // 系统原有主题

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		defTheme = this.getTheme();

		loadModule(savedInstanceState);
	}

	protected void loadModule(Bundle savedInstanceState) {
		FrameLayout rootView = new FrameLayout(this);
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		rootView.setId(android.R.id.primary);
		setContentView(rootView);
		rootView.setBackgroundColor(0xffffeed7);

        Drawable drawable = ImageUtil.getImageDrawableFromAssets(this, "bg.9.png");
        rootView.setBackgroundDrawable(drawable);

		try {
			String module = getIntent().getStringExtra("module");

			moduleLoader = LFApplication.instance().pluginLoader(module);

			if (moduleLoader == null || moduleLoader.dexModule() == null) {
				throw new Exception("not such module exist:" + module);
			}

			DexClassLoader dcl = new DexClassLoader(moduleLoader.srcPath(), moduleLoader.dexDir(),
					null, super.getClassLoader());
			dexClassLoader = dcl;

			AssetManager am = AssetManager.class.newInstance();
			am.getClass().getMethod("addAssetPath", String.class)
					.invoke(am, moduleLoader.srcPath());
			dexAssetManager = am;

			Resources superRes = super.getResources();
			dexResources = new Resources(getAssets(), superRes.getDisplayMetrics(),
					superRes.getConfiguration());

			dexTheme = dexResources.newTheme();
			dexTheme.setTo(super.getTheme());

			super.onCreate(savedInstanceState);

			if (savedInstanceState != null)
				return;

			DexModule dexModule = moduleLoader.dexModule();
			if (dexModule != null && !TextUtils.isEmpty(dexModule.getClazz())) {
				ClassLoader classLoader = getClassLoader();
				if (classLoader == null) {
					throw new Exception("load classloader error");
				}
				Class<?> clazz = classLoader.loadClass(dexModule.getClazz());
				LFFragment fragment = (LFFragment) clazz.newInstance();
				if (fragment != null) {
					pushFragment(fragment);
				}
			}
		} catch (Throwable e) {
			Logger.e(Tag, "load dex ui error", e);

			rollback();

			super.onCreate(savedInstanceState);

			LFFragment f = new PluginLoadErrorFragment();
			pushFragment(f);
		}
	}

	@Override
	protected void rollback() {
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
