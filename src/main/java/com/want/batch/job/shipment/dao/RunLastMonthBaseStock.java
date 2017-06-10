package com.want.batch.job.shipment.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.shipment.bo.StockBO;
import com.want.batch.job.shipment.bo.VtwegSpartBO;
import com.want.batch.job.shipment.util.DateFormater;
import com.want.utils.SapDAO;
@Component
public class RunLastMonthBaseStock {
	
	private static Log logger = LogFactory.getLog(RunLastMonthBaseStock.class);

	@Autowired
	private DataSource hw09DataSource;

	@Autowired
	public SapDAO sapDAO;

	private static DateFormater df = new DateFormater();
	private static final String CURR_YEARMONTH = df.getCurrentYearMonth();
	private static final String LAST_YEARMONTH = df
			.getLastYearMonth(CURR_YEARMONTH);
	private static final String STOCK_DATE = "25";
	private static final String Start_Day = LAST_YEARMONTH + "26";
	private static final String Last_Day = df.getLastDayOfMonth(LAST_YEARMONTH,
			"yyyyMMdd");

	public void run() throws SQLException {
		Connection conn_h = hw09DataSource.getConnection();

		HashMap mergeCustomer = this.getMergeCustomerMap(conn_h);

		/* 把25日的录入库存抓取过来 */
		this.getCustomerStorageByDate(conn_h);

		/* 将25日录入的库存按照客户编码合并规则,导入到历史结转库存表 */
		this.doTransStockBase(conn_h, mergeCustomer);
		/* 加入26日至30的出货 */
		this.addShipmentToStckBase(conn_h, mergeCustomer);
		/* 同步结转库存 */
		this.doUpdateStockHis(conn_h);

		conn_h.close();
	}

