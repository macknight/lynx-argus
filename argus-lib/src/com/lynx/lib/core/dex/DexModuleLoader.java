package com.lynx.lib.core.dex;

import android.content.Context;
import android.widget.Toast;
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
	protected DexModule dexModule;
	private DexModule tmpModule;
	private DexType type;


	public DexModuleLoader(DexType type, DexModule newModule) {
		this.type = type;
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

		try {
			loadLocalConfig();
			initConfig(newModule);
			// 当配置的源文件不存在时,下载该文件
			if (!new File(srcPath).exists()) {
				downloadSrcFile(this.dexModule);
			}
		} catch (Exception e) {
			Logger.e(dexModule.module(), "unable to read config at " + basicDir + "/config:" + e.getMessage());
		}

		try {
			deleteOldFile();
		} catch (Exception e) {
			Logger.e(dexModule.module(), "unable to delete old module file" + e.getMessage());
		}
	}

	/**
	 * 读取更新配置，下载模块更新
	 *
	 * @param module
	 */
	public void update(DexModule module) {
		if (dexModule.version() >= module.version()) {
			return;
		}
		downloadSrcFile(module);
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
	 * 读取本地配置文件
	 *
	 * @return
	 * @throws Exception
	 */
	private void loadLocalConfig() throws Exception {
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
		if (module.version() < dexModule.version()) {
			return;
		}
		dexModule = module;
		srcPath = new File(basicDir, "src").getAbsolutePath() + File.separator + dexModule.md5() + ".apk";
		dexPath = new File(basicDir, "dex").getAbsolutePath() + File.separator + dexModule.md5() + ".dex";
	}

	/**
	 * 将配置文件保存到本地
	 *
	 * @param module
	 * @throws Exception
	 */
	private void saveConfig(DexModule module) throws Exception {
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
				throw new Exception("unable to move config from " + configTmp
						+ " to " + config);
			}
		} catch (Exception e) {
			config.delete();
			configOld.renameTo(config);
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
		tmpModule = module;
		String tmpPath = String.format("%s/%s.apk", srcDir, module.md5());
		httpService.download(module.url(), tmpPath, true,
				new HttpCallback<File>() {
					@Override
					public void onStart() {
						super.onStart();
						Toast.makeText(context, "开始下载动态更新包", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(File file) {
						super.onSuccess(file);
						// TODO: md5校验
						Toast.makeText(context, "完成动态更新包下载", Toast.LENGTH_SHORT).show();

						try {
							// 配置升级
							saveConfig(tmpModule);
							initConfig(tmpModule);
						} catch (Exception e) {
							return;
						}
					}

					@Override
					public void onFailure(Throwable t, String strMsg) {
						super.onFailure(t, strMsg);
						Toast.makeText(context, strMsg, Toast.LENGTH_SHORT).show();
					}
				});
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
