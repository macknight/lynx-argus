package com.lynx.lib.core.dex;

import java.io.File;
import java.io.IOException;

import com.lynx.lib.core.Logger;
import com.lynx.lib.util.FileUtil;

/**
 * 动态Fragment加载器
 * 
 * @author zhufeng.liu
 * 
 * @version 13-9-29 下午4:12
 */
public class PluginLoader extends DexLoader {

	public PluginLoader(Plugin plugin, DexStatus status) {
		super(plugin, status);
	}

	/**
	 * 删除本地插件文件
	 */
	public void delete() throws IOException {
        File file = new File(basicDir);
		FileUtil.deleteFile(file);
	}

}
