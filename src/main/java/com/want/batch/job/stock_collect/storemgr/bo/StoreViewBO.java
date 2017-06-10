package com.want.batch.job.stock_collect.storemgr.bo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.want.batch.job.stock_collect.channelmgr.bo.StoreChannelBO;

public class StoreViewBO {
	private String STATE_NAME;
	private String COMPANY_NAME;
	private String BRANCH_NAME;
	private String THIRD_LV_NAME;
	private String FORTH_LV_NAME;
	private String is_city;
	private String STORE_NAME;
	private String STORE_ID;
	private String IDENTITY_ID;
	private String ACREAGE;
	private String ADDRESS;
	private String ADDRESS_NEW;
	private String STORE_OWNER;
	private String OWNER_GENDER;
	private String PHONE1;
	private String PHONE2;
	private String STORE_MOBILE1;
	private String STORE_MOBILE2;
	private String POST_CODE;
	private String REFRIGERATOR;
	private String STORE_TYPE;
	private String STORE_DESC;
	private String CREATE_DATE;
	private String UPDATE_DATE;
	private String status;
	private String SALE_METHOD;
	private int PAYMENT_TERM;
	private int PROJECT_SID;
	private String DOWNSTREAM;
	private int STORE_SID;
	private int THIRD_SID;
	private String MARKET_NAME;
	private String IS_EXHIBIT;
	private String IS_BENCHMARK;
	private StoreChannelBO scb;
	private BigDecimal WATER_SHELF_COUNT;
	private BigDecimal WANT_WATER_SHELF_COUNT;
	private BigDecimal FRIDGE_COUNT;
	private BigDecimal WANT_FRIDGE_COUNT;
	private BigDecimal LONGITUDE;
	private BigDecimal LATITUDE;
	private String MDM_STORE_ID;
	private DirectBussinessBO DirectBo;
	private String SALE_TYPE;
	private String IS_WHOLESALE;
	private String LOCATION_TYPE;
	private String DIANZHAO;
	private String CASHREGISTER_AMOUNT;

	public String getCASHREGISTER_AMOUNT() {
		return CASHREGISTER_AMOUNT;
	}

	public void setCASHREGISTER_AMOUNT(String cASHREGISTER_AMOUNT) {
		CASHREGISTER_AMOUNT = cASHREGISTER_AMOUNT;
	}

	private String PROVIDE_DATE;
	
	public StoreViewBO(int storeSid) {
		this.STORE_SID = storeSid;
	}

	public String getACREAGE() {
		return ACREAGE;
	}

	public String getBRANCH_NAME() {
		return BRANCH_NAME;
	}

	public String getADDRESS() {
		return ADDRESS;
	}

	public String getCOMPANY_NAME() {
		return COMPANY_NAME;
	}

	public String getCREATE_DATE() {
		return CREATE_DATE;
	}

	public String getFORTH_LV_NAME() {
		return FORTH_LV_NAME;
	}

	public String getIDENTITY_ID() {
		return IDENTITY_ID;
	}

	public String getIs_city() {
		return is_city;
	}

	public String getStatus() {
		return status;
	}

	public StoreChannelBO getScb() {
		return scb;
	}

	public BigDecimal getWATER_SHELF_COUNT() {
		return WATER_SHELF_COUNT;
	}

	public BigDecimal getWANT_WATER_SHELF_COUNT() {
		return WANT_WATER_SHELF_COUNT;
	}

	public BigDecimal getWANT_FRIDGE_COUNT() {
		return WANT_FRIDGE_COUNT;
	}

	public BigDecimal getFRIDGE_COUNT() {
		return FRIDGE_COUNT;
	}

	public String getIS_BENCHMARK() {
		return IS_BENCHMARK;
	}

	public String getIS_EXHIBIT() {
		return IS_EXHIBIT;
	}

	public String getMARKET_NAME() {
		return MARKET_NAME;
	}

	public int getSTORE_SID() {
		return STORE_SID;
	}

	public String getDOWNSTREAM() {
		return DOWNSTREAM;
	}

	public int getPROJECT_SID() {
		return PROJECT_SID;
	}

	public String getUPDATE_DATE() {
		return UPDATE_DATE;
	}

