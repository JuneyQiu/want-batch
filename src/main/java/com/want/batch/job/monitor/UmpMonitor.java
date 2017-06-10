package com.want.batch.job.monitor;

import java.util.Date;

public class UmpMonitor {

	private int id;
	private String systemName;
	private String ip;
	private int port;
	private String hostName;
	private String message;
	private UmpMonitorLevel monitorLevel;
	private Date createDate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMonitorLevel() {
		return monitorLevel.toString();
	}

	public void setMonitorLevel(UmpMonitorLevel monitorLevel) {
		this.monitorLevel = monitorLevel;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
}
