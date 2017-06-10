// ~ Package Declaration
// ==================================================

package com.want.batch.job.ghbatch.util;

import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;

// ~ Comments
// ==================================================

/**
 * 
 * 统一处理 Domain 中 可被共用且存在逻辑规则的工具类.
 * 
 * 
 * <pre>
 * 历史纪录：
 * 2010-3-12 Timothy
 * 	新建文件
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
public class DomainDateUtils {

	// ~ Static Fields
	// ==================================================

	// ~ Fields
	// ==================================================

	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================

	/**
	 * 
	 * 根据传入的日期，按照某种规则，获取新日期.
	 * <p>
	 * 注：由于规则难以以命名显示，故使用编号处理。
	 * </p>
	 * 
	 * <pre>
	 * 2010-3-3 Timothy
	 * 获取新日期规则：
	 *   传入日期为：5-14号，return "05"
	 *   传入日期为：15-24号，return "15"
	 *   传入日期为：26-31,1-4(月份要+1)，return "25"
	 * </pre>
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateByRule01(Date date) {

		if (date == null) {
			throw new NullPointerException("传入日期不可为空 ... ");
		}

		return DomainDateUtils.getDateByRule01(new DateTime(date));
	}

	/**
	 * 
	 * 根据传入的日期，按照某种规则，获取新日期.
	 * <p>
	 * 注：由于规则难以以命名显示，故使用编号处理。
	 * </p>
	 * 
	 * <pre>
	 * 2010-3-3 Timothy
	 * 获取新日期规则：
	 *   传入日期为：5-14号，return "05"
	 *   传入日期为：15-24号，return "15"
	 *   传入日期为：26-31,1-4(月份要+1)，return "25"
	 * </pre>
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateByRule01(Calendar date) {

		if (date == null) {
			throw new NullPointerException("传入日期不可为空 ... ");
		}

		return DomainDateUtils.getDateByRule01(new DateTime(date));
	}

	/**
	 * 
	 * 根据传入的日期，按照某种规则，获取新日期.
	 * <p>
	 * 注：由于规则难以以命名显示，故使用编号处理。
	 * </p>
	 * 2013-11-15 mirabelle add 当年节期间结束，二月份盘点由5号改为10号
	 * 2014-01-24 mirabelle update 取消当年节期间结束，二月份盘点由5号改为10号；2月份盘点日期修改为5,15,25
	 * <pre>
	 * 2010-3-3 Timothy
	 * 获取新日期规则：
	 *   传入日期为：5-14号，return "05"
	 *   传入日期为：15-24号，return "15"
	 *   传入日期为：26-31,1-4(月份要+1)，return "25"
	 * </pre>
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateByRule01(DateTime date) {

		if (date == null) {
			throw new NullPointerException("传入日期不可为空 ... ");
		}

		int dayOfMonth = date.getDayOfMonth();
		
		// 1-4号 范围内，为 上月25
		if (dayOfMonth < 5) {

			return date.minusMonths(1).withDayOfMonth(25).toDate();
		}

		// 5-14号 范围内，为 05
		if ((dayOfMonth >= 5) && (dayOfMonth <= 14)) {

			return date.withDayOfMonth(5).toDate();
		}

		// 15-24号 范围内，为 15
		if ((dayOfMonth >= 15) && (dayOfMonth <= 24)) {

			return date.withDayOfMonth(15).toDate();
		}

		// 25-月底 范围内，为 25
		return date.withDayOfMonth(25).toDate();
	}

}
