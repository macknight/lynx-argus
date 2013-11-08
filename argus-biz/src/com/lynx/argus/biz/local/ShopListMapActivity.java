package com.lynx.argus.biz.local;

import android.graphics.*;
import android.os.Bundle;
import com.lynx.argus.R;
import com.lynx.argus.app.BizApplication;
import com.lynx.lib.geo.GeoService;
import com.lynx.lib.geo.entity.Coord;
import com.mapbar.android.maps.*;

import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: chris.liu
 * Date: 13-11-8
 * Time: 下午3:51
 */
public class ShopListMapActivity extends MapActivity {
	private GeoService geoService;
	private MapView mapView;
	private MapController mapController;
	private GeoPoint gpCenter;
	Random random = new Random();
	private int color = Color.argb(60, random.nextInt(255), random.nextInt(255), random.nextInt(255));

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_local_shoplist_onmap);

		mapView = (MapView) findViewById(R.id.mv_mapbar);
		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();
		mapController.setZoom(mapView.getMaxZoomLevel() - 1);

		geoService = (GeoService) BizApplication.instance().service("geo");

		if (geoService.coord() == null) {
			gpCenter = new GeoPoint((int) (31.215999 * 1E6), (int) (121.419996 * 1E6));
		} else {
			gpCenter = new GeoPoint((int) (geoService.coord().getLat() * 1E6),
					                       (int) (geoService.coord().getLng() * 1E6));
		}
		mapController.setCenter(gpCenter);


		pinOnMap(geoService.coord());
	}

	private void pinOnMap(Coord coord) {
		try {
			GeoPoint gp = new GeoPoint((int) (coord.getLat() * 1E6),
					                          (int) (coord.getLng() * 1E6));
			mapController.animateTo(gp);

			List<Overlay> overlays = mapView.getOverlays();
			overlays.clear();

			MapbarMarker marker = new MapbarMarker(coord);
			overlays.add(marker);
		} catch (Exception e) {

		}
	}

	private class MapbarMarker extends Overlay {
		private Coord coord;
		private float accuracy;

		public MapbarMarker(Coord coord) {
			this.coord = coord;
			this.accuracy = (int) coord.getAcc();
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);

			Point screenPoint = new Point();
			GeoPoint gp = new GeoPoint((int) (coord.getLat() * 1E6),
					                          (int) (coord.getLng() * 1E6));
			mapView.getProjection().toPixels(gp, screenPoint);

			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setStrokeWidth(2);
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			paint.setColor(color);
			Paint paint_marker = new Paint();
			Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.cur_pos);
			canvas.drawBitmap(bmp, screenPoint.x - bmp.getWidth() / 2,
					                 screenPoint.y - bmp.getHeight() / 2, paint_marker);
			canvas.drawCircle(screenPoint.x, screenPoint.y,
					                 mapView.getProjection().metersToEquatorPixels(this.accuracy), paint);
		}
	}
}
