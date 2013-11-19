package com.lynx.lib.geo.entity;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/27/13 5:53 PM
 */
public class GSMCell implements Cell {
    private int mcc; // mobile country code
    private int mnc; // mobile network code
    private int lac; // com.lynx.argus.biz.plugin.plugin area code
    private int cid; // cell id
    private int asu; // single strength

    public GSMCell(int mcc, int mnc, int lac, int cid, int asu) {
        this.mcc = mcc;
        this.mnc = mnc;
        this.lac = lac;
        this.cid = cid;
        this.asu = asu;
    }

    public int getMcc() {
        return mcc;
    }

    public void setMcc(int mcc) {
        this.mcc = mcc;
    }

    public int getMnc() {
        return mnc;
    }

    public void setMnc(int mnc) {
        this.mnc = mnc;
    }

    public int getLac() {
        return lac;
    }

    public void setLac(int lac) {
        this.lac = lac;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getAsu() {
        return asu;
    }

    public void setAsu(int asu) {
        this.asu = asu;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s", mcc, mnc, lac, cid, asu);
    }

    @Override
    public CellType type() {
        return CellType.GSM;
    }
}
