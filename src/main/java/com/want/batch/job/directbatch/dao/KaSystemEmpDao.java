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
import org.springframework.stereotype.Component;

/**
 * @author MandyZhang
 *
 */
@Component
public class KaSystemEmpDao {

	protected final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	public DataSource iCustomerDataSource;
	
	public List<Map<String, Object>> getSysDirector(String divisionSids) throws SQLException {

		List<Map<String, Object>> returnList = new ArrayList<Map<String,Object>>();
		
		StringBuilder sql = new StringBuilder()
		  .append(" SELECT ")
		  .append(" 	DISTINCT KA_COMPANY_SYSTEM.COMPANY_ID, ")
		  .append(" 	KA_COMPANY_SYSTEM.SYS_ID, ")
		  .append("   USER_DIVISION.DIVISION_ID,")
		  .append(" 	USER_INFO_TBL.ACCOUNT, ")
		  .append(" 	USER_INFO_TBL.USER_NAME  ")
			.append(" FROM ")
			.append(" 	KA_SYSTEM_EMP ") 
			.append(" 		INNER JOIN KA_COMPANY_SYSTEM ") 
			.append(" 		ON KA_SYSTEM_EMP.SYS_SID = KA_COMPANY_SYSTEM.SYS_ID ") 
			.append(" 			INNER JOIN KA_SYSTEM  ")
			.append(" 			ON KA_COMPANY_SYSTEM.SYS_ID = KA_SYSTEM.SYS_ID ") 
			.append(" 				INNER JOIN USER_INFO_TBL  ")
			.append(" 				ON KA_SYSTEM_EMP.EMP_ID = USER_INFO_TBL.ACCOUNT AND ")
			.append(" 				USER_INFO_TBL.USER_TYPE_SID = 31 ")
			.append(" 					INNER JOIN COMPANY_EMP  ")
			.append(" 					ON KA_COMPANY_SYSTEM.COMPANY_ID = COMPANY_EMP.COMPANY_SAP_ID AND ")
			.append(" 					KA_SYSTEM_EMP.EMP_ID = COMPANY_EMP.EMP_ID  ")
			.append("             INNER JOIN USER_DIVISION")
			.append("             ON USER_INFO_TBL.ACCOUNT = USER_DIVISION.USER_ID")
			.append("              AND USER_DIVISION.DIVISION_ID IN (" + divisionSids + ")")
			.append(" ORDER BY ")
			.append(" 	KA_COMPANY_SYSTEM.COMPANY_ID, ")
			.append(" 	KA_COMPANY_SYSTEM.SYS_ID ");
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			
			conn = iCustomerDataSource.getConnection();
			pstmt = conn.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("COMPANY_ID", rs.getString("COMPANY_ID"));
				map.put("SYS_ID", rs.getString("SYS_ID"));
				map.put("DIVISION_ID", rs.getString("DIVISION_ID"));
				map.put("ACCOUNT", rs.getString("ACCOUNT"));
				map.put("USER_NAME", rs.getString("USER_NAME"));
				
				returnList.add(map);
			}
		} catch (SQLException e) {
			
			logger.error("KaSystemEmpDao: getSysDirector---------------------------" + e.getLocalizedMessage());
			throw e;
		}
		finally {
			
			rs.close();
			pstmt.close();
			conn.close();
		}
		
		return returnList;
	}
}
