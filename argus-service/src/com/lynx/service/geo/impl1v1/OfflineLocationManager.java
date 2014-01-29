package com.lynx.service.geo.impl1v1;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.lynx.lib.core.Logger;
import com.lynx.lib.geo.entity.Cell;
import com.lynx.lib.geo.entity.Location;
import org.json.JSONObject;

/**
 * @author chris.liu
 * @version 14-1-28 上午10:40
 */
public class OfflineLocationManager {
	private static final String Tag = "OfflineLocationManager";
	private LocationDBHelper locDBHelper;

	public OfflineLocationManager(Context context) {
		locDBHelper = new LocationDBHelper(context);
	}

	public void addLocation(Cell cell, Location location) {
		Log.d(Tag,
				String.format("add location into db(%s->%s,%s)",
						cell.toString(), location));
		try {
			locDBHelper.add(cell.toString(), location);
			locDBHelper.delete();
		} catch (Exception e) {
			Log.d(Tag,
					"add location data into db error:"
							+ e.getLocalizedMessage());
		}
	}

	/**
	 * @param cell
	 * @return
	 */
	public Location getLocation(Cell cell) {
		Logger.i(
				Tag,
				String.format("get location from db of cell:%s",
						cell.toString()));
        Location location = null;
		try {
			Cursor cursor = locDBHelper.get(cell.toString());
			if (cursor != null) {
				cursor.moveToFirst();
				String loc = cursor.getString(0);
                JSONObject jo = new JSONObject(loc);

				cursor.close();
				cursor = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			location = null;
		}
		return location;
	}
}