
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
public class GhTaskStoreSpecialExhibitDao {

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

	public List<Map<String, Object>> getSpecialDisplayBySid(String sid) {
		
		String sql = "select * from TASK_STORE_SPECIAL_EXHIBIT where sid in (" + sid + ")";
		
		return iCustomerJdbcOperations.queryForList(sql);
	}
	
	public void deleteSpecialDisplaySid(String sid) {
		
		String sql = "delete from TASK_STORE_SPECIAL_EXHIBIT where sid  in (" + sid + ")";
		iCustomerJdbcOperations.update(sql);
	}
	
	public void addSpecialDisplay(List<Object[]> args) {
		
		StringBuilder sql = new StringBuilder() 
		.append("	INSERT INTO ICUSTOMER.TASK_STORE_SPECIAL_EXHIBIT")
		.append(" 		(SID, TASK_STORE_LIST_SID, CUSTOMER_ID, CUSTOMER_NAME,")
		.append("			SPECIAL_IS_OK, LOCATION_TYPE_NAME, DISPLAY_TYPE_NAME,DISPLAY_ACREAGE, DISPLAY_SIDECOUNT,")
		.append("			LOCATION_TYPE_NAME_RESULT, DISPLAY_TYPE_NAME_RESULT, DISPLAY_ACREAGE_RESULT, DISPLAY_SIDECOUNT_RESULT,")
		.append("			CREATE_USER, CREATE_DATE, UPDATE_USER, UPDATE_DATE,")
		.append("			DIVSION_SID, SPECIAL_DISPLAY_LINE2_SID, STORE_DISPLAY_SID,")
		.append("			DISPLAY_PARAM_ID, DISPLAY_PARAM_ID_RESULT, SALES_INPUT_DATETIME,")
		.append("			SPECIAL_POLICY_SID, ASSETS_ID)") 
		.append(" VALUES (?, ?, ?, ?,")
		.append("			null, ?, ?, ?, ?,")
		.append("			null, null, null, null,")
		.append("			?, ?, null, null,")
		.append("			?, null, ?,")
		.append("			?, null, ?, ?, ?)");
		
		iCustomerJdbcOperations.batchUpdate(sql.toString(), args);
	}
	 public int getSpecialDisplaySid() {
		 
		 String sql = "select ICUSTOMER.TASK_STORE_SPECIAL_EXHIBIT_SEQ.NEXTVAL from dual";
		 
		 return iCustomerJdbcOperations.queryForInt(sql);
	 }
	 
	 public List<Map<String, Object>> getJhStoreDisplayInfo(String taskDate) {
			
			StringBuilder sql = new StringBuilder()
				.append(" SELECT ")
				.append("   TASK_STORE_LIST.sid TASK_STORE_LIST_SID,")
				.append("   TASK_STORE_LIST.STORE_ID,")
				.append(" 	TASK_STORE_SPECIAL_EXHIBIT.SID,")
				.append(" 	TASK_STORE_SPECIAL_EXHIBIT.STORE_DISPLAY_SID,")
				.append(" 	TASK_STORE_SPECIAL_EXHIBIT.LOCATION_TYPE_NAME")
				.append(" FROM")
				.append(" 	TASK_LIST") 
				.append(" 		INNER JOIN TASK_DETAIL") 
				.append(" 		ON TASK_LIST.TASK_LIST_ID=TASK_DETAIL.TASK_LIST_ID") 
				.append(" 			INNER JOIN TASK_STORE_SUBROUTE ")
				.append(" 			ON TASK_DETAIL.TASK_DETAIL_ID=TASK_STORE_SUBROUTE.TASK_DETAIL_ID") 
				.append(" 				INNER JOIN TASK_STORE_LIST ")
				.append(" 				ON TASK_STORE_SUBROUTE.SID=TASK_STORE_LIST.TASK_STORE_SUBROUTE_SID") 
				.append(" 					left JOIN TASK_STORE_SPECIAL_EXHIBIT ")
				.append(" 					ON TASK_STORE_LIST.SID=TASK_STORE_SPECIAL_EXHIBIT.TASK_STORE_LIST_SID")
				.append(" WHERE TASK_DETAIL.TASK_DATE = to_date('" + taskDate + "','yyyyMMdd')")
				.append("		ORDER BY TASK_STORE_LIST.STORE_ID,TASK_STORE_SPECIAL_EXHIBIT.STORE_DISPLAY_SID");
			
			return iCustomerJdbcOperations.queryForList(sql.toString());
			
		}
}
