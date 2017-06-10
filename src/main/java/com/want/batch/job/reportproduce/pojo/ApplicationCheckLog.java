// ~ Package Declaration
// ==================================================

package com.want.batch.job.reportproduce.pojo;

import java.util.Date;

// ~ Comments
// ==================================================

/**
 * 申请单审核记录表.
 * 
 * <pre>
 * 歷史紀錄：
 * 2010-6-29 wonci
 * 	新建檔案
 * </pre>
 * 
 * @author 
 * <pre>
 * SD
 * 	
 * PG
 *	wonci
 * UT
 *
 * MA
 * </pre>
 * @version $Rev$
 *
 * <p/> $Id$
 *
 */
public class ApplicationCheckLog {

	//~ Static Fields
	// ==================================================
	
	private static final long serialVersionUID = 798033423496551995L;

	/**
	 * 2010-6-29, wonci <br> 
	 * 审核状态：驳回
	 */
	public static final String CHECK_STATUS_DISAGREE = "0";
	
	/**
	 * 2010-6-29, wonci <br> 
	 * 审核状态：核准
	 */
	public static final String CHECK_STATUS_AGREE = "1";
	
	/**
	 * Wendy, 2010-6-29 <br> 
	 * 审核状态：呈所长
	 */
	public static final String CHECK_STATUS_SENDTOSZ = "2";
	
	/**
	 * Lucien, 2010-7-12 <br> 
	 * 审核状态：呈总监
	 */
	public static final String CHECK_STATUS_SENDTOZJ = "3";

	/**
	 * Ryan, 2010-7-17 <br> 
	 * 客户提交状态：已提交
	 */
	public static final String CHECK_STATUS_SENDTOKH = "4";
	
	/**
	 * 2010-6-29, wonci <br> 
	 * 审核人员：销管
	 */
	public static final String CHECK_USER_XG = "1";
	
	/**
	 * 2010-6-29, wonci <br> 
	 * 审核人员：主任
	 */
	public static final String CHECK_USER_ZR = "2";
	
	/**
	 * 2010-6-29, wonci <br> 
	 * 审核人员：所长
	 */
	public static final String CHECK_USER_SZ = "3";
	
	/**
	 * 2010-6-29, wonci <br> 
	 * 审核人员：总监
	 */
	public static final String CHECK_USER_ZJ = "4";

	/**
	 * 2010-7-17, Ryan <br> 
	 * 提交人员：客户
	 */
	public static final String CHECK_USER_KH = "5";
	
	/**
	 * 2010-8-24, Lucien <br> 
	 * 总监
	 */
	public static final String USER_TYPE_ZJ = "7";
	
	/**
	 * wonci, 2011-3-31 <br> 
	 * 总部人员
	 */
	public static final String USER_TYPE_ZB = "8";
	
	// ~ Fields
	// ==================================================
	
	private Integer ApplicationSid;
	private String CheckUser;
	private String CheckStatus;
	private Date CheckDate;
	private String CheckUserType;
	
	// wonci 2010-11-10 add
	private String applicationSdNo;
	private String customerId;
	private Integer policyDivision;
	
	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================

	/**
	 * @return 傳回 applicationSid。
	 */
	public Integer getApplicationSid() {
	
		return ApplicationSid;
	}
	
	/**
	 * @param applicationSid 要設定的 applicationSid。
	 */
	public void setApplicationSid(Integer applicationSid) {
	
		ApplicationSid = applicationSid;
	}
	
	/**
	 * @return 傳回 checkUser。
	 */
	public String getCheckUser() {
	
		return CheckUser;
	}
	
	/**
	 * @param checkUser 要設定的 checkUser。
	 */
	public void setCheckUser(String checkUser) {
	
		CheckUser = checkUser;
	}
	
	/**
	 * @return 傳回 checkStatus。
	 */
	public String getCheckStatus() {
	
		return CheckStatus;
	}
	
	/**
	 * @param checkStatus 要設定的 checkStatus。
	 */
	public void setCheckStatus(String checkStatus) {
	
		CheckStatus = checkStatus;
	}
	
	/**
	 * @return 傳回 checkDate。
	 */
	public Date getCheckDate() {
	
		return CheckDate;
	}
	
	/**
	 * @param checkDate 要設定的 checkDate。
	 */
	public void setCheckDate(Date checkDate) {
	
		CheckDate = checkDate;
	}
	
	/**
	 * @return 傳回 checkUserType。
	 */
	public String getCheckUserType() {
	
		return CheckUserType;
	}
	
	/**
	 * @param checkUserType 要設定的 checkUserType。
	 */
	public void setCheckUserType(String checkUserType) {
	
		CheckUserType = checkUserType;
	}

	/**
	 * @return 傳回 applicationSdNo。
	 */
	public String getApplicationSdNo() {
	
		return applicationSdNo;
	}

	/**
	 * @param applicationSdNo 要設定的 applicationSdNo。
	 */
	public void setApplicationSdNo(String applicationSdNo) {
	
		this.applicationSdNo = applicationSdNo;
	}

	/**
	 * @return 傳回 customerId。
	 */
	public String getCustomerId() {
	
		return customerId;
	}

	/**
	 * @param customerId 要設定的 customerId。
	 */
	public void setCustomerId(String customerId) {
	
		this.customerId = customerId;
	}
	
	/**
	 * @return 傳回 policyDivision。
	 */
	public Integer getPolicyDivision() {
	
		return policyDivision;
	}

	/**
	 * @param policyDivision 要設定的 policyDivision。
	 */
	public void setPolicyDivision(Integer policyDivision) {
	
		this.policyDivision = policyDivision;
	}
}
