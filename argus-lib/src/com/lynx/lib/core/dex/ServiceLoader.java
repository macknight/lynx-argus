package com.lynx.lib.core.dex;

import com.lynx.lib.core.Logger;
import dalvik.system.DexClassLoader;

/**
 *
 * @author zhufeng.liu
 *
 * @addtime 13-8-30 下午11:29
 */
public abstract class ServiceLoader extends DexModuleLoader {
	protected Class<?> clazz;
	protected Service service;

	/**
	 * @param dexModule    动态服务配置
	 * @param defaultClazz 默认服务版本
	 */
	public ServiceLoader(DexModule dexModule, Class<?> defaultClazz)
			throws Exception {
		super(DexType.SERVICE, dexModule, DexStatus.UPDATE);

		if (clazz == null) {
			clazz = defaultClazz;
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

	public Service service() {
		return service;
	}

	/**
	 * 显示调用则立即生效，否则在app下次载入时生效
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void replaceService() throws Exception {
		beforeLoad();
		deleteOldFile();
		try {
			DexClassLoader cl = new DexClassLoader(srcPath,
					dexDir, null, context.getClassLoader());
			clazz = (Class<Service>) cl.loadClass(dexModule.clazz());
		} catch (Exception e) {
			// TODO: roll back to the default service
			Logger.e(dexModule.module(), "replace service error", e);
		}

		loadService();

		afterLoad();
	}
}
