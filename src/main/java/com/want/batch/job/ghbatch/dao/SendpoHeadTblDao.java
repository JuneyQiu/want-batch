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
public class SendpoHeadTblDao {

	@Autowired
	public DataSource iCustomerDataSource;
	
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	// 取得客户到货量
	public List<Map<String, Object>> getProductQtyEnd(Map<String, String> param) throws SQLException {

		List<Map<String, Object>> returnList = new ArrayList<Map<String,Object>>();
		
		StringBuilder sql = new StringBuilder()
			.append(" SELECT")
			.append(" 	PROD_INFO2_TBL.LV_5_ID,")
			.append("   SUM(SENDPO_LINE_TBL.CONFIRM_QTY) CONFIRM_QTY")
			.append(" FROM")
			.append("		SENDPO_HEAD_TBL SENDPO_HEAD_TBL")
			.append("   	INNER JOIN SENDPO_LINE_TBL SENDPO_LINE_TBL")
			.append("			ON SENDPO_HEAD_TBL.PO_NO = SENDPO_LINE_TBL.PO_NO AND")
			.append("			SENDPO_HEAD_TBL.CREATOR = ? AND")
			.append("			SENDPO_HEAD_TBL.CONFIRM_DATE >= ? AND")
			.append("			SENDPO_HEAD_TBL.CONFIRM_DATE <= ? ")
			.append("				INNER JOIN PROD_INFO_TBL PROD_INFO2_TBL")
			.append("				ON SENDPO_LINE_TBL.PROD_ID = PROD_INFO2_TBL.PROD_ID ")
			.append("		GROUP BY PROD_INFO2_TBL.LV_5_ID");
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs= null;
		
		try {
			
			conn = iCustomerDataSource.getConnection();
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, param.get("customerId"));
			pstmt.setString(2, param.get("beginDate"));
			pstmt.setString(3, param.get("endDate"));
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("LV_5_ID", rs.getString("LV_5_ID"));
				map.put("CONFIRM_QTY", rs.getString("CONFIRM_QTY"));
				
				returnList.add(map);
			}
		} catch (SQLException e) {
			
			logger.error("SendpoHeadTblDao: getProductQtyEnd---------------------" + e.getLocalizedMessage());
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
