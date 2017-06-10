package com.want.batch.job.org.pojo;

/**
 * @author 00078588
 * 三级地－分公司对应关系
 */
public class ThirdCompanyRel {
	private String thirdId="";
	private String companyId="";
	
	public ThirdCompanyRel(){}
	public ThirdCompanyRel(String thirdId,String companyId){
		this.thirdId=thirdId;
		this.companyId=companyId;
	}
	
	public String getThirdId() {
		return thirdId;
	}
	public void setThirdId(String thirdId) {
		this.thirdId = thirdId;
	}
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	
	public boolean equals(Object o){
		if(!(o instanceof ThirdCompanyRel)) return false;
		
		ThirdCompanyRel tc=(ThirdCompanyRel)o;
		return tc.getThirdId().equals(this.getThirdId())&&tc.getCompanyId().equals(this.getCompanyId());
	}
	public int hashCode(){
		return this.getThirdId().hashCode()+this.getCompanyId().hashCode();
	}
}
