package com.want.batch.job.org.pojo;

public class EmpInfo {
	private String empId;
	
	private String empName;
	
	private long empTypeSid;
	
	private String mobile;
	
	private String status;
	
	private long branchSid;

	public EmpInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EmpInfo(String empId, String empName, long empTypeSid,
			String mobile, String status, long branchSid) {
		super();
		this.empId = empId;
		this.empName = empName;
		this.empTypeSid = empTypeSid;
		this.mobile = mobile;
		this.status = status;
		this.branchSid = branchSid;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public long getEmpTypeSid() {
		return empTypeSid;
	}

	public void setEmpTypeSid(long empTypeSid) {
		this.empTypeSid = empTypeSid;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getBranchSid() {
		return branchSid;
	}

	public void setBranchSid(long branchSid) {
		this.branchSid = branchSid;
	}

}
