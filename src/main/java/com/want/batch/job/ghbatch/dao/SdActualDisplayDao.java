
// ~ Package Declaration
// ==================================================

package com.want.batch.job.ghbatch.dao;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;


// ~ Comments
// ==================================================
@Component
public class SdActualDisplayDao {

	// ~ Static Fields
	// ==================================================

	// ~ Fields
	// ==================================================

	@Autowired
	public DataSource iCustomerDataSource;
	
	@Autowired
	public SimpleJdbcOperations iCustomerJdbcOperations;
	
	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================

	public List<Map<String, Object>> getYdDisplay(String yearMonth, String storeIds) {
		
		StringBuilder sql = new StringBuilder()
			.append(" SELECT ")
			.append(" 	DISTINCT ")
			.append("			SD_ACTUAL_DISPLAY.STORE_ID,")
			.append("			CUSTOMER_INFO_TBL.ID,")
			.append("			CUSTOMER_INFO_TBL.NAME,")
			.append("			SD_ACTUAL_DISPLAY.LOCATION_TYPE_SID,")
			.append("			SD_ACTUAL_DISPLAY.DISPLAY_TYPE_SID,")
			.append("			SD_ACTUAL_DISPLAY.DISPLAY_ACREAGE,")
			.append("			SD_ACTUAL_DISPLAY.DISPLAY_SIDECOUNT,")
			.append("			SD_ACTUAL_DISPLAY.DISPLAY_PARAM_ID,")
			.append("			SPECIAL_DISPLAY_POLICY.DIVISION_SID,")
			.append("			SD_ACTUAL_DISPLAY.STORE_DISPLAY_SID,")
			.append("     SPECIAL_DISPLAY_POLICY.SID SPECIAL_POLICY_SID,")
			.append("     SD_ACTUAL_DISPLAY.ASSETS_ID,")
			.append("     SD_ACTUAL_DISPLAY.SID ACTUAL_DISPLAY_SID,")
			.append("			NVL(SD_ACTUAL_DISPLAY.UPDATE_DATE, SD_ACTUAL_DISPLAY.CREATE_DATE) AS SALES_INPUT_DATETIME ")
			.append(" FROM")
			.append(" 	SD_ACTUAL_DISPLAY") 
			.append(" 		INNER JOIN APPLICATION_STORE_DISPLAY") 
			.append(" 		ON SD_ACTUAL_DISPLAY.STORE_DISPLAY_SID = APPLICATION_STORE_DISPLAY.SID") 
			.append(" 			INNER JOIN APPLICATION_STORE") 
			.append(" 			ON APPLICATION_STORE_DISPLAY.APPLICATION_STORE_SID = APPLICATION_STORE.SID") 
			.append(" 				INNER JOIN SPECIAL_DISPLAY_APPLICATION ")
			.append(" 				ON APPLICATION_STORE.APPLICATION_SID = SPECIAL_DISPLAY_APPLICATION.SID") 
			.append(" 					INNER JOIN SPECIAL_DISPLAY_ACTUAL ")
			.append(" 					ON SPECIAL_DISPLAY_APPLICATION.SID = SPECIAL_DISPLAY_ACTUAL.APPLICATION_SID") 
			.append(" 						INNER JOIN SPECIAL_DISPLAY_POLICY ")
			.append(" 						ON SPECIAL_DISPLAY_POLICY.SID = SPECIAL_DISPLAY_APPLICATION.POLICY_SID") 
			.append(" 							INNER JOIN CUSTOMER_INFO_TBL ")
			.append(" 							ON SPECIAL_DISPLAY_APPLICATION.CUSTOMER_SID = CUSTOMER_INFO_TBL.SID") 
			.append(" WHERE")
			.append(" 	SPECIAL_DISPLAY_APPLICATION.YEAR_MONTH = :yearmonth AND")
			.append(" 	SD_ACTUAL_DISPLAY.LOCATION_TYPE_SID <> 0 AND")
			.append(" 	APPLICATION_STORE_DISPLAY.PLAN_PAYMENT > 0 AND")
			.append(" 	APPLICATION_STORE.STORE_ID IN (" + storeIds + ") AND")
			.append(" 	SD_ACTUAL_DISPLAY.CREATE_USER_TYPE=1 ")
			.append(" ORDER BY")
			.append(" 	SD_ACTUAL_DISPLAY.STORE_ID,")
			.append(" 	SD_ACTUAL_DISPLAY.STORE_DISPLAY_SID");
		
		return iCustomerJdbcOperations.queryForList(sql.toString(), yearMonth);
	}
	
}
