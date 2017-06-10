package com.want.batch.job.sms.util;

public enum SapSMSFuncName {
	
      FUNC_NAME("ZMMRFC_002");
      
      String func_name;
      SapSMSFuncName(String func_name){
    	  this.func_name=func_name;
      }
	/**
	 * @return the func_name
	 */
	public String getFunc_name() {
		return func_name;
	}
	/**
	 * @param func_name the func_name to set
	 */
	public void setFunc_name(String func_name) {
		this.func_name = func_name;
	}
      
}
