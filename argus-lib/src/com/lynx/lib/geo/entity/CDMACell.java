package com.lynx.lib.geo.entity;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/27/13 5:52 PM
 */
public class CDMACell implements Cell {
    private int mcc; // mobile country code
    private int sid; // system id
    private int nid; // network id
    private int bid; // base station id
    private long lat;
    private long lng;

    public CDMACell(int mcc, int sid, int nid, int bid, long lat, long lng) {
        this.mcc = mcc;
        this.sid = sid;
        this.nid = nid;
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

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
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
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s", mcc, nid, sid, bid, lat, lng);
    }

    @Override
    public CellType type() {
        return CellType.CDMA;
    }
}
