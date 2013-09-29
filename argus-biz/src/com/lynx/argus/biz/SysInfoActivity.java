package com.lynx.argus.biz;

import com.lynx.argus.app.BizApplication;
import com.lynx.lib.core.DexServiceLoader;
import com.lynx.service.activity.BasicInfoActivity;

import java.util.Map;

/**
 * APP系统基本信息，包括android系统信息以及service描述信息
 * <p/>
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-11 下午1:35
 */
public class SysInfoActivity extends BasicInfoActivity {

    @Override
    protected void setDexLoaders() {
        Map<String, DexServiceLoader> loaders = BizApplication.instance().services();
        this.loaders = loaders;

    }
}
