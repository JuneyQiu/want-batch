
// ~ Package Declaration
// ==================================================

package com.want.batch.job.ghbatch.dao;


import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;


// ~ Comments
// ==================================================
@Component
public class SpecialExhibitProductLogDao {

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

	public void addSpecialProdLog(String specialSid){
		
		StringBuilder sql = new StringBuilder()
		.append(" insert into ICUSTOMER.SPECIALEXHIBIT_PRODUCT_log")
		.append(" SELECT")
		.append(" 	SID,")
		.append(" 	TASK_STORE_SPECIAL_EXHIBIT_SID,")
		.append(" 	PRODUCT_ID,")
		.append(" 	PRODUCT_NAME,")
		.append(" 	IS_HAVE_SPECIAL_EXHIBIT,")
		.append(" 	DATE_ROUTE ")
		.append(" FROM")
		.append(" 	ICUSTOMER.SPECIALEXHIBIT_PRODUCT") 
		.append(" WHERE")
		.append(" 	TASK_STORE_SPECIAL_EXHIBIT_SID ")
		.append(" 	 IN (" + specialSid + ")");
		
		iCustomerJdbcOperations.update(sql.toString());
	}
}
