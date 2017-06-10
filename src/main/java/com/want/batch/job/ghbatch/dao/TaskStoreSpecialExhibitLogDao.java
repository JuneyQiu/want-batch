
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
public class TaskStoreSpecialExhibitLogDao {

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

	public void addSpecialLog(String sid) {
		
		StringBuilder sql = new StringBuilder()
			.append(" INSERT")
			.append(" INTO")
			.append(" 	ICUSTOMER.TASK_STORE_SPECIAL_EXHIBIT_LOG") 
			.append(" SELECT")
			.append(" 	*") 
			.append(" FROM")
			.append(" 	ICUSTOMER.TASK_STORE_SPECIAL_EXHIBIT") 
			.append(" WHERE")
			.append(" SID IN (" + sid + ")");
		
		iCustomerJdbcOperations.update(sql.toString());
	}
}
