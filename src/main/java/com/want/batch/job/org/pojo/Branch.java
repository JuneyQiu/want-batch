package com.want.batch.job.org.pojo;

/**
 * @author 00078588
 * 营业所
 */
public class Branch {
	private String branchId="";
	private String branchName="";
	
	public Branch(){}
	public Branch(String branchId,String branchName){
		this.branchId=branchId;
		this.branchName=branchName;
	}
	
	public String getBranchId() {
		return branchId;
	}
	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	
	public boolean equals(Object o){
		if(!(o instanceof Branch)) return false;
		
		Branch b=(Branch)o;
		return b.getBranchId().equals(this.getBranchId());
	}
	public int hashCode(){
		return this.getBranchId().hashCode();
	}
}
