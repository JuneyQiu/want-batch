package com.want.batch.job.sms.util;

public enum SapReutnSMSTableName {

	TABLE_NAME("T_DATA");
	
	String tableName;
	SapReutnSMSTableName(String tableName){
		this.tableName=tableName;
	}
	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}
	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
}
