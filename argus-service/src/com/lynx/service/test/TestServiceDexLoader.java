package com.lynx.service.test;

import android.content.Context;
import com.lynx.service.core.ServiceDexLoader;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-5 下午5:59
 */
public class TestServiceDexLoader<TestService> extends ServiceDexLoader {
    private static final String Tag = "test";

    public TestServiceDexLoader(Context context) {
        super(context, Tag);


    }

    @Override
    protected void beforeLoad() {

    }

    @Override
    protected void loadService() {
        try {
            if (clazz != null) {
                service = (TestService) clazz.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void afterLoad() {

    }

}
