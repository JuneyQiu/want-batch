/**
 * 
 */
package com.want.batch.job.reportproduce.dao;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

/**
 * @author MandyZhang
 *
 */
@Component
public class TaskStoreSubrouteDao {

	@Autowired
	public SimpleJdbcOperations iCustomerJdbcOperations;
	
	/**
	 * <pre>
	 * 2010-3-17 Deli
	 * 	查询终端线路
	 * </pre>
	 * 
	 * @return Map<String, Object>
	 */
	public Map<String, Object> getStoreSubrote(String taskDetailId) {
		
		StringBuilder sql = new StringBuilder()
			.append(" SELECT ")
			.append(" 	branch.BRANCH_ID, ")
			.append(" 	branch.BRANCH_NAME, ")
			.append("   store.SID, ")
			.append(" 	store.EMP_ID, ")
			.append(" 	store.EMP_NAME, ")
			.append(" 	store.CUSTOMER_ID, ")
			.append(" 	store.CUSTOMER_NAME, ")
			.append(" 	store.FIT_LINE, ")
			.append(" 	store.REASON_FIT_LINE, ")
			.append("   store.SUBROUTE_SID, ")
			
			// 2010-08-25 Deli add
			.append("   store.IS_ALL ")
			
			.append(" FROM ")
			.append(" 	TASK_STORE_SUBROUTE store ")
			.append(" 		INNER JOIN BRANCH_EMP branchEmp ")
			.append(" 		ON store.EMP_ID = branchEmp.EMP_ID ")
			.append(" 			INNER JOIN BRANCH_INFO_TBL branch ")
			.append(" 			ON branchEmp.BRANCH_SAP_ID = branch.BRANCH_ID ")
			.append(" WHERE store.TASK_DETAIL_ID = :taskDetailId ");
		
		return iCustomerJdbcOperations.queryForMap(sql.toString(), taskDetailId);
	}
}
