/**
 * 
 */
package com.want.batch.job.reportproduce.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

/**
 * @author MandyZhang
 *
 */
@Component
public class SdActualNotcheckReportDao {

	@Autowired
	public SimpleJdbcOperations iCustomerJdbcOperations;
	
	/**
	 * @param param
	 * @return List<Map<String, Object>>
	 * @throws SQLException 
	 */
	public ResultSet getTchNotcheck(Map<String, Object> directiveTblMap, Connection conn) throws SQLException {
	
		String sql = directiveTblMap.get("SELECT_SQL").toString();
		
		PreparedStatement pstmt = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
		ResultSet rs = pstmt.executeQuery();
		
		return rs;
	}
}
