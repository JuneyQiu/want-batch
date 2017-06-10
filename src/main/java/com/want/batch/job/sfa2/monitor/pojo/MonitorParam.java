package com.want.batch.job.sfa2.monitor.pojo;

public class MonitorParam {
	private String projectSid;
	private String divisionSid;
	private String empId;
	private String customerId;
	private String yearmonth;

	public MonitorParam() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MonitorParam(String projectSid, String divisionSid, String empId,
			String customerId, String yearmonth) {
		super();
		this.projectSid = projectSid;
		this.divisionSid = divisionSid;
		this.empId = empId;
		this.customerId = customerId;
		this.yearmonth = yearmonth;
	}

	public String getProjectSid() {
		return projectSid;
	}

	public void setProjectSid(String projectSid) {
		this.projectSid = projectSid;
	}

	public String getDivisionSid() {
		return divisionSid;
	}

	public void setDivisionSid(String divisionSid) {
		this.divisionSid = divisionSid;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getYearmonth() {
		return yearmonth;
	}

	public void setYearmonth(String yearmonth) {
		this.yearmonth = yearmonth;
	}

}
