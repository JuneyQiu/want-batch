package com.want.batch.job.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;

@Component
public class UpdateAuthSFA2 extends AbstractWantJob {
	/**
	 * <p>
	 * Title:更新权限，SFA 2上线的渠道以及营业所不可使用业务网站日报，特陈功能
	 * </p>
	 * 
	 * <p>
	 * Description: 取得SFA2上线的营业所以及渠道对应的业代工号，
	 * 
	 * </p>
	 * 
	 * <p>
	 * Copyright: Copyright (c) 2013
	 * </p>
	 * 
	 * <p>
	 * Company: Want Want group
	 * </p>
	 * 
	 * @author David Luo
	 * @version 1.0
	 */

	public UpdateAuthSFA2() {

	}

	public static void main(String[] args) {
		// UpdateAuthSFA2.updateSfaAuth();
	}
	
	@Autowired
	public CreateEMPCompanyRel createEMPCompanyRel;

	public void updateSfaAuth() throws SQLException {
		// 1.取得sfa2 已上线的营业所该分公司下以及渠道的业代工号
		StringBuffer sqlcmd1 = new StringBuffer("");
		sqlcmd1.append(" SELECT D.ORG_ID,B.EMP_ID,G.SID FROM  DIVSION_SALES_CUSTOMER_REL a ");
		sqlcmd1.append(" INNER JOIN  EMP_POSITION_A b on a.USER_ID = b.EMP_ID AND B.MASTER_POS=1 and a.CREDIT_ID in (select CREDIT_ID from SFA2_CREDITID_TBL) ");
		sqlcmd1.append(" INNER JOIN  POSITION_B c on b.POS_ID=C.POS_ID ");
		sqlcmd1.append(" INNER JOIN ORGANIZATION_B d on c.ORG_ID=d.ORG_ID ");
		sqlcmd1.append(" INNER JOIN (SELECT * FROM COMPANY_BRANCH x  INNER JOIN SFA2_BRANCH_TBL xx ON X.BRANCH_SAP_ID=XX.BRANCH_ID) F ON A.COMPANY_ID=F.COMPANY_SAP_ID||'1'");
		sqlcmd1.append(" INNER JOIN USER_INFO_TBL G ON A.USER_ID=G.ACCOUNT  and G.USER_TYPE_SID=2 ");
		// 2. 取得上线营业所,将营业所ID branch_hr_id
		StringBuffer sqlcmd2 = new StringBuffer(
				" SELECT B.BRANCH_HR_ID,B.BRANCH_NAME  FROM SFA2_BRANCH_TBL A INNER JOIN COMPANY_BRANCH B ON A.BRANCH_ID=B.BRANCH_SAP_ID ");

		// 3.删除业代原始权限
		StringBuffer sqlcmd3 = new StringBuffer(
				" DELETE AUTH_USER_GROUP_REL WHERE USER_SID=?");

		// 4.新增业代SFA2 权限（一般权限外，取消特陈以及日报权限）
		StringBuffer sqlcmd4 = new StringBuffer(
				" INSERT INTO AUTH_USER_GROUP_REL(USER_SID,USERGROUP_SID,CREATE_USER,CREATE_DATE) VALUES(?,4050,'mis_sync',SYSDATE)");

		Connection conn = getICustomerConnection();
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		ArrayList emp_list = new ArrayList();
		HashMap branch_hm = new HashMap();
		String[] current_emp = { "", "", "" };

		pstmt = conn.prepareStatement(sqlcmd2.toString());
		rs = pstmt.executeQuery();
		while (rs.next()) {
			branch_hm.put(rs.getString("BRANCH_HR_ID"),
					rs.getString("BRANCH_NAME"));
		}
		rs.close();
		pstmt.close();
		// logger.info("branch_hm:"+branch_hm.size());
		pstmt = conn.prepareStatement(sqlcmd1.toString());
		rs = pstmt.executeQuery();
		while (rs.next()) {
			String[] emp = {
					createEMPCompanyRel.getCompanyId(rs.getString("ORG_ID"),
							"营业所", conn), rs.getString("EMP_ID"),
					rs.getString("SID") };
			if (branch_hm.containsKey(emp[0])) {
				emp_list.add(emp);
			}
		}
		rs.close();
		pstmt.close();

		pstmt = conn.prepareStatement(sqlcmd3.toString());
		pstmt1 = conn.prepareStatement(sqlcmd4.toString());

		for (int i = 0; i < emp_list.size(); i++) {
			current_emp = (String[]) emp_list.get(i);
			pstmt.clearParameters();
			pstmt.setString(1, current_emp[2]);
			pstmt.executeUpdate();

			pstmt1.clearParameters();
			pstmt1.setString(1, current_emp[2]);
			pstmt1.executeUpdate();
		}
		pstmt.close();
		pstmt1.close();
		logger.info("Total SFA2 emp:" + emp_list.size());
		logger.info("Total SFA2 Branch:" + branch_hm.size());

		pstmt.close();
		pstmt1.close();
		conn.close();

	}

	@Override
	public void execute() throws Exception {
		// TODO Auto-generated method stub

	}
}
