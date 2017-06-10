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

import org.apache.commons.lang.StringUtils;
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
public class RouteInfoDao {

	protected final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	public DataSource iCustomerDataSource;
	
	@Autowired
	public SimpleJdbcOperations hw09JdbcOperations;
	
	public List<Map<String, Object>> getRouteEmpByStoreIdsAndYearMonth(String dateParam, String storeIds, String divisionSids) throws SQLException {

		List<Map<String, Object>> returnList = new ArrayList<Map<String,Object>>();
		
		StringBuilder sql = new StringBuilder()
			.append(" SELECT ")
			.append(" 	DISTINCT SUBROUTE_STORE_TBL.YEARMONTH, ")
			.append(" 	DIVSION_PROJECT_REL.DIVSION_SID, ")
			.append(" 	STORE_INFO_TBL.MDM_STORE_ID AS STORE_ID, ")
			.append(" 	EMP_INFO_TBL.EMP_ID, ")
			.append(" 	EMP_INFO_TBL.EMP_NAME  ")
			.append(" FROM ")
			.append(" 	HW09.ROUTE_INFO_TBL ROUTE_INFO_TBL ") 
			.append(" 		INNER JOIN DIVSION_PROJECT_REL ") 
			.append(" 		ON DIVSION_PROJECT_REL.PROJECT_SID = ROUTE_INFO_TBL.PROJECT_SID ") 
			.append(" 			INNER JOIN HW09.SUBROUTE_INFO_TBL SUBROUTE_INFO_TBL  ")
			.append(" 			ON ROUTE_INFO_TBL.SID = SUBROUTE_INFO_TBL.ROUTE_SID  ")
			.append(" 				INNER JOIN HW09.SUBROUTE_STORE_TBL SUBROUTE_STORE_TBL ") 
			.append(" 				ON SUBROUTE_INFO_TBL.SID = SUBROUTE_STORE_TBL.SUBROUTE_SID ") 
			.append(" 					INNER JOIN HW09.STORE_INFO_TBL STORE_INFO_TBL  ")
			.append(" 					ON SUBROUTE_STORE_TBL.STORE_SID = STORE_INFO_TBL.SID ") 
			.append(" 						INNER JOIN hw09.EMP_INFO_TBL EMP_INFO_TBL  ")
			.append(" 						ON EMP_INFO_TBL.sid=ROUTE_INFO_TBL.emp_sid  ")
			.append(" WHERE ")
			.append(" 	HW09.SUBROUTE_STORE_TBL.YEARMONTH IN (" + dateParam + ") AND ")
			.append(" 	DIVSION_PROJECT_REL.DIVSION_SID IN (" + divisionSids + ") AND ")
			.append(" 	STORE_INFO_TBL.MDM_STORE_ID IN (" + storeIds + ") ")
			.append(" ORDER BY ")
			.append(" 	SUBROUTE_STORE_TBL.YEARMONTH, ")
			.append(" 	DIVSION_PROJECT_REL.DIVSION_SID, ")
			.append(" 	STORE_INFO_TBL.MDM_STORE_ID ");
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			
			conn = iCustomerDataSource.getConnection();
			pstmt = conn.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("YEARMONTH", rs.getString("YEARMONTH"));
				map.put("DIVSION_SID", rs.getString("DIVSION_SID"));
				map.put("STORE_ID", rs.getString("STORE_ID"));
				map.put("EMP_ID", rs.getString("EMP_ID"));
				map.put("EMP_NAME", rs.getString("EMP_NAME"));
				
				returnList.add(map);
			}
		} catch (SQLException e) {
			
			logger.error("RouteInfoDao: getRouteEmpByStoreIdsAndYearMonth------------------------" + e.getLocalizedMessage());
		}
		finally {
			
			rs.close();
			pstmt.close();
			conn.close();
		}
		
		return returnList;
	}
	
	/**
	 * <pre>
	 * 2010-4-12 Wendy
	 * 	根据业代查询所在区域对应的事业部。
	 * </pre>
	 * 
	 * @param empId
	 * @param projectSids
	 * @return
	 */
	public List<Map<String, Object>> findProjectNameByEmpId(String empId, String projectSids) {
		
		StringBuilder stbSQL = new StringBuilder()
				.append("	SELECT ")
				.append("		DISTINCT ROUTE_INFO_TBL.PROJECT_SID,")
				.append("		PROJECT_INFO_TBL.PROJECT_NAME ")
				.append("	FROM ")
				.append("		ROUTE_INFO_TBL ")
				.append("	INNER JOIN EMP_INFO_TBL ")
				.append("		ON EMP_INFO_TBL.SID = ROUTE_INFO_TBL.EMP_SID ")
				.append("		AND EMP_INFO_TBL.STATUS = '1' ")
				.append("	INNER JOIN PROJECT_INFO_TBL ")
				.append("		ON PROJECT_INFO_TBL.SID = ROUTE_INFO_TBL.PROJECT_SID ")
				.append("	WHERE ")
				.append("		ROUTE_INFO_TBL.PROJECT_SID IN (" + projectSids + ") ")
				.append("		AND ROUTE_INFO_TBL.ROUTE_ATT_SID IN(1,2,3) ") // 2010-05-27 Deli modify "ROUTE_ATT_SID"栏位添加"2"查询条件
				.append("		AND EMP_INFO_TBL.EMP_ID = ? ");
		
		return hw09JdbcOperations.queryForList(stbSQL.toString(), empId);
	}
	
	/**
	 * <pre>
	 * 2010-4-9 Wendy
	 * 	根据终端编号查询所对应的事业部。
	 * </pre>
	 * 
	 * @param storeId
	 * @param projectSids
	 * @return
	 */
	public List<Map<String, Object>> getProjectsByStoreId(String storeId, String projectSids) {

		String sql = "";

		try {
			sql = new StringBuilder()
				.append(" SELECT ")
				.append("   DISTINCT STORE_PROJECT_TBL.PROJECT_SID ") 
				.append(" FROM ")
				.append("   STORE_INFO_TBL ")
				.append("     INNER JOIN STORE_PROJECT_TBL ") 
				.append("     ON STORE_INFO_TBL.SID = STORE_PROJECT_TBL.STORE_SID ")
				.append(" WHERE STORE_ID = :storeId ")
				.append(" AND  STORE_PROJECT_TBL.PROJECT_SID IN (" + projectSids + ") ")
				.toString();

			try {
				logger.debug("getProjectsByStoreId sql is     >>> " + sql);

				try {
					logger.debug("getProjectsByStoreId storeId is >>> " + storeId);
				}
				catch (Exception e) {
//					this.sendMonitorMail(
//						"[Exception] " + this.getClass().getName() + ".getProjectsByStoreId(...)", 
//						String.format("storeId is null?(%s); \nexception : %s", sql, e)
//					);
				}
			}
			catch (Exception e) {
//				this.sendMonitorMail(
//					"[Exception] " + this.getClass().getName() + ".getProjectsByStoreId(...)", 
//					String.format("sql is null?(%s); \nexception : %s", sql, e)
//				);
			}
		}
		catch (Exception e) {
//			this.sendMonitorMail(
//				"[Exception] " + this.getClass().getName() + ".getProjectsByStoreId(...)", 
//				String.format("sql is null when new StringBuilder?(%s); \nexception : %s", sql, e)
//			);
		}

		if (StringUtils.isNotEmpty(sql)) {
			return hw09JdbcOperations.queryForList(sql, storeId);
		}
		else {
			return hw09JdbcOperations.queryForList(
				new StringBuilder()
					.append(" SELECT ")
					.append("   DISTINCT STORE_PROJECT_TBL.PROJECT_SID ") 
					.append(" FROM ")
					.append("   STORE_INFO_TBL ")
					.append("     INNER JOIN STORE_PROJECT_TBL ") 
					.append("     ON STORE_INFO_TBL.SID = STORE_PROJECT_TBL.STORE_SID ")
					.append(" WHERE STORE_ID = :storeId ")
					.append(" AND  STORE_PROJECT_TBL.PROJECT_SID IN (" + projectSids + ") ")
					.toString(), 
				storeId
			);
		}
	}
}
