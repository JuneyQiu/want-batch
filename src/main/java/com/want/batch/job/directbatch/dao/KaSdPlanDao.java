/**
 * 
 */
package com.want.batch.job.directbatch.dao;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

/**
 * @author MandyZhang
 *
 */
@Component
public class KaSdPlanDao {

	protected final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	public DataSource iCustomerDataSource;
	
	public List<Map<String, Object>> getKaSdActualInputTime(String yearMonth, String flag) throws SQLException {
		
		List<Map<String, Object>> returnList = new ArrayList<Map<String,Object>>();
		
		StringBuilder sql = new StringBuilder()
			.append("	SELECT ")
			.append("		KA_SD_ACTUAL_DETAIL.PLAN_DETAIL_SID, ");
			
		 // 业代
		 if ("sales".equals(flag)) {
			 
			 sql.append("		MIN(KA_SD_ACTUAL_DETAIL.UPDATE_DATE_SALES) AS UPDATE_DATE ");
				
		 }
		 
		 // 主任
		 else {
			 
			 sql.append("		MIN(KA_SD_ACTUAL_DETAIL.UPDATE_DATE_DIRECTOR) AS UPDATE_DATE ");
		 }
			
		 sql.append("	FROM ")
				.append("		KA_SD_PLAN ")
				.append("			INNER JOIN KA_SD_PLAN_DETAIL ")
				.append("			ON KA_SD_PLAN.SD_NO = KA_SD_PLAN_DETAIL.SD_NO ")
				.append("			AND KA_SD_PLAN.YEAR_MONTH IN (" + yearMonth + ") ")
				// modify 2013-10-09 mandy 添加KA_PLAN_ACTUAL_REL特陈实际与计划关联表
				.append("			INNER JOIN KA_PLAN_ACTUAL_REL ")
				.append("			ON KA_SD_PLAN_DETAIL.SID=KA_PLAN_ACTUAL_REL.PLAN_DETAIL_SID ")
				.append("				INNER JOIN KA_SD_ACTUAL_DETAIL ")
				.append("				ON KA_SD_ACTUAL_DETAIL.SID=KA_PLAN_ACTUAL_REL.ACTUAL_DETAIL_SID ");
	 
		 // 业代
		 if ("sales".equals(flag)) {
			 
			 sql.append("		AND KA_SD_ACTUAL_DETAIL.UPDATE_DATE_SALES IS NOT NULL  ");
				
		 }
		 
		 // 主任
		 else {
			 
			 sql.append("		AND KA_SD_ACTUAL_DETAIL.UPDATE_DATE_DIRECTOR IS NOT NULL  ");
		 }
		 
		 sql.append(" GROUP BY ")
				.append("		KA_SD_ACTUAL_DETAIL.PLAN_DETAIL_SID ")
				.append("	ORDER BY ")
				.append("		KA_SD_ACTUAL_DETAIL.PLAN_DETAIL_SID ");
		
		 Connection conn = null;
		 PreparedStatement pstmt = null;
		 ResultSet rs = null;
		 
		 try {
			 
			conn = iCustomerDataSource.getConnection();
			pstmt = conn.prepareStatement(sql.toString());
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("PLAN_DETAIL_SID", rs.getString("PLAN_DETAIL_SID"));
				map.put("UPDATE_DATE", rs.getString("UPDATE_DATE"));
				
				returnList.add(map);
			}
			
		} catch (SQLException e) {
			
			logger.error("KaSdPlanDao: getKaSdActualInputTime-------------------------------" + e.getLocalizedMessage());
			throw e;
		}
		finally {
			
			rs.close();
			pstmt.close();
			conn.close();
		}
		
