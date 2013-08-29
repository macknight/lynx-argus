package com.lynx.peleus.location.impl.core;

import android.content.Context;
import android.os.Handler;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import com.lynx.lib.location.entity.Cell;
import com.lynx.lib.location.entity.CDMACell;
import com.lynx.lib.location.entity.GSMCell;
import com.lynx.peleus.location.impl.LocationCenter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chris.liu
 */
public class CellInfoManager extends Handler {
    private LocationCenter locCenter;
    private TelephonyManager telManager;
    private final List<Cell> cells = new ArrayList<Cell>();
    private int asu = 0;

    public CellInfoManager(LocationCenter loc_center) {
        this.locCenter = loc_center;
        telManager = (TelephonyManager) loc_center.getContext()
                .getSystemService(Context.TELEPHONY_SERVICE);

    }

    public List<Cell> getCells() {
        return this.cells;
    }

    public void start() {
        cellScan();
    }

    public void cellScan() {
        int mcc = 0, mnc = 0, cid = 0, lac = 0, bid = 0, sid = 0, nid = 0;
        CellLocation cell_loc = telManager.getCellLocation();

        if (cell_loc == null) {
            locCenter.sendEmptyMessage(LocationCenter.CELL_SCAN_FIN);
            return;
        }

        switch (telManager.getPhoneType()) {
            case TelephonyManager.PHONE_TYPE_CDMA:
                mcc = 0;
                bid = 0;
                sid = 0;
                nid = 0;

                try {
                    String s = telManager.getNetworkOperator();
                    if (s == null || s.length() != 5 && s.length() != 6) {
                        s = telManager.getSimOperator();
                    }

                    if (s.length() == 5 || s.length() == 6) {
                        mcc = Integer.parseInt(s.substring(0, 3));
                    }

                    Method mBid = cell_loc.getClass().getMethod("getBaseStationId",
                            new Class[0]);
                    Method mSid = cell_loc.getClass().getMethod("getSystemId",
                            new Class[0]);
                    Method mNid = cell_loc.getClass().getMethod("getNetworkId",
                            new Class[0]);
                    bid = (Integer) mBid.invoke(cell_loc, new Object[0]);
                    sid = (Integer) mSid.invoke(cell_loc, new Object[0]);
                    nid = (Integer) mNid.invoke(cell_loc, new Object[0]);
                    Method mLat = cell_loc.getClass().getMethod(
                            "getBaseStationLatitude", new Class[0]);
                    Method mLng = cell_loc.getClass().getMethod(
                            "getBaseStationLongitude", new Class[0]);
                    int lat = (Integer) mLat.invoke(cell_loc, new Object[0]);
                    int lng = (Integer) mLng.invoke(cell_loc, new Object[0]);

                    cells.clear();
                    Cell cell = new CDMACell(mcc, sid, bid, nid, lat, lng);
                    cells.add(cell);
                    locCenter.sendEmptyMessage(LocationCenter.CELL_SCAN_FIN);
                } catch (Exception e) {
                    cells.clear();
                    locCenter.sendEmptyMessage(LocationCenter.CELL_SCAN_FIN);
                }
                break;
            case TelephonyManager.PHONE_TYPE_GSM:
                // this is a GSM Cell Phone.
                cells.clear();
                if (cell_loc instanceof GsmCellLocation) {
                    try {
                        GsmCellLocation gsm_cell_loc = (GsmCellLocation) cell_loc;
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
                                locCenter.sendEmptyMessage(LocationCenter.CELL_SCAN_FIN);
                                return;
                            }

                            Cell cell = new GSMCell(mcc, mnc, cid, lac, asu);
                            cells.add(cell);
                        }
                    } catch (Exception e) {
                    }
                }

                try {
                    List<NeighboringCellInfo> neighbor_cells = telManager
                            .getNeighboringCellInfo();
                    if (neighbor_cells != null) {
                        Method method_lac = null;
                        try {
                            method_lac = NeighboringCellInfo.class
                                    .getMethod("getLac");
                        } catch (Exception e) {
                            method_lac = null;
                        }

                        for (NeighboringCellInfo n_cell : neighbor_cells) {
                            cid = n_cell.getCid();
                            lac = 0;
                            if (method_lac != null) {
                                try {
                                    lac = (Integer) method_lac.invoke(n_cell);
                                } catch (Exception e) {
                                }
                            }

                            if (!(cid <= 0) || cid == 0xFFFF) {
                                Cell cell = new GSMCell(mcc, mnc, cid, lac, n_cell.getRssi());
                                cells.add(cell);
                            }
                        }
                        locCenter.sendEmptyMessage(LocationCenter.CELL_SCAN_FIN);
                    }
                } catch (Exception e) {
                    cells.clear();
                    locCenter.sendEmptyMessage(LocationCenter.CELL_SCAN_FIN);
                }
                break;
            default:
                cells.clear();
                locCenter.sendEmptyMessage(LocationCenter.CELL_SCAN_FIN);
        }
    }

    public void stop() {
        // do nothing now.
    }

    public static int dbm(int asu) {
        if (asu >= 0 && asu <= 31)
            return -113 + 2 * asu;
        else
            return 0;
    }
}
