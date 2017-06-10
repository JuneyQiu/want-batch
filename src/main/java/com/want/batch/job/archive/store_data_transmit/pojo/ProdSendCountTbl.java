package com.want.batch.job.archive.store_data_transmit.pojo;

//对应表PROD_SENDCOUNT_TBL
public class ProdSendCountTbl {
	private String CUSTOMER_ID="";
	private String CHECK_DATE="";
	private String PROD_ID="";
	private int PROD_SENDQTY=0;
	
	
	
	public String getCUSTOMER_ID() {
		return CUSTOMER_ID;
	}
	public void setCUSTOMER_ID(String cUSTOMERID) {
		CUSTOMER_ID = cUSTOMERID;
	}
	public String getCHECK_DATE() {
		return CHECK_DATE;
	}
	public void setCHECK_DATE(String cHECKDATE) {
		CHECK_DATE = cHECKDATE;
	}
	public String getPROD_ID() {
		return PROD_ID;
	}
	public void setPROD_ID(String pRODID) {
		PROD_ID = pRODID;
	}
	public int getPROD_SENDQTY() {
		return PROD_SENDQTY;
	}
	public void setPROD_SENDQTY(int pRODSENDQTY) {
		PROD_SENDQTY = pRODSENDQTY;
	}
}
