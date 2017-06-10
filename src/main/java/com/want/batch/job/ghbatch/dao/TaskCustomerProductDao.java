/**
 * 
 */
package com.want.batch.job.ghbatch.dao;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.ghbatch.pojo.TaskCustomer;
import com.want.batch.job.ghbatch.pojo.TaskCustomerProduct;

/**
 * @author MandyZhang
 *
 */
@Component
public class TaskCustomerProductDao {

	@Autowired
	public DataSource iCustomerDataSource;
	
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	// 根据 {@link TaskCustomer} 查询所对应的所有 {@link TaskCustomerProduct}
	public List<TaskCustomerProduct> findByTaskCustomer(TaskCustomer taskCustomer) throws SQLException {
		
		List<TaskCustomerProduct> lstTaskCustomerProduct = new ArrayList<TaskCustomerProduct>();
		
		StringBuilder sql = new StringBuilder()
			.append(" SELECT ")
			.append(" 	SID,TASK_DETAIL_ID,TASK_CUSTOMER_SID, ")
			.append("	TOTAL_QTY, DIFF_QTY,DIFF_RATE,RESULT, ")
			.append("	PUNISHMENT,LV_5_ID,CHECK_YEAR_MONTH, ")
			.append("	CHECK_DAY,PRODUCT_QTY_LAST,PRODUCT_QTY_END, ")
			.append("	PRODUCT_QTY_PROCESS,CREATE_USER,CREATE_DATE, ")
			.append("	UPDATE_USER,UPDATE_DATE,REASON, ")
			.append("	RECENTLY_QTY_AVG,SYSTEM_QTY_TOTAL, ")
			.append("	IS_IMPORTANT,CHECK_USER,CHECK_DATE, ")
			.append("	TASK_DATE,CUSTOMER_DIVISION ")
			.append(" FROM TASK_CUSTOMER_PRODUCT ")
			.append(" WHERE TASK_CUSTOMER_SID = ? ");
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			
			conn = iCustomerDataSource.getConnection();
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(1, taskCustomer.getSid());
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				TaskCustomerProduct taskCustomerProduct = new TaskCustomerProduct();
				taskCustomerProduct.setSid(rs.getInt("SID"));
				taskCustomerProduct.setTaskDetailId(rs.getInt("TASK_DETAIL_ID"));
				taskCustomerProduct.setTaskCustomerSid(rs.getInt("TASK_CUSTOMER_SID"));
				taskCustomerProduct.setTotalQty(rs.getInt("TOTAL_QTY"));
				taskCustomerProduct.setDiffQty(rs.getBigDecimal("DIFF_QTY"));
				taskCustomerProduct.setDiffRate(rs.getBigDecimal("DIFF_RATE"));
				taskCustomerProduct.setResult(rs.getString("RESULT"));
				taskCustomerProduct.setPunishment(rs.getString("PUNISHMENT"));
				taskCustomerProduct.setLv5Id(rs.getString("LV_5_ID"));
				taskCustomerProduct.setCheckYearMonth(rs.getString("CHECK_YEAR_MONTH"));
				taskCustomerProduct.setCheckDay(rs.getString("CHECK_DAY"));
				taskCustomerProduct.setProductQtyLast(rs.getInt("PRODUCT_QTY_LAST"));
				taskCustomerProduct.setProductQtyEnd(rs.getBigDecimal("PRODUCT_QTY_END"));
				taskCustomerProduct.setCreateUser(rs.getString("CREATE_USER"));
				taskCustomerProduct.setCreateDate(rs.getDate("CREATE_DATE"));
				taskCustomerProduct.setUpdateDate(rs.getDate("UPDATE_DATE"));
				taskCustomerProduct.setUpdateUser(rs.getString("UPDATE_USER"));
				taskCustomerProduct.setReason(rs.getString("REASON"));
				taskCustomerProduct.setRecentlyQtyAvg(rs.getInt("RECENTLY_QTY_AVG"));
				taskCustomerProduct.setSystemQtyTotal(rs.getInt("SYSTEM_QTY_TOTAL"));
				taskCustomerProduct.setIsImportant(rs.getString("IS_IMPORTANT"));
				taskCustomerProduct.setCheckUser(rs.getString("CHECK_USER"));
				taskCustomerProduct.setCheckDate(rs.getDate("CHECK_DATE"));
				taskCustomerProduct.setTaskDate(rs.getDate("TASK_DATE"));
				taskCustomerProduct.setCustomerDivision(rs.getInt("CUSTOMER_DIVISION"));
				
				lstTaskCustomerProduct.add(taskCustomerProduct);
			}
		} catch (SQLException e) {
			
			logger.error("TaskCustomerProductDao: findByTaskCustomer--------------------------" + e.getLocalizedMessage());
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
		
		return lstTaskCustomerProduct;
	}
	
	// 修改方法
	public void update(TaskCustomerProduct taskCustomerProduct) throws SQLException {
		
		StringBuilder sql = new StringBuilder()
			.append(" UPDATE TASK_CUSTOMER_PRODUCT ")
			.append(" SET CHECK_YEAR_MONTH=?,CHECK_DAY=?,PRODUCT_QTY_LAST=?,CHECK_USER=?, ")
			.append("	CHECK_DATE=?,TASK_DATE=?,CUSTOMER_DIVISION=?,RECENTLY_QTY_AVG=?,SYSTEM_QTY_TOTAL=?, ")
			.append("	PRODUCT_QTY_END=?,UPDATE_USER=?,UPDATE_DATE=? ")
			.append(" WHERE SID=? ");
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			
			conn = iCustomerDataSource.getConnection();
			pstmt = conn.prepareStatement(sql.toString());
			
			pstmt.setString(1, taskCustomerProduct.getCheckYearMonth());
			pstmt.setString(2, taskCustomerProduct.getCheckDay());
			pstmt.setInt(3, (null != taskCustomerProduct.getProductQtyLast()) ? taskCustomerProduct.getProductQtyLast() : 0);
			pstmt.setString(4, taskCustomerProduct.getCheckUser());
			pstmt.setTimestamp(5, (null != taskCustomerProduct.getCheckDate()) ? new Timestamp(taskCustomerProduct.getCheckDate().getTime()) : null);
			pstmt.setTimestamp(6, new Timestamp(taskCustomerProduct.getTaskDate().getTime()));
			pstmt.setLong(7, (null != taskCustomerProduct.getCustomerDivision()) ? taskCustomerProduct.getCustomerDivision() : 0);
			pstmt.setLong(8, (null != taskCustomerProduct.getRecentlyQtyAvg()) ? taskCustomerProduct.getRecentlyQtyAvg() : 0);
			pstmt.setLong(9, (null != taskCustomerProduct.getSystemQtyTotal()) ? taskCustomerProduct.getSystemQtyTotal() : 0);
			pstmt.setBigDecimal(10, taskCustomerProduct.getProductQtyEnd());
			pstmt.setString(11, "sys");
			pstmt.setTimestamp(12, new Timestamp(new Date().getTime()));
			pstmt.setInt(13, taskCustomerProduct.getSid());
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
			
			logger.error("TaskCustomerProductDao: update--------------------------" + e.getLocalizedMessage());
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
}
