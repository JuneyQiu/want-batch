
// ~ Package Declaration
// ==================================================

package com.want.batch.job.regulardeltchinfor.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;


// ~ Comments
// ==================================================

@Component
public class RegularDelTchInfoDao {

	// ~ Static Fields
	// ==================================================

	// ~ Fields
	// ==================================================
	
	@Autowired
	public SimpleJdbcOperations iCustomerJdbcOperations;

	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================
	
	/**
	 * <pre>
	 * 2015-10-28 Amy
	 * 	查询在SD_ACTUAL_DISPLAY表中删除异常信息后，异常信息对应的ACTUAL_SID在SD_ACTUAL_DISPLAY是否对应的数据
	 * </pre>
	 * 
	 * @param conn
	 * @param lstActualSid
	 * @return
	 * @throws Exception
	 */
	public List<Integer>  querySdActualDisplayInfoByspecialActualSid(Connection conn, String strActualSid)throws Exception {
		
		StringBuffer sql = new StringBuffer()
			.append("SELECT COUNT(SD_ACTUAL_DISPLAY.SID) COUNTSID, SPECIAL_DISPLAY_ACTUAL.SID  FROM ICUSTOMER.SPECIAL_DISPLAY_ACTUAL " )
			.append(" LEFT JOIN ICUSTOMER.SD_ACTUAL_DISPLAY ")
			.append(" ON SPECIAL_DISPLAY_ACTUAL.SID=SD_ACTUAL_DISPLAY.ACTUAL_SID ")
			.append(" WHERE ")
			.append(" SPECIAL_DISPLAY_ACTUAL.SID in (" + strActualSid + ") ")		
		  .append(" GROUP BY SPECIAL_DISPLAY_ACTUAL.SID ");
		
		System.out.println("sql >>>>>>>>>>> " + sql.toString());
		
	  // 记录SPECIAL_DISPLAY_ACTUAL的sid在SD_ACTUAL_DISPLAY没有资料的sid 
		List<Integer> actualSid = new ArrayList<Integer>();
		
		PreparedStatement pstmt = null;
		
		ResultSet rs = null;
		
		try {
			
			pstmt = conn.prepareStatement(sql.toString());
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				if(rs.getInt("COUNTSID") <= 0) {
					
					actualSid.add(rs.getInt("SID"));
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			
			rs.close();
			pstmt.close();
		}
		
		return actualSid;
	}
	
  /**
   * <pre>
   * 2015-10-23 Amy
   * 	查询特陈形式，或图片或品项为null的异常信息
   * </pre>
   * 
   * @param conn
   * @return
   * @throws SQLException
   */
  public ResultSet queryImperfectInfo(Connection conn, String applicationYearMonth) throws SQLException { 
  	
  	
		StringBuilder strSql = new StringBuilder()
			.append(" SELECT ")
			.append("  ICUSTOMER.SD_ACTUAL_DISPLAY.SID AS SDACTUALDISPLAYSID, ")
			.append("  ICUSTOMER.SD_ACTUAL_DISPLAY.ACTUAL_SID, ")
			.append("  ICUSTOMER.SD_ACTUAL_PROD.SID AS PROSID, ")
			.append("  ICUSTOMER.SD_ACTUAL_PROD.PRODUCT_ID, ")
			.append("	 ICUSTOMER.SD_ACTUAL_PICTURE.ACTUAL_DISPLAY_SID, ")
			.append("  ICUSTOMER.SD_ACTUAL_PICTURE.PICTURE_SID, ")
			.append("  ICUSTOMER.SPECIAL_DISPLAY_ACTUAL.SID AS SPECIALDISPLAYACTUALSID")
			.append(" FROM ")
			.append("  ICUSTOMER.SD_ACTUAL_DISPLAY ")
			.append(" INNER JOIN ICUSTOMER.SPECIAL_DISPLAY_ACTUAL ")
			.append(" ON ICUSTOMER.SPECIAL_DISPLAY_ACTUAL.SID = SD_ACTUAL_DISPLAY.ACTUAL_SID ")
			.append("  INNER JOIN ICUSTOMER.SPECIAL_DISPLAY_APPLICATION ")
			.append("  ON ICUSTOMER.SPECIAL_DISPLAY_APPLICATION.SID = SPECIAL_DISPLAY_ACTUAL. APPLICATION_SID ")
			.append("  AND ICUSTOMER.SPECIAL_DISPLAY_APPLICATION.YEAR_MONTH = '"+ applicationYearMonth +"' ")
			.append("  LEFT JOIN ICUSTOMER.SD_ACTUAL_PROD ")
			.append("  ON ICUSTOMER.SD_ACTUAL_PROD.ACTUAL_DISPLAY_SID = SD_ACTUAL_DISPLAY.SID ")
			.append("  LEFT JOIN ICUSTOMER.SD_ACTUAL_PICTURE ")
			.append("  ON ICUSTOMER.SD_ACTUAL_PICTURE.ACTUAL_DISPLAY_SID = SD_ACTUAL_DISPLAY.SID ")
			.append(" WHERE")
			.append("  ICUSTOMER.SD_ACTUAL_DISPLAY.DATA_SOURCE = 'SFA2.0' ")
			.append("  AND ICUSTOMER.SD_ACTUAL_DISPLAY.CREATE_USER_TYPE = '1' ")
			.append("  AND ( ICUSTOMER.SD_ACTUAL_DISPLAY.LOCATION_TYPE_SID <> 4 ")
			.append("  	AND ( ")
			.append("  	 	ICUSTOMER.SD_ACTUAL_DISPLAY.DISPLAY_TYPE_SID IS NULL ")
			.append("   	OR ICUSTOMER.SD_ACTUAL_PROD.PRODUCT_ID IS NULL ")
			.append("			OR ICUSTOMER.SD_ACTUAL_PICTURE.PICTURE_SID IS NULL ")
			.append("    	)  ")
			.append("   )  ")
			.append("  OR ( ")
			.append("		 ICUSTOMER.SD_ACTUAL_DISPLAY.LOCATION_TYPE_SID IS NULL")
			.append("    AND ICUSTOMER.SD_ACTUAL_DISPLAY.DATA_SOURCE = 'SFA2.0' ")
			.append("    AND ICUSTOMER.SD_ACTUAL_DISPLAY.CREATE_USER_TYPE = '1' ")
			.append("   ) ");

		System.out.println("sql >>>>>>>>>>> " + strSql.toString());
		
		PreparedStatement pstmt = conn.prepareStatement(strSql.toString());
		
		ResultSet rs = pstmt.executeQuery();
		
		return rs;
	}
  
  /**
   * <pre>
   * 2015-10-23 Amy
   * 	新增ICUSTOMER.SD_ACTUAL_DISPLAY_BACKUP备份表
   * </pre>
   * 
   * @param sid
   */
  public void addImperfectInfoSdActualDisplay(List<Object[]> sid){
  	
  	StringBuffer sql = new StringBuffer()
  		.append(" INSERT INTO")
  		.append("  ICUSTOMER.SD_ACTUAL_DISPLAY_BACKUP ")
  		.append("	 ( ")
  		.append("		SID, ")
  		.append("		ACTUAL_SID, ")
  		.append("		STORE_DISPLAY_SID, ")
  		.append("		STORE_ID, ")
  		.append("		DISPLAY_TYPE_SID, ")
  		.append("		ACTUAL_INPUT, ")
  		.append("		ACTUAL_SALES, ")
  		.append("		LOCATION_TYPE_SID, ")
  		.append("		IS_RECEIVE_RECEIPT, ")
  		.append("		DISPLAY_PARAM_ID, ")
  		.append("		CREATE_DATE, ")
  		.append("		CREATE_USER, ")
  		.append("		CREATE_USER_TYPE,")
  		.append("		DISPLAY_ACREAGE, ")
  		.append("		DISPLAY_SIDECOUNT, ")
  		.append("		ASSETS_ID, ")
  		.append("		IS_CANCEL, ")
  		.append("		SD_CHECK_STATUS_ZR, ")
  		.append("		SD_CHECK_STATUS_SZ, ")
  		.append("		SD_CHECK_STATUS_ZJ, ")
  		.append("		APPROVED_AMOUNT, ")
  		.append("		IS_LOCK, ")
  		.append("		UPDATE_DATE, ")
  		.append("		UPDATE_USER, ")
  		.append("		DATA_SOURCE, ")
  		.append("		INSERT_DATE ")
  		.append("  ) ")
  		.append("	SELECT ")
  		.append("		SID, ")
  		.append("		ACTUAL_SID, ")
  		.append("		STORE_DISPLAY_SID, ")
  		.append("		STORE_ID, ")
  		.append("		DISPLAY_TYPE_SID, ")
  		.append("		ACTUAL_INPUT, ")
  		.append("		ACTUAL_SALES, ")
  		.append("		LOCATION_TYPE_SID, ")
  		.append("		IS_RECEIVE_RECEIPT, ")
  		.append("		DISPLAY_PARAM_ID, ")
  		.append("		CREATE_DATE, ")
  		.append("		CREATE_USER, ")
  		.append("		CREATE_USER_TYPE,")
  		.append("		DISPLAY_ACREAGE, ")
  		.append("		DISPLAY_SIDECOUNT, ")
  		.append("		ASSETS_ID, ")
  		.append("		IS_CANCEL, ")
  		.append("		SD_CHECK_STATUS_ZR, ")
  		.append("		SD_CHECK_STATUS_SZ, ")
  		.append("		SD_CHECK_STATUS_ZJ, ")
  		.append("		APPROVED_AMOUNT, ")
  		.append("		IS_LOCK, ")
  		.append("		UPDATE_DATE, ")
  		.append("		UPDATE_USER, ")
  		.append("		DATA_SOURCE, ")
  		.append("   SYSDATE ")
  		.append("	FROM ")
  		.append("		ICUSTOMER.SD_ACTUAL_DISPLAY ")
  		.append(" WHERE ")
  		.append("	ICUSTOMER.SD_ACTUAL_DISPLAY.SID = ? ");
  	
  	this.iCustomerJdbcOperations.batchUpdate(sql.toString(), sid);
  }
	
  /**
   * <pre>
   * 2015-10-23 Amy
   * 	新增ICUSTOMER.SD_ACTUAL_PROD_BACKUP备份表
   * </pre>
   * 
   * @param sid
   */
  public void addImperfectInfoSdActualProd(List<Object[]> actualDisplaySid){
  	
  	StringBuffer sql = new StringBuffer()
  		.append(" INSERT INTO")
  		.append("  ICUSTOMER.SD_ACTUAL_PROD_BACKUP ")
  		.append("	 ( ")
  		.append("		SID, ")
  		.append("		ACTUAL_DISPLAY_SID, ")
  		.append("		PRODUCT_ID, ")
  		.append("		DATA_SOURCE, ")
  		.append("		INSERT_DATE ")
  		.append("  ) ")
  		.append("	SELECT ")
  		.append("		SID, ")
  		.append("		ACTUAL_DISPLAY_SID, ")
  		.append("		PRODUCT_ID, ")
  		.append("		DATA_SOURCE, ")
  		.append("   SYSDATE ")
  		.append("	FROM ")
  		.append("		ICUSTOMER.SD_ACTUAL_PROD ")
  		.append(" WHERE ")
  		.append("	ICUSTOMER.SD_ACTUAL_PROD.ACTUAL_DISPLAY_SID = ? ");
  	
  	this.iCustomerJdbcOperations.batchUpdate(sql.toString(), actualDisplaySid);
  }
  
  /**
   * <pre>
   * 2015-10-23 Amy
   * 	新增ICUSTOMER.SD_ACTUAL_PICTURE_BACKUP备份表
   * </pre>
   * 
   * @param sid
   */
  public void addImperfectInfoSdActualPicture(List<Object[]> actualDisplaySid){
  	
  	StringBuffer sql = new StringBuffer()
  		.append(" INSERT INTO")
  		.append("  ICUSTOMER.SD_ACTUAL_PICTURE_BACKUP ")
  		.append("	 ( ")
  		.append("		ACTUAL_DISPLAY_SID, ")
  		.append("		PICTURE_SID, ")
  		.append("		INSERT_DATE ")
  		.append("  ) ")
  		.append("	SELECT ")
  		.append("		ACTUAL_DISPLAY_SID, ")
  		.append("		PICTURE_SID, ")
  		.append("   SYSDATE ")
  		.append("	FROM ")
  		.append("		ICUSTOMER.SD_ACTUAL_PICTURE ")
  		.append(" WHERE ")
  		.append("	ICUSTOMER.SD_ACTUAL_PICTURE.ACTUAL_DISPLAY_SID = ? ");
  	
  	this.iCustomerJdbcOperations.batchUpdate(sql.toString(), actualDisplaySid);
  }
  
  /**
   * <pre>
   * 2015-10-23 Amy
   * 	新增ICUSTOMER.SPECIAL_DISPLAY_ACTUAL_BACKUP备份表
   * </pre>
   * 
   * @param sid
   */
  public void addImperfectInfoSpecialDisplayActual(List<Object[]> sid){
  	
  	StringBuffer sql = new StringBuffer()
  		.append(" INSERT INTO")
  		.append("  ICUSTOMER.SPECIAL_DISPLAY_ACTUAL_BACKUP ")
  		.append("	 ( ")
  		.append("		SID, ")
  		.append("		APPLICATION_SID, ")
  		.append("		SUBMIT_CHECK_STATUS_XG, ")
  		.append("		SUBMIT_CHECK_DATE_XG, ")
  		.append("		SUBMIT_CHECK_STATUS_ZR, ")
  		.append("		SUBMIT_CHECK_DATE_ZR, ")
  		.append("		SUBMIT_CHECK_STATUS_SZ, ")
  		.append("		SUBMIT_CHECK_STATUS_ZJ, ")
  		.append("		SUBMIT_CHECK_DATE_SZ, ")
  		.append("		SUBMIT_CHECK_DATE_ZJ, ")
  		.append("		CHECK_STATUS, ")
  		.append("		FILL_IN_STATUS_YD, ")
  		.append("		FILL_IN_STATUS_ZR,")
  		.append("		FILL_IN_STATUS_KH, ")
  		.append("		FILL_IN_DATE_YD, ")
  		.append("		FILL_IN_DATE_ZR, ")
  		.append("		FILL_IN_DATE_KH, ")
  		.append("		SUBMIT_AMOUNT, ")
  		.append("		DATA_SOURCE,")
  		.append("		INSERT_DATE ")
  		.append("  ) ")
  		.append("	SELECT ")
  		.append("		SID, ")
  		.append("		APPLICATION_SID, ")
  		.append("		SUBMIT_CHECK_STATUS_XG, ")
  		.append("		SUBMIT_CHECK_DATE_XG, ")
  		.append("		SUBMIT_CHECK_STATUS_ZR, ")
  		.append("		SUBMIT_CHECK_DATE_ZR, ")
  		.append("		SUBMIT_CHECK_STATUS_SZ, ")
  		.append("		SUBMIT_CHECK_STATUS_ZJ, ")
  		.append("		SUBMIT_CHECK_DATE_SZ, ")
  		.append("		SUBMIT_CHECK_DATE_ZJ, ")
  		.append("		CHECK_STATUS, ")
  		.append("		FILL_IN_STATUS_YD, ")
  		.append("		FILL_IN_STATUS_ZR,")
  		.append("		FILL_IN_STATUS_KH, ")
  		.append("		FILL_IN_DATE_YD, ")
  		.append("		FILL_IN_DATE_ZR, ")
  		.append("		FILL_IN_DATE_KH, ")
  		.append("		SUBMIT_AMOUNT, ")
  		.append("		DATA_SOURCE, ")
  		.append("   SYSDATE ")
  		.append("	FROM ")
  		.append("		ICUSTOMER.SPECIAL_DISPLAY_ACTUAL ")
  		.append(" WHERE ")
  		.append("	ICUSTOMER.SPECIAL_DISPLAY_ACTUAL.SID = ? ");
  	
  	this.iCustomerJdbcOperations.batchUpdate(sql.toString(), sid);
  }
  
	/**
	 * <pre>
	 * 2015-10-23 Amy
	 * 	删除ICUSTOMER.SD_ACTUAL_DISPLAY表中异常信息
	 * </pre>
	 * 
	 * @param sid
	 */
	public void delTchInfoSdActualDisplay(List<Object[]> sid) {
		
    String sql = "DELETE FROM ICUSTOMER.SD_ACTUAL_DISPLAY WHERE ICUSTOMER.SD_ACTUAL_DISPLAY.SID = ? ";
		
		this.iCustomerJdbcOperations.batchUpdate(sql, sid);
	}
	
	/**
	 * <pre>
	 * 2015-10-23 Amy
	 * 	 删除ICUSTOMER.SD_ACTUAL_PROD表中异常信息
	 * </pre>
	 * 
	 * @param sid
	 */
	public void delTchInfoSdActualProd(List<Object[]> actualDisplaySid) {
		
    String sql = "DELETE FROM ICUSTOMER.SD_ACTUAL_PROD WHERE ICUSTOMER.SD_ACTUAL_PROD.ACTUAL_DISPLAY_SID = ? ";
		
		this.iCustomerJdbcOperations.batchUpdate(sql, actualDisplaySid);
	}
	
	/**
	 * <pre>
	 * 2015-10-23 Amy
	 * 	 删除ICUSTOMER.SD_ACTUAL_PICTURE表中异常信息
	 * </pre>
	 * 
	 * @param sid
	 */
	public void delTchInfoSdActualPicture(List<Object[]> sid) {
		
    String sql = "DELETE FROM ICUSTOMER.SD_ACTUAL_PICTURE WHERE ICUSTOMER.SD_ACTUAL_PICTURE.ACTUAL_DISPLAY_SID = ? ";
		
		this.iCustomerJdbcOperations.batchUpdate(sql, sid);
	}
	
	/**
	 * <pre>
	 * 2015-10-23 Amy
	 * 	 删除ICUSTOMER.SPECIAL_DISPLAY_ACTUAL表中异常信息
	 * </pre>
	 * 
	 * @param sid
	 */
	public void delTchInfoSpecialDisplayActual(List<Object[]> sid) {
		
    String sql = "DELETE FROM ICUSTOMER.SPECIAL_DISPLAY_ACTUAL WHERE ICUSTOMER.SPECIAL_DISPLAY_ACTUAL.SID = ? ";
		
		this.iCustomerJdbcOperations.batchUpdate(sql, sid);
	}
}
