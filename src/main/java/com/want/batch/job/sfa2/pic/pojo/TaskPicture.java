package com.want.batch.job.sfa2.pic.pojo;

import java.util.Date;

public class TaskPicture {
	
	private String sid;
	private String name;
	private String description;
	private byte[] content;
	private String status;
	private String createUser;
	private Date createDate;
	private String updateUser;
	private Date updateDate;
	// 20121214 add by TaoJie
	private int compression;
	private String backup;
	
	
	public int getCompression() {
		return compression;
	}

	public void setCompression(int compression) {
		this.compression = compression;
	}

	public String getBackup() {
		return backup;
	}

	public void setBackup(String backup) {
		this.backup = backup;
	}

	// 20121214 add by TaoJie

	public String getSid() {
		return sid;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public byte[] getContent() {
		return content;
	}

	public String getStatus() {
		return status;
	}

	public String getCreateUser() {
		return createUser;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
}
