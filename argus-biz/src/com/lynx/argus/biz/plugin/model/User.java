package com.lynx.argus.biz.plugin.model;

import com.lynx.lib.db.anonation.Id;
import com.lynx.lib.db.anonation.Table;

import java.util.Date;

/**
 * @author chris
 * @version 3/12/14 7:55 PM
 */
@Table(name = "user_info")
public class User {
	@Id
	private int uid;

	private String name;
	private String sex;
	private Date registerDate;

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}
}
