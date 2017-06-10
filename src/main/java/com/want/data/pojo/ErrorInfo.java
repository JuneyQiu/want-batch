/**
 * 
 */
package com.want.data.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Timothy
 *
 */
public class ErrorInfo {

	private Long sid;
	private Date createDate;
	private String uri;
	private String loginUser;
	private String code;
	private String message;
	private String trace;
	private String clientIp;
	private String serverIp;
	private int serverPort;
	private String principalNo;
	private String principalName;

	private Integer traceLength;
	private List<String> sidLinks = new ArrayList<String>();

	public Long getSid() {
		return sid;
	}

	public void setSid(Long sid) {
		this.sid = sid;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getLoginUser() {
		return loginUser;
	}
	
	public void setLoginUser(String loginUser) {
		this.loginUser = loginUser;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTrace() {
		return trace;
	}

	public void setTrace(String trace) {
		this.trace = trace;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public void addSidLink(String sidLink) {
		this.sidLinks.add(sidLink);
	}

	public List<String> getSidLinks() {
		return sidLinks;
	}

	public void setSidLinks(List<String> sidLinks) {
		this.sidLinks = sidLinks;
	}

	public Integer getTraceLength() {
		return traceLength;
	}

	public void setTraceLength(Integer traceLength) {
		this.traceLength = traceLength;
	}

	public int getCount() {
		return this.sidLinks.size();
	}

	public String getSidLink() {
		if (sidLinks.isEmpty()) {
			return "";
		}
		return sidLinks.get(0);
	}

	public String getPrincipalNo() {
		return principalNo;
	}

	public void setPrincipalNo(String principalNo) {
		this.principalNo = principalNo;
	}

	public String getPrincipalName() {
		return principalName;
	}

	public void setPrincipalName(String principalName) {
		this.principalName = principalName;
	}

}
