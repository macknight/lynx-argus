package com.lynx.lib.db.table;

/**
 * @author chris
 * @version 3/12/14 7:05 PM
 */
public class OneToMany extends Property {

	private Class<?> oneClass;

	public Class<?> getOneClass() {
		return oneClass;
	}

	public void setOneClass(Class<?> oneClass) {
		this.oneClass = oneClass;
	}

}
