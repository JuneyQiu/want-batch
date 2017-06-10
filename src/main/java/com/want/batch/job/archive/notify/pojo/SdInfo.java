package com.want.batch.job.archive.notify.pojo;

import java.util.Date;

/**
 * @author 00078588
 * 产生异常的特陈
 */
public class SdInfo {
	private long sid=0;
	private long notifyId=0;//all_abnormality_info_tbl的sid
	private long storeDisplaySid=0;//申请终端特陈编号
	private String empId="";
	private String createUser="";
	private Date createDate=null;
	
	
	public long getSid() {
		return sid;
	}
	public void setSid(long sid) {
		this.sid = sid;
	}
	public long getNotifyId() {
		return notifyId;
	}
	public void setNotifyId(long notifyId) {
		this.notifyId = notifyId;
	}
	public long getStoreDisplaySid() {
		return storeDisplaySid;
	}
	public void setStoreDisplaySid(long storeDisplaySid) {
		this.storeDisplaySid = storeDisplaySid;
	}
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
}
