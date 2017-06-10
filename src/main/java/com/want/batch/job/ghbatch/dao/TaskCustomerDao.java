/**
 * 
 */
package com.want.batch.job.ghbatch.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.ghbatch.pojo.TaskCustomer;
import com.want.batch.job.ghbatch.pojo.TaskDetail;
import com.want.batch.job.ghbatch.util.DateFormatEnum;

/**
 * @author MandyZhang
 *
 */
@Component
public class TaskCustomerDao {

	@Autowired
	public DataSource iCustomerDataSource;
	
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	// 根据行程日期，查询所有稽核客户
	public List<TaskCustomer> findByTaskDetailDate(DateTime taskDetailDate) throws SQLException {

		List<TaskCustomer> lstTaskCustomer = new ArrayList<TaskCustomer>();
		
		String pattern = DateFormatEnum.DATE_NO_PARTITION.getPattern();

		String sql = new StringBuilder()
			.append("SELECT * ")
				.append("FROM TASK_CUSTOMER TC ")
				.append("  INNER JOIN TASK_DETAIL TD ON TD.TASK_DETAIL_ID = TC.TASK_DETAIL_ID ")
				.append("WHERE TO_CHAR(TASK_DATE, '%s') = ? AND IS_DELETE = ? ")
				.toString();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			
			conn = iCustomerDataSource.getConnection();
			pstmt = conn.prepareStatement(String.format(sql, pattern));
			pstmt.setString(1, taskDetailDate.toString(pattern));
			pstmt.setString(2, TaskDetail.UNDELETE);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				TaskCustomer taskCustomer = new TaskCustomer();
				taskCustomer.setSid(rs.getInt("SID"));
				taskCustomer.setCustomerId(rs.getString("CUSTOMER_ID"));
				
				lstTaskCustomer.add(taskCustomer);
			}
		} catch (SQLException e) {
			
			logger.error("TaskCustomerDao: findByTaskDetailDate--------------------" + e.getLocalizedMessage());
			throw e;
		}
		finally {
			
			if(rs != null) {
				
				rs.close();
			}
			
			if (pstmt != null) {
				
				pstmt.close();
			}
			
			if (conn != null) {
			
				conn.close();
			}
		}
		
		return lstTaskCustomer;
	}
	
	/**
	 * <pre>
	 * 2012-11-13 Mirabelle
	 * 	取得往稽核客户库存表的源资料
	 * </pre>
	 * 
	 * @param taskDetailDate
	 * @return
	 */
	public List<Map<String, Object>> getTaskCustomerProductInfos(DateTime taskDetailDate) throws SQLException {

		List<Map<String, Object>> returnList = new ArrayList<Map<String,Object>>();
		
		String sql = new StringBuilder()
			.append(" SELECT")
			.append(" 	DISTINCT D.SID,")
			.append(" 	A.CUSTOMER_THIRD_ID,")
			.append(" 	B.TASK_DATE,")
			.append(" 	D.CUSTOMER_DIVISION,")
			.append(" 	A.CUSTOMER_ID,")
			.append(" 	A.CUSTOMER_NAME,")
			.append(" 	D.LV_5_ID,")
			.append(" 	RTRIM(F.PROD_NAME, ' ') AS PROD_NAME,")
			.append(" 	D.TOTAL_QTY,")
			.append(" 	D.PRODUCT_QTY_LAST,")
			.append(" 	D.PRODUCT_QTY_END,")
			.append(" 	(D.RECENTLY_QTY_AVG * 0.2) AS RECENTLY_QTY_AVG,")
			.append(" 	D.CHECK_DATE,")
			.append(" 	TO_DATE(TO_CHAR(D.CHECK_DATE, 'yyyy-mm-dd'), 'yyyy-mm-dd') AS CREATE_DATE,")
			.append(" 	D.CHECK_USER AS CHECK_USER,")
			.append(" 	C.JH_ID,")
			.append(" 	C.JH_NAME,")
			.append(" 	(	SELECT")
			.append(" 			SUM(DD.PRODUCT_QTY_LAST)")
			.append(" 		FROM")
			.append(" 			TASK_CUSTOMER_PRODUCT DD,")
			.append(" 			TASK_CUSTOMER AA") 
			.append(" 		WHERE")
			.append(" 			DD.TASK_CUSTOMER_SID = AA.SID AND")
			.append(" 			DD.CHECK_USER = D.CHECK_USER AND")
			.append(" 			AA.CUSTOMER_ID = A.CUSTOMER_ID")
			.append(" 	)")
			.append(" 	ALL_SUM_RESULT,D.IS_IMPORTANT") // modify 2014-11-07 添加是否是重点品项标示
			.append(" FROM")
			.append(" 	TASK_CUSTOMER A,")
			.append(" 	TASK_DETAIL B,")
			.append(" 	TASK_LIST C,")
			.append(" 	TASK_CUSTOMER_PRODUCT D,")
			.append(" 	PROD_GROUP_TBL F")
			.append(" WHERE")
			.append(" 	A.TASK_DETAIL_ID = B.TASK_DETAIL_ID AND")
			.append(" 	B.TASK_LIST_ID = C.TASK_LIST_ID AND")
			.append(" 	A.SID = D.TASK_CUSTOMER_SID AND")
			.append(" 	D.LV_5_ID = F.PROD_ID AND")
			.append(" 	B.IS_DELETE = '0' AND")
			.append(" 	to_char(B.TASK_DATE,'yyyyMMdd') = ? ")
			.toString();
		
		String pattern = DateFormatEnum.DATE_NO_PARTITION.getPattern();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			
			conn = iCustomerDataSource.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, taskDetailDate.toString(pattern));
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("SID", rs.getString("SID"));
				map.put("CUSTOMER_THIRD_ID", rs.getString("CUSTOMER_THIRD_ID"));
				map.put("TASK_DATE", rs.getTimestamp("TASK_DATE"));
				map.put("CUSTOMER_DIVISION", rs.getString("CUSTOMER_DIVISION"));
				map.put("CUSTOMER_ID", rs.getString("CUSTOMER_ID"));
				map.put("CUSTOMER_NAME", rs.getString("CUSTOMER_NAME"));
				map.put("LV_5_ID", rs.getString("LV_5_ID"));
				map.put("PROD_NAME", rs.getString("PROD_NAME"));
				map.put("TOTAL_QTY", rs.getString("TOTAL_QTY"));
				map.put("PRODUCT_QTY_LAST", rs.getString("PRODUCT_QTY_LAST"));
				map.put("PRODUCT_QTY_END", rs.getString("PRODUCT_QTY_END"));
				map.put("RECENTLY_QTY_AVG", rs.getString("RECENTLY_QTY_AVG"));
				map.put("CHECK_DATE", rs.getTimestamp("CHECK_DATE"));
				map.put("CREATE_DATE", rs.getTimestamp("CREATE_DATE"));
				map.put("CHECK_USER", rs.getString("CHECK_USER"));
				map.put("JH_ID", rs.getString("JH_ID"));
				map.put("JH_NAME", rs.getString("JH_NAME"));
				map.put("ALL_SUM_RESULT", rs.getString("ALL_SUM_RESULT"));
				map.put("IS_IMPORTANT", rs.getString("IS_IMPORTANT"));
				
				returnList.add(map);
			}
		} catch (SQLException e) {
			
			logger.error("TaskCustomerDao: getTaskCustomerProductInfos--------------------" + e.getLocalizedMessage());
			throw e;
		}
		finally {
			
			if (rs != null) {
			
				rs.close();
			}
			
			if (pstmt != null) {
				
				pstmt.close();
			}
			
			if (conn != null) {
				
				conn.close();
			}
		}
		
		return returnList;
	}
}
