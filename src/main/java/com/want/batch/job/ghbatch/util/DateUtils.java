// ~ Package Declaration
// ==================================================

package com.want.batch.job.ghbatch.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

// ~ Comments
// ==================================================

/**
 * 
 * 用于 日期計算或比較的一些工具類別.
 * 
 * <pre>
 * 歷史紀錄：
 * 2009/3/12 Timothy
 * 	新建檔案
 * </pre>
 * 
 * @author <pre>
 * SD
 * 	Timothy
 * PG
 * 
 * UT
 * 
 * MA
 * </pre>
 * @version $Rev$
 * 
 *          <p/>
 *          $Id$
 * 
 */
public class DateUtils {

	// ~ Static Fields
	// ==================================================

	public static final long SECONDS_EVERY_MINUTE = 60;
	public static final long SECONDS_EVERY_HOUR = 60 * DateUtils.SECONDS_EVERY_MINUTE;
	public static final long SECONDS_EVERY_DAY = 24 * DateUtils.SECONDS_EVERY_HOUR;

	private static final long MAX_COUNT = 100;
	private static final long MAX_HOUR_SUBTRACT_SECONDS = DateUtils.MAX_COUNT * DateUtils.SECONDS_EVERY_HOUR - 1;
	private static final long MAX_DAY_SUBTRACT_SECONDS = DateUtils.MAX_COUNT * DateUtils.SECONDS_EVERY_DAY - 1;

	// ~ Fields
	// ==================================================

	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================

	/**
	 * 
	 * 获取到本周某天的日期.
	 * 
	 * <pre>
	 * 2010-3-24 Timothy
	 * </pre>
	 * 
	 * @param dayOfWeek
	 *          1~7 之间的某个整数，依次对应周一~周日
	 * @param date
	 * @return
	 * @throws Exception 
	 */
	public static Date shiftDayOfThisWeek(int dayOfWeek, Date date) throws Exception {

		if (date == null) {
			return null;
		}

		if ((dayOfWeek < 1) || (dayOfWeek > 7)) {

			throw new Exception(new IllegalArgumentException("dayOfWeek必须是 1 ~ 7中的某个数字，依次代表周一~周日"));
		}

		DateTime dateTime = new DateTime(date.getTime());

		return dateTime.minusDays(dateTime.getDayOfWeek() - 1).plusDays(dayOfWeek - 1).toDate();
	}

	/**
	 * <pre>
	 * 2010-3-24 Timothy
	 * 	获取当前系统日期所在周的某一天
	 * </pre>
	 * 
	 * @param dayOfWeek
	 *          1~7 之间的某个整数，依次对应周一~周日
	 * @return
	 * @throws Exception 
	 */
	public static Date shiftDayOfThisWeek(int dayOfWeek) throws Exception {

		return DateUtils.shiftDayOfThisWeek(dayOfWeek, new Date());
	}

	/**
	 * 
	 * 本周周一日期.
	 * 
	 * <pre>
	 * 2010-3-24 Timothy
	 * </pre>
	 * 
	 * @return
	 * @throws Exception 
	 */
	public static Date shiftMondayThisWeek() throws Exception {

		return DateUtils.shiftDayOfThisWeek(Calendar.MONDAY - 1);
	}

	/**
	 * 
	 * 本周周一日期.
	 * 
	 * <pre>
	 * 2010-3-24 Timothy
	 * </pre>
	 * 
	 * @param date
	 * @return
	 * @throws Exception 
	 */
	public static Date shiftMondayThisWeek(Date date) throws Exception {

		return DateUtils.shiftDayOfThisWeek(Calendar.MONDAY - 1, date);
	}

	/**
	 * 
	 * 本周周二日期.
	 * 
	 * <pre>
	 * 2010-3-24 Timothy
	 * </pre>
	 * 
	 * @return
	 * @throws Exception 
	 */
	public static Date shiftTuesdayThisWeek() throws Exception {

		return DateUtils.shiftDayOfThisWeek(Calendar.TUESDAY - 1);
	}

