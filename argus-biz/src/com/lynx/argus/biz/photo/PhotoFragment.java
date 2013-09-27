package com.lynx.argus.biz.photo;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.lynx.argus.R;
import com.lynx.argus.app.BizFragment;
import com.lynx.lib.core.ErrorFragment;
import com.lynx.lib.core.LFApplication;
import dalvik.system.DexClassLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-12 下午6:20
 */
public class PhotoFragment extends BizFragment {

    public static final String Tag = "Photo";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_photo, container, false);

        Button btn = (Button) v.findViewById(R.id.btn_photo_dex_laoder);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    AssetManager asset = tabActivity.getAssets();
                    String path = "apk/" + asset.list("apk")[0];
                    String clazz = "com.lynx.argus.biz.plugin.test.DemoFragment";
                    contextInit(path);
                    loadFragment(clazz);
                } catch (Exception e) {
                    e.printStackTrace();
                    Fragment fragment = new ErrorFragment();
                    tabActivity.pushFragments(Tag, fragment, true, true);
                }
            }
        });

        return v;
    }

    private void contextInit(String path) {
        try {
            InputStream ins = LFApplication.instance().getAssets()
                    .open(path);
            byte[] bytes = new byte[ins.available()];
            ins.read(bytes);
            ins.close();

            File f = new File(LFApplication.instance().getFilesDir(), "ui/");
            if (!f.exists()) {
                f.mkdirs();
            }
            f = new File(f, "FL_" + Integer.toHexString(path.hashCode()) + ".apk");
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bytes);
            fos.close();

            File fo = new File(LFApplication.instance().getFilesDir(), "ui/dexout");
            if (!fo.exists()) {
                fo.mkdirs();
            }

            DexClassLoader dcl = new DexClassLoader(f.getAbsolutePath(),
                    fo.getAbsolutePath(), null, tabActivity.getClassLoader());
            tabActivity.setClassLoader(dcl);

            AssetManager asm = null;
            try {
                asm = (AssetManager) AssetManager.class.newInstance();

                asm.getClass().getMethod("addAssetPath", String.class)
                        .invoke(asm, f.getAbsolutePath());
                tabActivity.setAssetManager(asm);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Resources superRes = super.getResources();

            Resources res = new Resources(asm, superRes.getDisplayMetrics(),
                    superRes.getConfiguration());

            Resources.Theme thm = res.newTheme();
            thm.setTo(tabActivity.getTheme());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFragment(String clazz) throws Exception {
        Fragment fragment = (Fragment) tabActivity.getClassLoader().loadClass(clazz).newInstance();
        tabActivity.pushFragments(Tag, fragment, true, true);
    }

}
