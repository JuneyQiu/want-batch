package com.want.service;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SmsModel {

	public static final int SMS_STATUS = 1;

	private Log logger = LogFactory.getLog(this.getClass());
	
	private Integer funcSid;
	private String funcCaseId;
	private Date createDate;
	private Date updateDate;
	private Date sendDate;
	private Long phoneNumber;
	private String content;
	private Integer status = SMS_STATUS;
	
	private String personalName;

	@Override
	public String toString() {

		logger.debug("Sms.funcSid     >> " + this.getFuncSid());
		logger.debug("Sms.funcCaseId  >> " + this.getFuncCaseId());
		logger.debug("Sms.createDate  >> " + this.getCreateDate());
		logger.debug("Sms.sendDate    >> " + this.getSendDate());
		logger.debug("Sms.phoneNumber >> " + this.getPhoneNumber());
		logger.debug("Sms.content     >> " + this.getContent());
		logger.debug("Sms.status      >> " + this.getStatus());

		return super.toString();
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}

	public Integer getFuncSid() {
		return funcSid;
	}

	public void setFuncSid(Integer funcSid) {
		this.funcSid = funcSid;
	}

	public String getFuncCaseId() {
		return funcCaseId;
	}

	public void setFuncCaseId(String funcCaseId) {
		this.funcCaseId = funcCaseId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public Long getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(Long phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public String getPersonalName() {
		return personalName;
	}

	public void setPersonalName(String personalName) {
		this.personalName = personalName;
	}

}
