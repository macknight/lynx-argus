package com.lynx.service.geo.impl1v1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.lynx.lib.geo.entity.Location;

import java.util.Date;

/**
 * 
 * @author chris.liu
 * 
 * @version 14-1-28 上午10:38
 */
public class LocationDBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "location";
	private static final int DB_VERSION = 1;
	private static final String TABLE_NAME = "location";

	public static final String COL_ID = "_id";
	public static final String COL_CELL = "cell";
	public static final String COL_LOCATION = "location";
	public static final String COL_TIME = "time";

	private static final long TIME_SPAN = 14 * 24 * 36001000;

	public LocationDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "Create table " + TABLE_NAME + "(" + COL_ID
				+ " integer primary key autoincrement," + COL_CELL + " text,"
				+ COL_LOCATION + " text, " + COL_TIME + " text);";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = " DROP TABLE IF EXISTS " + TABLE_NAME;
		db.execSQL(sql);
		onCreate(db);
	}

	public Cursor getAll() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null) {
			return db.query(TABLE_NAME, null, null, null, null, null, null);
		}
		return null;
	}

	public Cursor get(String cell) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = String.format("%s='%s'", COL_CELL, cell);
		if (db != null) {
			return db.query(TABLE_NAME, new String[] { COL_LOCATION }, where,
					null, null, null, null);
		}
		return null;
	}

	public void add(String cell, Location location) {
		Cursor cursor = get(cell);
		if (cursor != null) {
			if (cursor.isAfterLast()) {
				insert(cell, location);
			} else {
				update(cell, location);
			}
			cursor.close();
		}
	}

	public long insert(String cell, Location location) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(COL_CELL, cell);
		cv.put(COL_LOCATION, location.toString());
		cv.put(COL_TIME, new Date().getTime());
		if (db != null) {
			return db.insert(TABLE_NAME, null, cv);
		}
		return 0;
	}

	public void delete() {
		SQLiteDatabase db = getWritableDatabase();
		String where = String.format("%s<%s", COL_TIME, new Date().getTime()
				- TIME_SPAN);
		if (db != null) {
			db.delete(TABLE_NAME, where, null);
		}
	}

	public void update(String cell, Location location) {
		SQLiteDatabase db = getWritableDatabase();
		String where = String.format("%s='%s'", COL_CELL, cell);
		ContentValues cv = new ContentValues();
		cv.put(COL_LOCATION, location.toString());
		cv.put(COL_TIME, new Date().getTime());
		if (db != null) {
			db.update(TABLE_NAME, cv, where, null);
		}
	}

}
