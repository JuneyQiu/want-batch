package com.want.batch.job.org.pojo;

/**
 * @author 00078588
 * 分公司
 */
public class Company {
	private String companyId="";
	private String companyName="";
	
	public Company(){}
	public Company(String companyId,String companyName){
		this.companyId=companyId;
		this.companyName=companyName;
	}
	
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	public boolean equals(Object o){
		if(!(o instanceof Company)) return false;
		
		Company c=(Company)o;
		return c.getCompanyId().equals(this.getCompanyId());
	}
	public int hashCode(){
		return this.getCompanyId().hashCode();
	}
}
