package com.want.batch.job.archive.storemgr.pojo;

import java.util.Date;

import com.want.batch.job.archive.storemgr.util.Constant;


public class SdActualUnCheckRpt {
	private long sid=Constant.DEFAULT_LONG;
	private int divisionSid=Constant.DEFAULT_INT;
	private String companyId="";
	private String branchId="";
	private String thirdId="";
	private String thirdName="";
	private String customerId="";
	private String sdNo="";
	private String empId="";
	private String empName="";
	private String empStatus="";
	private String storeId="";
	private String storeName="";
	private String isLock="";
	private int locationTypeSid=Constant.DEFAULT_INT;
	private int displayTypeSid=Constant.DEFAULT_INT;
	private double displayAcreage=Constant.DEFAULT_DOUBLE;
	private double displaySideCount=Constant.DEFAULT_DOUBLE;
	private int displayParamId=Constant.DEFAULT_INT;
	private int visitCount=Constant.DEFAULT_INT;
	private Date createDate=null;
	
	// 2014-06-16 mirabelle add begin
	private String mdm_store_id="";
	private int store_display_sid=0;
	private Date first_input_date;
	// 2014-06-16 mirabelle add end
	
	private String yearMonth="";
	private int day=Constant.DEFAULT_INT;
	
	
	public SdActualUnCheckRpt initDay(int day){
		this.setDay(day);
		return this;
	}
	public SdActualUnCheckRpt initYearMonth(String yearMonth){
		this.setYearMonth(yearMonth);
		return this;
	}
	public SdActualUnCheckRpt initCreateDate(Date createDate){
		this.setCreateDate(createDate);
		return this;
	}
	public SdActualUnCheckRpt initVisitCount(int visitCount){
		this.setVisitCount(visitCount);
		return this;
	}
	public SdActualUnCheckRpt initDisplayParamId(int displayParamId){
		this.setDisplayParamId(displayParamId);
		return this;
	}
	public SdActualUnCheckRpt initDisplaySideCount(double displaySideCount){
		this.setDisplaySideCount(displaySideCount);
		return this;
	}
	public SdActualUnCheckRpt initDisplayAcreage(double displayAcreage){
		this.setDisplayAcreage(displayAcreage);
		return this;
	}
	public SdActualUnCheckRpt initDisplayTypeSid(int displayTypeSid){
		this.setDisplayTypeSid(displayTypeSid);
		return this;
	}
	public SdActualUnCheckRpt initLocationTypeSid(int locationTypeSid){
		this.setLocationTypeSid(locationTypeSid);
		return this;
	}
	public SdActualUnCheckRpt initIsLock(String isLock){
		this.setIsLock(isLock);
		return this;
	}
	public SdActualUnCheckRpt initStoreName(String storeName){
		this.setStoreName(storeName);
		return this;
	}
	public SdActualUnCheckRpt initStoreId(String storeId){
		this.setStoreId(storeId);
		return this;
	}
	public SdActualUnCheckRpt initEmpStatus(String empStatus){
		this.setEmpStatus(empStatus);
		return this;
	}
	public SdActualUnCheckRpt initEmpName(String empName){
		this.setEmpName(empName);
		return this;
	}
	public SdActualUnCheckRpt initEmpId(String empId){
		this.setEmpId(empId);
		return this;
	}
	public SdActualUnCheckRpt initSdNo(String sdNo){
		this.setSdNo(sdNo);
		return this;
	}
	public SdActualUnCheckRpt initCustomerId(String customerId){
		this.setCustomerId(customerId);
		return this;
	}
	public SdActualUnCheckRpt initThirdName(String thirdName){
		this.setThirdName(thirdName);
		return this;
	}
	public SdActualUnCheckRpt initThirdId(String thirdId){
		this.setThirdId(thirdId);
		return this;
	}
	public SdActualUnCheckRpt initBranchId(String branchId){
		this.setBranchId(branchId);
		return this;
	}
	public SdActualUnCheckRpt initCompanyId(String companyId){
		this.setCompanyId(companyId);
		return this;
	}
	public SdActualUnCheckRpt initDivisionSid(int divisionSid){
		this.setDivisionSid(divisionSid);
		return this;
	}
	public SdActualUnCheckRpt initSid(long sid){
		this.setSid(sid);
		return this;
	}
	// 2014-06-17 mirabelle add begin
	public SdActualUnCheckRpt initMdmStoreId(String mdm_store_id){
		
		this.setMdm_store_id(mdm_store_id);
		return this;
	}
	