	/**
	 * doUpdateStockHis
	 * 
	 * @param conn_h
	 *            Connection
	 * @throws SQLException
	 */
	private void doUpdateStockHis(Connection conn_h) throws SQLException {

		StringBuffer SQL = new StringBuffer();
		SQL.append("select c.TCOMPANY_SEQ_ID,c.PRODUCT_SEQ_ID ,c.QUANTITY-nvl(d.QTY,0) as  QUANTITY,'"
				+ LAST_YEARMONTH
				+ "' as YEARMONTH,'10|12' as PROJECT_SID,sysdate,'mis' as CREATOR from ");
		SQL.append(" (select a.TCOMPANY_SEQ_ID,a.PRODUCT_SEQ_ID,a.QUANTITY+nvl(b.QUANTITY,0) as QUANTITY from  ");
		SQL.append(" (select TCOMPANY_SEQ_ID,PRODUCT_SEQ_ID,QUANTITY,YEARMONTH from TRANS_CUSTOMER_STOCK_MERGE_TBL where YEARMONTH='"
				+ LAST_YEARMONTH + "') a left join  ");
		SQL.append(" (select b.SID as TCOMPANY_SEQ_ID ,c.SID as PRODUCT_SEQ_ID ,a.QTY as QUANTITY  from TRANS_SAP_AFTER25SHIPMENT_TBL a inner join FORWARDER_INFO_TBL b on upper(a.CUSTOMER_ID)=upper(b.FORWARDER_ID) inner join  PROD_INFO_TBL c  on a.MATERIAL_ID=c.PROD_ID ) b ");
		SQL.append(" on a.TCOMPANY_SEQ_ID=b.TCOMPANY_SEQ_ID and a.PRODUCT_SEQ_ID=b.PRODUCT_SEQ_ID ) c left join  ");
		SQL.append(" (select e.FORWARDER_SID , e.PROD_SID , e.QTY+nvl(f.QTY,0) as QTY from  ");
		SQL.append(" (select a.FORWARDER_SID , c.PROD_SID, sum(round(b.CONFIRM_QTY*c.RATIO,4)) as QTY from (select SID ,FORWARDER_SID from PO_HEAD_TBL where ORDER_DATE>to_date('"
				+ Start_Day
				+ "','yyyymmdd') and ORDER_DATE<to_date('"
				+ CURR_YEARMONTH + "01','yyyymmdd') and STATUS!='9' ");
		SQL.append(" and PO_TYPE_SID in (select a.SID  from PO_TYPE_TBL a inner join TCOMPANY_STOCK_FUNCTION b on a.PROJECT_SID=b.PROJECT_SID where b.TC_PROJECT_SID=1) ");
		SQL.append(" ) a inner join  PO_LINE_TBL b ");
		SQL.append(" on a.SID=b.PO_SID inner join PROD_PRICE_VIEW c on b.PRICE_INFO2_SID = c.PRICE_INFO2_SID group by a.FORWARDER_SID,c.PROD_SID )  e left join  ");
		SQL.append(" (select a.FORWARDER_SID , c.SID, sum(round(b.QTY/c.CONVERT_RATE,4)) as QTY from (select SID ,FORWARDER_SID from PRESENT_HEAD_TBL  where PRESENT_DATE>to_date('"
				+ Start_Day
				+ "','yyyymmdd') and PRESENT_DATE<to_date('"
				+ CURR_YEARMONTH + "01','yyyymmdd')  and STATUS!='9' ");
		SQL.append(" and PO_TYPE_SID in (select a.SID from PO_TYPE_TBL a inner join TCOMPANY_STOCK_FUNCTION b on a.PROJECT_SID=b.PROJECT_SID where b.TC_PROJECT_SID=1) ");
		SQL.append(" ) a inner join PRESENT_LINE_TBL b ");
		SQL.append(" on a.SID=b.PRESENT_PO_SID inner join PROD_INFO_TBL c on b.PROD_INFO_SID = c.SID group by a.FORWARDER_SID,c.SID )  f on ");
		SQL.append(" e.FORWARDER_SID=f.FORWARDER_SID and e.PROD_SID=f.SID ) d ");
		SQL.append(" on c.TCOMPANY_SEQ_ID=d.FORWARDER_SID and c.PRODUCT_SEQ_ID=d.PROD_SID ");

		PreparedStatement pstmt = conn_h.prepareStatement(SQL.toString());
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			StockBO sb = new StockBO();
			sb.setTCOMPANY_SEQ_ID(rs.getString("TCOMPANY_SEQ_ID"));
			sb.setPRODUCT_SEQ_ID(rs.getString("PRODUCT_SEQ_ID"));
			sb.setQTY(rs.getBigDecimal("QUANTITY"));
			sb.setYEARMONTH(rs.getString("YEARMONTH"));
			sb.setPROJECT_SID(rs.getString("PROJECT_SID"));

			boolean flag = isSavedInStockHis(sb, conn_h); // 判断是否已经存在
			if (flag) {
				updateStockHIS(sb, conn_h);
			} else {
				insertStockHIS(sb, conn_h);
			}
		}

