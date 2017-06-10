package com.want.batch.job.archive.notify.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.want.batch.job.archive.notify.util.Constant;

/**
 * @author 00078588
 *对应表all_abnormality_info_tbl
 */
public class NotifyBo {
	private long sid=Constant.DEFAULT_LONG;
	private String companyId="";
	private String companyName="";
	private String branchId="";
	private String branchName="";
	private String divisionId="";
	private String divisionName="";
	private String zjlId="";
	private String zjlName="";
	private String zongjianId="";
	private String zongjianName="";
	private String zhuanyuanId="";
	private String zhuanyuanName="";
	private String suozhangId="";
	private String suozhangName="";
	private String zhurenId="";
	private String zhurenName="";
	private String yedaiId="";
	private String yedaiName="";
	private String custId="";
	private String isFirst="";
	private String startYmd="";
	private String excType="";
	private String excDesc="";
	private String excInfo="";
	private Date createDate;

	//特陈类的异常通报比较特殊(需要记录到特陈)
	private List<SdInfo> sdInfoList;

	public NotifyBo(){
		sdInfoList=new ArrayList<SdInfo>();
	}
	public List<SdInfo> getSdInfoList() {
		return sdInfoList;
	}

	public void setSdInfoList(List<SdInfo> sdInfoList) {
		this.sdInfoList = sdInfoList;
	}
	
	public void addSdInfo(SdInfo sdInfo){
		sdInfoList.add(sdInfo);
	}
	
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getBranchId() {
		return branchId;
	}
	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getDivisionId() {
		return divisionId;
	}
	public void setDivisionId(String divisionId) {
		this.divisionId = divisionId;
	}
	public String getDivisionName() {
		return divisionName;
	}
	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}
	public String getZjlId() {
		return zjlId;
	}
	public void setZjlId(String zjlId) {
		this.zjlId = zjlId;
	}
	public String getZjlName() {
		return zjlName;
	}
	public void setZjlName(String zjlName) {
		this.zjlName = zjlName;
	}
	public String getZongjianId() {
		return zongjianId;
	}
	public void setZongjianId(String zongjianId) {
		this.zongjianId = zongjianId;
	}
	public String getZongjianName() {
		return zongjianName;
	}
	public void setZongjianName(String zongjianName) {
		this.zongjianName = zongjianName;
	}
	public String getZhuanyuanId() {
		return zhuanyuanId;
	}
	public void setZhuanyuanId(String zhuanyuanId) {
		this.zhuanyuanId = zhuanyuanId;
	}
	public String getZhuanyuanName() {
		return zhuanyuanName;
	}
	public void setZhuanyuanName(String zhuanyuanName) {
		this.zhuanyuanName = zhuanyuanName;
	}
	public String getSuozhangId() {
		return suozhangId;
	}
	public void setSuozhangId(String suozhangId) {
		this.suozhangId = suozhangId;
	}
	public String getSuozhangName() {
		return suozhangName;
	}
	public void setSuozhangName(String suozhangName) {
		this.suozhangName = suozhangName;
	}
	public String getZhurenId() {
		return zhurenId;
	}
	public void setZhurenId(String zhurenId) {
		this.zhurenId = zhurenId;
	}
	public String getZhurenName() {
		return zhurenName;
	}
	public void setZhurenName(String zhurenName) {
		this.zhurenName = zhurenName;
	}
	public String getYedaiId() {
		return yedaiId;
	}
	public void setYedaiId(String yedaiId) {
		this.yedaiId = yedaiId;
	}
	public String getYedaiName() {
		return yedaiName;
	}
	public void setYedaiName(String yedaiName) {
		this.yedaiName = yedaiName;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getIsFirst() {
		return isFirst;
	}
	public void setIsFirst(String isFirst) {
		this.isFirst = isFirst;
	}
	public String getStartYmd() {
		return startYmd;
	}
	public void setStartYmd(String startYmd) {
		this.startYmd = startYmd;
	}
	public String getExcType() {
		return excType;
	}
	public void setExcType(String excType) {
		this.excType = excType;
	}
	public String getExcDesc() {
		return excDesc;
	}
	public void setExcDesc(String excDesc) {
		this.excDesc = excDesc;
	}
	public String getExcInfo() {
		return excInfo;
	}
	public void setExcInfo(String excInfo) {
		this.excInfo = excInfo;
	}
	public long getSid() {
		return sid;
	}
	public void setSid(long sid) {
		this.sid = sid;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
}
