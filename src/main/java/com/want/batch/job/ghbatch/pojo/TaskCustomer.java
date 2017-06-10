// ~ Package Declaration
// ==================================================

package com.want.batch.job.ghbatch.pojo;

import java.math.BigDecimal;
import java.util.Date;

// ~ Comments
// ==================================================

/**
 * 行程类别-稽核客户.
 * 
 * <pre>
 * 歷史紀錄：
 * 2010-3-12 Deli
 * 	新建檔案
 * </pre>
 * 
 * @author <pre>
 * SD
 * 	
 * PG
 * Deli
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
public class TaskCustomer {

	// ~ Static Fields
	// ==================================================

	private static final long serialVersionUID = -5840848700760155725L;

	// ~ Fields
	// ==================================================

	private Integer sid;
	private Integer taskDetailId;
	private String customerId;
	private String customerName;
	private Date sTime;
	private Date eTime;
	private String addrWrong;
	private String reasonAddr;
	private String hardCheck;
	private String reasonCheck;
	private String thirdName;
	private BigDecimal longitude;
	private BigDecimal latitude;

	// 2010-08-11 Deli add 是否刷码
	private String isBrushCode;

	/**
	 * Deli, 2011-1-5 <br>
	 * add 客户所在三级地
	 */
	private String customerThirdId;

	// 2011-03-02 Deli add 信控范围
	private String creditIds;

	/**
	 * Deli, 2011-5-17 <br>
	 * 客户不配合盘点
	 */
	private String mismatch;

	/**
	 * Deli, 2011-5-17 <br>
	 * 客户不配合盘点备注
	 */
	private String mismatchRemarks;

	/**
	 * Deli, 2011-5-17 <br>
	 * 有过期品
	 */
	private String isOutdate;

	/**
	 * Deli, 2011-5-17 <br>
	 * 过期金额
	 */
	private BigDecimal outdateMoney;

	/**
	 * Deli, 2011-5-17 <br>
	 * 过期品备注
	 */
	private String outdateRemarks;

	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================

	/**
	 * @return 傳回 creditIds。
	 */
	public String getCreditIds() {

		return creditIds;
	}

	/**
	 * @return 傳回 mismatch。
	 */
	public String getMismatch() {

		return mismatch;
	}

	/**
	 * @param mismatch
	 *          要設定的 mismatch。
	 */
	public void setMismatch(String mismatch) {

		this.mismatch = mismatch;
	}

	/**
	 * @return 傳回 mismatchRemarks。
	 */
	public String getMismatchRemarks() {

		return mismatchRemarks;
	}

	/**
	 * @param mismatchRemarks
	 *          要設定的 mismatchRemarks。
	 */
	public void setMismatchRemarks(String mismatchRemarks) {

		this.mismatchRemarks = mismatchRemarks;
	}

	/**
	 * @return 傳回 isOutdate。
	 */
	public String getIsOutdate() {

		return isOutdate;
	}

	/**
	 * @param isOutdate
	 *          要設定的 isOutdate。
	 */
	public void setIsOutdate(String isOutdate) {

		this.isOutdate = isOutdate;
	}

	/**
	 * @return 傳回 outdateMoney。
	 */
	public BigDecimal getOutdateMoney() {

		return outdateMoney;
	}

	/**
	 * @param outdateMoney
	 *          要設定的 outdateMoney。
	 */
	public void setOutdateMoney(BigDecimal outdateMoney) {

		this.outdateMoney = outdateMoney;
	}

	/**
	 * @return 傳回 outdateRemarks。
	 */
	public String getOutdateRemarks() {

		return outdateRemarks;
	}

	/**
	 * @param outdateRemarks
	 *          要設定的 outdateRemarks。
	 */
	public void setOutdateRemarks(String outdateRemarks) {

		this.outdateRemarks = outdateRemarks;
	}

	/**
	 * @param creditIds
	 *          要設定的 creditIds。
	 */
	public void setCreditIds(String creditIds) {

		this.creditIds = creditIds;
	}

	/**
	 * @return 傳回 customerThirdId。
	 */
	public String getCustomerThirdId() {

		return customerThirdId;
	}

	/**
	 * @param customerThirdId
	 *          要設定的 customerThirdId。
	 */
	public void setCustomerThirdId(String customerThirdId) {

		this.customerThirdId = customerThirdId;
	}

	/**
	 * @return 传回 isBrushCode。
	 */
	public String getIsBrushCode() {

		return isBrushCode;
	}

	/**
	 * @param isBrushCode
	 *          要设定的 isBrushCode。
	 */
	public void setIsBrushCode(String isBrushCode) {

		this.isBrushCode = isBrushCode;
	}

	/**
	 * @return 傳回 taskDetailId。
	 */
	public Integer getTaskDetailId() {

		return taskDetailId;
	}

	public String getThirdName() {

		return thirdName;
	}

	public void setThirdName(String thirdName) {

		this.thirdName = thirdName;
	}

	/**
	 * @param taskDetailId
	 *          要設定的 taskDetailId。
	 */
	public void setTaskDetailId(Integer taskDetailId) {

		this.taskDetailId = taskDetailId;
	}

	/**
	 * @return 傳回 customerId。
	 */
	public String getCustomerId() {

		return customerId;
	}

	/**
	 * @param customerId
	 *          要設定的 customerId。
	 */
	public void setCustomerId(String customerId) {

		this.customerId = customerId;
	}

	/**
	 * @return 傳回 customerName。
	 */
	public String getCustomerName() {

		return customerName;
	}

	/**
	 * @param customerName
	 *          要設定的 customerName。
	 */
	public void setCustomerName(String customerName) {

		this.customerName = customerName;
	}

	/**
	 * @return 傳回 sTime。
	 */
	public Date getsTime() {

		return sTime;
	}

	/**
	 * @param sTime
	 *          要設定的 sTime。
	 */
	public void setsTime(Date sTime) {

		this.sTime = sTime;
	}

	/**
	 * @return 傳回 eTime。
	 */
	public Date geteTime() {

		return eTime;
	}

	/**
	 * @param eTime
	 *          要設定的 eTime。
	 */
	public void seteTime(Date eTime) {

		this.eTime = eTime;
	}

	/**
	 * @return 傳回 longitude。
	 */
	public BigDecimal getLongitude() {

		return longitude;
	}

	/**
	 * @param longitude
	 *          要設定的 longitude。
	 */
	public void setLongitude(BigDecimal longitude) {

		this.longitude = longitude;
	}

	/**
	 * @return 傳回 latitude。
	 */
	public BigDecimal getLatitude() {

		return latitude;
	}

	/**
	 * @param latitude
	 *          要設定的 latitude。
	 */
	public void setLatitude(BigDecimal latitude) {

		this.latitude = latitude;
	}

	/**
	 * @return 傳回 addrWrong。
	 */
	public String getAddrWrong() {

		return addrWrong;
	}

	/**
	 * @param addrWrong
	 *          要設定的 addrWrong。
	 */
	public void setAddrWrong(String addrWrong) {

		this.addrWrong = addrWrong;
	}

	/**
	 * @return 傳回 reasonAddr。
	 */
	public String getReasonAddr() {

		return reasonAddr;
	}

	/**
	 * @param reasonAddr
	 *          要設定的 reasonAddr。
	 */
	public void setReasonAddr(String reasonAddr) {

		this.reasonAddr = reasonAddr;
	}

	public String getHardCheck() {

		return hardCheck;
	}

	public void setHardCheck(String hardCheck) {

		this.hardCheck = hardCheck;
	}

	/**
	 * @return 傳回 reasonCheck。
	 */
	public String getReasonCheck() {

		return reasonCheck;
	}

	/**
	 * @param reasonCheck
	 *          要設定的 reasonCheck。
	 */
	public void setReasonCheck(String reasonCheck) {

		this.reasonCheck = reasonCheck;
	}

	/**
	 * @return the sid
	 */
	public Integer getSid() {
		return sid;
	}

	/**
	 * @param sid the sid to set
	 */
	public void setSid(Integer sid) {
		this.sid = sid;
	}

}
