/**
 * 
 */
package com.want.batch.job.ghbatch.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

import com.want.batch.job.ghbatch.util.DateFormatEnum;

/**
 * @author MandyZhang
 *
 */
@Component
public class AuditCustomerStoreage02Dao {

	@Autowired
	public DataSource dataMartDataSource;
	
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	/**
	 * <pre>
	 * 2012-11-13 Mirabelle
	 * 	删除指定行程日期下的资料
	 * </pre>
	 * 
	 * @param taskDate
	 */
	public void deleteAuditCustomerStoreage02(DateTime taskDate) throws SQLException {

		String sql = "DELETE FROM ICUSTOMER.AUDIT_CUSTOMER_STOREAGE02 WHERE TO_CHAR(TASK_DATE,'yyyyMMdd')=?";
		String pattern = DateFormatEnum.DATE_NO_PARTITION.getPattern();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			
			conn = dataMartDataSource.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, taskDate.toString(pattern));
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
			
			logger.error("AuditCustomerStoreage02Dao: deleteAuditCustomerStoreage02---------------------" + e.getLocalizedMessage());
			throw e;
		}
		finally {
			
			if (pstmt != null) {
				
				pstmt.close();
			}
			
			if (conn != null) {
				
				conn.close();
			}
		}
	}
	
	/**
	 * <pre>
	 * 2012-11-13 Mirabelle
	 * 	批量新增
	 * </pre>
	 * 
	 * @param batchArgs
	 */
	public void createAuditCustomerStoreage02(List<Object[]> batchArgs, SimpleJdbcOperations dataMartDataSource) {

		String sql = new StringBuilder()
			.append(" INSERT")
			.append(" INTO")
			.append(" 	ICUSTOMER.AUDIT_CUSTOMER_STOREAGE02") 
			.append(" 	(")
			.append(" 		SID,")
			.append(" 		CUSTOMER_THIRD_ID,")
			.append(" 		TASK_DATE,")
			.append(" 		CUSTOMER_DIVISION,")
			.append(" 		CUSTOMER_ID,")
			.append(" 		CUSTOMER_NAME,")
			.append(" 		LV_5_ID,")
			.append(" 		PROD_NAME,")
			.append(" 		TOTAL_QTY,")
			.append(" 		PRODUCT_QTY_LAST,")
			.append(" 		PRODUCT_QTY_END,")
			.append(" 		RECENTLY_QTY_AVG,")
			.append(" 		CHECK_DATE,")
			.append(" 		CREATE_DATE,")
			.append(" 		CHECK_USER,")
			.append(" 		JH_ID,")
			.append(" 		JH_NAME,")
			.append(" 		ALL_SUM_RESULT")
			.append(" 	)")
			.append(" VALUES")
			.append(" 	(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)")
			.toString();
		
		dataMartDataSource.batchUpdate(sql, batchArgs);
	}
}
