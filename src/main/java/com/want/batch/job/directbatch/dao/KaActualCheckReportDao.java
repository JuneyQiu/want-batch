/**
 * 
 */
package com.want.batch.job.directbatch.dao;

import java.util.List;

import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

/**
 * @author MandyZhang
 *
 */
@Component
public class KaActualCheckReportDao {

	// 删除KA_ACTUAL_CHECK_REPORT表中的资料
	public void deleteAllData(SimpleJdbcOperations iCustomerJdbcOperations) {
		
		String sql = "TRUNCATE TABLE KA_ACTUAL_CHECK_REPORT ";
		
		iCustomerJdbcOperations.update(sql);
	}
	
	// 批量增入数据
	public void insertKaActualCheckReport(List<Object[]> args, SimpleJdbcOperations iCustomerJdbcOperations) {

		StringBuilder sql = new StringBuilder()
		.append("	INSERT INTO ")
		.append(" 		KA_ACTUAL_CHECK_REPORT  ")
		.append(" VALUES ")
		.append(" (")
		.append("KA_ACTUAL_CHECK_REPORT_SEQ.nextval, ")
		.append("?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		
		iCustomerJdbcOperations.batchUpdate(sql.toString(), args);
	}
}
