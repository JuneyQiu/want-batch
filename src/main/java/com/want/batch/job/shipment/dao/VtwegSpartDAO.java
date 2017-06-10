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

// ~ Comments
// ==================================================

@Component
public class VtwegSpartDAO {

	protected final Log logger = LogFactory.getLog(VtwegSpartDAO.class);

	@Autowired
	public SapDAO sapDAO;

	@Autowired
	private DataSource hw09DataSource;

	public static String First_Day;
	public static String Last_Day;

	public VtwegSpartDAO() {
		DateFormater df = new DateFormater();
		First_Day = df.getCurrentYearMonth() + "01";
		Last_Day = df.getLastDayOfMonth(df.getCurrentYearMonth(), "yyyyMMdd");
	}

	/**
	 * 获取需要抓库存的销售范围和产品组
	 * 
	 * @throws SQLException
	 */
	public ArrayList<VtwegSpartBO> getVtwegSpart() throws SQLException {

		ArrayList<VtwegSpartBO> list = new ArrayList<VtwegSpartBO>();

		String sql = "select VTWEG,SPART,COMPANY_ID,FORWARDER_ID from VTWEG_SPART_BASE_TBL where STATUS='1'";
		Connection conn = hw09DataSource.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			VtwegSpartBO vsb = new VtwegSpartBO();
			vsb.setVTWEG(rs.getString("VTWEG"));
			vsb.setSPART(rs.getString("SPART"));
			vsb.setCOMPANY_ID(rs.getString("COMPANY_ID"));
			vsb.setFORWARDER_ID(rs.getString("FORWARDER_ID"));
			list.add(vsb);
		}
		rs.close();
		pstmt.close();
		conn.close();
		return list;
	}

	/**
	 * 按销售范围和产品组抓取出货
	 * 
	 * @throws SQLException
	 */
	public void getShipment(String I_VTWEG, String I_SPART) throws SQLException {

		Connection conn = hw09DataSource.getConnection();

		// 建立查询条件,取全值,条件设为空
		HashMap querydata = new HashMap(5);
		// querydata.put("KUNNR", ""); //客户号
		// querydata.put("BUKRS", ""); //公司号
		querydata.put("I_SDATE", First_Day); // 起始日
		querydata.put("I_EDATE", Last_Day); // 截止日
		querydata.put("I_VTWEG", I_VTWEG); // 分销渠道
		querydata.put("I_SPART", I_SPART); // 产品组

		// 取得结果集
		ArrayList list = sapDAO.getSAPData("ZRFC24", "T_ZRFC24_OUT", querydata);
		logger.info("list.size()=" + list.size());
		for (int l = 0; l < list.size(); l++) {
			HashMap temphm = (HashMap) list.get(l);
			StockBO sbo = new StockBO();
			sbo.setCUSTOMER_ID(temphm.get("KUNNR").toString().substring(2, 10));
			sbo.setVTWEG(temphm.get("VTWEG").toString());
			sbo.setSPART(temphm.get("SPART").toString());
			String matnr = temphm.get("MATNR").toString();
			if (matnr.length() == 18) {
				sbo.setMATERIAL_ID(temphm.get("MATNR").toString()
						.substring(6, 18));
			} else {
				sbo.setMATERIAL_ID(temphm.get("MATNR").toString());
			}
			sbo.setQTY(new BigDecimal(temphm.get("LGMNG").toString()));
			putInTbl(sbo, conn);
		}
		conn.close();
	}

	/**
	 * 按客户抓取出货
	 * 
	 * @throws SQLException
	 */
	public void getShipment(String I_VTWEG, String I_SPART, String company_id,
			String forwarder_id) throws SQLException {

		Connection conn = hw09DataSource.getConnection();

		// 建立查询条件,取全值,条件设为空
		HashMap querydata = new HashMap(5);

		querydata.put("I_SDATE", First_Day); // 起始日
		querydata.put("I_EDATE", Last_Day); // 截止日
		querydata.put("I_VTWEG", I_VTWEG); // 分销渠道
		querydata.put("I_SPART", I_SPART); // 产品组

		ArrayList bodyList = new ArrayList();
		HashMap map = new HashMap();
		map.put("BUKRS", company_id); // 公司号
		map.put("KUNNR", "00" + forwarder_id); // 客户号
		bodyList.add(map);
		querydata.put("L_T_ZRFC24_IN", bodyList);

		// 取得结果集
		ArrayList list = sapDAO
				.getSAPData4("ZRFC24", "T_ZRFC24_OUT", querydata);
		for (int l = 0; l < list.size(); l++) {
			HashMap temphm = (HashMap) list.get(l);
			StockBO sbo = new StockBO();
			sbo.setCUSTOMER_ID(temphm.get("KUNNR").toString().substring(2, 10));
			sbo.setVTWEG(temphm.get("VTWEG").toString());
			sbo.setSPART(temphm.get("SPART").toString());
			sbo.setMATERIAL_ID(temphm.get("MATNR").toString().substring(6, 18));
			sbo.setQTY(new BigDecimal(temphm.get("LGMNG").toString()));
			putInTbl(sbo, conn);
		}
		conn.close();
	}

	/**
	 * 处理sap抓到的出货数据 *
	 * 
	 * @throws SQLException
	 */
	public boolean putInTbl(StockBO sbo, Connection conn) throws SQLException {

		// 1.判断该笔记录是否已存在
		boolean isSaved = isSaved(sbo, conn);
		boolean flag = false;
		if (isSaved) {
			flag = updateShipment(sbo, conn);
		} else {
			flag = insertShipment(sbo, conn);
		}

		return flag;
	}

	private boolean insertShipment(StockBO sbo, Connection conn)
			throws SQLException {

		String sql = "insert into SAP_SHIPMENT_TBL (CUSTOMER_ID,MATERIAL_ID,QTY,YEARMONTH,VTWEG,SPART,UPDATE_DATE) values(?,?,?,?,?,?,sysdate) ";
		boolean flag = false;

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, sbo.getCUSTOMER_ID());
		pstmt.setString(2, sbo.getMATERIAL_ID());
		pstmt.setBigDecimal(3, sbo.getQTY());
		pstmt.setString(4, sbo.getYEARMONTH());
		pstmt.setString(5, sbo.getVTWEG());
		pstmt.setString(6, sbo.getSPART());
		pstmt.execute();
		flag = true;
		pstmt.close();

		return flag;

	}

	private boolean updateShipment(StockBO sbo, Connection conn)
			throws SQLException {
		String sql = "update SAP_SHIPMENT_TBL set QTY = ?,UPDATE_DATE=sysdate  where CUSTOMER_ID = ? and MATERIAL_ID = ? and YEARMONTH = ? and VTWEG = ? and SPART = ? ";
		boolean flag = false;

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setBigDecimal(1, sbo.getQTY());
		pstmt.setString(2, sbo.getCUSTOMER_ID());
		pstmt.setString(3, sbo.getMATERIAL_ID());
		pstmt.setString(4, sbo.getYEARMONTH());
		pstmt.setString(5, sbo.getVTWEG());
		pstmt.setString(6, sbo.getSPART());
		pstmt.executeUpdate();
		flag = true;
		pstmt.close();
		return flag;
	}

	/**
	 * 判断某个客户某品项的出货是否已经存在 *
	 * 
	 * @throws SQLException
	 */
	private boolean isSaved(StockBO sbo, Connection conn) throws SQLException {
		boolean flag = false;
		String sql = "select QTY from SAP_SHIPMENT_TBL where CUSTOMER_ID=? and MATERIAL_ID=? and YEARMONTH=? and VTWEG=? and SPART =?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, sbo.getCUSTOMER_ID());
		pstmt.setString(2, sbo.getMATERIAL_ID());
		pstmt.setString(3, sbo.getYEARMONTH());
		pstmt.setString(4, sbo.getVTWEG());
		pstmt.setString(5, sbo.getSPART());
		ResultSet rs = pstmt.executeQuery();
		if (rs.next())
			flag = true;
		rs.close();
		pstmt.close();
		return flag;
	}

	/**
	 * 抓取需要计算库存的部分
	 * 
	 * @throws SQLException
	 */
	public ArrayList<VtwegSpartBO> getVtwegSpartByUsage(Connection conn)
			throws SQLException {

		ArrayList<VtwegSpartBO> list = new ArrayList<VtwegSpartBO>();

		String sql = "select VTWEG,SPART from VTWEG_SPART_BASE_TBL where LASTMONTH_STOCK_USAGE='1'";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			VtwegSpartBO vsb = new VtwegSpartBO();
			vsb.setVTWEG(rs.getString("VTWEG"));
			vsb.setSPART(rs.getString("SPART"));
			list.add(vsb);
		}
		rs.close();
		pstmt.close();
		return list;
	}
}
