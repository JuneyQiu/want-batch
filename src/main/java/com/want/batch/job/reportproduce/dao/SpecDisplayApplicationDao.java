/**
 * 
 */
package com.want.batch.job.reportproduce.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * @author MandyZhang
 *
 */
@Component
public class SpecDisplayApplicationDao {

	/**
	 * @param conn
	 * @param directiveTblMap
	 * @return
	 * @throws SQLException
	 */
	public ResultSet getSpecDispalyApplicationRpt(Connection conn, Map<String, Object> directiveTblMap) throws SQLException {
		
		String sql = directiveTblMap.get("SELECT_SQL").toString();
		PreparedStatement pstmt = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
		ResultSet rs = pstmt.executeQuery();
		
		return rs;
	}
}
