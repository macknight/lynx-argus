package com.lynx.lib.util;

import java.util.Calendar;
import java.util.Date;

/**
 * @author chris.liu
 * @version 3/24/14 4:48 PM
 */
public class DateUtil {

	private static final String[] weekDays = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };

	public static String getWeekOfDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return weekDays[w];
	}

	public static Date tomorrowOf(Date date) {
		return dateOffset(date, 1);
	}

	public static Date yesterdayOf(Date date) {
		return dateOffset(date, -1);
	}

	public static Date dateOffset(Date date, int offset) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int day = calendar.get(Calendar.DATE);
		calendar.set(Calendar.DATE, day + offset);
		return calendar.getTime();
	}
}
