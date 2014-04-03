package com.lynx.service.geo.impl1v1;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

import com.lynx.lib.core.LFLogger;
import com.lynx.lib.geo.entity.CDMACell;
import com.lynx.lib.geo.entity.Cell;
import com.lynx.lib.geo.entity.GSMCell;

/**
 * 
 * @author zhufeng.liu
 * @version 13-11-8 下午5:01
 */
public class CellInfoManager {
	private static final String Tag = "CellInfoManager";
	private static final int INTERVAL_CELL = 200;
	private static final int INTERVAL_COUNT = 6;

	private LocationCenter locationCenter;
	private TelephonyManager telManager;
	private final List<Cell> cells = new ArrayList<Cell>();
	private int asu = 0;
	private AtomicInteger loop = new AtomicInteger(0);

	private static Timer timer;
	private static TimerTask timerTask;

	public CellInfoManager(Context context) {
		this.locationCenter = null;
		telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	}

	public CellInfoManager(LocationCenter locationCenter) {
		this(locationCenter.context());
		this.locationCenter = locationCenter;
	}

	public void start() {
		LFLogger.i(Tag, "start cell info scan");
		stop();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				if (loop.intValue() >= INTERVAL_COUNT) {
					stop();
					if (locationCenter != null) {
						locationCenter.cellScanFin();
					}
					return;
				} else {
					loop.set(loop.intValue() + 1);
				}
				cellScan();
			}
		};

		timer = new Timer();
		timer.schedule(timerTask, 0, INTERVAL_CELL);
	}

	public void stop() {
		LFLogger.i(Tag, "stop cell info scan");
		// 还原状态
		loop.set(0);
		if (timerTask != null) {
			timerTask.cancel();
			timerTask = null;
		}
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	public void cellScan() {
		int mcc = 0, mnc = 0, cid = 0, lac = 0, bid = 0, sid = 0, nid = 0;
		CellLocation cellLocation = telManager.getCellLocation();

		if (cellLocation == null) {
			return;
		}

		switch (telManager.getPhoneType()) {
		case TelephonyManager.PHONE_TYPE_CDMA:
			mcc = 0;

			try {
				String s = telManager.getNetworkOperator();
				if (s == null || s.length() != 5 && s.length() != 6) {
					s = telManager.getSimOperator();
				}

				if (s.length() == 5 || s.length() == 6) {
					mcc = Integer.parseInt(s.substring(0, 3));
				}

				Method mBid = cellLocation.getClass().getMethod("getBaseStationId", new Class[0]);
				Method mSid = cellLocation.getClass().getMethod("getSystemId", new Class[0]);
				Method mNid = cellLocation.getClass().getMethod("getNetworkId", new Class[0]);
				bid = (Integer) mBid.invoke(cellLocation);
				sid = (Integer) mSid.invoke(cellLocation);
				nid = (Integer) mNid.invoke(cellLocation);
				Method mLat = cellLocation.getClass().getMethod("getBaseStationLatitude",
						new Class[0]);
				Method mLng = cellLocation.getClass().getMethod("getBaseStationLongitude",
						new Class[0]);
				int lat = (Integer) mLat.invoke(cellLocation, new Object[0]);
				int lng = (Integer) mLng.invoke(cellLocation, new Object[0]);

				cells.clear();
				Cell cell = new CDMACell(mcc, sid, nid, bid, lat, lng);
				cells.add(cell);
			} catch (Exception e) {
				cells.clear();
			}
			break;
		case TelephonyManager.PHONE_TYPE_GSM:
			// this is a GSM Cell Phone.
			cells.clear();
			if (cellLocation instanceof GsmCellLocation) {
				try {
					GsmCellLocation gsm_cell_loc = (GsmCellLocation) cellLocation;
					cid = gsm_cell_loc.getCid();
					lac = gsm_cell_loc.getLac();
					if (!(cid <= 0 || cid == 0xFFFF)) {
						String s = telManager.getNetworkOperator();
						if (s == null || s.length() != 5 && s.length() != 6) {
							s = telManager.getSimOperator();
						}
						if (s.length() == 5 || s.length() == 6) {
							mcc = Integer.parseInt(s.substring(0, 3));
							mnc = Integer.parseInt(s.substring(3, s.length()));
						}

						if (mcc == 0 && mnc == 0) {
							cells.clear();
							return;
						}

						Cell cell = new GSMCell(mcc, mnc, lac, cid, asu);
						cells.add(cell);
					}
				} catch (Exception e) {
				}
			}

			try {
				List<NeighboringCellInfo> neighborCells = telManager.getNeighboringCellInfo();
				if (neighborCells != null) {
					Method methodLac = null;
					try {
						methodLac = NeighboringCellInfo.class.getMethod("getLac");
					} catch (Exception e) {
						methodLac = null;
					}

					for (NeighboringCellInfo nCell : neighborCells) {
						cid = nCell.getCid();
						lac = 0;
						if (methodLac != null) {
							try {
								lac = (Integer) methodLac.invoke(nCell);
							} catch (Exception e) {
							}
						}

						if (!(cid <= 0) || cid == 0xFFFF) {
							Cell cell = new GSMCell(mcc, mnc, cid, lac, nCell.getRssi());
							cells.add(cell);
						}
					}
				}
			} catch (Exception e) {
				cells.clear();
			}
			break;
		default:
			cells.clear();
		}
	}

	public List<Cell> cells() {
		return cells;
	}

	public String cells2str() {
		if (cells.size() > 0) {
			String result = "";
			for (Cell cell : cells) {
				result = String.format("%s|%s", result, cell.toString());
			}
			return result.substring(1);
		}

		return null;
	}

	public static int dbm(int asu) {
		if (asu >= 0 && asu <= 31)
			return -113 + 2 * asu;
		else
			return 0;
	}
}