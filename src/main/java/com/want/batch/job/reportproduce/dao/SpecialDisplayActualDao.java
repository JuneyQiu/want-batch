
// ~ Package Declaration
// ==================================================

package com.want.batch.job.reportproduce.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

import com.want.batch.job.reportproduce.pojo.Constant;

// ~ Comments
// ==================================================

/**
 * SpecialDisplayActualDao
 * 
 * <table>
 * 	<tr>
 * 		<th>日期</th>
 * 		<th>变更说明</th>
 * 	</tr>
 * 	<tr>
 * 		<td>2013-6-21</td>
 * 		<td>Nash新建</td>
 * 	</tr>
 * </table>
 *
 *@author Nash
 */
@Component
public class SpecialDisplayActualDao {

	// ~ Static Fields
	// ==================================================

	// ~ Fields
	// ==================================================
	
	@Autowired
	public SimpleJdbcOperations iCustomerJdbcOperations;
	
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================

	/**
	 * <pre>
	 * 2013-6-20 Nash
	 * 特陈实际报表查询
	 * </pre>	
	 * 
	 * @param map
	 * @return
	 * @throws SQLException
	 */
	public ResultSet getSpecDisplayActualRpt(Connection conn, Map<String, Object> specDisplayActualRptMap) throws Exception{
		
		String sql = specDisplayActualRptMap.get("SELECT_SQL").toString();
		PreparedStatement pstmt = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
		ResultSet rs = pstmt.executeQuery();
		
		return rs;
	}
	
	/**
	 * <pre>
	 * 2013-6-20 Nash
	 * 更新状态
	 * </pre>	
	 * 
	 *	<ol>
	 * 		<li>
	 *  	<li>
	 *	</ol>
	 * @param status
	 * @param sid
	 */
	public void updataReportStatus(String status, int sid, String exceptionReason) {

		String updateSql = "";
		
		// 如果状态修改成‘RUNNING’，修改报表开始时间
		if(status.equalsIgnoreCase(Constant.DIRECTIVE_STATUS_RUNNING)) {
		
			updateSql = "UPDATE DIRECTIVE_TBL SET STATUS = ? ,UPDATE_DATE = ?, START_TIME = ? WHERE SID = ?";
			
			this.iCustomerJdbcOperations.update(updateSql, status, new Date(), new Date(), sid);
		}
		// 如果状态修改成‘FINISH’或‘EXCEPTION’，修改报表结束时间
		else if(status.equalsIgnoreCase(Constant.DIRECTIVE_STATUS_FINISH)) {
			
			updateSql = "UPDATE DIRECTIVE_TBL SET STATUS = ? ,UPDATE_DATE = ?, END_TIME = ?  WHERE SID = ?";
			
			this.iCustomerJdbcOperations.update(updateSql, status, new Date(), new Date(), sid);
		}
		// 如果状态修改成‘EXCEPTION’，修改异常原因栏位
		else if(status.equalsIgnoreCase(Constant.DIRECTIVE_STATUS_EXCEPTION)) {
			
			updateSql = "UPDATE DIRECTIVE_TBL SET STATUS = ? ,UPDATE_DATE = ?, END_TIME = ?, EXCEPTION_REASON = ?  WHERE SID = ?";
			
			this.iCustomerJdbcOperations.update(updateSql, status, new Date(), new Date(), exceptionReason, sid);
		}
		else {
			
			updateSql = "UPDATE DIRECTIVE_TBL SET STATUS = ? ,UPDATE_DATE = ? WHERE SID = ?";
			
			this.iCustomerJdbcOperations.update(updateSql, status, new Date(), sid);
		}
	}
}
