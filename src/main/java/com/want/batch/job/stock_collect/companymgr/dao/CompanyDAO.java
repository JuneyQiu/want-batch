package com.want.batch.job.stock_collect.companymgr.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.stock_collect.companymgr.bo.CompanyInfoBO;

//import com.hewang.ddtimer.companymgr.bo.StateInfoBO;

@Component
public class CompanyDAO {

	@Autowired
	public DataSource hw09DataSource;

	public ArrayList listCompanyByStateSID(int v_state_sid, String emp) throws SQLException {
		ArrayList companylist = new ArrayList();

		String sql = " select COMPANY_SID as SID,COMPANY_ID,COMPANY_NAME,(select STATUS from COMPANY_INFO_TBL where SID=COMPANY_SID ) as STATUS from ORG_THREE_LV_VIEW_2 "
				+ "where  (COMPANY_SID in (select LV_SID from  USER_AUTHORITY_TBL where LEVEL_TYPE=2 and UPPER(EMP_ID)=UPPER('"
				+ emp
				+ "')) "
				+ " or BRANCH_SID in (select LV_SID from  USER_AUTHORITY_TBL where LEVEL_TYPE=3 and UPPER(EMP_ID)=UPPER('"
				+ emp
				+ "')) "
				+ "  or STATE_SID in (select LV_SID from  USER_AUTHORITY_TBL where LEVEL_TYPE=1 and UPPER(EMP_ID)=UPPER('"
				+ emp
				+ "'))) and STATE_SID="
				+ v_state_sid
				+ "group by COMPANY_SID,COMPANY_ID,COMPANY_NAME";

		Connection conn = hw09DataSource.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			CompanyInfoBO cib = new CompanyInfoBO();
			cib.setSID(rs.getInt("SID"));
			if (cib.getSID() != 0) {
				cib.setCOMPANY_ID(rs.getString("COMPANY_ID"));
				cib.setCOMPANY_NAME(rs.getString("COMPANY_NAME"));
				cib.setSTATUS(rs.getString("STATUS"));
				companylist.add(cib);
			}
		}
		rs.close();
		pstmt.close();
		conn.close();

