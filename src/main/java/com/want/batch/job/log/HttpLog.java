package com.want.batch.job.log;

public class HttpLog {
	private String responseTime;
	private String logDate;
	private String logTime;
	private String userId;
	private String responseCode;
	private String protocol;
	private String method;
	private String clientIP;
	private String serverIP;
	private String uri;
	private String size;	
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getLogDate() {
		return logDate;
	}
	public void setLogDate(String logDate) {
		this.logDate = logDate;
	}	
	public String getServerIP() {
		return serverIP;
	}
	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}
	public String getResponseTime() {
		return responseTime;
	}
	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
	}
	public String getLogTime() {
		return logTime;
	}
	public void setLogTime(String logTime) {
		this.logTime = logTime;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getClientIP() {
		return clientIP;
	}
	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String toString() {
		StringBuffer result=new StringBuffer();
		result.append("Log Date:"+logDate+"\t");		
		result.append("Log Time:"+logTime+"\t");
		result.append("URI:"+uri+"\t");
		result.append("userId:"+userId+"\t");
		result.append("responseCode:"+responseCode+"\t");
		result.append("protocol:"+protocol+"\t");
		result.append("method:"+method+"\t");
		result.append("clientIP:"+clientIP+"\t");
		result.append("responseTime:"+responseTime+"\t");	
		result.append("size:"+size+"\t");		
		return result.toString();
	}
}
