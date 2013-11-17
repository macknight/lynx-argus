package com.lynx.lib.core.dex;

import java.io.File;

/**
 * 动态Fragment加载器
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 9/29/13 4:12 PM
 */
public class PluginLoader extends DexModuleLoader {

	public PluginLoader(Plugin plugin) {
		super(DexType.PLUGIN, plugin);
	}

	/**
	 * 删除本地插件文件
	 */
	public void delete() {
		File file = new File(basicDir);
		file.delete();
	}
}