		return companylist;
	}

	public ArrayList listCompanyByStateSID_List(int v_state_sid) throws SQLException {
		ArrayList companylist = new ArrayList();
		String sql = "select SID,COMPANY_ID,COMPANY_NAME,STATE_SID,STATUS,REAL_STATE_SID from COMPANY_INFO_TBL where STATE_SID="
				+ v_state_sid;
		Connection conn = hw09DataSource.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			CompanyInfoBO cib = new CompanyInfoBO();
			cib.setSID(rs.getInt("SID"));
			cib.setCOMPANY_ID(rs.getString("COMPANY_ID"));
			cib.setCOMPANY_NAME(rs.getString("COMPANY_NAME"));
			cib.setSTATUS(rs.getString("STATUS"));
			cib.setREAL_STATE_SID(rs.getInt("REAL_STATE_SID"));
			companylist.add(cib);
		}
		rs.close();
		pstmt.close();
		conn.close();

		return companylist;
	}

	public boolean createCompany(CompanyInfoBO v_company_bo)
			throws SQLException {
		String sqlcmd = " insert into COMPANY_INFO_TBL (SID,COMPANY_ID,COMPANY_NAME,STATE_SID,STATUS,REAL_STATE_SID,CREATOR,CREATE_DATE) "
				+ " values (COMPANY_INFO_TBL_SEQ.nextval,?,?,?,?,?,?,SYSDATE) ";

		Connection conn = hw09DataSource.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(sqlcmd.toString());
		pstmt.setString(1, v_company_bo.getCOMPANY_ID());
		pstmt.setString(2, v_company_bo.getCOMPANY_NAME());
		pstmt.setInt(3, v_company_bo.getSTATE_SID());
		pstmt.setString(4, v_company_bo.getSTATUS());
		pstmt.setInt(5, v_company_bo.getREAL_STATE_SID());
		pstmt.setString(6, v_company_bo.getCREATOR());
		pstmt.execute();
		pstmt.close();
		conn.close();
		return true;
	}

	public boolean editCompany(CompanyInfoBO v_company_bo) throws SQLException {
		String sqlcmd = "update COMPANY_INFO_TBL set COMPANY_NAME =?,COMPANY_ID=?,STATUS=?,REAL_STATE_SID=?,EMAIL=?,REMARK=?,UPDATOR=?,UPDATE_DATE=SYSDATE where SID=?";
		Connection conn = hw09DataSource.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(sqlcmd.toString());
		pstmt.setString(1, v_company_bo.getCOMPANY_NAME());
		pstmt.setString(2, v_company_bo.getCOMPANY_ID());
		pstmt.setString(3, v_company_bo.getSTATUS());
		pstmt.setInt(4, v_company_bo.getREAL_STATE_SID());
		pstmt.setString(5, v_company_bo.getEMAIL());
		pstmt.setString(6, v_company_bo.getREMARK());
		pstmt.setString(7, v_company_bo.getUPDATOR());
		pstmt.setInt(8, v_company_bo.getSID());
		pstmt.execute();
		pstmt.close();
		conn.close();
		return true;
	}

	public CompanyInfoBO showCompany(int v_company_sid) throws SQLException {
		CompanyInfoBO cib = new CompanyInfoBO();
		String sqlcmd = "select SID,COMPANY_ID,COMPANY_NAME,STATE_SID,STATUS,REAL_STATE_SID,EMAIL,REMARK,CREATOR,CREATE_DATE from COMPANY_INFO_TBL where SID="
				+ v_company_sid;
		Connection conn = hw09DataSource.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sqlcmd);
		while (rs.next()) {
			cib.setSID(rs.getInt("SID"));
			cib.setCOMPANY_ID(rs.getString("COMPANY_ID"));
			cib.setCOMPANY_NAME(rs.getString("COMPANY_NAME"));
			cib.setSTATE_SID(rs.getInt("STATE_SID"));
			cib.setSTATUS(rs.getString("STATUS"));
			cib.setREAL_STATE_SID(rs.getInt("REAL_STATE_SID"));
			cib.setEMAIL(rs.getString("EMAIL"));
			cib.setREMARK(rs.getString("REMARK"));
			cib.setCREATOR(rs.getString("CREATOR"));
			cib.setCREATE_DATE(rs.getTimestamp("CREATE_DATE"));
		}
		rs.close();
		stmt.close();
		conn.close();

		return cib;

	}

	public boolean deleteCompany(int m_company_sid) throws SQLException {
		String sqlcmd = " delete COMPANY_INFO_TBL where SID=? ";

		Connection conn = hw09DataSource.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(sqlcmd.toString());
		pstmt.setInt(1, m_company_sid);
		pstmt.execute();
		pstmt.close();
		conn.close();
		return true;
	}

	public int getCompanyId(String company_id) throws SQLException {
		int status = 0;
		String sql = "select count(*) as STATUS from  COMPANY_INFO_TBL where COMPANY_ID= '"
				+ company_id + "'";

		Connection conn = hw09DataSource.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		rs.next();
		status = rs.getInt("STATUS");
		rs.close();
		stmt.close();
		conn.close();
		return status;
	}

	public ArrayList<CompanyInfoBO> getAllCompnay() throws SQLException {
		ArrayList list = new ArrayList();
		String sql = "select SID,PIN_YIN from COMPANY_INFO_TBL where status=1 order by SID";

		Connection conn = hw09DataSource.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			CompanyInfoBO cb = new CompanyInfoBO();
			cb.setSID(rs.getInt("SID"));
			cb.setPIN_YIN(rs.getString("PIN_YIN"));
			list.add(cb);
		}
		rs.close();
		stmt.close();
		conn.close();
		return list;
	}

}
