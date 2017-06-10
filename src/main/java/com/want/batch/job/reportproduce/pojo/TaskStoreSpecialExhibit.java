// ~ Package Declaration
// ==================================================

package com.want.batch.job.reportproduce.pojo;

import java.math.BigDecimal;
import java.util.Date;

// ~ Comments
// ==================================================

/**
 * 行程类别-终端特陈.
 * 
 * <pre>
 * 历史纪录：
 * 2010-3-17 Lucien
 * 	新建文件
 * </pre>
 * 
 * @author <pre>
 * SD
 * 	Lucien
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
public class TaskStoreSpecialExhibit {

	// ~ Static Fields
	// ==================================================

	private static final long serialVersionUID = 6108846685487055609L;

	// ~ Fields
	// ==================================================

	private Integer id;
	private Integer taskStoreListSid;
	private String customerId;
	private String customerName;
	private String specialIsOk;
	private String locationTypeName;
	private String displayTypeName;
	private Integer displayAcreage;
	private String displaySideCount;
	private String locationTypeNameResult;
	private String displayTypeNameResult;
	private BigDecimal displayAcreageResult;
	private String displaySideCountResult;

	// Lucien 2010-06-17 Add:新增栏位[SPECIAL_DISPLAY_LINE2_SID]
	private Integer specialDisplayLine2Sid;

	// Lucien 2010-06-22 Add:新增
	private Integer attSid;
	private String displayTypeTblNamea;
	private String displayTypeTblNameb;

	/**
	 * Wendy, 2010-4-13 <br>
	 * 特陈所属事业部。
	 */
	private Integer divsionSid;

	// Deli 2010-07-22 add
	private Integer storeDisplaySid;

	// 2010-11-15 Deli add 展架形式
	private String displayParamId;
	private String displayParamIdResult;

	// 2011-02-17 Deli add
	private Date salesInputDatetime;

	// 2012-12-07 John add
	private String sdNo;
	
	
	private String createUser;
	private Date createDate;
	private String updateUser;
	private Date updateDate;

	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================

	/**
	 * @return 傳回 salesInputDatetime。
	 */
	public Date getSalesInputDatetime() {

		return salesInputDatetime;
	}

	/**
	 * @return 传回 sdNo。
	 */
	public String getSdNo() {

		return sdNo;
	}

	/**
	 * @param sdNo
	 *          要设定的 sdNo。
	 */
	public void setSdNo(String sdNo) {

		this.sdNo = sdNo;
	}

	/**
	 * @param salesInputDatetime
	 *          要設定的 salesInputDatetime。
	 */
	public void setSalesInputDatetime(Date salesInputDatetime) {

		this.salesInputDatetime = salesInputDatetime;
	}

	public Integer getSpecialDisplayLine2Sid() {

		return specialDisplayLine2Sid;
	}

	/**
	 * @return 傳回 displayParamId。
	 */
	public String getDisplayParamId() {

		return displayParamId;
	}

	/**
	 * @return 傳回 displayParamIdResult。
	 */
	public String getDisplayParamIdResult() {

		return displayParamIdResult;
	}

	/**
	 * @param displayParamId
	 *          要設定的 displayParamId。
	 */
	public void setDisplayParamId(String displayParamId) {

		this.displayParamId = displayParamId;
	}

	/**
	 * @param displayParamIdResult
	 *          要設定的 displayParamIdResult。
	 */
	public void setDisplayParamIdResult(String displayParamIdResult) {

		this.displayParamIdResult = displayParamIdResult;
	}

	public void setSpecialDisplayLine2Sid(Integer specialDisplayLine2Sid) {

		this.specialDisplayLine2Sid = specialDisplayLine2Sid;
	}

	/**
	 * @return 传回 taskStoreListSid。
	 */
	public Integer getTaskStoreListSid() {

		return this.taskStoreListSid;
	}

	/**
	 * @param taskStoreListSid
	 *          要设定的 taskStoreListSid。
	 */
	public void setTaskStoreListSid(Integer taskStoreListSid) {

		this.taskStoreListSid = taskStoreListSid;
	}

	/**
	 * @return 传回 customerId。
	 */
	public String getCustomerId() {

		return this.customerId;
	}

	/**
	 * @param customerId
	 *          要设定的 customerId。
	 */
	public void setCustomerId(String customerId) {

		this.customerId = customerId;
	}

	/**
	 * @return 传回 customerName。
	 */
	public String getCustomerName() {

		return this.customerName;
	}

	/**
	 * @param customerName
	 *          要设定的 customerName。
	 */
	public void setCustomerName(String customerName) {

		this.customerName = customerName;
	}

	/**
	 * @return 传回 specialIsOk。
	 */
	public String getSpecialIsOk() {

		return this.specialIsOk;
	}

	/**
	 * @param specialIsOk
	 *          要设定的 specialIsOk。
	 */
	public void setSpecialIsOk(String specialIsOk) {

		this.specialIsOk = specialIsOk;
	}

	/**
	 * @return 传回 locationTypeName。
	 */
	public String getLocationTypeName() {

		return this.locationTypeName;
	}

	/**
	 * @param locationTypeName
	 *          要设定的 locationTypeName。
	 */
	public void setLocationTypeName(String locationTypeName) {

		this.locationTypeName = locationTypeName;
	}

	/**
	 * @return 传回 displayTypeName。
	 */
	public String getDisplayTypeName() {

		return this.displayTypeName;
	}

	/**
	 * @param displayTypeName
	 *          要设定的 displayTypeName。
	 */
	public void setDisplayTypeName(String displayTypeName) {

		this.displayTypeName = displayTypeName;
	}

	/**
	 * @return 传回 displayAcreage。
	 */
	public Integer getDisplayAcreage() {

		return this.displayAcreage;
	}

	/**
	 * @param displayAcreage
	 *          要设定的 displayAcreage。
	 */
	public void setDisplayAcreage(Integer displayAcreage) {

		this.displayAcreage = displayAcreage;
	}

	/**
	 * @return 传回 displaySideCount。
	 */
	public String getDisplaySideCount() {

		return this.displaySideCount;
	}

	/**
	 * @param displaySideCount
	 *          要设定的 displaySideCount。
	 */
	public void setDisplaySideCount(String displaySideCount) {

		this.displaySideCount = displaySideCount;
	}

	/**
	 * @return 传回 locationTypeNameResult。
	 */
	public String getLocationTypeNameResult() {

		return this.locationTypeNameResult;
	}

	/**
	 * @param locationTypeNameResult
	 *          要设定的 locationTypeNameResult。
	 */
	public void setLocationTypeNameResult(String locationTypeNameResult) {

		this.locationTypeNameResult = locationTypeNameResult;
	}

	/**
	 * @return 传回 displayTypeNameResult。
	 */
	public String getDisplayTypeNameResult() {

		return this.displayTypeNameResult;
	}

	/**
	 * @param displayTypeNameResult
	 *          要设定的 displayTypeNameResult。
	 */
	public void setDisplayTypeNameResult(String displayTypeNameResult) {

		this.displayTypeNameResult = displayTypeNameResult;
	}

	/**
	 * @return 传回 displaySideCountResult。
	 */
	public String getDisplaySideCountResult() {

		return this.displaySideCountResult;
	}

	/**
	 * @return 傳回 displayAcreageResult。
	 */
	public BigDecimal getDisplayAcreageResult() {

		return displayAcreageResult;
	}

	/**
	 * @param displayAcreageResult
	 *          要設定的 displayAcreageResult。
	 */
	public void setDisplayAcreageResult(BigDecimal displayAcreageResult) {

		this.displayAcreageResult = displayAcreageResult;
	}

	/**
	 * @param displaySideCountResult
	 *          要设定的 displaySideCountResult。
	 */
	public void setDisplaySideCountResult(String displaySideCountResult) {

		this.displaySideCountResult = displaySideCountResult;
	}

	/**
	 * @return 传回 divsionSid(特陈所属事业部)。
	 */
	public Integer getDivsionSid() {

		return this.divsionSid;
	}

	/**
	 * @param divsionSid
	 *          要设定的 divsionSid(特陈所属事业部)。
	 */
	public void setDivsionSid(Integer divsionSid) {

		this.divsionSid = divsionSid;
	}

	/**
	 * @return 传回 storeDisplaySid。
	 */
	public Integer getStoreDisplaySid() {

		return storeDisplaySid;
	}

	/**
	 * @param storeDisplaySid
	 *          要设定的 storeDisplaySid。
	 */
	public void setStoreDisplaySid(Integer storeDisplaySid) {

		this.storeDisplaySid = storeDisplaySid;
	}

	public Integer getAttSid() {

		return attSid;
	}

	public void setAttSid(Integer attSid) {

		this.attSid = attSid;
	}

	public String getDisplayTypeTblNamea() {

		return displayTypeTblNamea;
	}

	public void setDisplayTypeTblNamea(String displayTypeTblNamea) {

		this.displayTypeTblNamea = displayTypeTblNamea;
	}

	public String getDisplayTypeTblNameb() {

		return displayTypeTblNameb;
	}

	public void setDisplayTypeTblNameb(String displayTypeTblNameb) {

		this.displayTypeTblNameb = displayTypeTblNameb;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
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
}
