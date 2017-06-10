
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
public class SdActualProdDao {

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

	public List<Map<String, Object>> getSpecialProdByActualDisplaySid(String actualDisplaySid) {
		
		StringBuilder sql = new StringBuilder()
			.append(" SELECT")
			.append(" 	SD_ACTUAL_DISPLAY.STORE_ID,")
			.append("   SD_ACTUAL_DISPLAY.SID ACTUAL_DISPLAY_SID, ")
			.append(" 	PRODUCT_ID          AS prodId,")
			.append(" 	PROD_INFO_TBL.NAME1 AS prodName") 
			.append(" FROM")
			.append(" 	SD_ACTUAL_DISPLAY") 
			.append(" 		INNER JOIN SD_ACTUAL_PROD") 
			.append(" 		ON SD_ACTUAL_DISPLAY.SID=SD_ACTUAL_PROD.ACTUAL_DISPLAY_SID") 
			.append(" 			INNER JOIN PROD_INFO_TBL") 
			.append(" 			ON SD_ACTUAL_PROD.PRODUCT_ID = PROD_INFO_TBL.PROD_ID") 
			.append(" WHERE")
			.append(" 	ACTUAL_DISPLAY_SID  in (" + actualDisplaySid + ")") 
			.append(" ORDER BY")
			.append(" 	SD_ACTUAL_DISPLAY.STORE_ID,SD_ACTUAL_DISPLAY.STORE_DISPLAY_SID");
		
		return iCustomerJdbcOperations.queryForList(sql.toString());
	}
}
