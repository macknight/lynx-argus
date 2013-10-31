package com.lynx.lib.core.dex;

import android.content.Context;
import android.widget.Toast;
import com.lynx.lib.core.LFApplication;
import com.lynx.lib.core.Logger;
import com.lynx.lib.http.HttpService;
import com.lynx.lib.http.handler.HttpCallback;
import org.json.JSONException;
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
	public static final String K_MODULE = "module";
	public static final String K_CLAZZ = "clazz"; // 入口类名
	public static final String K_VERSION = "version"; // 版本
	public static final String K_URL = "url"; // module包下载地址
	public static final String K_MD5 = "md5"; // module包md5摘要
	public static final String K_DESC = "desc"; // module描述

	protected HttpService httpService;
	protected Context context;
	protected String basicDir; // /data/data/app.name/files/prefrix/module
	protected String dexDir; // /data/data/app.name/files/prefrix/module/dex
	protected String dexPath; // /data/data/app.name/files/prefrix/module/dex/xxxxxx.dex
	protected String srcDir; // /data/data/app.name/files/prefrix/module/src
	protected String srcPath; // /data/data/app.name/files/prefix/module/src/xxxxxx

	private String tmpPath;
	private JSONObject tmpConfig;

	protected String moduleName; // 模块名
	protected String clazzName = null; // 入口类名
	protected int version = -1; // 版本号
	protected String md5 = null; // md5摘要
	protected String desc; // 动态模块描述

	/**
	 * @param context
	 * @param moduleName 动态服务标签
	 * @param minVersion 最小动态服务包版本
	 */
	public DexModuleLoader(Context context, String prefix, String moduleName, int minVersion) {
		this.context = context;
		this.moduleName = moduleName;
		this.version = minVersion;
		this.httpService = (HttpService) ((LFApplication) context).service("http");

		File tmp = new File(context.getFilesDir(), prefix + "/" + moduleName);
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
			JSONObject config = loadLocalConfig();
			initConfig(config);
			// 当配置的源文件不存在时,下载该文件
			if (!new File(srcPath).exists()) {
				downloadSrcFile(config);
			}
		} catch (Exception e) {
			Logger.e(moduleName, "unable to read config at " + basicDir + "/config:" + e.getMessage());
		}

		try {
			deleteOldFile();
		} catch (Exception e) {
			Logger.e(moduleName, "unable to delete old module file" + e.getMessage());
		}
	}

	/**
	 * 读取更新配置，下载模块更新
	 *
	 * @param config
	 */
	public void update(JSONObject config) {
		try {
			int newVersion = config.getInt(K_VERSION);
			if (version >= newVersion) {
				return;
			}
			downloadSrcFile(config);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.w(moduleName, e.getLocalizedMessage());
		}
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

	public String moduleName() {
		return moduleName;
	}

	public String clazzName() {
		return clazzName;
	}

	public int version() {
		return version;
	}

	public String desc() {
		return desc;
	}

	/**
	 * 读取本地配置文件
	 *
	 * @return
	 * @throws Exception
	 */
	private JSONObject loadLocalConfig() throws Exception {
		File path = new File(basicDir + "/config");
		if (path.length() == 0)
			return null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(path);
			byte[] bytes = new byte[fis.available()];
			fis.read(bytes);
			fis.close();
			String str = new String(bytes, "UTF-8");
			JSONObject json = new JSONObject(str);
			return json;
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
	 * @param config
	 * @throws JSONException
	 */
	private void initConfig(JSONObject config) throws JSONException {
		int ver = config.getInt(K_VERSION);
		if (ver < version) {
			return;
		}

		version = ver;
		clazzName = config.getString(K_CLAZZ);
		md5 = config.getString(K_MD5);
		desc = config.getString(K_DESC);
		srcPath = new File(basicDir, "src").getAbsolutePath() + "/" + md5 + ".apk";
		dexPath = new File(basicDir, "dex").getAbsolutePath() + "/" + md5 + ".dex";
	}

	/**
	 * 将配置文件保存到本地
	 *
	 * @param json
	 * @throws Exception
	 */
	private void saveConfig(JSONObject json) throws Exception {
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
			byte[] bytes = json.toString().getBytes("UTF-8");
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
	 * @param config
	 * @throws JSONException
	 */
	private void downloadSrcFile(JSONObject config) throws JSONException {
		tmpConfig = config;
		tmpPath = String.format("%s/%s.apk", srcDir, config.getString(K_MD5));
		httpService.download(config.getString(K_URL), tmpPath, true,
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
							saveConfig(tmpConfig);
							initConfig(tmpConfig);
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
}
