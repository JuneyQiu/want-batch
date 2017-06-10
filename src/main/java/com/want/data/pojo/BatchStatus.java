package com.want.data.pojo;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class BatchStatus {

	private Long sid;
	private String funcId;
	private Date startDate = new Date();
	private Date endDate;
	private String status;
	private String reason;

	public Long getSid() {
		return sid;
	}

	public void setSid(Long sid) {
		this.sid = sid;
	}

	public String getFuncId() {
		return funcId;
	}

	public void setFuncId(String funcId) {
		this.funcId = funcId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}


	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public void setReason(Exception reason) {
		setReason(generateExceptionMessage(reason));
	}

	public <T> void appendReason(T reason) {
		if (reason != null) {
			this.setReason(this.getReason() + "\n" + reason);
		}
	}

	private String generateExceptionMessage(Throwable e) {

		StringBuilder result = new StringBuilder(e.toString());

        for (StackTraceElement trace : e.getStackTrace()) {
        	String traceString = trace.toString();
        	
        	if (StringUtils.contains(traceString, "com.want.")) {
        		traceString = String.format("<font color='red'>%s</font>", traceString);
        	}
        	
			result.append("\n\tat ").append(traceString).append("<p>");
		}

        if (e.getCause() != null) {
        	result.append("\nCause : ").append(this.generateExceptionMessage(e.getCause()));
        }

		return result.toString();
	}
	
	private String getStatusDescription() {
		String desc = status;
		
		if (status != null) {
			if (status.equals("1"))
				desc = "成功";
		}
		return desc;
	}
	
	public String toString() {
		return "function " + funcId + ", " + getStatusDescription() + ", " + reason;
	}
}