	public SdActualUnCheckRpt initStoreDisplaySid(int store_display_sid) {
		
		this.setStore_display_sid(store_display_sid);
		return this;
	}
	
	public SdActualUnCheckRpt initFirstInputDate(Date first_input_date) {
		
		this.setFirst_input_date(first_input_date);
		return this;
	}
	// 2014-06-17 mirabelle add end
	
	public long getSid() {
		return sid;
	}
	public void setSid(long sid) {
		this.sid = sid;
	}
	public int getDivisionSid() {
		return divisionSid;
	}
	public void setDivisionSid(int divisionSid) {
		this.divisionSid = divisionSid;
	}
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public String getBranchId() {
		return branchId;
	}
	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
	public String getThirdId() {
		return thirdId;
	}
	public void setThirdId(String thirdId) {
		this.thirdId = thirdId;
	}
	public String getThirdName() {
		return thirdName;
	}
	public void setThirdName(String thirdName) {
		this.thirdName = thirdName;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getSdNo() {
		return sdNo;
	}
	public void setSdNo(String sdNo) {
		this.sdNo = sdNo;
	}
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	public String getEmpName() {
		return empName;
	}
	public void setEmpName(String empName) {
		this.empName = empName;
	}
	public String getEmpStatus() {
		return empStatus;
	}
	public void setEmpStatus(String empStatus) {
		this.empStatus = empStatus;
	}
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getIsLock() {
		return isLock;
	}
	public void setIsLock(String isLock) {
		this.isLock = isLock;
	}
	public int getLocationTypeSid() {
		return locationTypeSid;
	}
	public void setLocationTypeSid(int locationTypeSid) {
		this.locationTypeSid = locationTypeSid;
	}
	public int getDisplayTypeSid() {
		return displayTypeSid;
	}
	public void setDisplayTypeSid(int displayTypeSid) {
		this.displayTypeSid = displayTypeSid;
	}
	public double getDisplayAcreage() {
		return displayAcreage;
	}
	public void setDisplayAcreage(double displayAcreage) {
		this.displayAcreage = displayAcreage;
	}
	public double getDisplaySideCount() {
		return displaySideCount;
	}
	public void setDisplaySideCount(double displaySideCount) {
		this.displaySideCount = displaySideCount;
	}
	public int getDisplayParamId() {
		return displayParamId;
	}
	public void setDisplayParamId(int displayParamId) {
		this.displayParamId = displayParamId;
	}
	public int getVisitCount() {
		return visitCount;
	}
	public void setVisitCount(int visitCount) {
		this.visitCount = visitCount;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getYearMonth() {
		return yearMonth;
	}
	public void setYearMonth(String yearMonth) {
		this.yearMonth = yearMonth;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}

	/**
	 * @return 传回 mdm_store_id。
	 */
	public String getMdm_store_id() {

		return mdm_store_id;
	}

	/**
	 * @param mdmStoreId
	 *          要设定的 mdm_store_id。
	 */
	public void setMdm_store_id(String mdmStoreId) {

		mdm_store_id = mdmStoreId;
	}

	/**
	 * @return 传回 store_display_sid。
	 */
	public int getStore_display_sid() {

		return store_display_sid;
	}

	/**
	 * @param storeDisplaySid
	 *          要设定的 store_display_sid。
	 */
	public void setStore_display_sid(int storeDisplaySid) {

		store_display_sid = storeDisplaySid;
	}
	
	/**
	 * @return 传回 first_input_date。
	 */
	public Date getFirst_input_date() {
	
		return first_input_date;
	}
	
	/**
	 * @param firstInputDate 要设定的 first_input_date。
	 */
	public void setFirst_input_date(Date firstInputDate) {
	
		first_input_date = firstInputDate;
	}

	
}
