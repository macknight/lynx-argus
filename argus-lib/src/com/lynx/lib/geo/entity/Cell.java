package com.lynx.lib.geo.entity;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: zhufeng.liu
 * Date: 8/27/13 5:53 PM
 */
public interface Cell extends Serializable {
    CellType type();

    public enum CellType {
        GSM, CDMA, UNKNWON;
    }
}
