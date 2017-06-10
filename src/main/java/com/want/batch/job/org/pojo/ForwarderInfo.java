package com.want.batch.job.org.pojo;

public class ForwarderInfo {
	private String id;
	private long branchSid;
	private String name;
	private String owner;
	private String zip1;
	private String phone1;
	private String mobile;
	private String address1;
	private String forwarderIdB;
	private String status;

	public ForwarderInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ForwarderInfo(String id, long branchSid, String name, String owner,
			String zip1, String phone1, String mobile, String address1,
			String forwarderIdB, String status) {
		super();
		this.id = id;
		this.branchSid = branchSid;
		this.name = name;
		this.owner = owner;
		this.zip1 = zip1;
		this.phone1 = phone1;
		this.mobile = mobile;
		this.address1 = address1;
		this.forwarderIdB = forwarderIdB;
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getBranchSid() {
		return branchSid;
	}

	public void setBranchSid(long branchSid) {
		this.branchSid = branchSid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getZip1() {
		return zip1;
	}

	public void setZip1(String zip1) {
		this.zip1 = zip1;
	}

	public String getPhone1() {
		return phone1;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getForwarderIdB() {
		return forwarderIdB;
	}

	public void setForwarderIdB(String forwarderIdB) {
		this.forwarderIdB = forwarderIdB;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
