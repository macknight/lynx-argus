package com.lynx.lib.core.dex;

import com.lynx.lib.util.FileUtil;

import java.io.File;
import java.io.IOException;

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
    public void delete() throws IOException {
        File file = new File(basicDir);
        FileUtil.deleteFile(file);
    }
}
