package com.want.batch.job.stock_collect.branchmgr.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BranchDAO {

	@Autowired
	public DataSource hw09DataSource;

	public ArrayList<Integer> getAllBranch() throws SQLException {
		ArrayList list = new ArrayList();
		String sql = "select SID from BRANCH_INFO_TBL where STATUS=1 order by sid";

		Connection conn = hw09DataSource.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			list.add(new Integer(rs.getInt("SID")));
		}
		rs.close();
		stmt.close();
		conn.close();
		return list;
	}

	public ArrayList getBranchByCompany(int company_sid) throws SQLException {
		ArrayList list = new ArrayList();
		String sql = "select SID from BRANCH_INFO_TBL where COMPANY_SID = "
				+ company_sid + " and STATUS=1 order by sid";
		Connection conn = hw09DataSource.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			list.add(String.valueOf(rs.getInt("SID")));
		}
		rs.close();
		stmt.close();
		conn.close();
		return list;
	}

}
