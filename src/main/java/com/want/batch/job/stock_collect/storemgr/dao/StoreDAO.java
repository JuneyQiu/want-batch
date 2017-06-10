package com.want.batch.job.stock_collect.storemgr.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

import com.want.batch.job.stock_collect.channelmgr.dao.ChannelDAO;
import com.want.batch.job.stock_collect.storemgr.bo.DirectBussinessBO;
import com.want.batch.job.stock_collect.storemgr.bo.StoreViewBO;

@Component
public class StoreDAO {

	// log4j
	static Logger logger = Logger.getLogger(StoreDAO.class.getName());

	@Autowired
	public ChannelDAO channelDAO;

	@Autowired
	public DataSource hw09DataSource;
	
	@Autowired
	public SimpleJdbcOperations hw09JdbcOperations;
	
	public ArrayList getStoreCollect(int company_sid, int branch_sid) throws SQLException {
		boolean flag = false;
		
		hw09JdbcOperations.update("delete from store_detail_temp where company_sid = ? and branch_sid = ?", new Integer(company_sid), new Integer(branch_sid));
		

		ArrayList<StoreViewBO> slist = new ArrayList<StoreViewBO>();
		logger.info("company sid = " + company_sid + ", branch sid = " + branch_sid);
		
		
		StringBuffer sqlcmd = new StringBuffer(
				"insert into store_detail_temp select " + company_sid + ", " + branch_sid
						+ " ,a.STATE_NAME,a.COMPANY_NAME,a.BRANCH_NAME,THIRD_SID,a.THIRD_LV_NAME,a.FORTH_LV_NAME,DECODE(a.IS_CITY,'1','城区','0','乡镇')as is_city, a.store_sid,a.STORE_NAME,a.STORE_ID,"
						+ "a.IDENTITY_ID,a.ACREAGE,trim(a.ADDRESS) as ADDRESS,trim(a.ADDRESS_NEW) as ADDRESS_NEW ,trim(a.STORE_OWNER) as STORE_OWNER,a.OWNER_GENDER,a.PHONE1, a.STORE_MOBILE1,a.POST_CODE,a.REFRIGERATOR,a.STORE_TYPE,"
						+ "trim(a.STORE_DESC) as STORE_DESC,a.CREATE_DATE,a.UPDATE_DATE,  DECODE(a.STATUS,1,'营业中',0,'已歇业',9,'已休业')as status,"
						+ "a.LONGITUDE, a.LATITUDE,a.MDM_STORE_ID,b.CASHREGISTER_AMOUNT,b.IS_WHOLESALE,b.LOCATION_TYPE,b.PROVIDE_DATE,b.SALE_TYPE,b.WOSIDIANZHAO "
						+ " from ALL_ORG_STORE_VIEW3 a left join STORE_INFO_EXTEND b on a.STORE_SID=b.store_sid");
		if (company_sid > 0) {
			sqlcmd.append((flag ? " AND " : " where ") + " company_sid="
					+ company_sid);
			flag = true;
		}

		if (branch_sid > 0) {
			sqlcmd.append((flag ? " AND " : " where ") + " branch_sid="
					+ branch_sid);
			flag = true;
		}
		sqlcmd.append((flag ? " AND " : " where ") + " a.STATUS<>9");

		logger.info("Executing SQL: " + sqlcmd.toString());
		hw09JdbcOperations.update(sqlcmd.toString());
		logger.info("Done SQL: " + sqlcmd.toString());

		HashMap<Integer,String> markets = getThirdMarkets(company_sid, branch_sid);
		Connection conn = hw09DataSource.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("select * from store_detail_temp where company_sid = " + company_sid + " and branch_sid = " + branch_sid);

		while (rs.next()) {
			StoreViewBO svb = new StoreViewBO(rs.getInt("store_sid"));
			svb.setSTATE_NAME(rs.getString("STATE_NAME"));
			svb.setCOMPANY_NAME(rs.getString("COMPANY_NAME"));
			svb.setBRANCH_NAME(rs.getString("BRANCH_NAME"));
			svb.setTHIRD_LV_NAME(rs.getString("THIRD_LV_NAME"));
			svb.setFORTH_LV_NAME(rs.getString("FORTH_LV_NAME"));
			svb.setMARKET_NAME(markets.get(new Integer(rs.getInt("THIRD_SID"))));
			svb.setTHIRD_SID(rs.getInt("THIRD_SID"));
			svb.setSTORE_ID(rs.getString("STORE_ID"));
			svb.setSTORE_NAME(rs.getString("STORE_NAME"));
			svb.setSTORE_DESC(rs.getString("STORE_DESC"));
			svb.setIs_city(rs.getString("is_city"));
			svb.setIDENTITY_ID(rs.getString("IDENTITY_ID"));
			svb.setACREAGE(rs.getString("ACREAGE"));
			svb.setADDRESS(rs.getString("ADDRESS"));
			svb.setADDRESS_NEW(rs.getString("ADDRESS_NEW"));
			svb.setSTORE_OWNER(rs.getString("STORE_OWNER"));
			svb.setOWNER_GENDER(rs.getString("OWNER_GENDER"));
			svb.setPHONE1(rs.getString("PHONE1"));
			svb.setSTORE_MOBILE1(rs.getString("STORE_MOBILE1"));
			svb.setPOST_CODE(rs.getString("POST_CODE"));
			svb.setREFRIGERATOR(rs.getString("REFRIGERATOR"));
			svb.setSTORE_TYPE(rs.getString("STORE_TYPE"));
			svb.setStatus(rs.getString("status"));
			svb.setLATITUDE(rs.getBigDecimal("LATITUDE"));
			svb.setLONGITUDE(rs.getBigDecimal("LONGITUDE"));
			svb.setMDM_STORE_ID(rs.getString("MDM_STORE_ID"));
			svb.setCREATE_DATE(rs.getString("CREATE_DATE"));
			svb.setUPDATE_DATE(rs.getString("UPDATE_DATE"));
			svb.setCASHREGISTER_AMOUNT(rs.getString("CASHREGISTER_AMOUNT"));
			svb.setLOCATION_TYPE(rs.getString("LOCATION_TYPE"));
			svb.setDIANZHAO(rs.getString("WOSIDIANZHAO"));
			svb.setSALE_TYPE(rs.getString("SALE_TYPE"));
			svb.setIS_WHOLESALE(rs.getString("IS_WHOLESALE"));
			svb.setPROVIDE_DATE(rs.getString("PROVIDE_DATE"));
			slist.add(svb);
		}
		
		logger.info("slist size = " + slist.size());
		rs.close();
		stmt.close();
		conn.close();

		HashMap<Integer, DirectBussinessBO> storeBusinesses = getStoreBusinessRel(company_sid, branch_sid);
		logger.info("storeBusinesses size = " + storeBusinesses.size());

		for (StoreViewBO bo : slist) {
			Integer sid = new Integer(bo.getSTORE_SID());
			DirectBussinessBO directBusiness = storeBusinesses.get(sid);
			if (directBusiness != null)
				bo.setDirectBo(directBusiness);
			else
				bo.setDirectBo(null);
		}
		
		storeBusinesses = null;
		Runtime.getRuntime().gc();

		HashMap<Integer, List<Integer>> storeProjects = channelDAO.getStoreProjects(company_sid, branch_sid);
		logger.info("storeProjects size = " + storeProjects.size());

		HashMap<String,String> routes = channelDAO.getRouts(company_sid, branch_sid);

		for (StoreViewBO bo : slist) {
			Integer sid = new Integer(bo.getSTORE_SID());
			bo.setScb(channelDAO.getStore_Channel(routes, bo.getSTORE_SID(), storeProjects.get(sid)));
		}
		
		storeProjects = null;
		Runtime.getRuntime().gc();

		hw09JdbcOperations.update("delete from store_detail_temp where company_sid = ? and branch_sid = ?", new Integer(company_sid), new Integer(branch_sid));

		return slist;
	}

