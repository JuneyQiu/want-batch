// ~ Package Declaration
// ==================================================

package com.want.batch.job.ghbatch.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

// ~ Comments
// ==================================================

/**
 * 
 * 定义专案中使用的日期格式，并提供对应的Format物件.
 * 
 * <pre>
 * 歷史紀錄：
 * 2009/4/22 Timothy
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
public enum DateFormatEnum {

	// ~ Enums
	// ==================================================

	/**
	 * Timothy, 2009/4/22 <br>
	 * 日期
	 */
	DATE("yyyy%sMM%sdd"),

	/**
	 * Timothy, 2009/4/22 <br>
	 * 無樣式的日期
	 */
	DATE_NO_PARTITION("yyyyMMdd"),

	/**
	 * Timothy, 2009/4/22 <br>
	 * 時間，精確到秒的
	 */
	TIME("HH:mm:ss"),

	/**
	 * Timothy, 2009/4/22 <br>
	 * 無樣式的時間，精確到秒的
	 */
	TIME_NO_PARTITION("HHmmss"),

	/**
	 * Timothy, 2009/4/22 <br>
	 * 日期時間，精確到秒的
	 */
	DATETIME("yyyy%sMM%sdd HH:mm:ss"),

	/**
	 * Timothy, 2009/4/22 <br>
	 * 無樣式的日期時間，精確到秒的
	 */
	DATETIME_NO_PARTITION("yyyyMMddHHmmss"),

	/**
	 * Timothy, 2009/4/22 <br>
	 * 時間，精確到毫秒的
	 */
	TIME_ALL("HH:mm:ss.SSS"),

	/**
	 * Timothy, 2009/4/22 <br>
	 * 無樣式的時間，精確到毫秒的
	 */
	TIME_ALL_NO_PARTITION("HHmmssSSS"),

	/**
	 * Timothy, 2009/4/22 <br>
	 * 日期時間，精確到毫秒的
	 */
	DATETIME_ALL("yyyy%sMM%sdd HH:mm:ss.SSS"),

	/**
	 * Timothy, 2009/4/22 <br>
	 * 無樣式的日期時間，精確到毫秒的
	 */
	DATETIME_ALL_NO_PARTITION("yyyyMMddHHmmssSSS");

	// ~ Static Fields
	// ==================================================

	// ~ Fields
	// ==================================================

	private String pattern;

	private DateFormat dateFormat;

	// ~ Constructors
	// ==================================================

	private DateFormatEnum(String pattern) {

		String dateSeparator = StringUtils.defaultIfEmpty(ReadResourceUtils
			.getProjectProperties()
				.getProperty("project.date.separator"), ConstantString.OBLIQUE);

		this.pattern = String.format(pattern, dateSeparator, dateSeparator);
		this.dateFormat = new SimpleDateFormat(this.getPattern());
	}

	// ~ Methods
	// ==================================================

	/**
	 * <pre>
	 * 2009/4/22 Timothy
	 * </pre>
	 * 
	 * @param date
	 * @return
	 */
	public String format(Date date) {

		if (date == null) {

			return ConstantString.EMPTY;
		}

		return this.getDateFormat().format(date);
	}

	/**
	 * <pre>
	 * 2009/4/23 Timothy
	 * </pre>
	 * 
	 * @param calendar
	 * @return
	 */
	public String format(Calendar calendar) {

		if (calendar == null) {

			return ConstantString.EMPTY;
		}

		return this.format(calendar.getTime());
	}

	/**
	 * <pre>
	 * 2009/4/23 Timothy
	 * 	獲取被格式化後的當前系統時間
	 * </pre>
	 * 
	 * @return
	 */
	public String getCurrentDate() {

		return this.format(new Date());
	}

	/**
	 * @return 傳回 pattern。
	 */
	public String getPattern() {

		return this.pattern;
	}

	/**
	 * @return 傳回 dateFormat。
	 */
	public DateFormat getDateFormat() {

		return this.dateFormat;
	}

	/**
	 * <pre>
	 * 2009/5/12 Timothy
	 * </pre>
	 * 
	 * @param date
	 * @throws VisualSoftComponentException
	 *           當傳入字串與當前模式不匹配時！
	 * @return if date is blank, return null!
	 * @throws Exception 
	 */
	public Date parse(String date) throws Exception {

		if (StringUtils.isBlank(date)) {

			return null;
		}

		try {

			return this.getDateFormat().parse(date);
		}
		catch (ParseException pe) {

			pe.printStackTrace();

			throw new Exception("The date string didn't match pattern(" + this.getPattern() + ")!", pe);
		}
	}

	/**
	 * <pre>
	 * 2010-3-27 Timothy
	 * 按照当前的 format pattern 比对两个日期大小
	 * </pre>
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public int compareTo(Date date1, Date date2) {

		return this.format(date1).compareTo(this.format(date2));
	}

	/**
	 * <pre>
	 * 2010-3-27 Timothy
	 * 按照当前的 format pattern 比对两个日期大小
	 * </pre>
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public int compareTo(Calendar date1, Calendar date2) {

		return this.format(date1).compareTo(this.format(date2));
	}

	/**
	 * <pre>
	 * 2010-3-27 Timothy
	 * 按照当前的 format pattern 与当前系统日期进行比对
	 * </pre>
	 * 
	 * @param date
	 * @return
	 */
	public int compareTo(Date date) {

		return this.getCurrentDate().compareTo(this.format(date));
	}

	/**
	 * <pre>
	 * 2010-3-27 Timothy
	 * 按照当前的 format pattern 与当前系统日期进行比对
	 * </pre>
	 * 
	 * @param calendar
	 * @return
	 */
	public int compareTo(Calendar calendar) {

		return this.getCurrentDate().compareTo(this.format(calendar));
	}

	/**
	 * <pre>
	 * 2009/4/23 Timothy
	 * 	返回被格式化後的當前系統時間
	 * </pre>
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {

		return this.getCurrentDate();
	}

}
