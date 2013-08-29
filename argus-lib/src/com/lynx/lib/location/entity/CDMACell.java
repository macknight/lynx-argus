package com.lynx.lib.location.entity;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/27/13 5:52 PM
 */
public class CDMACell implements Cell {
    private int mcc;
    private int nid;
    private int sid;
    private int bid;
    private long lat;
    private long lng;

    public CDMACell(int mcc, int nid, int sid, int bid, long lat, long lng) {
        this.mcc = mcc;
        this.nid = nid;
        this.sid = sid;
        this.bid = bid;
        this.lat = lat;
        this.lng = lng;
    }

    public int getMcc() {
        return mcc;
    }

    public void setMcc(int mcc) {
        this.mcc = mcc;
    }

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public long getLat() {
        return lat;
    }

    public void setLat(long lat) {
        this.lat = lat;
    }

    public long getLng() {
        return lng;
    }

    public void setLng(long lng) {
        this.lng = lng;
    }

    @Override
    public CellType type() {
        return CellType.CDMA;
    }
}