		rs.close();
		pstmt.close();
	}

	/**
	 * updateStockHIS
	 * 
	 * @param sb
	 *            StockBO
	 * @param conn_h
	 *            Connection
	 * @throws SQLException
	 */
	private void updateStockHIS(StockBO sb, Connection conn_h)
			throws SQLException {
		String SQL = "update TRANS_COMPANY_STOCK_TBL_HIS set QUANTITY = ?,PROJECT_SID = '10|12',CREATE_DATE=sysdate where TCOMPANY_SEQ_ID = ? and PRODUCT_SEQ_ID = ? and YEARMONTH = ? ";

		PreparedStatement pstmt = conn_h.prepareStatement(SQL);
		pstmt.setBigDecimal(1, sb.getQTY());
		pstmt.setInt(2, Integer.parseInt(sb.getTCOMPANY_SEQ_ID()));
		pstmt.setString(3, sb.getPRODUCT_SEQ_ID());
		pstmt.setString(4, sb.getYEARMONTH());
		pstmt.executeUpdate();
		pstmt.close();
	}

	/**
	 * insertStockHIS
	 * 
	 * @param sb
	 *            StockBO
	 * @param conn_h
	 *            Connection
	 * @throws SQLException
	 */
	private void insertStockHIS(StockBO sb, Connection conn_h)
			throws SQLException {
		String SQL = "insert into TRANS_COMPANY_STOCK_TBL_HIS values(?,?,?,?,'10|12',sysdate,'mis') ";

		PreparedStatement pstmt = conn_h.prepareStatement(SQL);
		pstmt.setInt(1, Integer.parseInt(sb.getTCOMPANY_SEQ_ID()));
		pstmt.setString(2, sb.getPRODUCT_SEQ_ID());
		pstmt.setBigDecimal(3, sb.getQTY());
		pstmt.setString(4, sb.getYEARMONTH());
		pstmt.execute();
		pstmt.close();
	}

	/**
	 * isSavedInStockHis
	 * 
	 * @param sb
	 *            StockBO
	 * @param conn_h
	 *            Connection
	 * @return boolean
	 * @throws SQLException
	 */
	private boolean isSavedInStockHis(StockBO sb, Connection conn_h)
			throws SQLException {
		boolean isSaved = false;
		String SQL = "select 1 from TRANS_COMPANY_STOCK_TBL_HIS where TCOMPANY_SEQ_ID = ? and PRODUCT_SEQ_ID = ? and YEARMONTH = ?";
		PreparedStatement pstmt = conn_h.prepareStatement(SQL);
		pstmt.setInt(1, Integer.parseInt(sb.getTCOMPANY_SEQ_ID()));
		pstmt.setString(2, sb.getPRODUCT_SEQ_ID());
		pstmt.setString(3, sb.getYEARMONTH());
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			isSaved = true;
		}
		rs.close();
		pstmt.close();
		return isSaved;
	}

	/**
	 * 加入26-30出货 addShipmentToStckBase
	 * 
	 * @throws SQLException
	 */
	private void addShipmentToStckBase(Connection conn_h, HashMap mergeCustomer)
			throws SQLException {

		VtwegSpartDAO vsd = new VtwegSpartDAO();
		ArrayList<VtwegSpartBO> list = vsd.getVtwegSpartByUsage(conn_h);
		/** 2. 循环抓取所有销售范围和产品组的出货数据 * */
		for (int i = 0; i < list.size(); i++) {
			VtwegSpartBO vb = list.get(i);
			/** 3. 根据客户编码抓取出货数 * */
			this.getShipment(vb.getVTWEG(), vb.getSPART(), conn_h,
					mergeCustomer);
		}
	}

	/**
	 * doTransStockBase
	 * 
	 * @param conn_h
	 *            Connection
	 * @throws SQLException
	 */
	private void doTransStockBase(Connection conn_h, HashMap mergeCustomer)
			throws SQLException {
		/* 先清空TRANS_CUSTOMER_STOCK_MERGE_TBL表内容 */
		flushAfter25Shipment(conn_h);
		StringBuffer DEL_SQL = new StringBuffer(
				"delete from hw09.TRANS_CUSTOMER_STOCK_MERGE_TBL where YEARMONTH='"
						+ LAST_YEARMONTH + "'");
		StringBuffer SQL = new StringBuffer();
		SQL.append("select a.CUSTOMER_ID,b.SID as TCOMPANY_SEQ_ID,a.PROD_ID,c.SID AS PRODUCT_SEQ_ID,a.YEARMONTH,a.TOTAL_QTY ");
		SQL.append("from TRANS_CUSTOMER_STORAGE_TBL a ");
		SQL.append("inner join FORWARDER_INFO_TBL b on a.CUSTOMER_ID = b.FORWARDER_ID ");
		SQL.append("inner join HW09.PROD_INFO_TBL c on c.PROD_ID =a.PROD_ID ");
		SQL.append("where YEARMONTH='" + LAST_YEARMONTH + "'");

		PreparedStatement pstmt_del = conn_h.prepareStatement(DEL_SQL
				.toString());
		pstmt_del.execute();
		PreparedStatement pstmt = conn_h.prepareStatement(SQL.toString());
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			StockBO sb = new StockBO();
			sb.setCUSTOMER_ID(rs.getString("CUSTOMER_ID"));
			sb.setTCOMPANY_SEQ_ID(rs.getString("TCOMPANY_SEQ_ID"));
			sb.setMATERIAL_ID(rs.getString("PROD_ID"));
			sb.setPRODUCT_SEQ_ID(rs.getString("PRODUCT_SEQ_ID"));
			sb.setYEARMONTH(rs.getString("YEARMONTH"));
			sb.setQTY(rs.getBigDecimal("TOTAL_QTY"));
			String merge_id = (String) mergeCustomer.get(sb.getCUSTOMER_ID());
			if (merge_id != null) {
				sb.setTCOMPANY_SEQ_ID(this.getFWD_SID_By_ID(merge_id, conn_h));
				sb.setCUSTOMER_ID(merge_id);
			}
			boolean flag = isSaved(sb, conn_h); // 判断是否已经存在
			if (flag) {
				updateStockMerge(sb, conn_h);
			} else {
				insertStockMerge(sb, conn_h);
			}
		}
		rs.close();
		pstmt.close();
		pstmt_del.close();
	}

	/**
	 * insertStockHIS
	 * 
	 * @param sb
	 *            StockBO
	 * @param conn_h
	 *            Connection
	 * @throws SQLException
	 */
	private void insertStockMerge(StockBO sb, Connection conn_h)
			throws SQLException {
		String SQL = "insert into TRANS_CUSTOMER_STOCK_MERGE_TBL values(?,?,?,?,'1',sysdate,'mis') ";
		PreparedStatement pstmt = conn_h.prepareStatement(SQL);
		pstmt.setInt(1, Integer.parseInt(sb.getTCOMPANY_SEQ_ID()));
		pstmt.setString(2, sb.getPRODUCT_SEQ_ID());
		pstmt.setBigDecimal(3, sb.getQTY());
		pstmt.setString(4, sb.getYEARMONTH());
		pstmt.execute();
		pstmt.close();
	}

	/**
	 * updateStockHIS
	 * 
	 * @param sb
	 *            StockBO
	 * @param conn_h
	 *            Connection
	 * @throws SQLException
	 */
	private void updateStockMerge(StockBO sb, Connection conn_h)
			throws SQLException {
		String SQL = "update TRANS_CUSTOMER_STOCK_MERGE_TBL set QUANTITY = QUANTITY+? where TCOMPANY_SEQ_ID = ? and PRODUCT_SEQ_ID = ? and YEARMONTH = ? ";
		PreparedStatement pstmt = conn_h.prepareStatement(SQL);
		pstmt.setBigDecimal(1, sb.getQTY());
		pstmt.setInt(2, Integer.parseInt(sb.getTCOMPANY_SEQ_ID()));
		pstmt.setString(3, sb.getPRODUCT_SEQ_ID());
		pstmt.setString(4, sb.getYEARMONTH());
		pstmt.executeUpdate();
		pstmt.close();
	}

	/**
	 * getFWD_SID_By_ID
	 * 
	 * @param merge_id
	 *            String
	 * @param conn_h
	 *            Connection
	 * @return String
	 * @throws SQLException
	 */
	private String getFWD_SID_By_ID(String merge_id, Connection conn_h)
			throws SQLException {
		String SQL = "select SID from FORWARDER_INFO_TBL where FORWARDER_ID='"
				+ merge_id + "'";
		String SID = "";
		PreparedStatement pstmt = conn_h.prepareStatement(SQL.toString());
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			SID = rs.getString("SID");
		}
		rs.close();
		pstmt.close();
		return SID;
	}

	/**
	 * isSaved
	 * 
	 * @param sb
	 *            StockBO
	 * @param conn_h
	 *            Connection
	 * @return boolean
	 * @throws SQLException
	 */
	private boolean isSaved(StockBO sb, Connection conn_h) throws SQLException {
		boolean isSaved = false;
		String SQL = "select 1 from TRANS_CUSTOMER_STOCK_MERGE_TBL where TCOMPANY_SEQ_ID = ? and PRODUCT_SEQ_ID = ? and YEARMONTH = ?";
		PreparedStatement pstmt = conn_h.prepareStatement(SQL);
		pstmt.setInt(1, Integer.parseInt(sb.getTCOMPANY_SEQ_ID()));
		pstmt.setString(2, sb.getPRODUCT_SEQ_ID());
		pstmt.setString(3, sb.getYEARMONTH());
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			isSaved = true;
		}
		rs.close();
		pstmt.close();
		return isSaved;
	}

	/**
	 * getCustomerStorageByDate
	 * 
	 * @param LAST_YEARMOHTH
	 *            String
	 * @param STOCK_DATE
	 *            String
	 * @param conn_c
	 *            Connection
	 * @throws SQLException
	 */
	private boolean getCustomerStorageByDate(Connection conn_h)
			throws SQLException {
		StringBuffer SQL = new StringBuffer();
		String del_sql = "delete from TRANS_CUSTOMER_STORAGE_TBL where yearmonth = '"
				+ LAST_YEARMONTH + "' and DAY = '" + STOCK_DATE + "' ";
		SQL.append("insert into TRANS_CUSTOMER_STORAGE_TBL  (");
		SQL.append("select substr(c.CUSTOMER_ID,3),substr(c.PROD_ID,7),c.YEARMONTH,c.DAY,c.TOTAL_QTY,c.CREDIT_ID,d.VTWEG,d.SPART,'MIS_ZL',sysdate ");
		SQL.append("from iCustomer.CUSTOMER_STORAGE_INFO_TBL c ");
		SQL.append("inner join ( ");
		SQL.append("SELECT a.CREDIT_ID,b.VTWEG,b.SPART  FROM iCustomer.SALES_AREA_REL a ");
		SQL.append("inner  JOIN (");
		SQL.append("select VTWEG,SPART FROM HW09.VTWEG_SPART_BASE_TBL WHERE LASTMONTH_STOCK_USAGE=1");
		SQL.append(") b ON trim(a.CHANNEL_ID) =  b.VTWEG and trim(a.PROD_GROUP_ID) = b.SPART ) d on c.CREDIT_ID = d.CREDIT_ID ");
		SQL.append("where  c.yearmonth = '" + LAST_YEARMONTH+ "' and c.DAY = '" + STOCK_DATE+ "' and length(c.PROD_ID)=18 ");
		SQL.append(" and c.CUSTOMER_ID in (SELECT distinct '00'|| b.forwarder_id FROM hw09.FORWARDER_INFO_TBL b  inner join hw09.forwarder_project_tbl c" );
		SQL.append(" on b.sid = c.forwarder_sid WHERE b.status= '1' and b.branch_sid in (select sid from hw09.branch_info_tbl where company_sid in (22,31,18)) and c.project_sid in (10,12)))");

		PreparedStatement del_pstmt = conn_h.prepareStatement(del_sql);
		del_pstmt.execute();
		PreparedStatement pstmt = conn_h.prepareStatement(SQL.toString());
		pstmt.execute();
		pstmt.close();
		del_pstmt.close();
		return true;
	}

	/**
	 * getMergeCustomerMap
	 * 
	 * @return HashMap
	 * @throws SQLException
	 */
	private HashMap getMergeCustomerMap(Connection conn_h) throws SQLException {
		HashMap map = new HashMap();
		String SQL = "select BEFORE_FWD_ID,AFTER_FWD_ID from TRANS_MERGE_CUSTOMER_MAP_TBL ";
		PreparedStatement pstmt = conn_h.prepareStatement(SQL);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			map.put(rs.getString("BEFORE_FWD_ID"), rs.getString("AFTER_FWD_ID"));
		}
		rs.close();
		pstmt.close();
		return map;
	}

	/**
	 * 按销售范围和产品组抓取出货
	 * 
	 * @throws SQLException
	 */
	public void getShipment(String I_VTWEG, String I_SPART, Connection conn,
			HashMap mergeCustomer) throws StringIndexOutOfBoundsException, SQLException {

		// 建立查询条件,取全值,条件设为空
		HashMap querydata = new HashMap(5);
		querydata.put("I_SDATE", Start_Day); // 起始日
		querydata.put("I_EDATE", Last_Day); // 截止日
		querydata.put("I_VTWEG", I_VTWEG); // 分销渠道
		querydata.put("I_SPART", I_SPART); // 产品组

		// 取得结果集
		ArrayList list = sapDAO.getSAPData("ZRFC24", "T_ZRFC24_OUT", querydata);

		for (int l = 0; l < list.size(); l++) {

			HashMap temphm = (HashMap) list.get(l);

			if ("00".equals(temphm.get("KUNNR").toString().substring(0, 2))) {
				StockBO sbo_shipment = new StockBO();
				logger.debug("temphm.get(KUNNR).toString()="
						+ temphm.get("KUNNR").toString());
				sbo_shipment.setCUSTOMER_ID(temphm.get("KUNNR").toString()
						.substring(2, 10));
				sbo_shipment.setVTWEG(temphm.get("VTWEG").toString());
				sbo_shipment.setSPART(temphm.get("SPART").toString());
				String matnr = temphm.get("MATNR").toString();
				if (matnr.length() == 18) {
					sbo_shipment.setMATERIAL_ID(temphm.get("MATNR").toString()
							.substring(6, 18));
				} else {
					sbo_shipment.setMATERIAL_ID(temphm.get("MATNR").toString());
				}
				sbo_shipment.setQTY(new BigDecimal(temphm.get("LGMNG")
						.toString()));
				this.putInTbl(sbo_shipment, conn, mergeCustomer);
			}
		}

	}

	/**
	 * 处理sap抓到的出货数据 *
	 * 
	 * @throws SQLException
	 */
	private boolean putInTbl(StockBO sbo_shipment, Connection conn_h,
			HashMap mergeCustomer) throws SQLException {

		String C_ID = (String) mergeCustomer.get(sbo_shipment.getCUSTOMER_ID());
		if (C_ID != null) {
			sbo_shipment
					.setTCOMPANY_SEQ_ID(this.getFWD_SID_By_ID(C_ID, conn_h));
			sbo_shipment.setCUSTOMER_ID(C_ID);
		}
		// 1.判断该笔记录是否已存在
		StockBO bo = isSavedInShipment(sbo_shipment, conn_h);
		boolean flag = false;

		if (null != bo) {
			BigDecimal qulity = sbo_shipment.getQTY();
			sbo_shipment.setQTY(bo.getQTY().add(qulity));
			flag = updateAfter25Shipment(sbo_shipment, conn_h);
		} else {
			flag = insertAfter25Shipment(sbo_shipment, conn_h);
		}

		return flag;
	}

	/**
	 * flushAfter25Shipment
	 * 
	 * @param conn_h
	 *            Connection
	 * @throws SQLException
	 */
	private boolean flushAfter25Shipment(Connection conn_h) throws SQLException {
		String sql = "delete TRANS_SAP_AFTER25SHIPMENT_TBL  ";
		PreparedStatement pstmt = conn_h.prepareStatement(sql);
		System.out
				.println("     --- 正在清空经销商网站录入库存表 - TRANS_SAP_AFTER25SHIPMENT_TBL --- ");
		pstmt.execute();
		pstmt.close();
		return true;
	}

	/**
	 * updateAfter25Shipment
	 * 
	 * @param sbo_shipment
	 *            StockBO
	 * @param conn_h
	 *            Connection
	 * @return boolean
	 * @throws SQLException
	 */
	private boolean updateAfter25Shipment(StockBO sbo_shipment,
			Connection conn_h) throws SQLException {
		String sql = "update TRANS_SAP_AFTER25SHIPMENT_TBL set QTY=? ,UPDATE_DATE=sysdate  where CUSTOMER_ID = ? and MATERIAL_ID = ? ";
		PreparedStatement pstmt = conn_h.prepareStatement(sql);
		pstmt.setBigDecimal(1, sbo_shipment.getQTY());
		pstmt.setString(2, sbo_shipment.getCUSTOMER_ID());
		pstmt.setString(3, sbo_shipment.getMATERIAL_ID());
		pstmt.execute();
		pstmt.close();
		return true;
	}

	/**
	 * insertAfter25Shipment
	 * 
	 * @param sbo
	 *            StockBO
	 * @param conn_h
	 *            Connection
	 * @return boolean
	 * @throws SQLException
	 */
	private boolean insertAfter25Shipment(StockBO sbo_shipment,
			Connection conn_h) throws SQLException {
		String sql = "insert into TRANS_SAP_AFTER25SHIPMENT_TBL (CUSTOMER_ID,MATERIAL_ID,QTY,UPDATE_DATE) values(?,?,?,sysdate) ";
		PreparedStatement pstmt = conn_h.prepareStatement(sql);
		pstmt.setString(1, sbo_shipment.getCUSTOMER_ID());
		pstmt.setString(2, sbo_shipment.getMATERIAL_ID());
		pstmt.setBigDecimal(3, sbo_shipment.getQTY());
		pstmt.execute();
		pstmt.close();
		return true;

	}

	/**
	 * isSavedInShipment
	 * 
	 * @param sbo
	 *            StockBO
	 * @param conn
	 *            Connection
	 * @return boolean
	 * @throws SQLException
	 */
	private StockBO isSavedInShipment(StockBO sb, Connection conn_h)
			throws SQLException {
		String SQL = "select  CUSTOMER_ID,MATERIAL_ID, QTY, UPDATE_DATE from TRANS_SAP_AFTER25SHIPMENT_TBL where CUSTOMER_ID = ? and MATERIAL_ID = ? ";

		PreparedStatement pstmt = conn_h.prepareStatement(SQL);
		pstmt.setString(1, sb.getCUSTOMER_ID());
		pstmt.setString(2, sb.getMATERIAL_ID());
		ResultSet rs = pstmt.executeQuery();
		StockBO bo = null;
		if (rs.next()) {
			bo = new StockBO();
			bo.setCUSTOMER_ID(rs.getString("CUSTOMER_ID"));
			bo.setMATERIAL_ID(rs.getString("MATERIAL_ID"));
			bo.setQTY(rs.getBigDecimal("QTY"));
			bo.setUPDATE_DATE(rs.getDate("UPDATE_DATE").toString());
		}
		return bo;
	}

	// ---------------------------------------------------------------------------------
	/**
	 * delNextMonthStockHis
	 * 
	 * @param string
	 *            String
	 * @throws SQLException
	 */
	public void delNextMonthStockHis(String yearmonth) throws SQLException {
		String SQL = "delete from TRANS_COMPANY_STOCK_TBL_HIS where YEARMONTH=?  ";
		Connection conn_h = hw09DataSource.getConnection();
		PreparedStatement pstmt = conn_h.prepareStatement(SQL);
		pstmt.setString(1, yearmonth);
		pstmt.execute();
		pstmt.close();
		conn_h.close();
	}

	/**
	 * insertWSIM_HIS
	 * 
	 * @param string
	 *            String
	 * @throws SQLException
	 */
	public void insertWSIM_HIS(String yearmonth) throws SQLException {

		String SQL = "insert into WSIM_TC_TBL_HIS "
				+ "select PROJECT_SID,CUSTOMER_ID,MATERIAL_ID,QTY,UPDATE_DATE,COMPANY_KEY,? from WSIM_TC_TBL  ";
		Connection conn_h = hw09DataSource.getConnection();
		PreparedStatement pstmt = conn_h.prepareStatement(SQL);
		pstmt.setString(1, yearmonth);
		pstmt.execute();
		pstmt.close();
		conn_h.close();
	}

	/**
	 * doPeiSong
	 * 
	 * @param string
	 *            String
	 * @param string1
	 *            String
	 * @throws SQLException 
	 */
	public void doPeiSong(String last_yearmonth, String before_yearmonth) throws SQLException {

		String SQL = " insert into TRANS_COMPANY_STOCK_TBL_HIS  ("
				+ " select c.TCOMPANY_SEQ_ID,c.PRODUCT_SEQ_ID ,c.QUANTITY-nvl(d.QTY,0) as  QUANTITY,'"
				+ last_yearmonth
				+ "','1',sysdate,'mis'  from  "
				+ " (select a.TCOMPANY_SEQ_ID,a.PRODUCT_SEQ_ID,a.QUANTITY+nvl(b.QUANTITY,0) as QUANTITY from "
				+ " (select TCOMPANY_SEQ_ID,PRODUCT_SEQ_ID,QUANTITY,YEARMONTH from TRANS_COMPANY_STOCK_TBL_HIS where YEARMONTH='"
				+ before_yearmonth
				+ "' and project_sid = '1' ) a left join  "
				+ // --5月份的结转库存
				" (select b.SID as TCOMPANY_SEQ_ID ,c.SID as PRODUCT_SEQ_ID ,a.QTY as QUANTITY  from WSIM_TC_TBL_HIS a inner join FORWARDER_INFO_TBL b on upper(a.CUSTOMER_ID)=upper(b.FORWARDER_ID) inner join  PROD_INFO_TBL c  on a.MATERIAL_ID=c.PROD_ID and a.YEARMONTH='"
				+ last_yearmonth
				+ "' ) b "
				+ // --6月份出货
				" on a.TCOMPANY_SEQ_ID=b.TCOMPANY_SEQ_ID and a.PRODUCT_SEQ_ID=b.PRODUCT_SEQ_ID ) c left join  "
				+ " (select e.FORWARDER_SID , e.PROD_SID , e.QTY+nvl(f.QTY,0) as QTY from  "
				+ " (select a.FORWARDER_SID , c.PROD_SID, sum(round(b.CONFIRM_QTY*c.RATIO,4)) as QTY   from  (select SID ,FORWARDER_SID from PO_HEAD_TBL where to_char(CREATE_DATE,'yyyymm')='"
				+ last_yearmonth
				+ "'  and STATUS!='9' "
				+ // --6月份回单
				" and PO_TYPE_SID in (select a.SID  from PO_TYPE_TBL a inner join TCOMPANY_STOCK_FUNCTION b on a.PROJECT_SID=b.PROJECT_SID where b.TC_PROJECT_SID=1) "
				+ " ) a inner join  PO_LINE_TBL b "
				+ " on a.SID=b.PO_SID inner join PROD_PRICE_VIEW c on b.PRICE_INFO2_SID = c.PRICE_INFO2_SID group by a.FORWARDER_SID,c.PROD_SID )  e left join  "
				+ " (select a.FORWARDER_SID , c.SID, sum(round(b.QTY/c.CONVERT_RATE,4)) as QTY   from  (select SID ,FORWARDER_SID from PRESENT_HEAD_TBL  where to_char(CREATE_DATE,'yyyymm')='"
				+ last_yearmonth
				+ "'  and STATUS!='9'  "
				+ // --6月份赠品
				" and PO_TYPE_SID in (select a.SID  from PO_TYPE_TBL a inner join TCOMPANY_STOCK_FUNCTION b on a.PROJECT_SID=b.PROJECT_SID where b.TC_PROJECT_SID=1) "
				+ " ) a inner join  PRESENT_LINE_TBL b "
				+ " on a.SID=b.PRESENT_PO_SID inner join PROD_INFO_TBL c on b.PROD_INFO_SID = c.SID group by a.FORWARDER_SID,c.SID )  f on "
				+ " e.FORWARDER_SID=f.FORWARDER_SID and e.PROD_SID=f.SID ) d "
				+ " on c.TCOMPANY_SEQ_ID=d.FORWARDER_SID and c.PRODUCT_SEQ_ID=d.PROD_SID "
				+ " )";

		Connection conn_h = hw09DataSource.getConnection();
		PreparedStatement pstmt = conn_h.prepareStatement(SQL);
		pstmt.execute();
		pstmt.close();
		conn_h.close();
	}

}