	public String getTHIRD_LV_NAME() {
		return THIRD_LV_NAME;
	}

	public String getSTORE_TYPE() {
		return STORE_TYPE;
	}

	public String getSTORE_OWNER() {
		return STORE_OWNER;
	}

	public String getSTORE_NAME() {
		return STORE_NAME;
	}

	public String getSTORE_MOBILE2() {
		return STORE_MOBILE2;
	}

	public String getSTORE_MOBILE1() {
		return STORE_MOBILE1;
	}

	public String getSTORE_ID() {
		return STORE_ID;
	}

	public String getSTORE_DESC() {
		return STORE_DESC;
	}

	public String getSTATE_NAME() {
		return STATE_NAME;
	}

	public String getSALE_METHOD() {
		return SALE_METHOD;
	}

	public String getREFRIGERATOR() {
		return REFRIGERATOR;
	}

	public String getPOST_CODE() {
		return POST_CODE;
	}

	public String getPHONE2() {
		return PHONE2;
	}

	public String getPHONE1() {
		return PHONE1;
	}

	public int getPAYMENT_TERM() {
		return PAYMENT_TERM;
	}

	public String getOWNER_GENDER() {
		return OWNER_GENDER;
	}

	public void setACREAGE(String ACREAGE) {
		this.ACREAGE = ACREAGE;
	}

	public void setADDRESS(String ADDRESS) {
		this.ADDRESS = ADDRESS;
	}

	public void setBRANCH_NAME(String BRANCH_NAME) {
		this.BRANCH_NAME = BRANCH_NAME;
	}

	public void setCOMPANY_NAME(String COMPANY_NAME) {
		this.COMPANY_NAME = COMPANY_NAME;
	}

	public void setCREATE_DATE(String CREATE_DATE) {
		if (CREATE_DATE != null) {
			this.CREATE_DATE = CREATE_DATE.substring(0, 10);
		}
	}

	public void setFORTH_LV_NAME(String FORTH_LV_NAME) {
		this.FORTH_LV_NAME = FORTH_LV_NAME;
	}

	public void setIDENTITY_ID(String IDENTITY_ID) {
		this.IDENTITY_ID = IDENTITY_ID;
	}

