package com.lynx.service.geo.entity;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/27/13 5:53 PM
 */
public interface Cell {
    CellType type();

    public enum CellType {
        GSM, CDMA, UNKNWON;
    }
}
