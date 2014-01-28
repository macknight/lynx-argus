package com.lynx.lib.core.model;

import java.io.Serializable;

/**
 * 
 * @author chris.liu
 * 
 * @version 14-1-28 上午10:46
 */
public abstract class LFObject implements Serializable {

	/**
	 * 将对象序列化JSON格式string
	 * 
	 * @return
	 */
	public abstract String toLFString();

}