	public void setIs_city(String is_city) {
		this.is_city = is_city;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setScb(StoreChannelBO scb) {
		this.scb = scb;
	}

	public void setWATER_SHELF_COUNT(BigDecimal WATER_SHELF_COUNT) {
		this.WATER_SHELF_COUNT = WATER_SHELF_COUNT;
	}

	public void setWANT_WATER_SHELF_COUNT(BigDecimal WANT_WATER_SHELF_COUNT) {
		this.WANT_WATER_SHELF_COUNT = WANT_WATER_SHELF_COUNT;
	}

	public void setWANT_FRIDGE_COUNT(BigDecimal WANT_FRIDGE_COUNT) {
		this.WANT_FRIDGE_COUNT = WANT_FRIDGE_COUNT;
	}

	public void setFRIDGE_COUNT(BigDecimal FRIDGE_COUNT) {
		this.FRIDGE_COUNT = FRIDGE_COUNT;
	}

	public void setIS_BENCHMARK(String IS_BENCHMARK) {
		this.IS_BENCHMARK = IS_BENCHMARK;
	}

	public void setIS_EXHIBIT(String IS_EXHIBIT) {
		this.IS_EXHIBIT = IS_EXHIBIT;
	}

	public void setTHIRD_SID(int THIRD_SID) {
		this.THIRD_SID = THIRD_SID;
	}
	public void setMARKET_NAME(String MARKET_NAME) {
		this.MARKET_NAME = MARKET_NAME;
	}

	public void setSTORE_SID(int STORE_SID) {
		this.STORE_SID = STORE_SID;
	}

	public void setDOWNSTREAM(String DOWNSTREAM) {
		this.DOWNSTREAM = DOWNSTREAM;
	}

	public void setPROJECT_SID(int PROJECT_SID) {
		this.PROJECT_SID = PROJECT_SID;
	}

	public void setUPDATE_DATE(String UPDATE_DATE) {
		if (UPDATE_DATE != null) {
			this.UPDATE_DATE = UPDATE_DATE.substring(0, 10);
		}
	}

	public void setTHIRD_LV_NAME(String THIRD_LV_NAME) {
		this.THIRD_LV_NAME = THIRD_LV_NAME;
	}

	public void setSTORE_TYPE(String STORE_TYPE) {
		this.STORE_TYPE = STORE_TYPE;
	}

	public void setSTORE_OWNER(String STORE_OWNER) {
		this.STORE_OWNER = STORE_OWNER;
	}

	public void setSTORE_NAME(String STORE_NAME) {
		this.STORE_NAME = STORE_NAME;
	}

	public void setSTORE_MOBILE2(String STORE_MOBILE2) {
		this.STORE_MOBILE2 = STORE_MOBILE2;
	}

	public void setSTORE_MOBILE1(String STORE_MOBILE1) {
		this.STORE_MOBILE1 = STORE_MOBILE1;
	}

	public void setSTORE_ID(String STORE_ID) {
		this.STORE_ID = STORE_ID;
	}

	public void setSTORE_DESC(String STORE_DESC) {
		this.STORE_DESC = STORE_DESC;
	}

	public void setSTATE_NAME(String STATE_NAME) {
		this.STATE_NAME = STATE_NAME;
	}

	public void setSALE_METHOD(String SALE_METHOD) {
		this.SALE_METHOD = SALE_METHOD;
	}

	public void setREFRIGERATOR(String REFRIGERATOR) {
		this.REFRIGERATOR = REFRIGERATOR;
	}

	public void setPHONE2(String PHONE2) {
		this.PHONE2 = PHONE2;
	}

	public void setPOST_CODE(String POST_CODE) {
		this.POST_CODE = POST_CODE;
	}

	public void setPHONE1(String PHONE1) {
		this.PHONE1 = PHONE1;
	}

	public void setPAYMENT_TERM(int PAYMENT_TERM) {
		this.PAYMENT_TERM = PAYMENT_TERM;
	}

	public void setOWNER_GENDER(String OWNER_GENDER) {
		this.OWNER_GENDER = OWNER_GENDER;
	}

	public BigDecimal getLONGITUDE() {
		return LONGITUDE;
	}

	public void setLONGITUDE(BigDecimal longitude) {
		LONGITUDE = longitude;
	}

	public BigDecimal getLATITUDE() {
		return LATITUDE;
	}

	public void setLATITUDE(BigDecimal latitude) {
		LATITUDE = latitude;
	}

	public DirectBussinessBO getDirectBo() {
		return DirectBo;
	}

	public void setDirectBo(DirectBussinessBO directBo) {
		DirectBo = directBo;
	}

	public String getMDM_STORE_ID() {
		return MDM_STORE_ID;
	}

	public void setMDM_STORE_ID(String mdm_store_id) {
		MDM_STORE_ID = mdm_store_id;
	}

	public String getADDRESS_NEW() {
		return ADDRESS_NEW;
	}

	public void setADDRESS_NEW(String address_new) {
		ADDRESS_NEW = address_new;
	}

	public String getSALE_TYPE() {
		return SALE_TYPE;
	}

	public void setSALE_TYPE(String sALE_TYPE) {
		SALE_TYPE = sALE_TYPE;
	}

	public String getIS_WHOLESALE() {
		return IS_WHOLESALE;
	}

	public void setIS_WHOLESALE(String iS_WHOLESALE) {
		IS_WHOLESALE = iS_WHOLESALE;
	}

	public String getLOCATION_TYPE() {
		return LOCATION_TYPE;
	}

	public void setLOCATION_TYPE(String lOCATION_TYPE) {
		LOCATION_TYPE = lOCATION_TYPE;
	}

	public String getDIANZHAO() {
		return DIANZHAO;
	}

	public void setDIANZHAO(String dIANZHAO) {
		DIANZHAO = dIANZHAO;
	}

	public String getPROVIDE_DATE() {
		return PROVIDE_DATE;
	}

	public void setPROVIDE_DATE(String pROVIDE_DATE) {
		PROVIDE_DATE = pROVIDE_DATE;
	}
    
	
}
