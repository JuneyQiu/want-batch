package com.want.batch.job.spring_example.bo;

import com.want.batch.job.spring_example.util.bo.BaseObject;

/**
 * Knvv的值对象类，对应的数据库表是：knvv
 * 
 * @author suyulin
 */
public class KnvvBO extends BaseObject {

	private static final long serialVersionUID = 3257128230151828326L;

	private String id;
	private String customerId;
	private String companyId;
	private String salesChannel;
	private String product;
	private String branchId;
	private String customerGroup1;
	private String customerGroup2;
	private String customerGroup3;
	private String customerGroup4;
	//CRQ000000019248新增经销商门市与办公地址信息
	//办公地址
	private String STR_SUPPL2;
	//门市地址
	private String LOCATION;
	
	
	public String getSTR_SUPPL2() {
		return STR_SUPPL2;
	}
	public void setSTR_SUPPL2(String sTR_SUPPL2) {
		STR_SUPPL2 = sTR_SUPPL2;
	}
	public String getLOCATION() {
		return LOCATION;
	}
	public void setLOCATION(String lOCATION) {
		LOCATION = lOCATION;
	}
	public String getCustomerGroup1() {
		return customerGroup1;
	}
	public void setCustomerGroup1(String customerGroup1) {
		this.customerGroup1 = customerGroup1;
	}
	public String getCustomerGroup2() {
		return customerGroup2;
	}
	public void setCustomerGroup2(String customerGroup2) {
		this.customerGroup2 = customerGroup2;
	}
	public String getCustomerGroup3() {
		return customerGroup3;
	}
	public void setCustomerGroup3(String customerGroup3) {
		this.customerGroup3 = customerGroup3;
	}
	public String getCustomerGroup4() {
		return customerGroup4;
	}
	public void setCustomerGroup4(String customerGroup4) {
		this.customerGroup4 = customerGroup4;
	}
	//下面这3个字段是为了存放客户的“送达方”
	private int sid;
	private String idFriend;
	private String address;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public String getSalesChannel() {
		return salesChannel;
	}
	public void setSalesChannel(String salesChannel) {
		this.salesChannel = salesChannel;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getBranchId() {
		return branchId;
	}
	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
	public int getSid() {
		return sid;
	}
	public void setSid(int sid) {
		this.sid = sid;
	}
	public String getIdFriend() {
		return idFriend;
	}
	public void setIdFriend(String idFriend) {
		this.idFriend = idFriend;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

}
