package com.want.batch.job.org.pojo;

/**
 * @author 00078588
 * 三级地
 */
public class Third {
	private String thirdId="";
	private String thirdName="";
	
	public Third(){}
	public Third(String thirdId,String thirdName){
		this.thirdId=thirdId;
		this.thirdName=thirdName;
	}
	
	public String getThirdId() {
		return thirdId;
	}
	public void setThirdId(String thirdId) {
		this.thirdId = thirdId;
	}
	public String getThirdName() {
		return thirdName;
	}
	public void setThirdName(String thirdName) {
		this.thirdName = thirdName;
	}
	
	public boolean equals(Object o){
		if(!(o instanceof Third)) return false;
		
		Third t=(Third)o;
		return t.getThirdId().equals(this.getThirdId());
	}
	public int hashCode(){
		return this.getThirdId().hashCode();
	}
}
