// ~ Package Declaration
// ==================================================

package com.want.batch.job.ghbatch.pojo;

import java.math.BigDecimal;
import java.util.Date;

// ~ Comments
// ==================================================

/**
 * 
 * 行程类别-稽核客户-产品清单.
 * 
 * <pre>
 * 历史纪录：
 * 2010-4-8 Timothy
 * 	新建文件
 * 2012-09-28 mirabelle update productQtyEnd,diffQty类型由int改为BigDecimal
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
public class TaskCustomerProduct {

	// ~ Static Fields
	// ==================================================

	private static final long serialVersionUID = -7165870596514767709L;

	/**
	 * Timothy, 2010-4-8 <br>
	 * 稽核结果 - 1 正常
	 */
	public static final String RESULT_NORMAL = "1";

	/**
	 * Timothy, 2010-4-8 <br>
	 * 稽核结果 - 0 异常
	 */
	public static final String RESULT_ABNORMAL = "0";

	/**
	 * Timothy, 2010-4-8 <br>
	 * 惩处意见 - 01：申诫
	 */
	public static final String PUNISHMENT_REPRIMAND = "01";

	/**
	 * Timothy, 2010-4-8 <br>
	 * 惩处意见 - 02：小过
	 */
	public static final String PUNISHMENT_DEMERIT_LESS = "02";

	/**
	 * Timothy, 2010-4-8 <br>
	 * 惩处意见 - 03：大过
	 */
	public static final String PUNISHMENT_DEMERIT_MORE = "03";

	/**
	 * Timothy, 2010-4-8 <br>
	 * 惩处意见 - 04：开除
	 */
	public static final String PUNISHMENT_DISMISS = "04";

	/**
	 * Timothy, 2010-4-8 <br>
	 * 差异率 的 小数保留位数
	 */
	public static final int DIFF_RATE_SCALE = 2;

	/**
	 * Timothy, 2010-4-8 <br>
	 * 判断 稽核结果正常 or 异常的临界值
	 */
	public static final BigDecimal DIFF_RATE_THRESHOLD = new BigDecimal("0.1");

	/**
	 * Timothy, 2010-4-8 <br>
	 * 判断 稽核结果正常 or 异常的临界值
	 */
	public static final int RECENTLY_QTY_AVG_PERCENT = 20;

	public static final int RECENTLY_QTY_AVG_MONTHS = 3;

	// ~ Fields
	// ==================================================

	private Integer sid;
	private Integer taskDetailId;
	private Integer taskCustomerSid;
	private String lv5Id;
	private Integer totalQty;
	private String checkYearMonth;
	private String checkDay;
	private Integer productQtyLast;
	private BigDecimal productQtyEnd;
	private Integer recentlyQtyAvg;
	private Integer systemQtyTotal;
	private BigDecimal diffQty;
	private BigDecimal diffRate;
	private String result;
	private String reason;
	private String punishment;

	// 2010-12-10 Deli add
	private String isImportant;
	private String checkUser;
	private Date checkDate;
	private Date taskDate;

	// 2011-01-12 Deli add 客户类型(事业部)
	private Integer customerDivision;

	private BigDecimal productQtyProcess;
	
	private Date updateDate;
	private String updateUser;
	
	private Date createDate;
	private String createUser;
	
	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================

	/**
	 * @return 傳回 customerDivsion。
	 */
	public Integer getCustomerDivision() {

		return customerDivision;
	}

	/**
	 * @param customerDivsion
	 *          要設定的 customerDivsion。
	 */
	public void setCustomerDivision(Integer customerDivision) {

		this.customerDivision = customerDivision;
	}

	/**
	 * @return 傳回 taskDate。
	 */
	public Date getTaskDate() {

		return taskDate;
	}

	/**
	 * @param taskDate
	 *          要設定的 taskDate。
	 */
	public void setTaskDate(Date taskDate) {

		this.taskDate = taskDate;
	}

	/**
	 * @return 傳回 checkUser。
	 */
	public String getCheckUser() {

		return checkUser;
	}

	/**
	 * @return 傳回 checkDate。
	 */
	public Date getCheckDate() {

		return checkDate;
	}

	/**
	 * @param checkUser
	 *          要設定的 checkUser。
	 */
	public void setCheckUser(String checkUser) {

		this.checkUser = checkUser;
	}

	/**
	 * @param checkDate
	 *          要設定的 checkDate。
	 */
	public void setCheckDate(Date checkDate) {

		this.checkDate = checkDate;
	}

	/**
	 * @return 傳回 isImportant。
	 */
	public String getIsImportant() {

		return isImportant;
	}

	/**
	 * @param isImportant
	 *          要設定的 isImportant。
	 */
	public void setIsImportant(String isImportant) {

		this.isImportant = isImportant;
	}

	/**
	 * @return 传回 sid。
	 */
	public Integer getSid() {

		return this.sid;
	}

	/**
	 * @param sid
	 *          要设定的 sid。
	 */
	public void setSid(Integer sid) {

		this.sid = sid;
	}

	/**
	 * @return 传回 taskDetailId。
	 */
	public Integer getTaskDetailId() {

		return this.taskDetailId;
	}

	/**
	 * @param taskDetailId
	 *          要设定的 taskDetailId。
	 */
	public void setTaskDetailId(Integer taskDetailId) {

		this.taskDetailId = taskDetailId;
	}

	/**
	 * @return 传回 taskCustomerSid。
	 */
	public Integer getTaskCustomerSid() {

		return this.taskCustomerSid;
	}

	/**
	 * @param taskCustomerSid
	 *          要设定的 taskCustomerSid。
	 */
	public void setTaskCustomerSid(Integer taskCustomerSid) {

		this.taskCustomerSid = taskCustomerSid;
	}

	/**
	 * @return 传回 lv5Id。
	 */
	public String getLv5Id() {

		return this.lv5Id;
	}

	/**
	 * @param lv5Id
	 *          要设定的 lv5Id。
	 */
	public void setLv5Id(String lv5Id) {

		this.lv5Id = lv5Id;
	}

	/**
	 * @return 传回 totalQty。
	 */
	public Integer getTotalQty() {

		return this.totalQty;
	}

	/**
	 * @param totalQty
	 *          要设定的 totalQty。
	 */
	public void setTotalQty(Integer totalQty) {

		this.totalQty = totalQty;
	}

	/**
	 * @return 传回 checkYearMonth。
	 */
	public String getCheckYearMonth() {

		return this.checkYearMonth;
	}

	/**
	 * @param checkYearMonth
	 *          要设定的 checkYearMonth。
	 */
	public void setCheckYearMonth(String checkYearMonth) {

		this.checkYearMonth = checkYearMonth;
	}

	/**
	 * @return 传回 checkDay。
	 */
	public String getCheckDay() {

		return this.checkDay;
	}

	/**
	 * @param checkDay
	 *          要设定的 checkDay。
	 */
	public void setCheckDay(String checkDay) {

		this.checkDay = checkDay;
	}

	/**
	 * @return 传回 productQtyLast。
	 */
	public Integer getProductQtyLast() {

		return this.productQtyLast;
	}

	/**
	 * @param productQtyLast
	 *          要设定的 productQtyLast。
	 */
	public void setProductQtyLast(Integer productQtyLast) {

		this.productQtyLast = productQtyLast;
	}

	/**
	 * @return 传回 productQtyEnd。
	 */
	public BigDecimal getProductQtyEnd() {

		return this.productQtyEnd;
	}

	/**
	 * @param productQtyEnd
	 *          要设定的 productQtyEnd。
	 */
	public void setProductQtyEnd(BigDecimal productQtyEnd) {

		this.productQtyEnd = productQtyEnd;
	}

	/**
	 * @return 传回 recentlyQtyAvg。
	 */
	public Integer getRecentlyQtyAvg() {

		return this.recentlyQtyAvg;
	}

	/**
	 * @param recentlyQtyAvg
	 *          要设定的 recentlyQtyAvg。
	 */
	public void setRecentlyQtyAvg(Integer recentlyQtyAvg) {

		this.recentlyQtyAvg = recentlyQtyAvg;
	}

	/**
	 * @return 传回 systemQtyTotal。
	 */
	public Integer getSystemQtyTotal() {

		return this.systemQtyTotal;
	}

	/**
	 * @param systemQtyTotal
	 *          要设定的 systemQtyTotal。
	 */
	public void setSystemQtyTotal(Integer systemQtyTotal) {

		this.systemQtyTotal = systemQtyTotal;
	}

	/**
	 * @return 传回 diffQty。
	 */
	public BigDecimal getDiffQty() {

		return this.diffQty;
	}

	/**
	 * @param diffQty
	 *          要设定的 diffQty。
	 */
	public void setDiffQty(BigDecimal diffQty) {

		this.diffQty = diffQty;
	}

	/**
	 * @return 传回 diffRate。
	 */
	public BigDecimal getDiffRate() {

		return this.diffRate;
	}

	/**
	 * @param diffRate
	 *          要设定的 diffRate。
	 */
	public void setDiffRate(BigDecimal diffRate) {

		this.diffRate = diffRate;
	}

	/**
	 * @return 传回 result。
	 */
	public String getResult() {

		return this.result;
	}

	/**
	 * @param result
	 *          要设定的 result。
	 */
	public void setResult(String result) {

		this.result = result;
	}

	/**
	 * @return 传回 reason。
	 */
	public String getReason() {

		return this.reason;
	}

	/**
	 * @param reason
	 *          要设定的 reason。
	 */
	public void setReason(String reason) {

		this.reason = reason;
	}

	/**
	 * @return 传回 punishment。
	 */
	public String getPunishment() {

		return this.punishment;
	}

	/**
	 * @param punishment
	 *          要设定的 punishment。
	 */
	public void setPunishment(String punishment) {

		this.punishment = punishment;
	}

	/**
	 * @return the productQtyProcess
	 */
	public BigDecimal getProductQtyProcess() {
		return productQtyProcess;
	}

	/**
	 * @param productQtyProcess the productQtyProcess to set
	 */
	public void setProductQtyProcess(BigDecimal productQtyProcess) {
		this.productQtyProcess = productQtyProcess;
	}

	/**
	 * @return the updateDate
	 */
	public Date getUpdateDate() {
		return updateDate;
	}

	/**
	 * @param updateDate the updateDate to set
	 */
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	/**
	 * @return the updateUser
	 */
	public String getUpdateUser() {
		return updateUser;
	}

	/**
	 * @param updateUser the updateUser to set
	 */
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	/**
	 * @return the createDate
	 */
	public Date getCreateDate() {
		return createDate;
	}

	/**
	 * @param createDate the createDate to set
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	/**
	 * @return the createUser
	 */
	public String getCreateUser() {
		return createUser;
	}

	/**
	 * @param createUser the createUser to set
	 */
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
}
