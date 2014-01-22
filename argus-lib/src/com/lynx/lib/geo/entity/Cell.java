package com.lynx.lib.geo.entity;

import java.io.Serializable;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-8-27 下午5:53
 */
public interface Cell extends Serializable {
	CellType type();

	public enum CellType {
		GSM, CDMA, UNKNWON;
	}
}
