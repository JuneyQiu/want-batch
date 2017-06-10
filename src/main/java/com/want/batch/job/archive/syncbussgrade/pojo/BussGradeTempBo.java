package com.want.batch.job.archive.syncbussgrade.pojo;

public class BussGradeTempBo {
	private String companyId="";//分公司Id
	private String bussDptId="";//事业部Id
	private String customerAccount="";//客户帐号
	private String year="";
	private String month="";
	private String lineTypeId="";//小批线Id
	private long checkedAmount=0;//当月业绩-开单已过帐金额
	private long unCheckedAmount=0;//当月业绩-开单未过帐金额
	

	public void addCheckedAmount(long checkedAmount){
		this.checkedAmount+=checkedAmount;
	}
	public void addUnCheckedAmount(long unCheckedAmount){
		this.unCheckedAmount+=unCheckedAmount;
	}
	
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public String getBussDptId() {
		return bussDptId;
	}
	public void setBussDptId(String bussDptId) {
		this.bussDptId = bussDptId;
	}
	public String getCustomerAccount() {
		return customerAccount;
	}
	public void setCustomerAccount(String customerAccount) {
		this.customerAccount = customerAccount;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getLineTypeId() {
		return lineTypeId;
	}
	public void setLineTypeId(String lineTypeId) {
		this.lineTypeId = lineTypeId;
	}
	public long getCheckedAmount() {
		return checkedAmount;
	}
	public void setCheckedAmount(long checkedAmount) {
		this.checkedAmount = checkedAmount;
	}
	public long getUnCheckedAmount() {
		return unCheckedAmount;
	}
	public void setUnCheckedAmount(long unCheckedAmount) {
		this.unCheckedAmount = unCheckedAmount;
	}
	//如果分公司Id、事业部Id、客户帐号、年份、月份和小批线编号都相同，则视为同一条记录
	public boolean equals(Object obj){
		if(!(obj instanceof BussGradeTempBo)) return false;
		BussGradeTempBo bo=(BussGradeTempBo)obj;
		if(this.getCompanyId().equals(bo.getCompanyId())&&this.getBussDptId().equals(bo.getBussDptId())&&
				this.getCustomerAccount().equals(bo.getCustomerAccount())&&this.getYear().equals(bo.getYear())&&this.getMonth().equals(bo.getMonth())&&
				this.getLineTypeId().equals(bo.getLineTypeId())) return true;
		return false;
	}
}
