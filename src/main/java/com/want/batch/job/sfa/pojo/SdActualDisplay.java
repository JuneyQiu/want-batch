package com.want.batch.job.sfa.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**sd_actual_display
 * @author 00078588
 *
 */
public class SdActualDisplay {
	private Long sid=null;
	private Long actualSid=null;
	private Long storeDisplaySid=null;
	private String storeId="";
	private Integer displayTypeSid=null;
	private Double actualInput=null;
	private Double actualSales=null;
	private Integer locationTypeSid=null;
	private String isReceiveReceipt="";
	private Date createDate=null;
	private String createUser="";
	private String createUserType="";
	private Long displayAcreage=null;
	private Double displaySideCount=null;
	private String assetsId="";
	private String isCancel="";
	private String sdCheckStatusZr="";
	private String sdCheckStatusSz="";
	private String sdCheckStatusZj="";
	private Double approvedAmount=null;
	private String isLock="";
	private Date updateDate=null;
	private String updateUser="";
	private Integer displayParamId=null;
	private String dataSource="";
	private List<SdActualProd> sapList=new ArrayList<SdActualProd>();
	private List<SdActualPicture> sapictList=new ArrayList<SdActualPicture>();
	
	
	public Long getSid() {
		return sid;
	}
	public void setSid(Long sid) {
		this.sid = sid;
	}
	public Long getActualSid() {
		return actualSid;
	}
	public void setActualSid(Long actualSid) {
		this.actualSid = actualSid;
	}
	public Long getStoreDisplaySid() {
		return storeDisplaySid;
	}
	public void setStoreDisplaySid(Long storeDisplaySid) {
		this.storeDisplaySid = storeDisplaySid;
	}
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public String getIsReceiveReceipt() {
		return isReceiveReceipt;
	}
	public void setIsReceiveReceipt(String isReceiveReceipt) {
		this.isReceiveReceipt = isReceiveReceipt;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getCreateUserType() {
		return createUserType;
	}
	public void setCreateUserType(String createUserType) {
		this.createUserType = createUserType;
	}
	public String getAssetsId() {
		return assetsId;
	}
	public void setAssetsId(String assetsId) {
		this.assetsId = assetsId;
	}
	public String getIsCancel() {
		return isCancel;
	}
	public void setIsCancel(String isCancel) {
		this.isCancel = isCancel;
	}
	public String getSdCheckStatusZr() {
		return sdCheckStatusZr;
	}
	public void setSdCheckStatusZr(String sdCheckStatusZr) {
		this.sdCheckStatusZr = sdCheckStatusZr;
	}
	public String getSdCheckStatusSz() {
		return sdCheckStatusSz;
	}
	public void setSdCheckStatusSz(String sdCheckStatusSz) {
		this.sdCheckStatusSz = sdCheckStatusSz;
	}
	public String getSdCheckStatusZj() {
		return sdCheckStatusZj;
	}
	public void setSdCheckStatusZj(String sdCheckStatusZj) {
		this.sdCheckStatusZj = sdCheckStatusZj;
	}
	public String getIsLock() {
		return isLock;
	}
	public void setIsLock(String isLock) {
		this.isLock = isLock;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	public String getDataSource() {
		return dataSource;
	}
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	public List<SdActualProd> getSapList() {
		return sapList;
	}
	public void setSapList(List<SdActualProd> sapList) {
		this.sapList = sapList;
	}
	public List<SdActualPicture> getSapictList() {
		return sapictList;
	}
	public void setSapictList(List<SdActualPicture> sapictList) {
		this.sapictList = sapictList;
	}
	public Integer getDisplayTypeSid() {
		return displayTypeSid;
	}
	public void setDisplayTypeSid(Integer displayTypeSid) {
		this.displayTypeSid = displayTypeSid;
	}
	public Double getActualInput() {
		return actualInput;
	}
	public void setActualInput(Double actualInput) {
		this.actualInput = actualInput;
	}
	public Double getActualSales() {
		return actualSales;
	}
	public void setActualSales(Double actualSales) {
		this.actualSales = actualSales;
	}
	public Integer getLocationTypeSid() {
		return locationTypeSid;
	}
	public void setLocationTypeSid(Integer locationTypeSid) {
		this.locationTypeSid = locationTypeSid;
	}
	public Long getDisplayAcreage() {
		return displayAcreage;
	}
	public void setDisplayAcreage(Long displayAcreage) {
		this.displayAcreage = displayAcreage;
	}
	public Double getDisplaySideCount() {
		return displaySideCount;
	}
	public void setDisplaySideCount(Double displaySideCount) {
		this.displaySideCount = displaySideCount;
	}
	public Double getApprovedAmount() {
		return approvedAmount;
	}
	public void setApprovedAmount(Double approvedAmount) {
		this.approvedAmount = approvedAmount;
	}
	public Integer getDisplayParamId() {
		return displayParamId;
	}
	public void setDisplayParamId(Integer displayParamId) {
		this.displayParamId = displayParamId;
	}
}
