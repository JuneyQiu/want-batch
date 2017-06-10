package com.want.batch.job.lds.bo;

import java.io.Serializable;
import java.sql.Timestamp;

public class Record implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2560361477473557129L;

	private String tableName;
	private int sid;
	private Timestamp startTime;
	private Timestamp endTime;
	private int syncFlag;
	private int currentVer;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public int getSyncFlag() {
		return syncFlag;
	}

	public void setSyncFlag(int syncFlag) {
		this.syncFlag = syncFlag;
	}

	public int getCurrentVer() {
		return currentVer;
	}

	public void setCurrentVer(int currentVer) {
		this.currentVer = currentVer;
	}

}