	/**
	 * 
	 * 本周周二日期.
	 * 
	 * <pre>
	 * 2010-3-24 Timothy
	 * </pre>
	 * 
	 * @param date
	 * @return
	 * @throws Exception 
	 */
	public static Date shiftTuesdayThisWeek(Date date) throws Exception {

		return DateUtils.shiftDayOfThisWeek(Calendar.TUESDAY - 1, date);
	}

	/**
	 * 
	 * 本周周三日期.
	 * 
	 * <pre>
	 * 2010-3-24 Timothy
	 * </pre>
	 * 
	 * @return
	 * @throws Exception 
	 */
	public static Date shiftWednesdayThisWeek() throws Exception {

		return DateUtils.shiftDayOfThisWeek(Calendar.WEDNESDAY - 1);
	}

	/**
	 * 
	 * 本周周三日期.
	 * 
	 * <pre>
	 * 2010-3-24 Timothy
	 * </pre>
	 * 
	 * @param date
	 * @return
	 * @throws Exception 
	 */
	public static Date shiftWednesdayThisWeek(Date date) throws Exception {

		return DateUtils.shiftDayOfThisWeek(Calendar.WEDNESDAY - 1, date);
	}

	/**
	 * 
	 * 本周周四日期.
	 * 
	 * <pre>
	 * 2010-3-24 Timothy
	 * </pre>
	 * 
	 * @return
	 * @throws Exception 
	 */
	public static Date shiftThursdayThisWeek() throws Exception {

		return DateUtils.shiftDayOfThisWeek(Calendar.THURSDAY - 1);
	}

	/**
	 * 
	 * 本周周四日期.
	 * 
	 * <pre>
	 * 2010-3-24 Timothy
	 * </pre>
	 * 
	 * @param date
	 * @return
	 * @throws Exception 
	 */
	public static Date shiftThursdayThisWeek(Date date) throws Exception {

		return DateUtils.shiftDayOfThisWeek(Calendar.THURSDAY - 1, date);
	}

	/**
	 * 
	 * 本周周五日期.
	 * 
	 * <pre>
	 * 2010-3-24 Timothy
	 * </pre>
	 * 
	 * @return
	 * @throws Exception 
	 */
	public static Date shiftFridayThisWeek() throws Exception {

		return DateUtils.shiftDayOfThisWeek(Calendar.FRIDAY - 1);
	}

	/**
	 * 
	 * 本周周五日期.
	 * 
	 * <pre>
	 * 2010-3-24 Timothy
	 * </pre>
	 * 
	 * @param date
	 * @return
	 * @throws Exception 
	 */
	public static Date shiftFridayThisWeek(Date date) throws Exception {

		return DateUtils.shiftDayOfThisWeek(Calendar.FRIDAY - 1, date);
	}

	/**
	 * 
	 * 本周周六日期.
	 * 
	 * <pre>
	 * 2010-3-24 Timothy
	 * </pre>
	 * 
	 * @return
	 * @throws Exception 
	 */
	public static Date shiftSaturdayThisWeek() throws Exception {

		return DateUtils.shiftDayOfThisWeek(Calendar.SATURDAY - 1);
	}

	/**
	 * 
	 * 本周周六日期.
	 * 
	 * <pre>
	 * 2010-3-24 Timothy
	 * </pre>
	 * 
	 * @param date
	 * @return
	 * @throws Exception 
	 */
	public static Date shiftSaturdayThisWeek(Date date) throws Exception {

		return DateUtils.shiftDayOfThisWeek(Calendar.SATURDAY - 1, date);
	}

	/**
	 * 
	 * 本周周日日期.
	 * 
	 * <pre>
	 * 2010-3-24 Timothy
	 * </pre>
	 * 
	 * @return
	 * @throws Exception 
	 */
	public static Date shiftSundayThisWeek() throws Exception {

		return DateUtils.shiftDayOfThisWeek(Calendar.SUNDAY + 7 - 1);
	}

