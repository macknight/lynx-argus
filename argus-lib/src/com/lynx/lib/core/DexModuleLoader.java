package com.lynx.lib.core;

import android.content.Context;
import android.widget.Toast;
import com.lynx.lib.http.HttpService;
import com.lynx.lib.http.handler.HttpCallback;
import com.lynx.lib.util.IOUtil;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by chris on 13-10-29.
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
	protected File basicPath; // /data/data/app.name/files/PREFIX/module
	protected String dexPath; // /data/data/app.name/files/PREFIX/module/dex
	protected String apkPath; // /data/data/app.name/files/PREFIX/module/version/xxx.apk
	protected String moduleName;
	protected String clazzName = null;
	protected int curVersion = -1;
	protected String md5 = null;
	protected String desc;

	/**
	 * @param context
	 * @param moduleName 动态服务标签
	 * @param minVersion 最小动态服务包版本
	 */
	public DexModuleLoader(Context context, String prefrix, String moduleName, int minVersion) {
		this.context = context;
		this.moduleName = moduleName;
		this.curVersion = minVersion;
		this.httpService = (HttpService) ((LFApplication) context).service("http");

		basicPath = new File(context.getFilesDir(), prefrix + "/" + moduleName);
		if (!basicPath.exists()) {
			basicPath.mkdirs();
		}

		File dexDir = new File(basicPath, "dex");
		if (!dexDir.exists()) {
			dexDir.mkdirs();
		}
		dexPath = dexDir.getAbsolutePath();

		try {
			JSONObject config = IOUtil.loadLocalConfig(basicPath, "config");
			if (config != null) {
				int version = config.getInt(K_VERSION);
				if (version > minVersion) {
					curVersion = version;
					clazzName = config.getString(K_CLAZZ);
					md5 = config.getString(K_MD5);
					desc = config.getString(K_DESC);
					apkPath = new File(basicPath, "" +
							config.getInt(K_VERSION)).getAbsolutePath() +
							String.format("/%s.apk", config.getString(K_MD5));
				}
			}
		} catch (Exception e) {
			Logger.e(moduleName, "unable to read config at " + new File(basicPath, "config"), e);
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
			if (curVersion >= newVersion) {
				return;
			}

			// 版本升级
			IOUtil.saveConfig(basicPath, config);
			File path = new File(basicPath, "" + config.getInt(K_VERSION));
			path.mkdir();

			try {
				clazzName = config.getString(K_CLAZZ);
				curVersion = config.getInt(K_VERSION);
				md5 = config.getString(K_MD5);
				desc = config.getString(K_DESC);
			} catch (Exception e) {
				return;
			}

			apkPath = String.format("%s/%s.apk", path.getAbsolutePath(), md5);
			httpService.download(config.getString(K_URL), apkPath, true,
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

							// 删除老的dex文件
							try {
								deleteOldDexFile();
							} catch (Exception e) {
								Logger.w("module loader", "删除老的Dex文件错误" + e.getMessage());
							}
						}

						@Override
						public void onFailure(Throwable t, String strMsg) {
							super.onFailure(t, strMsg);
							Toast.makeText(context, strMsg, Toast.LENGTH_SHORT).show();
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
			Logger.w(moduleName, e.getLocalizedMessage());
		}
	}

	public String moduleName() {
		return moduleName;
	}

	/**
	 * 动态模块类名
	 *
	 * @return
	 */
	public String clazzName() {
		return clazzName;
	}

	/**
	 * 动态模块路径
	 *
	 * @return
	 */
	public String modulePath() {
		return basicPath.getAbsolutePath();
	}

	/**
	 * 动态模块包路径
	 *
	 * @return
	 */
	public String apkPath() {
		return apkPath;
	}

	/**
	 * 动态包输出路径
	 *
	 * @return
	 */
	public String dexPath() {
		return dexPath;
	}

	public int version() {
		return curVersion;
	}

	/**
	 * 动态模块描述
	 *
	 * @return
	 */
	public String desc() {
		return desc;
	}

	protected void deleteOldDexFile() throws IOException {
		File dexDir = new File(basicPath, "dex");
		IOUtil.deleteFile(dexDir);
		dexDir = new File(basicPath, "dex");
		dexDir.mkdirs();
	}
}
