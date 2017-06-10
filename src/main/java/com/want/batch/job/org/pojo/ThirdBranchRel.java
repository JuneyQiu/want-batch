package com.want.batch.job.org.pojo;

import java.math.BigDecimal;

public class ThirdBranchRel {
	private String third_sap_id;
	private BigDecimal thirdSid;
	private String thirdName;
	private BigDecimal branchSid;
	private String branchName;
	private String marketName;
	private String marketSid;
	
	public ThirdBranchRel() {
	}
	
	public ThirdBranchRel(String third_sap_id, BigDecimal thirdSid, String thirdName,
			BigDecimal branchSid, String branchName) {
		super();
		this.third_sap_id = third_sap_id;
		this.thirdSid = thirdSid;
		this.thirdName = thirdName;
		this.branchSid = branchSid;
		this.branchName = branchName;
	}


	public String getThird_sap_id() {
		return third_sap_id;
	}

	public void setThird_sap_id(String third_sap_id) {
		this.third_sap_id = third_sap_id;
	}

	public BigDecimal getThirdSid() {
		return thirdSid;
	}

	public void setThirdSid(BigDecimal thirdSid) {
		this.thirdSid = thirdSid;
	}

	public String getThirdName() {
		return thirdName;
	}

	public void setThirdName(String thirdName) {
		this.thirdName = thirdName;
	}

	public BigDecimal getBranchSid() {
		return branchSid;
	}

	public void setBranchSid(BigDecimal branchSid) {
		this.branchSid = branchSid;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getMarketName() {
		return marketName;
	}

	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}

	public String getMarketSid() {
		return marketSid;
	}

	public void setMarketSid(String marketSid) {
		this.marketSid = marketSid;
	}
	
}
