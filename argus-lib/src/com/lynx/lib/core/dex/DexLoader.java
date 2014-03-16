package com.lynx.lib.core.dex;

import android.text.TextUtils;
import com.lynx.lib.core.LFApplication;
import com.lynx.lib.core.Logger;
import com.lynx.lib.db.DBService;
import com.lynx.lib.http.HttpCallback;
import com.lynx.lib.http.HttpService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chris
 * @version 3/15/14 8:30 PM
 */
public class DexLoader {
	private static final String Tag = "DexModuleLoader";

	private static final String PREFIX_PLUGIN = "plugin";
	private static final String PREFIX_SERVICE = "service";

	protected HttpService httpService;

	protected String prefix;
	protected static String basicDir; // /data/data/app.name/files/prefrix/module
	protected String dexDir; // /data/data/app.name/files/prefrix/module/dex
	protected String dexPath; // /data/data/app.name/files/prefrix/module/dex/xxxxxx.dex
	protected String srcDir; // /data/data/app.name/files/prefrix/module/src
	protected String srcPath; // /data/data/app.name/files/prefix/module/src/xxxxxx.apk

	protected DexModule dexModule;
	private DexStatus status; // 加载器当前状态，包括正在安装、更新
	protected List<DexListener> listeners = new ArrayList<DexListener>();
	private DexModule tmpModule;

	private DBService dbService;

	public DexLoader(DexModule dexModule, DexStatus status) {
		this.status = status;
		this.dexModule = dexModule;
		if (dexModule instanceof Plugin) {
			prefix = PREFIX_PLUGIN;
		} else if (dexModule instanceof Service) {
			prefix = PREFIX_SERVICE;
		} else {
			throw new RuntimeException(String.format("cant init dexloader of unknown module[%s]",
					dexModule.getModule()));
		}
		LFApplication application = LFApplication.instance();
		this.httpService = (HttpService) application.service("http");
		this.dbService = application.dbService();

		initConfig(dexModule, false);
		// 当配置的源文件不存在时,下载该文件
		if (!new File(srcPath).exists()) {
			downloadSrcFile(dexModule);
		}
	}

	private HttpCallback<File> httpCallback = new HttpCallback<File>() {
		@Override
		public void onSuccess(File data) {
			super.onSuccess(data);
			// TODO: md5校验
			try {
				// 配置升级
				initConfig(tmpModule, true);
				deleteOldFile();

				switch (status) {
				case INSTALL:
					dispatchChange(DexListener.DEX_INSTALL_SUCCESS);
					break;
				case UPDATE:
					dispatchChange(DexListener.DEX_DOWNLOAD_SUCCESS);
					break;
				}
			} catch (Exception e) {
				Logger.e(dexModule.getModule(), "download dex file error", e);
				switch (status) {
				case INSTALL:
					dispatchChange(DexListener.DEX_INSTALL_FAIL);
					break;
				case UPDATE:
					dispatchChange(DexListener.DEX_DOWNLOAD_FAIL);
					break;
				}
			}
		}

		@Override
		public void onFailure(Throwable t, String strMsg) {
			super.onFailure(t, strMsg);
			switch (status) {
			case INSTALL:
				dispatchChange(DexListener.DEX_INSTALL_FAIL);
				break;
			case UPDATE:
				dispatchChange(DexListener.DEX_DOWNLOAD_FAIL);
				break;
			}
		}
	};

	/**
	 * 读取更新配置，下载模块更新
	 * 
	 * @param newModule
	 */
	public void update(DexModule newModule) {
		if (dexModule.getVersion() >= newModule.getVersion()) {
			return;
		}
		downloadSrcFile(newModule);
	}

	/**
	 * 动态模块路径
	 * 
	 * @return
	 */
	public String moduleDir() {
		return basicDir;
	}

	/**
	 * 动态模块包路径
	 * 
	 * @return
	 */
	public String srcPath() {
		return srcPath;
	}

	/**
	 * 动态包输出路径
	 * 
	 * @return
	 */
	public String dexDir() {
		return dexDir;
	}

	public DexModule dexModule() {
		return dexModule;
	}

	public int version() {
		return dexModule.getVersion();
	}

	/**
	 * 动态模块名
	 * 
	 * @return
	 */
	public String module() {
		return dexModule.getModule();
	}

	/**
	 * 有更新包
	 * 
	 * @param newModule
	 */
	public void hasUpdate(DexModule newModule) {
		if (dexModule.getVersion() >= newModule.getVersion()) {
			return;
		}

		dispatchChange(DexListener.DEX_HAS_UPDATE);
	}

	public void addListener(DexListener listener) {
		listeners.add(listener);
	}

	public void removeListener(DexListener listener) {
		listeners.remove(listener);
	}

	/**
	 * 配置项初始化
	 * 
	 * @param module
	 */
	private boolean initConfig(DexModule module, boolean needUpdate) {
		if (module != null) {
			if (needUpdate) {
				try {
					saveConfig(module);
					dexModule = module;
				} catch (Exception e) {
					Logger.e(module.getModule(), "init config error", e);
				}
			}

			File tmp = new File(LFApplication.instance().getFilesDir(), prefix + File.separator
					+ module.getModule());
			if (!tmp.exists()) {
				tmp.mkdirs();
			}
			basicDir = tmp.getAbsolutePath();

			tmp = new File(basicDir, "src");
			if (!tmp.exists()) {
				tmp.mkdir();
			}
			srcDir = tmp.getAbsolutePath();

			tmp = new File(basicDir, "dex");
			if (!tmp.exists()) {
				tmp.mkdirs();
			}
			dexDir = tmp.getAbsolutePath();

			srcPath = srcDir + File.separator + module.getMd5() + ".apk";
			dexPath = new File(basicDir, "dex").getAbsolutePath() + File.separator
					+ module.getMd5() + ".dex";
		}

		return false;
	}

	/**
	 * 将配置文件保存到本地
	 */
	private void saveConfig(DexModule module) throws Exception {
		dbService.update(module);
	}

	/**
	 * 获取配置描述的动态更新包
	 * 
	 * @param module
	 */
	private void downloadSrcFile(DexModule module) {
		if (module == null || TextUtils.isEmpty(module.getUrl())
				|| TextUtils.isEmpty(module.getMd5())) {
			return;
		}

		tmpModule = module;
		String tmpPath = String.format("%s/%s.apk", srcDir, module.getMd5());
		httpService.download(module.getUrl(), tmpPath, true, httpCallback);
	}

	/**
	 * 清理旧版本动态更新模块文件,包括源文件以及产出dex文件
	 * 
	 * @throws IOException
	 */
	protected void deleteOldFile() throws IOException {
		File srcFile = new File(srcPath);
		if (srcFile.exists()) {
			File[] files = new File(srcDir).listFiles();
			for (File file : files) {
				if (file.getAbsolutePath().equals(srcPath)) {
					continue;
				}
				file.delete();
			}
		}

		File dexFile = new File(dexPath);
		if (dexFile.exists()) {
			File[] files = new File(dexDir).listFiles();
			for (File file : files) {
				if (file.getAbsolutePath().equals(dexPath)) {
					continue;
				}
				file.delete();
			}
		}
	}

	private void dispatchChange(int msg) {
		for (DexListener listener : listeners) {
			listener.onStatusChanged(this, msg);
		}
	}

	/**
	 * 描述loader状态
	 */
	public enum DexStatus {
		LOAD, // 载入已有模块
		INSTALL, // 安装新模块
		UPDATE, // 更新已有模块
	}
}
