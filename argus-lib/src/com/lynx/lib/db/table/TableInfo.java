package com.lynx.lib.db.table;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import com.lynx.lib.core.LFLogger;
import com.lynx.lib.db.core.ClassUtil;
import com.lynx.lib.db.core.FieldUtil;

/**
 * 
 * @author chris.liu
 * @version 3/12/14 7:05 PM
 */
public class TableInfo {

	private static final String Tag = "TableInfo";

	private String className;
	private String tableName;

	private Id id;

	public final HashMap<String, Property> propertyMap = new HashMap<String, Property>();
	public final HashMap<String, OneToMany> oneToManyMap = new HashMap<String, OneToMany>();
	public final HashMap<String, ManyToOne> manyToOneMap = new HashMap<String, ManyToOne>();

	private boolean checkDatabese;// 在对实体进行数据库操作的时候查询是否已经有表了，只需查询一遍，用此标示

	private static final HashMap<String, TableInfo> tableInfoMap = new HashMap<String, TableInfo>();

	private TableInfo() {
	}

	@SuppressWarnings("unused")
	public static TableInfo get(Class<?> clazz) {
		if (clazz == null)
			return null;

		TableInfo tableInfo = tableInfoMap.get(clazz.getName());
		if (tableInfo == null) {
			tableInfo = new TableInfo();

			tableInfo.setTableName(ClassUtil.getTableName(clazz));
			tableInfo.setClassName(clazz.getName());

			Field idField = ClassUtil.getPrimaryKeyField(clazz);
			if (idField != null) {
				Id id = new Id();
				id.setColumn(FieldUtil.getColumnByField(idField));
				id.setFieldName(idField.getName());
				id.setSet(FieldUtil.getFieldSetMethod(clazz, idField));
				id.setGet(FieldUtil.getFieldGetMethod(clazz, idField));
				id.setDataType(idField.getType());

				tableInfo.setId(id);
			} else {
				LFLogger.e(Tag, String.format("the class[%s]'s idField is null", clazz));
				return null;
			}

			List<Property> pList = ClassUtil.getPropertyList(clazz);
			if (pList != null) {
				for (Property p : pList) {
					if (p != null)
						tableInfo.propertyMap.put(p.getColumn(), p);
				}
			}

			List<ManyToOne> mList = ClassUtil.getManyToOneList(clazz);
			if (mList != null) {
				for (ManyToOne m : mList) {
					if (m != null)
						tableInfo.manyToOneMap.put(m.getColumn(), m);
				}
			}

			List<OneToMany> oList = ClassUtil.getOneToManyList(clazz);
			if (oList != null) {
				for (OneToMany o : oList) {
					if (o != null)
						tableInfo.oneToManyMap.put(o.getColumn(), o);
				}
			}

			tableInfoMap.put(clazz.getName(), tableInfo);
		}

		if (tableInfo == null) {
			LFLogger.e(Tag, String.format("the class[%s]'s table is null", clazz));
			return null;
		}

		return tableInfo;
	}

	public static TableInfo get(String className) {
		try {
			return get(Class.forName(className));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {

		}
		return null;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Id getId() {
		return id;
	}

	public void setId(Id id) {
		this.id = id;
	}

	public boolean isCheckDatabese() {
		return checkDatabese;
	}

	public void setCheckDatabese(boolean checkDatabese) {
		this.checkDatabese = checkDatabese;
	}

}
