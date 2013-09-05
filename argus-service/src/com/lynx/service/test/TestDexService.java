package com.lynx.service.test;

import android.content.Context;
import android.os.Environment;
import dalvik.system.DexClassLoader;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-5 下午5:59
 */
public class TestDexService implements TestService {

    private Context context;
    private TestService testService;

    public TestDexService(Context context) {
        this.context = context;
    }



    private Class<?> loadClazz(String clazz, String md5, int version) {
        final File dexFile = new File(Environment.getExternalStorageDirectory().toString()
                + File.separator + "test.jar");
        DexClassLoader cl = new DexClassLoader(dexFile.getAbsolutePath(),
                Environment.getExternalStorageDirectory().toString(), null,
                context.getClassLoader());
        Class l = null;

        try {
            return cl.loadClass(clazz);
//            IDynamic lib = (IDynamic)l.newInstance();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public String hello(String name) {
        return testService.hello(name);
    }
}
