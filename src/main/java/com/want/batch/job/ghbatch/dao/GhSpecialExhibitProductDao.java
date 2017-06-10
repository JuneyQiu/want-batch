
// ~ Package Declaration
// ==================================================

package com.want.batch.job.ghbatch.dao;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;


// ~ Comments
// ==================================================
@Component
public class GhSpecialExhibitProductDao {

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

	public void deleteSpecialProductBySid(String sid) {
		
		String sql = "delete from ICUSTOMER.SPECIALEXHIBIT_PRODUCT where TASK_STORE_SPECIAL_EXHIBIT_SID in (" + sid + ")";
		iCustomerJdbcOperations.update(sql);
	}
	
	public void addSpecialProduct(List<Object[]> args) {
		
		StringBuilder sql = new StringBuilder()
			.append(" INSERT INTO ICUSTOMER.SPECIALEXHIBIT_PRODUCT ")
			.append("			(SID, TASK_STORE_SPECIAL_EXHIBIT_SID, PRODUCT_ID, PRODUCT_NAME,")
			.append("			IS_HAVE_SPECIAL_EXHIBIT, DATE_ROUTE)") 
			.append("		VALUES(ICUSTOMER.SPECIALEXHIBIT_PRODUCT_SEQ.NEXTVAL, ?, ?, ?, null, null)");
		
		iCustomerJdbcOperations.batchUpdate(sql.toString(), args);
	}
}
