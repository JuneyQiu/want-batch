package com.want.batch.job.shipment.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Iterator;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.shipment.bo.UpdateBO;

@Component
public class UpdateStockTask {

	protected final Log logger = LogFactory.getLog(UpdateStockTask.class);

	@Autowired
	private DataSource hw09DataSource;

	public UpdateStockTask() {
	}

	public boolean updateStockTCompany() throws SQLException {
		boolean isUpdateStockTCcompany = false;
		String StockTCcompany = "insert  into TRANS_COMPANY_STOCK_TBL "
				+ "select n.FORWARDER_SID, m.PROD_SID,0,sysdate,'1'  from (select FORWARDER_SID from FORWARDER_PROJECT_TBL where PROJECT_SID in (  "
				+ "select PROJECT_SID from TCOMPANY_STOCK_FUNCTION where TC_PROJECT_SID=1 ))  n "
				+ "left join (select distinct PROD_SID  from PRICE_INFO_TBL  where PROJECT_SID in (  "
				+ "select PROJECT_SID from TCOMPANY_STOCK_FUNCTION where TC_PROJECT_SID=1 ))  m  on 1=1 "
				+ "where n.FORWARDER_SID not in "
				+ "(select distinct TCOMPANY_SEQ_ID  from TRANS_COMPANY_STOCK_TBL)  ";

		String StockTCcompanyHis = " insert  into TRANS_COMPANY_STOCK_TBL_HIS "
				+ " select n.FORWARDER_SID, m.PROD_SID,0,to_char(add_months(SYSDATE,-1),'yyyymm'),'1' ,SYSDATE,'mis'  from (select FORWARDER_SID from FORWARDER_PROJECT_TBL where PROJECT_SID in (  "
				+ " select PROJECT_SID from TCOMPANY_STOCK_FUNCTION where TC_PROJECT_SID=1 ))  n   "
				+ " left join (select distinct PROD_SID  from PRICE_INFO_TBL  where PROJECT_SID in (  "
				+ " select PROJECT_SID from TCOMPANY_STOCK_FUNCTION where TC_PROJECT_SID=1 )) m  on 1=1 "
				+ " where n.FORWARDER_SID  not in  "
				+ " (select distinct TCOMPANY_SEQ_ID from TRANS_COMPANY_STOCK_TBL_HIS where YEARMONTH=to_char(add_months(SYSDATE,-1),'yyyymm') ) ";

		Connection conn = hw09DataSource.getConnection();
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		try {
			ps = conn.prepareStatement(StockTCcompany);
			ps.executeUpdate();
			logger.info("TCOMPANY of TRANS_COMPANY_STOCK_TBL has updated @ ");
			ps1 = conn.prepareStatement(StockTCcompanyHis);
			ps1.executeUpdate();
			logger.info("TCOMPANY of TRANS_COMPANY_STOCK_TBL_HIS has updated @ ");
			isUpdateStockTCcompany = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeStatement(ps);
			closeStatement(ps1);
			closeConnection(conn);
		}
		return isUpdateStockTCcompany;
	}

