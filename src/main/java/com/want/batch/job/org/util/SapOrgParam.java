package com.want.batch.job.org.util;

public enum SapOrgParam{
	FUNC_NAME("ZRFC12")
	,THIRD("ADRCITYPRT")
	,MARKET("ZBW_SDT003")
	,BRANCH("TVKBT")
	,COMPANY("ZBW_SDT018")
	,THIRD_MARKET("ZBW_SDT004")
	,MARKET_BRANCH("ZBW_SDT015")
	,THIRD_COMPANY("ZBW_SDT025");
	
	String tableName;
	SapOrgParam(String tableName){
		this.tableName=tableName;
	}
	
	public String getTableName(){
		return tableName;
	}
	public void setTableName(String tableName){
		this.tableName=tableName;
	}
}
