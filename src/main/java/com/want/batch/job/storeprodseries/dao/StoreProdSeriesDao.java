/**
 * 
 */
package com.want.batch.job.storeprodseries.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

/**
 * @author MandyZhang
 *
 */
@Component
public class StoreProdSeriesDao {

	@Autowired
	public SimpleJdbcOperations iCustomerJdbcOperations;
	
	/**
	 * 查询65DB表MAIN_STORE_REL、STORE_PROD_REL，获取181表STORE_PROD_SERIES的数据源
	 * 
	 * @return List<Map<String, Object>>
	 * @throws SQLException 
	 */
	public ResultSet findStoreProdSeries(Connection conn, String yearMonth) throws SQLException {
		
		StringBuilder strSql = new StringBuilder()
			.append(" SELECT ")
			.append(" 	MAIN_STORE_REL.COMPANY_ID COMPANY_ID, ")
			.append(" 	MAIN_STORE_REL.BARNCH_ID BARNCH_ID, ")
			.append(" 	MAIN_STORE_REL.STORE_TYPE_SID STORE_AREA_ID, ")
			.append(" 	MAX(STORE_PROD_REL.EXHBIT_STANDARD) EXHBIT_STANDARD, ")
			.append("	STORE_PROD_REL.RECOMMEND_ID RECOMMEND_ID, ")
			.append(" 	PROD_INFO_TBL.LV_5_ID LV_5_ID  ")
			.append(" FROM ")
			.append(" 	SFA2.MAIN_STORE_REL MAIN_STORE_REL  ")
			.append(" 		INNER JOIN SFA2.STORE_PROD_REL STORE_PROD_REL  ")
			.append(" 		ON MAIN_STORE_REL.SID=STORE_PROD_REL.MAIN_STORE_SID ")
			.append(" 			INNER JOIN ICUSTOMER.PROD_INFO_TBL PROD_INFO_TBL  ")
			.append(" 			ON STORE_PROD_REL.PROD_ID=PROD_INFO_TBL.PROD_ID ")
			.append(" 		WHERE ")
			.append("			MAIN_STORE_REL.YEARMONTH = ? AND MAIN_STORE_REL.DIVISION_SID NOT IN (24,35)") // 2014-03-21 modify mandy 添加事业部条件，去掉事业部24,35
			.append(" 		GROUP BY ")
			.append(" 			MAIN_STORE_REL.COMPANY_ID, ")
			.append(" 			MAIN_STORE_REL.BARNCH_ID, ")
			.append(" 			MAIN_STORE_REL.STORE_TYPE_SID, ")
			.append(" 			STORE_PROD_REL.RECOMMEND_ID, ")
			.append(" 			PROD_INFO_TBL.LV_5_ID ")
			.append(" 		ORDER BY ")
			.append(" 			MAIN_STORE_REL.COMPANY_ID, ")
			.append(" 			MAIN_STORE_REL.BARNCH_ID, ")
			.append(" 			MAIN_STORE_REL.STORE_TYPE_SID, ")
			.append(" 			PROD_INFO_TBL.LV_5_ID ");
		
		PreparedStatement pstmt = conn.prepareStatement(strSql.toString(), ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
		pstmt.setString(1, yearMonth);
		ResultSet rs = pstmt.executeQuery();
		
		return rs;
	}
	
	/**
	 * 将获取的数据批量插入181表STORE_PROD_SERIES表
	 */
	public void insertStoreProdSeries(List<Object[]> batchArgs) {
		
		StringBuilder strSql = new StringBuilder()
			.append(" INSERT INTO ")
			.append(" 	ICUSTOMER.STORE_PROD_SERIES ")
			.append(" (EFFECTIVE_DATE,LEVEL_ID,COMPANY_ID,BRANCH_ID,LV_5_ID,STORE_AREA_ID,RECOMMEND_ID,EXHIBIT_STANDARD,CREATE_USER,CREATE_DATE) ")
			.append(" VALUES ")
			.append(" (to_date(?, 'yyyy-mm-dd'), ?, ?, ?, ?, ?, ?, ?, 'MIS_SYS', sysdate) ");
		
		this.iCustomerJdbcOperations.batchUpdate(strSql.toString(), batchArgs);
	}
	
	/**
	 * 删除符合当前系统时间的数据
	 */
	public void deleteStoreProdSeries(String yearMonth) {
	
		String sql = "DELETE FROM ICUSTOMER.STORE_PROD_SERIES WHERE TO_CHAR(EFFECTIVE_DATE, 'YYYYMM')=?";
		
		this.iCustomerJdbcOperations.update(sql, yearMonth);
	}
}