	public Hashtable getUpdateQty() throws SQLException {

		Hashtable stocktable = new Hashtable();

		String newStockSql = " select c.TCOMPANY_SEQ_ID,c.PRODUCT_SEQ_ID ,c.QUANTITY-nvl(d.QTY,0) as  QUANTITY from  "
				+ "(select a.TCOMPANY_SEQ_ID,a.PRODUCT_SEQ_ID,a.QUANTITY+nvl(b.QUANTITY,0) as QUANTITY from  "
				+
				// "(select TCOMPANY_SEQ_ID,PRODUCT_SEQ_ID,QUANTITY,YEARMONTH from TRANS_COMPANY_STOCK_TBL_HIS where YEARMONTH=to_char(add_months(SYSDATE,-1),'yyyymm' ) and TCOMPANY_SEQ_ID in(43341)) a left join  "+
				// //单独更新某客户
				"(select TCOMPANY_SEQ_ID,PRODUCT_SEQ_ID,QUANTITY,YEARMONTH from TRANS_COMPANY_STOCK_TBL_HIS where YEARMONTH=to_char(add_months(SYSDATE,-1),'yyyymm' )  ) a left join  "
				+ // 更新所有客户
				"(select b.SID as TCOMPANY_SEQ_ID ,c.SID as PRODUCT_SEQ_ID ,a.QTY as QUANTITY  from WSIM_TC_TBL a inner join FORWARDER_INFO_TBL b on upper(a.CUSTOMER_ID)=upper(b.FORWARDER_ID) inner join  PROD_INFO_TBL c  on a.MATERIAL_ID=c.PROD_ID) b  "
				+ "on a.TCOMPANY_SEQ_ID=b.TCOMPANY_SEQ_ID and a.PRODUCT_SEQ_ID=b.PRODUCT_SEQ_ID ) c left join  "
				+ "(select e.FORWARDER_SID , e.PROD_SID , e.QTY+nvl(f.QTY,0) as QTY from  "
				+ "(select a.FORWARDER_SID , c.PROD_SID, sum(round(b.CONFIRM_QTY*c.RATIO,4)) as QTY   from  (select SID ,FORWARDER_SID from PO_HEAD_TBL where order_DATE >=  to_date(to_char(sysdate,'yyyymm'),'yyyymm') and STATUS!='9' "
				+ "and PO_TYPE_SID in (select a.SID  from PO_TYPE_TBL a inner join TCOMPANY_STOCK_FUNCTION b on a.PROJECT_SID=b.PROJECT_SID where b.TC_PROJECT_SID=1) "
				+ ") a inner join  PO_LINE_TBL b "
				+ "on a.SID=b.PO_SID inner join PROD_PRICE_VIEW c on b.PRICE_INFO2_SID = c.PRICE_INFO2_SID group by a.FORWARDER_SID,c.PROD_SID )  e left join  "
				+ "(select a.FORWARDER_SID , c.SID, sum(round(b.QTY/c.CONVERT_RATE,4)) as QTY   from  (select SID ,FORWARDER_SID from PRESENT_HEAD_TBL  where PRESENT_DATE >=  to_date(to_char(sysdate,'yyyymm'),'yyyymm') and STATUS!='9'  "
				+ "and PO_TYPE_SID in (select a.SID  from PO_TYPE_TBL a inner join TCOMPANY_STOCK_FUNCTION b on a.PROJECT_SID=b.PROJECT_SID where b.TC_PROJECT_SID=1) "
				+ ") a inner join  PRESENT_LINE_TBL b "
				+ "on a.SID=b.PRESENT_PO_SID inner join PROD_INFO_TBL c on b.PROD_INFO_SID = c.SID group by a.FORWARDER_SID,c.SID )  f on "
				+ "e.FORWARDER_SID=f.FORWARDER_SID and e.PROD_SID=f.SID ) d "
				+ "on c.TCOMPANY_SEQ_ID=d.FORWARDER_SID and c.PRODUCT_SEQ_ID=d.PROD_SID ";

		Connection conn = hw09DataSource.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		logger.info("start to get UpdateQTY");
		try {
			ps = conn.prepareStatement(newStockSql);
			rs = ps.executeQuery();

			while (rs.next()) {
				UpdateBO ubo = new UpdateBO();
				ubo.setTCOMPANY_SID(rs.getString("TCOMPANY_SEQ_ID"));
				ubo.setPRODUCT_SID(rs.getString("PRODUCT_SEQ_ID"));
				ubo.setQTY(rs.getBigDecimal("QUANTITY"));
				String combinekey = rs.getString("TCOMPANY_SEQ_ID") + "*"
						+ rs.getString("PRODUCT_SEQ_ID");
				stocktable.put(combinekey, ubo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rs.close();
			ps.close();
			conn.close();
		}
		return stocktable;
	}

	public boolean updateStock() throws SQLException {
		boolean flag = false;
		String updateNewSql = "update TRANS_COMPANY_STOCK_TBL "
				+ " set QUANTITY = ? " + " ,UPDATE_DATE=sysdate "
				+ " where TCOMPANY_SEQ_ID = ? "
				+ " and PRODUCT_SEQ_ID = ? and PROJECT_SID='1' ";

		String showRunningStatus = "select ISRUNNING from TCOMPANY_STOCK_SCHADULE_TBL where SEQ_ID=1 ";
		String showSuccessDate = "select to_char(SUCCESS_DATE,'yyyymmdd') as SUCCESS_DATE from TCOMPANY_STOCK_SCHADULE_TBL where SEQ_ID=1 ";
		String setRunningStatusTrue = "update TCOMPANY_STOCK_SCHADULE_TBL set ISRUNNING=1 where SEQ_ID=1 ";
		String setRunningStatusFalse = "update TCOMPANY_STOCK_SCHADULE_TBL set ISRUNNING=0 where SEQ_ID=1 ";
		String setStartDate = "update TCOMPANY_STOCK_SCHADULE_TBL set START_DATE=sysdate where SEQ_ID=1 ";
		String setSuccessDate = "update TCOMPANY_STOCK_SCHADULE_TBL set SUCCESS_DATE=sysdate where SEQ_ID=1 ";
		String getSysdate = " select to_char(sysdate,'yyyymmdd') as SYS_DATE from dual ";

		String getBoStatus = "select BO_STATUS from TCOMPANY_STOCK_SCHADULE_TBL where SEQ_ID=1 ";

		String setBoStatus = "update TCOMPANY_STOCK_SCHADULE_TBL set BO_STATUS=0 where SEQ_ID=1 ";

		Connection conn = hw09DataSource.getConnection();
		PreparedStatement ps = null;

		PreparedStatement ps_showR = null;
		PreparedStatement ps_showS = null;
		PreparedStatement ps_setRT = null;
		PreparedStatement ps_setRF = null;
		PreparedStatement ps_setST = null;
		PreparedStatement ps_setSU = null;
		PreparedStatement ps_getSD = null;
		PreparedStatement ps_getBS = null;
		PreparedStatement ps_setBS = null;

		ResultSet rs_showR = null;
		ResultSet rs_showS = null;
		ResultSet rs_getSD = null;
		ResultSet rs_getBS = null;
		try {
			ps_showR = conn.prepareStatement(showRunningStatus);
			rs_showR = ps_showR.executeQuery();

			ps_showS = conn.prepareStatement(showSuccessDate);
			rs_showS = ps_showS.executeQuery();

			ps_getSD = conn.prepareStatement(getSysdate);
			rs_getSD = ps_getSD.executeQuery();

			ps_getBS = conn.prepareStatement(getBoStatus);
			rs_getBS = ps_getBS.executeQuery();

			ps_setRT = conn.prepareStatement(setRunningStatusTrue);
			ps_setRF = conn.prepareStatement(setRunningStatusFalse);
			ps_setST = conn.prepareStatement(setStartDate);
			ps_setSU = conn.prepareStatement(setSuccessDate);
			ps_setBS = conn.prepareStatement(setBoStatus);
			ps = conn.prepareStatement(updateNewSql);

			String isrunning = null;
			String SUCCESS_DATE = null;
			String SYS_DATE = null;
			String BO_STATUS = null;

			if (rs_showR.next()) {
				isrunning = rs_showR.getString("ISRUNNING"); // 取是否正在更新的状态 0:否
																// 1：是
				logger.info("ISRUNNING=" + isrunning);
			}
			if (rs_showS.next()) {
				SUCCESS_DATE = rs_showS.getString("SUCCESS_DATE"); // 取最后一次更新完成时间
				logger.info("SUCCESS_DATE=" + SUCCESS_DATE);
			}
			if (rs_getSD.next()) {
				SYS_DATE = rs_getSD.getString("SYS_DATE"); // 取数据库的系统时间
				logger.info("SYS_DATE=" + SYS_DATE);
			}
			if (rs_getBS.next()) {
				BO_STATUS = rs_getBS.getString("BO_STATUS");
				logger.info("BO_STATUS=" + BO_STATUS);
			}
			// if ("0".equals(isrunning) && !SUCCESS_DATE.equals(SYS_DATE) &&
			// "1".equals(BO_STATUS)) {
			// ps_setST.executeUpdate();//设置开始时间
			// ps_setRT.executeUpdate(); // 设置为正在运行状态
			// boolean isUpdateStockTCcompany = false;
			// logger.info("Start to update Tcompany");
			// isUpdateStockTCcompany = this.updateStockTCompany();

			if (true) {
				Hashtable updateoldstock = this.getUpdateQty();
				Iterator it = updateoldstock.values().iterator();
				while (it.hasNext()) {
					UpdateBO newStockBo = (UpdateBO) it.next();
					ps.setBigDecimal(1, newStockBo.getQTY());
					ps.setString(2, newStockBo.getTCOMPANY_SID());
					ps.setString(3, newStockBo.getPRODUCT_SID());
					ps.executeUpdate();
				}
				flag = true;
				logger.info("update size=" + updateoldstock.size());
				ps_setSU.executeUpdate();
				ps_setRF.executeUpdate();
				ps_setBS.executeUpdate();
			}
			// }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeResultSet(rs_showR);
			closeResultSet(rs_showS);
			closeResultSet(rs_getSD);
			closeStatement(ps);
			closeStatement(ps_showR);
			closeStatement(ps_showS);
			closeStatement(ps_setRT);
			closeStatement(ps_setRF);
			closeStatement(ps_setST);
			closeStatement(ps_setSU);
			closeStatement(ps_getSD);
			closeConnection(conn);
		}
		return flag;
	}

	protected void closeConnection(Connection conn) {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

	protected void closeStatement(Statement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

	protected void closeResultSet(ResultSet rslts) {
		try {
			if (rslts != null) {
				rslts.close();
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}
}
