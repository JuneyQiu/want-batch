package com.want.batch.job.shipment.util;

import java.text.SimpleDateFormat;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
public class CheckStockBase {
	
	protected final Log logger = LogFactory.getLog(CheckStockBase.class);

	public SimpleDateFormat sFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	public DateFormater df = new DateFormater();
	private String BASE_YEARMONTH = df.getLastYearMonth(df
			.getCurrentYearMonth());
	/* 库存基数年月 */

	@Autowired
	private DataSource hw09DataSource;

	public void checkStockBase() throws SQLException {
		Connection conn = hw09DataSource.getConnection();

		logger.info("---同步库存基数开始 : 时间:"
				+ sFormat.format(new java.util.Date()));
		// 1 . 抓取所有品项
		ArrayList prod_list = getAllProd(conn);
		// 2 . 抓取所有客户
		ArrayList fwd_list = getAllFWD(conn);
		// 3 . 匹配客户和品项
		findBaseInHisByYearmonth(fwd_list, prod_list, BASE_YEARMONTH);
		logger.info("--- 同步库存基数结束 : 时间:"
				+ sFormat.format(new java.util.Date()));
		conn.close();
	}

	/**
	 * findBaseInHisByYearmonth
	 * 
	 * @param fwd_list
	 *            ArrayList
	 * @param prod_list
	 *            ArrayList
	 * @param BASE_YEARMONTH
	 *            String
	 * @throws SQLException
	 */
	private void findBaseInHisByYearmonth(ArrayList fwd_list,
			ArrayList prod_list, String BASE_YEARMONTH) throws SQLException {

		Connection conn = hw09DataSource.getConnection();
		int i = 0;
		for (int f = 0; f < fwd_list.size(); f++) {
			int fwd_sid = Integer.parseInt(fwd_list.get(f).toString());

			for (int p = 0; p < prod_list.size(); p++) {
				if (i % 50000 == 0) {
					logger.debug("----" + i + " ---");
				}
				String prod_sid = (String) prod_list.get(p);
				checkBaseInHis(fwd_sid, prod_sid, BASE_YEARMONTH, conn); // 补历史基数
				checkBase(fwd_sid, prod_sid, conn); // 补目前库存基数
				i++;
			}
		}
		logger.info("总共:" + i);
		conn.close();
	}

	/**
	 * checkBase
	 * 
	 * @param fwd_sid
	 *            int
	 * @param prod_sid
	 *            String
	 * @param conn
	 *            Connection
	 * @throws SQLException
	 */
	private static void checkBase(int fwd_sid, String prod_sid, Connection conn)
			throws SQLException {

		String sql = "SELECT TCOMPANY_SEQ_ID FROM TRANS_COMPANY_STOCK_TBL where TCOMPANY_SEQ_ID ="
				+ fwd_sid + " and PRODUCT_SEQ_ID='" + prod_sid + "'";

		String sql_insert = "insert into TRANS_COMPANY_STOCK_TBL values ("
				+ fwd_sid + ",'" + prod_sid + "',0,sysdate,'1')";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		PreparedStatement pstmt2 = conn.prepareStatement(sql_insert);
		ResultSet rs = pstmt.executeQuery();
		if (!rs.next()) {
			// logger.info("sql_insert=" + sql_insert);
			pstmt2.execute();
		}
		rs.close();
		pstmt2.close();
		pstmt.close();
	}

	/**
	 * checkBaseInHis
	 * 
	 * @param fwd_sid
	 *            int
	 * @param prod_sid
	 *            String
	 * @param BASE_YEARMONTH
	 *            String
	 * @param conn
	 *            Connection
	 * @throws SQLException
	 * 
	 */
	private static void checkBaseInHis(int fwd_sid, String prod_sid,
			String BASE_YEARMONTH, Connection conn) throws SQLException {

		String sql = "SELECT TCOMPANY_SEQ_ID FROM TRANS_COMPANY_STOCK_TBL_HIS where TCOMPANY_SEQ_ID ="
				+ fwd_sid
				+ " and PRODUCT_SEQ_ID='"
				+ prod_sid
				+ "' and yearmonth = '" + BASE_YEARMONTH + "'";

		String sql_insert = "insert into TRANS_COMPANY_STOCK_TBL_HIS values ("
				+ fwd_sid + ",'" + prod_sid + "',0,'" + BASE_YEARMONTH
				+ "','1',sysdate,'mis')";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		PreparedStatement pstmt2 = conn.prepareStatement(sql_insert);
		ResultSet rs = pstmt.executeQuery();
		if (!rs.next()) {
			// logger.info("客户:" + fwd_sid + ", 品项:" + prod_sid +
			// "");
			// logger.info("sql_insert_his=" + sql_insert);
			pstmt2.execute();
		}
		rs.close();
		pstmt2.close();
		pstmt.close();
	}

	/**
	 * getAllFWD
	 * 
	 * @return ArrayList
	 * @throws SQLException
	 */
	private static ArrayList getAllFWD(Connection conn) throws SQLException {

		ArrayList list = new ArrayList();

		String fwd_sid = "";
		String sql = "SELECT DISTINCT to_char(TCOMPANY_SEQ_ID) as TCOMPANY_SEQ_ID "
				+ "FROM TRANS_COMPANY_STOCK_TBL_HIS a inner join FORWARDER_INFO_TBL b on a.TCOMPANY_SEQ_ID = b.sid where b.status= '1'";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			fwd_sid = rs.getString("TCOMPANY_SEQ_ID");
			list.add(fwd_sid);
		}
		rs.close();
		pstmt.close();

		return list;

	}

	/**
	 * getAllProd
	 * 
	 * @return ArrayList
	 * @throws SQLException 
	 */
	private static ArrayList getAllProd(Connection conn) throws SQLException {

		ArrayList list = new ArrayList();

		String prod_sid = "";
		String sql = "SELECT DISTINCT PRODUCT_SEQ_ID FROM TRANS_COMPANY_STOCK_TBL_HIS where yearmonth = '201007'";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			prod_sid = rs.getString("PRODUCT_SEQ_ID");
			list.add(prod_sid);
		}
		rs.close();
		pstmt.close();
		return list;
	}

}
