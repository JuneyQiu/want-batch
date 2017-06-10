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
import com.want.batch.job.shipment.util.DateFormater;
import com.want.utils.SapDAO;

@Component
public class UpdateHeWangShipment {

	protected final Log logger = LogFactory.getLog(UpdateHeWangShipment.class);

	public static String First_Day;
	public static String Last_Day;

	@Autowired
	public SapDAO sapDAO;

	@Autowired
	private DataSource hw09DataSource;

	public UpdateHeWangShipment() {
		DateFormater df = new DateFormater();
		First_Day = df.getCurrentYearMonth() + "01";
		Last_Day = df.getLastDayOfMonth(df.getCurrentYearMonth(), "yyyyMMdd");

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
			if ("00".equals(temphm.get("KUNNR").toString().substring(0, 2))) {
				StockBO sbo = new StockBO();
				sbo.setCUSTOMER_ID(temphm.get("KUNNR").toString()
						.substring(2, 10));
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
		// if(null == list){
		// logger.info("forwarder: "+forwarder_id +" wstl is null");
		// return ;
		// }
		for (int l = 0; l < list.size(); l++) {
			HashMap temphm = (HashMap) list.get(l);
			if ("00".equals(temphm.get("KUNNR").toString().substring(0, 2))) {
				StockBO sbo = new StockBO();
				sbo.setCUSTOMER_ID(temphm.get("KUNNR").toString()
						.substring(2, 10));
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
				StringBuffer sb = new StringBuffer("");
				sb.append(sbo.getCUSTOMER_ID() + " | ");
				sb.append(sbo.getVTWEG() + " | ");
				sb.append(sbo.getSPART() + " | ");
				sb.append(sbo.getMATERIAL_ID() + " | ");
				sb.append(sbo.getQTY() + " | ");
				logger.debug("sb = " + sb.toString());
				putInTbl(sbo, conn);
			}
		}
		conn.close();
	}

	/** 处理sap抓到的出货数据 * 
	 * @throws SQLException */
	public boolean putInTbl(StockBO sbo, Connection conn) throws SQLException {
		// 1、判断是否AB版客户
		String C_ID = getMappingID(sbo, conn);
		if (!"".equals(C_ID)) {
			sbo.setCUSTOMER_ID(C_ID);
		}

		// 2.判断该笔记录是否已存在
		StockBO bo = isSaved(sbo, conn);
		boolean flag = false;
		if (null != bo) {
			BigDecimal qulity = sbo.getQTY();
			sbo.setQTY(bo.getQTY().add(qulity));
			flag = updateShipment(sbo, conn);
		} else {
			flag = insertShipment(sbo, conn);
		}

		return flag;
	}

	public boolean emptyShipMent() throws SQLException {
		String sql = "delete from WSIM_TC_TBL";
		Connection conn = hw09DataSource.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.execute();
		pstmt.close();
		conn.close();
		return true;
	}

	/**
	 * getMappingID
	 * 
	 * @param sbo
	 *            StockBO
	 * @param conn
	 *            Connection
	 * @return String
	 * @throws SQLException
	 */
	private String getMappingID(StockBO sbo, Connection conn)
			throws SQLException {
		String sql = "select AFTER_FWD_ID from TRANS_MERGE_CUSTOMER_MAP_TBL where BEFORE_FWD_ID = ?";
		String C_ID = "";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, sbo.getCUSTOMER_ID());
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			C_ID = rs.getString("AFTER_FWD_ID");
		}
		rs.close();
		pstmt.close();
		return C_ID;
	}

	private boolean insertShipment(StockBO sbo, Connection conn)
			throws SQLException {

		String sql = "insert into WSIM_TC_TBL (PROJECT_SID,CUSTOMER_ID,MATERIAL_ID,QTY,UPDATE_DATE,COMPANY_KEY) values(1,?,?,?,sysdate,1) ";
		boolean flag = false;
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, sbo.getCUSTOMER_ID());
		pstmt.setString(2, sbo.getMATERIAL_ID());
		pstmt.setBigDecimal(3, sbo.getQTY());
		pstmt.execute();
		flag = true;
		pstmt.close();
		return flag;
	}

	private boolean updateShipment(StockBO sbo, Connection conn)
			throws SQLException {
		String sql = "update WSIM_TC_TBL set QTY = ?,UPDATE_DATE=sysdate  where CUSTOMER_ID = ? and MATERIAL_ID = ?  ";
		boolean flag = false;
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setBigDecimal(1, sbo.getQTY());
		pstmt.setString(2, sbo.getCUSTOMER_ID());
		pstmt.setString(3, sbo.getMATERIAL_ID());

		pstmt.executeUpdate();
		flag = true;
		pstmt.close();
		return flag;
	}

	/** 判断某个客户某品项的出货是否已经存在 * 
	 * @throws SQLException */
	private StockBO isSaved(StockBO sbo, Connection conn) throws SQLException {
		String sql = "select CUSTOMER_ID,MATERIAL_ID,QTY,UPDATE_DATE from WSIM_TC_TBL where CUSTOMER_ID=? and MATERIAL_ID=? ";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, sbo.getCUSTOMER_ID());
		pstmt.setString(2, sbo.getMATERIAL_ID());

		ResultSet rs = pstmt.executeQuery();
		StockBO bo = null;
		if (rs.next()) {
			bo = new StockBO();
			bo.setCUSTOMER_ID(rs.getString("CUSTOMER_ID"));
			bo.setMATERIAL_ID(rs.getString("MATERIAL_ID"));
			bo.setQTY(rs.getBigDecimal("QTY"));
			bo.setUPDATE_DATE(rs.getDate("UPDATE_DATE").toString());

		}
		rs.close();
		pstmt.close();
		return bo;
	}
}