		return returnList;
	}
	
	public void updateKaSdPlanTime(List<Object[]> list, String flag, SimpleJdbcOperations iCustomerJdbcOperations) {
		
		String salesSql = " UPDATE KA_SD_PLAN_DETAIL SET SUBMIT_DATE_SALES = to_date(?,'yyyy-mm-dd hh24:mi:ss') WHERE SID = ? ";
		String directorSql = " UPDATE KA_SD_PLAN_DETAIL SET SUBMIT_DATE_DIRECTOR = to_date(?,'yyyy-mm-dd hh24:mi:ss') WHERE SID = ? ";
		
		iCustomerJdbcOperations.batchUpdate("sales".equals(flag) ? salesSql : directorSql, list);
			
	}
	
	public List<Map<String, Object>> getTwoYearMonthData(String dateParam, SimpleJdbcOperations iCustomerJdbcOperations) {

		StringBuilder sql = new StringBuilder()
			.append(" SELECT ")
			.append("		DISTINCT KA_SD_PLAN.DIVISION_SID, ")
			.append("		KA_SD_PLAN.SYS_ID, ")
		  .append("		KA_SD_PLAN.COMPANY_ID, ")
		  .append("		KA_SD_PLAN_DETAIL.STORE_ID, ")
		  .append("		KA_SD_PLAN.SD_NO, ")
		  .append("		KA_SD_PLAN_DETAIL.BPM_ID, ")
		  .append("		KA_SD_PLAN_DETAIL.TPM_ID, ")
		  .append("		KA_SD_PLAN.YEAR_MONTH, ")
		  .append("		KA_SD_PLAN_DETAIL.PROMOTION_DATE_START, ")
		  .append("		KA_SD_PLAN_DETAIL.PROMOTION_DATE_END, ")
		  .append("		KA_SD_PLAN_DETAIL.DISPLAY_ID, ")
		  .append("		PROD_GROUP_TBL.PROD_ID, ")
		  .append("		KA_SD_ACTUAL_DETAIL.UPDATE_USER_SALES, ")
		  .append("		KA_SD_PLAN_DETAIL.SUBMIT_STATUS_SALES, ")
		  .append("		KA_SD_PLAN_DETAIL.SUBMIT_DATE_SALES, ")
		  .append("		KA_SD_ACTUAL_DETAIL.UPDATE_USER_DIRECTOR, ")
		  .append("		KA_SD_PLAN_DETAIL.SUBMIT_STATUS_DIRECTOR, ")
		  .append("		KA_SD_PLAN_DETAIL.SUBMIT_DATE_DIRECTOR  ")
			.append("	FROM ")
			.append("		KA_SD_PLAN ") 
	    .append("		INNER JOIN KA_SD_PLAN_DETAIL ") 
	    .append("		ON KA_SD_PLAN_DETAIL.SD_NO = KA_SD_PLAN.SD_NO ") 
	    .append("			LEFT JOIN PROD_GROUP_TBL ") 
	    .append("			ON PROD_GROUP_TBL.PROD_ID = substr(KA_SD_PLAN_DETAIL.PRODUCT_LV4,0,6) ")
	    // modify 2013-10-09 mandy 添加KA_PLAN_ACTUAL_REL特陈实际与计划关联表
	    .append("				LEFT JOIN KA_PLAN_ACTUAL_REL ")
	    .append("				ON KA_SD_PLAN_DETAIL.SID=KA_PLAN_ACTUAL_REL.PLAN_DETAIL_SID ")
	    .append("				LEFT JOIN KA_SD_ACTUAL_DETAIL  ")
	    .append("				ON KA_PLAN_ACTUAL_REL.ACTUAL_DETAIL_SID = KA_SD_ACTUAL_DETAIL.SID ") 
	    .append("	WHERE ")
	    .append("		KA_SD_PLAN.YEAR_MONTH IN (" + dateParam + ")")
	    .append("	ORDER BY ")
	    .append("		KA_SD_PLAN.YEAR_MONTH, ")
	    .append("		KA_SD_PLAN.COMPANY_ID, ")
	    .append("		KA_SD_PLAN.SYS_ID, ")
	    .append("		KA_SD_PLAN.SD_NO, ")
		  .append("		KA_SD_PLAN_DETAIL.STORE_ID");
		
		return iCustomerJdbcOperations.queryForList(sql.toString());
	}
}