	/**
	 * 
	 * 本周周日日期.
	 * 
	 * <pre>
	 * 2010-3-24 Timothy
	 * </pre>
	 * 
	 * @param date
	 * @return
	 * @throws Exception 
	 */
	public static Date shiftSundayThisWeek(Date date) throws Exception {

		return DateUtils.shiftDayOfThisWeek(Calendar.SUNDAY + 7 - 1, date);
	}

	/**
	 * <pre>
	 * 2009/4/27 Timothy
	 * 	獲取當前日期的Calendar
	 * </pre>
	 * 
	 * @param date
	 * @return
	 */
	public static Calendar getCalendarInstance() {

		return Calendar.getInstance();
	}

	/**
	 * <pre>
	 * 2010-3-2 Timothy
	 * 	獲取當前日期的Calendar，直接使用 field & amount 处理一个预期的Calendar
	 * </pre>
	 * 
	 * @param field
	 * @param amount
	 * @return
	 */
	public static Calendar getCalendarInstance(int field, int amount) {

		Calendar calendar = DateUtils.getCalendarInstance();

		calendar.add(field, amount);

		return calendar;
	}

	/**
	 * <pre>
	 * 2009/4/27 Timothy
	 * 	獲取指定日期的Calendar
	 * </pre>
	 * 
	 * @param date
	 * @return
	 */
	public static Calendar getCalendarInstance(Date date) {

		Calendar calendar = DateUtils.getCalendarInstance();

		if (date != null) {

			calendar.setTime(date);
		}

		return calendar;
	}

	/**
	 * <pre>
	 * 2009/4/27 Timothy
	 *  計算與當前系統時間的時間差，以最高顯示單位為小時的計數器形式顯示 [00:00:00 &tilde; 99:59:59]
	 * </pre>
	 * 
	 * @param calendar
	 * @return
	 */
	public static String hourCounter(Calendar calendar) {

		if (calendar == null) {
			return "";
		}

		return DateUtils.hourCounter(calendar, Calendar.getInstance());
	}

	/**
	 * <pre>
	 * 2009/4/27 Timothy
	 *  計算與當前系統時間的時間差，以最高顯示單位為小時的計數器形式顯示 [00:00:00 &tilde; 99:59:59]
	 * </pre>
	 * 
	 * @param date
	 * @return
	 */
	public static String hourCounter(Date date) {

		if (date == null) {
			return "";
		}

		return DateUtils.hourCounter(date, new Date());
	}

	/**
	 * <pre>
	 * 2009/4/27 Timothy
	 *  計算時間差，以最高顯示單位為小時的計數器形式顯示 [00:00:00 &tilde; 99:59:59]
	 * </pre>
	 * 
	 * @param calendar1
	 * @param calendar2
	 * @return
	 */
	public static String hourCounter(Calendar calendar1, Calendar calendar2) {

		if ((calendar1 == null) || (calendar2 == null)) {
			return "";
		}

		return DateUtils.hourCounter(calendar1.getTime(), calendar2.getTime());
	}

