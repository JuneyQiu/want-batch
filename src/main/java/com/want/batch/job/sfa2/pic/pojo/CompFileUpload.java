package com.want.batch.job.sfa2.pic.pojo;

import java.math.BigDecimal;
import java.util.Date;

public class CompFileUpload {
	// select
	// FILE_SID,FILE_NAME,FILE_SIZE,FILE_PATH,FILE_STATUS,b.UPDATE_DATE,b.UPDATER
	private BigDecimal fileSid;
	private String fileName;
	private BigDecimal fileSize;
	private String filePath;
	private String fileStatus;
	private Date updateDate;
	private String updator;
	public BigDecimal getFileSid() {
		return fileSid;
	}
	public void setFileSid(BigDecimal fileSid) {
		this.fileSid = fileSid;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public BigDecimal getFileSize() {
		return fileSize;
	}
	public void setFileSize(BigDecimal fileSize) {
		this.fileSize = fileSize;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFileStatus() {
		return fileStatus;
	}
	public void setFileStatus(String fileStatus) {
		this.fileStatus = fileStatus;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public String getUpdator() {
		return updator;
	}
	public void setUpdator(String updator) {
		this.updator = updator;
	}

	
}
