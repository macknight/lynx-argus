package com.lynx.lib.db.anonation;

import java.lang.annotation.*;

/**
 * @author chris.liu
 * @version 3/12/14 6:58 PM
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Property {
	public String column() default "";

	public String defaultValue() default "";
}
