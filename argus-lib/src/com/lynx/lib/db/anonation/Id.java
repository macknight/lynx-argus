package com.lynx.lib.db.anonation;

import java.lang.annotation.*;

/**
 * Id主键配置,不配置的时候默认找类的id或_id字段作为主键，column不配置的是默认为字段名
 * 
 * @author chris.liu
 * @version 3/12/14 7:55 PM
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Id {
	public String column() default "";
}