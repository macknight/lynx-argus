package com.lynx.lib.core.dex;

import android.content.Context;
import com.lynx.lib.core.LFApplication;
import com.lynx.lib.core.Logger;
import com.lynx.lib.http.HttpCallback;
import com.lynx.lib.http.HttpService;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 13-10-29 11:29 AM
 */
public class DexModuleLoader {
	protected HttpService httpService;
	protected Context context;
	protected String basicDir; // /data/data/app.name/files/prefrix/module
	protected String dexDir; // /data/data/app.name/files/prefrix/module/dex
	protected String dexPath; // /data/data/app.name/files/prefrix/module/dex/xxxxxx.dex
	protected String srcDir; // /data/data/app.name/files/prefrix/module/src
	protected String srcPath; // /data/data/app.name/files/prefix/module/src/xxxxxx.apk

	private DexType type;
	protected DexModule dexModule;
	protected DexModuleListener listener;
	private DexModule tmpModule;

	public DexModuleLoader(DexType type, DexModule newModule, DexModuleListener listener) {
		this.type = type;
		this.listener = listener;
		this.context = LFApplication.instance();
		this.httpService = (HttpService) LFApplication.instance().service("http");

		File tmp = new File(context.getFilesDir(),
				type.type() + File.separator + newModule.module());
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

		loadLocalConfig();
		initConfig(newModule);
		// 当配置的源文件不存在时,下载该文件
		if (!new File(srcPath).exists()) {
			downloadSrcFile(dexModule);
		}

		try {
			deleteOldFile();
		} catch (Exception e) {
			Logger.e(dexModule.module(), "unable to delete old module file" + e.getMessage());
		}
	}

	private HttpCallback<File> httpCallback = new HttpCallback<File>() {
		@Override
		public void onSuccess(File data) {
			super.onSuccess(data);
			// TODO: md5校验
			try {
				// 配置升级
				initConfig(tmpModule);
				deleteOldFile();
			} catch (Exception e) {
				return;
			}

			if (listener != null) {
				Logger.w("chris", "download fin");
				listener.onStatusChanged(DexModuleListener.DEX_DOWNLOAD_SUCCESS);
			}
		}

		@Override
		public void onFailure(Throwable t, String strMsg) {
			super.onFailure(t, strMsg);
			if (listener != null) {
				listener.onStatusChanged(DexModuleListener.DEX_DOWNLOAD_FAIL);
			}
		}
	};

	/**
	 * 读取更新配置，下载模块更新
	 *
	 * @param newModule
	 */
	public void update(DexModule newModule) {
		if (dexModule.version() >= newModule.version()) {
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

	public String module() {
		return dexModule.module();
	}

	/**
	 * 有更新包
	 *
	 * @param newModule
	 */
	public void hasUpdate(DexModule newModule) {
		if (dexModule.version() >= newModule.version()) {
			return;
		}
		if (listener != null) {
			listener.onStatusChanged(DexModuleListener.DEX_HAS_UPDATE);
		}
	}

	public void setListener(DexModuleListener listener) {
		this.listener = listener;
	}

	/**
	 * 读取本地配置文件
	 */
	private void loadLocalConfig() {
		File path = new File(basicDir + "/config");
		if (path.length() == 0)
			return;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(path);
			byte[] bytes = new byte[fis.available()];
			fis.read(bytes);
			fis.close();
			String str = new String(bytes, "UTF-8");
			JSONObject json = new JSONObject(str);
			dexModule = DexUtil.json2dexModule(type, json);
		} catch (Exception e) {
			Logger.w("dexmodule", "no local config found");
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {

				}
			}
		}

	}

	/**
	 * 配置项初始化
	 *
	 * @param module
	 */
	private void initConfig(DexModule module) {
		if (dexModule == null || module.version() > dexModule.version()) {
			try {
				saveConfig(module);
				dexModule = module;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		srcPath = srcDir + File.separator + dexModule.md5() + ".apk";
		dexPath = new File(basicDir, "dex").getAbsolutePath() + File.separator + dexModule.md5() + ".dex";
	}

	/**
	 * 将配置文件保存到本地
	 *
	 * @param module
	 */
	private void saveConfig(DexModule module) throws IOException {
		File config = new File(basicDir, "config");
		File configTmp = new File(basicDir, "config_tmp");
		File configOld = new File(basicDir, "config_old");
		FileOutputStream fos = null;

		if (configOld.exists()) {
			configOld.delete();
		}
		if (config.exists()) {
			config.renameTo(configOld);
		}

		try {
			byte[] bytes = DexUtil.dexModule2json(type, module).toString().getBytes("UTF-8");
			fos = new FileOutputStream(configTmp);
			fos.write(bytes);
			fos.close();
			fos = null;
			config.delete();
			if (!configTmp.renameTo(config)) {
				// revert to old config file
				if (config.exists()) {
					config.delete();
				}
				configOld.renameTo(config);
				throw new Exception("unable to move config from " +
						configTmp + " to " + config);
			}
		} catch (Exception e) {
			config.delete();
			configOld.renameTo(config);
			throw new IOException(e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {

				}
			}
			configTmp.delete();
			configOld.delete();
		}
	}

	/**
	 * 获取配置描述的动态更新包
	 *
	 * @param module
	 */
	private void downloadSrcFile(DexModule module) {
		if (module == null || module.url() == null || module.md5() == null) {
			return;
		}

		tmpModule = module;
		String tmpPath = String.format("%s/%s.apk", srcDir, module.md5());

		httpService.download(module.url(), tmpPath, true, httpCallback);
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

	public enum DexType {
		PLUGIN("plugin"),
		SERVICE("service");

		private String type;

		DexType(String type) {
			this.type = type;
		}

		public String type() {
			return type;
		}
	}

}
