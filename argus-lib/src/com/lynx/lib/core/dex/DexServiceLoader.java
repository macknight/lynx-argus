package com.lynx.lib.core.dex;

import android.content.Context;
import com.lynx.lib.core.Logger;
import dalvik.system.DexClassLoader;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/30/13 11:29 AM
 */
public abstract class DexServiceLoader extends DexModuleLoader {
	protected Class<?> clazz;
	protected DexService service;

	/**
	 * @param context
	 * @param moduleName   动态服务标签
	 * @param minVersion   最小动态服务包版本
	 * @param defaultClazz 默认服务版本
	 */
	public DexServiceLoader(Context context, String moduleName, int minVersion, Class<?> defaultClazz)
			throws Exception {
		super(context, "service", moduleName, minVersion);

		if (clazz == null) {
			clazz = defaultClazz;
			clazzName = defaultClazz.getName();
		}

		try {
			loadService();
		} catch (Exception e) {
			throw new Exception("loading service error");
		}
	}

	/**
	 * 在载入service之前调用，预处理
	 */
	protected abstract void beforeLoad();

	/**
	 * 读取本地config，载入service
	 */
	protected abstract void loadService() throws Exception;

	/**
	 * 在载入service之后调用，恢复现场
	 */
	protected abstract void afterLoad();

	public DexService service() {
		return service;
	}

	/**
	 * 显示调用则立即生效，否则在app下次载入时生效
	 *
	 * @throws Exception
	 */
	public void replaceService() throws Exception {
		beforeLoad();
		deleteOldFile();
		try {
			loadClass(version, md5, clazzName);
		} catch (Exception e) {
			// TODO: roll back to the default service
			Logger.e(moduleName, "replace service error", e);
		}

		loadService();

		afterLoad();
	}

	@SuppressWarnings("unchecked")
	private void loadClass(int version, String md5, String className)
			throws Exception {
		DexClassLoader cl = new DexClassLoader(srcPath,
				dexDir, null, context.getClassLoader());
		clazz = (Class<DexService>) cl.loadClass(className);
	}
}
