package com.want.batch.job.stock_collect.storemgr.bo;

public class DirectBussinessBO {
	private String bussinessId;
	private String bussinessName;
	private String erpId;
	private String erpName;
	private String keyAccountId;
	private String keyAccountName;
	private String CUSTSTORE_ID;
	private String rcStoreType;

	private int rcType;
	public String getBussinessId() {
		return bussinessId;
	}
	public void setBussinessId(String bussinessId) {
		this.bussinessId = bussinessId;
	}
	public String getBussinessName() {
		return bussinessName;
	}
	public void setBussinessName(String bussinessName) {
		this.bussinessName = bussinessName;
	}
	public String getErpId() {
		return erpId;
	}
	public void setErpId(String erpId) {
		this.erpId = erpId;
	}
	public String getErpName() {
		return erpName;
	}
	public void setErpName(String erpName) {
		this.erpName = erpName;
	}
	public String getKeyAccountId() {
		return keyAccountId;
	}
	public void setKeyAccountId(String keyAccountId) {
		this.keyAccountId = keyAccountId;
	}
	public String getKeyAccountName() {
		return keyAccountName;
	}
	public void setKeyAccountName(String keyAccountName) {
		this.keyAccountName = keyAccountName;
	}
	public int getRcType() {
		return rcType;
	}
	public void setRcType(int rcType) {
		this.rcType = rcType;
	}
	public String getCUSTSTORE_ID() {
		return CUSTSTORE_ID;
	}
	public void setCUSTSTORE_ID(String cUSTSTORE_ID) {
		CUSTSTORE_ID = cUSTSTORE_ID;
	}
	public String getRcStoreType() {
		return rcStoreType;
	}
	public void setRcStoreType(String rcStoreType) {
		this.rcStoreType = rcStoreType;
	}
	
	
}
