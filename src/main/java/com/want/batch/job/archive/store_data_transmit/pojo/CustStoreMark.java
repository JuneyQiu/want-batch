package com.want.batch.job.archive.store_data_transmit.pojo;

//对应表CUSTOMER_STORE_MARK
public class CustStoreMark {
	private String CUSTOMER_ID="";
	private String COMPANY_ID="";
	private String CHANNEL_ID="";
	private String PROD_GROUP_ID="";
	private String CREDIT_ID="";
	private String PRODUCT_ID="";
	private String ISMARK="";
	private String RECORD_DATE="";
	
	
	
	public String getCUSTOMER_ID() {
		return CUSTOMER_ID;
	}
	public void setCUSTOMER_ID(String cUSTOMERID) {
		CUSTOMER_ID = cUSTOMERID;
	}
	public String getCOMPANY_ID() {
		return COMPANY_ID;
	}
	public void setCOMPANY_ID(String cOMPANYID) {
		COMPANY_ID = cOMPANYID;
	}
	public String getCHANNEL_ID() {
		return CHANNEL_ID;
	}
	public void setCHANNEL_ID(String cHANNELID) {
		CHANNEL_ID = cHANNELID;
	}
	public String getPROD_GROUP_ID() {
		return PROD_GROUP_ID;
	}
	public void setPROD_GROUP_ID(String pRODGROUPID) {
		PROD_GROUP_ID = pRODGROUPID;
	}
	public String getCREDIT_ID() {
		return CREDIT_ID;
	}
	public void setCREDIT_ID(String cREDITID) {
		CREDIT_ID = cREDITID;
	}
	public String getPRODUCT_ID() {
		return PRODUCT_ID;
	}
	public void setPRODUCT_ID(String pRODUCTID) {
		PRODUCT_ID = pRODUCTID;
	}
	public String getISMARK() {
		return ISMARK;
	}
	public void setISMARK(String iSMARK) {
		ISMARK = iSMARK;
	}
	public String getRECORD_DATE() {
		return RECORD_DATE;
	}
	public void setRECORD_DATE(String rECORDDATE) {
		RECORD_DATE = rECORDDATE;
	}
}
