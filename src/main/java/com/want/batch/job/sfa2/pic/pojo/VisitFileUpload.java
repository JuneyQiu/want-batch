package com.want.batch.job.sfa2.pic.pojo;

import java.math.BigDecimal;
import java.util.Date;

public class VisitFileUpload {
	private String oriFileName;
	private String fileName;
	private String path;

	private BigDecimal storeDisplaySid;

	private BigDecimal actualDisplaySid;
	
	private String createUser;
	private Date createDate;

	public VisitFileUpload() {
		super();
	}

	public String getOriFileName() {
		return oriFileName;
	}

	public void setOriFileName(String oriFileName) {
		this.oriFileName = oriFileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public BigDecimal getStoreDisplaySid() {
		return storeDisplaySid;
	}

	public void setStoreDisplaySid(BigDecimal storeDisplaySid) {
		this.storeDisplaySid = storeDisplaySid;
	}

	public BigDecimal getActualDisplaySid() {
		return actualDisplaySid;
	}

	public void setActualDisplaySid(BigDecimal actualDisplaySid) {
		this.actualDisplaySid = actualDisplaySid;
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
