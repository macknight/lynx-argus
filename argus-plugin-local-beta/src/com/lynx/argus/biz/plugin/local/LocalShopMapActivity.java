package com.lynx.argus.biz.plugin.local;

import android.os.Bundle;
import com.lynx.argus.biz.plugin.com.lynx.argus.biz.plugin.local.R;
import com.mapbar.android.maps.GeoPoint;
import com.mapbar.android.maps.MapActivity;
import com.mapbar.android.maps.MapController;
import com.mapbar.android.maps.MapView;

/**
 * Created with IntelliJ IDEA.
 * User: chris.liu
 * Date: 13-11-5
 * Time: 下午6:14
 */
public class LocalShopMapActivity extends MapActivity {

	private MapView mapView;
	private MapController mapController;
	private GeoPoint gpDef;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_local_shoplist_onmap);
	}
}
