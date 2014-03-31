package com.lynx.lib.db.core;

import java.util.ArrayList;
import java.util.List;

import com.lynx.lib.db.DBService;

/**
 * 
 * 一对多延迟加载类
 * 
 * @author chris.liu
 * @version 3/12/14 7:05 PM
 * 
 * @param <O>
 *            宿主实体的class
 * @param <M>
 *            多放实体class
 */
public class OneToManyLazyLoader<O, M> {
	O ownerEntity;
	Class<O> ownerClazz;
	Class<M> listItemClazz;
	DBService db;

	public OneToManyLazyLoader(O ownerEntity, Class<O> ownerClazz, Class<M> listItemclazz,
			DBService db) {
		this.ownerEntity = ownerEntity;
		this.ownerClazz = ownerClazz;
		this.listItemClazz = listItemclazz;
		this.db = db;
	}

	List<M> entities;

	/**
	 * 如果数据未加载，则调用loadOneToMany填充数据
	 * 
	 * @return
	 */
	public List<M> getList() {
		if (entities == null) {
			this.db.loadOneToMany((O) this.ownerEntity, this.ownerClazz, this.listItemClazz);
		}
		if (entities == null) {
			entities = new ArrayList<M>();
		}
		return entities;
	}

	public void setList(List<M> value) {
		entities = value;
	}

}
