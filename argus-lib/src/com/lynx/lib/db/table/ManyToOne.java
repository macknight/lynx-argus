package com.lynx.lib.db.table;

/**
 * 
 * @author chris.liu
 * @version 3/12/14 7:05 PM
 */
public class ManyToOne extends Property {

	private Class<?> manyClass;

	public Class<?> getManyClass() {
		return manyClass;
	}

	public void setManyClass(Class<?> manyClass) {
		this.manyClass = manyClass;
	}

}
