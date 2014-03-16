package com.lynx.service.geo;

import android.content.Context;
import com.lynx.lib.core.LFActivity;
import com.lynx.lib.core.LFApplication;
import com.lynx.lib.core.dex.DexModule;
import com.lynx.lib.core.dex.Service;
import com.lynx.lib.core.dex.ServiceLoader;
import com.lynx.lib.geo.GeoService;
import com.lynx.lib.geo.LocationListener;
import com.lynx.service.geo.impl1v1.GeoServiceImpl;

import java.util.List;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-8-13 下午11:16
 */
public class GeoServiceDexLoader extends ServiceLoader {

	public static final String Tag = "geo";

	private List<LocationListener> listeners = null;

	private static Service defService;

	static {
		defService = new Service();
		defService.setModule("geo");
		defService.setClazz("com.lynx.service.geo.impl1v1.GeoServiceImpl");
		defService.setVersion(1);
		defService.setDesc("地理位置信息服务");
	}

	public GeoServiceDexLoader() throws Exception {
		super(defService, GeoServiceImpl.class);
	}

	@Override
	protected void beforeLoad() {
		GeoService geoService = (GeoService) service;
		listeners = geoService.listeners();
		if (geoService != null) {
			geoService.stop();
		}
	}

	@Override
	protected void loadService() {
		try {
			Context context = LFApplication.instance();
			if (clazz != null) {
				service = (GeoService) clazz.getConstructor(Context.class).newInstance(context);
			}

			if (service == null) {
				service = new GeoServiceImpl(context);
			}

			if (service != null && listeners != null && listeners.size() > 0) {
				for (LocationListener listener : listeners) {
					((GeoService) service).addListener(listener);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void afterLoad() {
		service.start();
	}
}