	/**
	 * <pre>
	 * 2009/3/12 Timothy
	 *  計算時間差，以最高顯示單位為小時的計數器形式顯示 [00:00:00 &tilde; 99:59:59]
	 * </pre>
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static String hourCounter(Date date1, Date date2) {

		if ((date1 == null) || (date2 == null)) {
			return "";
		}

		long allSeconds = DateUtils.subtractSecond(date1, date2);

		if (allSeconds >= DateUtils.MAX_HOUR_SUBTRACT_SECONDS) {

			return DateUtils.appendTime(DateUtils.MAX_COUNT - 1, 59, 59);
		}

		long minute = 60;
		long hour = 60 * minute;

		String hours = StringUtils.leftPad(String.valueOf(allSeconds / hour), 2, "0");
		long remainderHours = allSeconds % hour;

		String minutes = StringUtils.leftPad(String.valueOf(remainderHours / minute), 2, "0");
		String seconds = StringUtils.leftPad(String.valueOf(remainderHours % minute), 2, "0");

		return DateUtils.appendTime(hours, minutes, seconds);
	}

	/**
	 * <pre>
	 * 2009/4/27 Timothy
	 *  計算與當前系統時間的時間差，以最高顯示單位為小時的計數器形式顯示 [00:00:00:00 &tilde; 99:23:59:59]
	 * </pre>
	 * 
	 * @param calendar
	 * @return
	 */
	public static String dayCounter(Calendar calendar) {

		if (calendar == null) {
			return "";
		}

		return DateUtils.dayCounter(calendar, Calendar.getInstance());
	}

	/**
	 * <pre>
	 * 2009/4/27 Timothy
	 *  計算與當前系統時間的時間差，以最高顯示單位為小時的計數器形式顯示 [00:00:00:00 &tilde; 99:23:59:59]
	 * </pre>
	 * 
	 * @param date
	 * @return
	 */
	public static String dayCounter(Date date) {

		if (date == null) {
			return "";
		}

		return DateUtils.dayCounter(date, new Date());
	}

	/**
	 * <pre>
	 * 2009/4/27 Timothy
	 *  計算時間差，以最高顯示單位為小時的計數器形式顯示 [00:00:00:00 &tilde; 99:23:59:59]
	 * </pre>
	 * 
	 * @param calendar1
	 * @param calendar2
	 * @return
	 */
	public static String dayCounter(Calendar calendar1, Calendar calendar2) {

		if ((calendar1 == null) || (calendar2 == null)) {
			return "";
		}

		return DateUtils.dayCounter(calendar1.getTime(), calendar2.getTime());
	}

	/**
	 * <pre>
	 * 2009/3/12 Timothy
	 * 	計算時間差，最高顯示單位為天數的計數器 [00:00:00:00 &tilde; 99:23:59:59]
	 * </pre>
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static String dayCounter(Date date1, Date date2) {

		if ((date1 == null) || (date2 == null)) {
			return "";
		}

		long allSeconds = DateUtils.subtractSecond(date1, date2);

		if (allSeconds >= DateUtils.MAX_DAY_SUBTRACT_SECONDS) {

			return DateUtils.appendTime(DateUtils.MAX_COUNT - 1, 23, 59, 59);
		}

		long minute = 60;
		long hour = 60 * minute;
		long day = 24 * hour;

		String days = StringUtils.leftPad(String.valueOf(allSeconds / day), 2, "0");
		long remainderDays = allSeconds % day;

		String hours = StringUtils.leftPad(String.valueOf(remainderDays / hour), 2, "0");
		long remainderHours = remainderDays % hour;

		String minutes = StringUtils.leftPad(String.valueOf(remainderHours / minute), 2, "0");
		String seconds = StringUtils.leftPad(String.valueOf(remainderHours % minute), 2, "0");

		return DateUtils.appendTime(days, hours, minutes, seconds);
	}

	/**
	 * <pre>
	 * 2009/4/27 Timothy
	 * 	獲取兩個日期之間的秒數差
	 * </pre>
	 * 
	 * @param calendar1
	 * @param calendar2
	 * @return
	 */
	public static long subtractSecond(Calendar calendar1, Calendar calendar2) {

		if ((calendar1 == null) || (calendar2 == null)) {
			return 0;
		}

		return DateUtils.subtractSecond(calendar1.getTime(), calendar2.getTime());
	}

