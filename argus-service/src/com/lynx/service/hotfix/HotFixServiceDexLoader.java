package com.lynx.service.hotfix;

import android.content.Context;
import com.lynx.lib.core.DexServiceLoader;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-12 下午1:34
 */
public class HotFixServiceDexLoader extends DexServiceLoader {

    public HotFixServiceDexLoader(Context context, String tag, int minVersion, Class<?> defaultClazz)
            throws Exception {
        super(context, tag, minVersion, defaultClazz);
    }

    @Override
    protected void beforeLoad() {

    }

    @Override
    protected void loadService() throws Exception {

    }

    @Override
    protected void afterLoad() {

    }

    @Override
    public String name() {
        return HotFixService.class.getSimpleName();
    }
}
