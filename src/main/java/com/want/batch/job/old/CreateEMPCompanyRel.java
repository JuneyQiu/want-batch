package com.want.batch.job.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2010</p>
 *
 * <p>Company: </p>
 *
 * @author David
 * @version 1.0
 */

/**
 * 建立员工与分公司，营业所关系
 * 
 */
@Component
public class CreateEMPCompanyRel extends AbstractWantJob {

	@Override
	public void execute() throws SQLException {
		logger.info("Create Emp and Company relationship START!!!");
		createEMPCompanyRel();
		logger.info("Create Emp and Company relationship END!!!");
	}

	public static void main(String[] args) {
		// createEMPCompanyRel();
	}

	public CreateEMPCompanyRel() {
	}

	// 找所有业务- POS_NAME like '%业代%'
	// modify 2014-07-09 mandy 将POS_NAME like '%业代%'修改成002,003
	private static String searceEMP = "select EMP_ID as EMP_ID, b.POS_ID as POS_ID,b.ORG_ID as ORG_ID from EMP_POSITION_A a inner join POSITION_B b on a.POS_ID=b.POS_ID and (b.POS_TYPE_ID='" + RoleConstant.SALES + "' or b.POS_TYPE_ID='" + RoleConstant.ZSSALES + "')";
	// 找上级ORG_ID
	private static String searchParentOrg = "select a.ORG_ID as ORG_ID, a.ORG_NAME as ORG_NAME from ORGANIZATION_B a inner join ORGANIZATION_B b on a.ORG_ID=b.ORG_PARENT_DEPT where b.ORG_ID=? ";
	// 找本级ORG_ID
	private static String searchOrg = "select a.ORG_ID as ORG_ID, a.ORG_NAME as ORG_NAME from ORGANIZATION_B a  where a.ORG_ID=? ";

	// 新增工号到分公司关系之前 删除旧关系
	private static String delCompanyEmpRel = "delete from COMPANY_EMP where EMP_ID=? ";

	// 新增工号到分公司关系
	private static String CreCompanyEmpRel = "insert into COMPANY_EMP values(?,?)";

	/**
	 * <pre>
	 * 建立员工工号与分公司
	 * 建立步骤
	 * 1.取得HR系统中的员工与岗位，分公司。
	 * 2.将hr分公司id换成erp分公司id
	 * 3.开始循环取得的业务帐号。
	 * 4.新增员工到分公司。（新增之前做删除）
	 * 
	 * </pre>
	 * 
	 * @throws SQLException
	 * 
	 */
	public void createEMPCompanyRel() throws SQLException {
		ArrayList emp_list = new ArrayList();
		Connection conn = getICustomerConnection();
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		ResultSet rs = null;
		String key_word = "分公司";

		// 取得所有业务帐号与基本资料,以及与分公司关系
		logger.info("取得所有业务帐号与岗位资料!");
		pstmt = conn.prepareStatement(searceEMP);
		rs = pstmt.executeQuery();
		while (rs.next()) {
			String[] temp_emp = { rs.getString("EMP_ID"),// 0
					rs.getString("POS_ID"),// 1
					rs.getString("ORG_ID"),// 2
					CreateEMPCompanyRel.parseHRCompanyIDtoSAPCompanyID(
							CreateEMPCompanyRel.getCompanyId(
									rs.getString("ORG_ID"), key_word, conn),
							conn) };// 3
			emp_list.add(temp_emp);
		}
		rs.close();
		pstmt.close();

		// 新增业代与分公司关系
		pstmt = conn.prepareStatement(delCompanyEmpRel);
		pstmt2 = conn.prepareStatement(CreCompanyEmpRel);
		for (int i = 0; i < emp_list.size(); i++) {
			String[] temp_emp = (String[]) emp_list.get(i);
			if (!"".equals(temp_emp[3])) {
				// 将原来的业代与分公司关系移除
				pstmt.setString(1, temp_emp[0]);
				pstmt.executeUpdate();
				// 新增业代与分公司关系
				pstmt2.setString(1, temp_emp[3]);
				pstmt2.setString(2, temp_emp[0]);
				pstmt2.executeUpdate();

				logger.debug("insert data:" + temp_emp[3] + "-"
						+ temp_emp[0]);
			} else {
				logger.error("we lose emp-company relation {EMP_ID:"
						+ temp_emp[0] + " POS_ID:" + temp_emp[1] + " ORG_ID:"
						+ temp_emp[2] + " COMPANY_ID:" + temp_emp[3]);
			}
		}

		pstmt.close();
		pstmt2.close();
		conn.close();

	}

	// 用递归方式查找分公司

	public static String getCompanyId(String org_id, String key_word,
			Connection conn) throws SQLException {
		String target_id = "";
		String org_name = "";
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		pstmt = conn.prepareStatement(searchOrg);
		pstmt.setString(1, org_id);
		rs = pstmt.executeQuery();
		if (rs.next()) {
			target_id = rs.getString("ORG_ID");
			org_name = rs.getString("ORG_NAME");
		}
		rs.close();
		pstmt.close();
		// org_name.lastIndexOf(key_word)<0
		if (org_name != null && org_name.lastIndexOf(key_word) < 0) {
			pstmt = conn.prepareStatement(searchParentOrg);
			pstmt.setString(1, org_id);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				String r_org_id = rs.getString("ORG_ID");
				org_name = rs.getString("ORG_NAME");
				target_id = getCompanyId(r_org_id,
						key_word, conn);
			}
			rs.close();
			pstmt.close();
		}
		rs.close();
		pstmt.close();

		// logger.info("company_id:"+target_id);
		return target_id;
	}

	// SAP & HR 分公司id对照，使用前四码来确认分公司
	public static String parseHRCompanyIDtoSAPCompanyID(String company_id,
			Connection conn) throws SQLException {
		PreparedStatement pstmt = conn
				.prepareStatement(" select COMPANY_SAP_ID from COMPANY_SAP_HR_REL where COMPANY_HR_ID=? ");
		pstmt.setString(1, company_id);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			company_id = rs.getString("COMPANY_SAP_ID");
		}
		rs.close();
		pstmt.close();

		return company_id;
	}

}
