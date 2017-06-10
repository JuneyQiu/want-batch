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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author MandyZhang
 *
 */
@Component
public class ProdCustomerDao {

	@Autowired
	public DataSource iCustomerDataSource;
	
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	// 根据客户编号和lv5Id查询对应的分公司
	public String getCompanyIdByCustomerIdAndLv5Id(String customerId,
			String lv5Id) throws SQLException {

		List<Map<String, Object>> companyIds = new ArrayList<Map<String, Object>>();
		
		StringBuilder sql = new StringBuilder()
				.append(" SELECT ")
				.append("  PROD_CUSTOMER_REL.COMPANY_ID ")
				.append(" FROM ")
				.append("  PROD_CUSTOMER_REL ")
				.append("   INNER JOIN PROD_INFO_TBL ")
				.append("   ON PROD_CUSTOMER_REL.MATNR = PROD_INFO_TBL.PROD_ID ")
				.append(" WHERE ")
				.append("  CUSTOMER_ID = ? AND ")
				.append("  PROD_INFO_TBL.LV_5_ID = ? ");

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			
			conn = iCustomerDataSource.getConnection();
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, customerId);
			pstmt.setString(2, lv5Id);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("COMPANY_ID", rs.getString("COMPANY_ID"));
				
				companyIds.add(map);
			}
		} catch (SQLException e) {
			
			logger.error("ProdCustomerDao: getCompanyIdByCustomerIdAndLv5Id-----------------" + e.getLocalizedMessage());
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
		
		return (companyIds.isEmpty()) ? null : companyIds.get(0)
				.get("COMPANY_ID").toString();
	}
}
