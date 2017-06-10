package com.want.batch.job.archive.notify.pojo;

import java.util.ArrayList;
import java.util.List;


public class MessageBo {
	/**
	 * 员工工号
	 */
	private String empId="";
	/**
	 * 员工所在的事业部
	 */
	private int divisionId=0;
	/**
	 * 员工电话
	 */
	private String mobile="";
	/**
	 * 异常个数
	 */
	private int excNum=0;
	
	/**
	 * 分类异常
	 */
	private List<NotifyCategoryNum> notifyCategoryNumList=new ArrayList<NotifyCategoryNum>();
	
	
	public List<NotifyCategoryNum> getNotifyCategoryNumList() {
		return notifyCategoryNumList;
	}
	public void setNotifyCategoryNumList(
			List<NotifyCategoryNum> notifyCategoryNumList) {
		this.notifyCategoryNumList = notifyCategoryNumList;
	}
	public int getDivisionId() {
		return divisionId;
	}
	public void setDivisionId(int divisionId) {
		this.divisionId = divisionId;
	}
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public int getExcNum() {
		return excNum;
	}
	public void setExcNum(int excNum) {
		this.excNum = excNum;
	}
	
	/**短信内容按异常类别分别显示条数
	 * @return
	 */
	public String getMessageInfo(){
		StringBuffer buf=new StringBuffer("");
		buf.append("您今天有异常的客户总数为")
			.append(this.getExcNum())
			.append("(");
		
		for(NotifyCategoryNum notifyCategoryNum:notifyCategoryNumList){
			buf.append(notifyCategoryNum.getCategoryName()+":"+notifyCategoryNum.getExcNum()+",");
		}
		
		buf.append(")");
		
		return buf.toString();
	}
}