	/**
	 * <pre>
	 * 2009/3/12 Timothy
	 * 	獲取兩個日期之間的秒數差
	 * </pre>
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static long subtractSecond(Date date1, Date date2) {

		if ((date1 == null) || (date2 == null)) {
			return 0;
		}

		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date1);
		long dateSecond1 = calendar.getTimeInMillis();

		calendar.setTime(date2);
		long dateSecond2 = calendar.getTimeInMillis();

		return Math.abs(dateSecond1 - dateSecond2);
	}

	/**
	 * <pre>
	 * 2009/4/27 Timothy
	 * 	連接時間字串，用 : 分隔
	 * </pre>
	 * 
	 * @param timeCounts
	 * @return
	 */
	private static String appendTime(Object... timeCounts) {

		StringBuilder result = new StringBuilder();

		for (Object timeCount : timeCounts) {

			result.append(timeCount).append(ConstantString.COLON);
		}

		return StringUtils.removeEnd(result.toString(), ConstantString.COLON);
	}

	/**
	 * <pre>
	 * 2009/6/1 Timothy
	 * </pre>
	 * 
	 * @param dateFormat
	 * @param date
	 * @return
	 */
	public static String format(DateFormat dateFormat, Date date) {

		if (date == null) {

			return ConstantString.EMPTY;
		}

		DateFormat realFormatInstance = (dateFormat == null) ? (DateFormatEnum.DATE.getDateFormat()) : (dateFormat);

		return realFormatInstance.format(date);
	}

	/**
	 * <pre>
	 * 2009/6/1 Timothy
	 * </pre>
	 * 
	 * @param pattern
	 * @param date
	 * @return
	 */
	public static String format(String pattern, Date date) {

		if (date == null) {

			return ConstantString.EMPTY;
		}

		DateFormat realFormatInstance = (StringUtils.isBlank(pattern)) ? (DateFormatEnum.DATE.getDateFormat())
				: (new SimpleDateFormat(pattern));

		return realFormatInstance.format(date);
	}

	/**
	 * <pre>
	 * 2009/6/18 Timothy
	 * </pre>
	 * 
	 * @param dateFormat
	 * @param date
	 * @return
	 */
	public static String format(DateFormat dateFormat, Calendar date) {

		if (date == null) {

			return ConstantString.EMPTY;
		}

		return DateUtils.format(dateFormat, date.getTime());
	}

	/**
	 * <pre>
	 * 2009/6/18 Timothy
	 * </pre>
	 * 
	 * @param pattern
	 * @param date
	 * @return
	 */
	public static String format(String pattern, Calendar date) {

		if (date == null) {

			return ConstantString.EMPTY;
		}

		return DateUtils.format(pattern, date.getTime());
	}

	/**
	 * 
	 * 根据传入的 dateFormat，获取符合格式的date.
	 * 
	 * <pre>
	 * 2010-3-3 Timothy
	 * </pre>
	 * 
	 * @param dateFormat
	 * @param date
	 * @return
	 * @throws Exception 
	 */
	public static Date parse(DateFormat dateFormat, String date) throws Exception {

		if (StringUtils.isEmpty(date)) {
			return null;
		}

		DateFormat realFormatInstance = (dateFormat == null) ? (DateFormatEnum.DATE.getDateFormat()) : (dateFormat);

		try {
			return realFormatInstance.parse(date);
		}
		catch (ParseException pe) {

			pe.printStackTrace();

			throw new Exception("The date string didn't match pattern("
					+ ((SimpleDateFormat) dateFormat).toPattern()
					+ ")!", pe);
		}
	}

	/**
	 * 
	 * 根据传入的 pattern，获取符合格式的date.
	 * 
	 * <pre>
	 * 2010-3-3 Timothy
	 * </pre>
	 * 
	 * @param pattern
	 * @param date
	 * @return
	 * @throws Exception 
	 */
	public static Date parse(String pattern, String date) throws Exception {

		if (StringUtils.isEmpty(date)) {
			return null;
		}

		return DateUtils.parse((StringUtils.isBlank(pattern)) ? (DateFormatEnum.DATE.getDateFormat())
				: (new SimpleDateFormat(pattern)), date);
	}

}