	/**
	 * getMarketName
	 * 
	 * @param conn
	 *            Connection
	 * @param THIRD_SID
	 *            int
	 * @return String
	 * @throws SQLException
	 */
	private HashMap<Integer,String>getThirdMarkets(int companySid, int branchSid)
			throws SQLException {
		String sqlcmd = "select distinct b.sid, MARKET_NAME from MARKET_INFO_TBL a inner join THIRD_LV_INFO_TBL b on b.MARKET_SID=a.SID join store_detail_temp c on c.THIRD_SID=b.sid and c.company_sid=? and c.branch_sid=? ";
		Connection conn = hw09DataSource.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(sqlcmd);
		pstmt.setInt(1, companySid);
		pstmt.setInt(2, branchSid);
		logger.info("Executing SQL: " + sqlcmd);
		ResultSet rs = pstmt.executeQuery();
		HashMap<Integer,String> map = new HashMap<Integer,String>();
		while (rs.next())
			map.put(new Integer(rs.getInt("sid")), rs.getString("market_name"));
		logger.info("Done SQL with " + map.size() + " result");
		rs.close();
		pstmt.close();
		conn.close();
		return map;
	}

	/**
	 * getColumnValueByStore
	 * 
	 * @param store_sid
	 *            int
	 * @param columns_name
	 *            String
	 * @param conn
	 *            Connection
	 * @return String
	 * @throws SQLException
	 */
	public HashMap<Integer, String> getColumnValuesByStore(int companySid, int branchSid, String columns_name)
			throws SQLException {
		String sql = "select sid, " + columns_name + " from STORE_INFO_TBL "
				+ " join store_detail_temp c on c.store_sid=b.store_sid and c.company_sid=? and c.branch_sid=?";

		String value = "";
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		Connection conn = hw09DataSource.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql);

		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			Integer sid = new Integer(rs.getInt("sid"));
			value = String.valueOf(rs.getObject(columns_name));
			if ("null".equals(value)) {
				value = "";
			}
			map.put(sid, value);
		}

		rs.close();
		pstmt.close();
		conn.close();
		return map;
	}

	public HashMap<Integer, DirectBussinessBO> getStoreBusinessRel(int companySid, int branchSid)
			throws SQLException {
		String sql = "select b.store_sid, a.BUSSINESS_ID,BUSSINESS_NAME,b.ERP_ID,b.KEYACCOUNT_ID, b.RC_TYPE_SID, b.CUSTSTORE_ID ,d.ATTRIBUTE_CONTENT from DIRECT_BUSINESS_TBL a "
				+ "inner join STORE_BUSINESS_REL b"
				+ " on a.BUSSINESS_ID=b.BUSINESS_ID"
				+"  left join STORE_ATTRIBUTE_INFO d on b.RC_STORETYPE_ID = d.ATTRIBUTE_VALUE and ATTRIBUTE_NAME ='RC_STORETYPE' "
				+ " join store_detail_temp c on c.store_sid=b.store_sid and c.company_sid=? and c.branch_sid=?";
		Connection conn = hw09DataSource.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, companySid);
		pstmt.setInt(2, branchSid);

		logger.info("Executing SQL: " + sql);
		ResultSet rs = pstmt.executeQuery();
		HashMap<Integer, DirectBussinessBO> map = new HashMap<Integer, DirectBussinessBO>();
		DirectBussinessBO bo = null;
		while (rs.next()) {
			bo = new DirectBussinessBO();
			Integer sid = new Integer(rs.getInt("store_sid"));
			bo.setBussinessId(rs.getString("BUSSINESS_ID"));
			bo.setBussinessName(rs.getString("BUSSINESS_NAME"));
			bo.setErpId(rs.getString("ERP_ID"));
			bo.setKeyAccountId(rs.getString("KEYACCOUNT_ID"));
			bo.setRcType(rs.getInt("RC_TYPE_SID"));
			bo.setCUSTSTORE_ID(rs.getString("CUSTSTORE_ID"));
			bo.setRcStoreType(rs.getString("ATTRIBUTE_CONTENT"));
			map.put(sid, bo);
		}
		logger.info("Done SQL with " + map.size() + " results");
		rs.close();
		pstmt.close();
		conn.close();
		return map;
	}
}
