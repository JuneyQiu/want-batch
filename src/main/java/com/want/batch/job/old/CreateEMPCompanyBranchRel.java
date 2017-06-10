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
public class CreateEMPCompanyBranchRel extends AbstractWantJob {

	@Override
	public void execute() throws SQLException {
		logger.info("Create Emp and Company/Branch relationship START!!!");
		createEMPCompanyBranchRel();
		logger.info("Create Emp and Company/Branch relationship END!!!");
	}

	public static void main(String[] args) {
		// CreateEMPCompanyBranchRel.createEMPCompanyBranchRel();
	}

	public CreateEMPCompanyBranchRel() {
	}

	// 建立营业所员工所属营业所与分公司资料，一层&零层
	private static String sqlcmd1 = " select e.company_sap_id,e.branch_sap_id,a.emp_id from EMP_POSITION a "
			+ " inner join POSITION_B b on a.pos_id=b.pos_id "
			+ " inner join ORGANIZATION_B c on b.org_id=c.org_id  "
			+ " inner join ORGANIZATION_B d on c.org_PARENT_DEPT=d.org_id and d.org_name like'%营业所%' "
			+ " inner join COMPANY_BRANCH e on d.org_id=e.BRANCH_HR_ID "
			+ " union  select e.company_sap_id,e.branch_sap_id,a.emp_id from EMP_POSITION a inner join POSITION_B b on a.pos_id=b.pos_id "
			+ " inner join ORGANIZATION_B d on d.org_id=b.org_id and d.org_name like'%营业所%' inner join COMPANY_BRANCH e on d.org_id=e.BRANCH_HR_ID ";

	// 建立营业所员工所属营业所与分公司资料，二层
	private static String sqlcmd2 = " select e.company_sap_id,e.branch_sap_id,a.emp_id from EMP_POSITION a "
			+ " inner join POSITION_B b on a.pos_id=b.pos_id "
			+ " inner join ORGANIZATION_B c on b.org_id=c.org_id  "
			+ " inner join ORGANIZATION_B d on c.org_PARENT_DEPT=d.org_id  "
			+ " inner join ORGANIZATION_B f on d.org_PARENT_DEPT=f.org_id and f.org_name like'%营业所%' "
			+ " inner join COMPANY_BRANCH e on f.org_id=e.BRANCH_HR_ID ";

	// 建立营业所员工所属营业所与分公司资料，三层
	private static String sqlcmd3 = " select e.company_sap_id,e.branch_sap_id,a.emp_id from EMP_POSITION a  "
			+ " inner join POSITION_B b on a.pos_id=b.pos_id  "
			+ " inner join ORGANIZATION_B c on b.org_id=c.org_id   "
			+ " inner join ORGANIZATION_B d on c.org_PARENT_DEPT=d.org_id   "
			+ " inner join ORGANIZATION_B f on d.org_PARENT_DEPT=f.org_id   "
			+ " inner join ORGANIZATION_B g on f.org_PARENT_DEPT=g.org_id and g.org_name like'%营业所%'  "
			+ " inner join COMPANY_BRANCH e on g.org_id=e.BRANCH_HR_ID";

	// 新增工号到分公司关系之前 删除旧关系
	private static String sqlcmd6_del = "delete from COMPANY_EMP where EMP_ID=? ";

	// 新增工号到分公司关系
	private static String sqlcmd6 = "insert into COMPANY_EMP values(?,?)";

	// 新增工号到营业所关系之前 删除旧关系
	private static String sqlcmd7_del = "delete from BRANCH_EMP where EMP_ID=? ";

	// 新增工号到营业所关系
	private static String sqlcmd7 = "insert into BRANCH_EMP values(?,?) ";

	// 新增工号辨别總部或是地方之前 删除旧的
	private static String sqlcmd99_del = "delete from USER_LEVEL where USER_ID=? ";

	// 新增工号辨别總部或是地方
	private static String sqlcmd99 = "insert into USER_LEVEL (USER_ID, LEVEL_ID, CREATE_USER, CREATE_DATE, UPDATE_USER, UPDATE_DATE) values (?,?,'MIS_SYS',sysdate,null,null) ";

	// 新增遗漏的'工号辨别总部或是地方'
	private static String sqlcmd4 = "insert into USER_LEVEL select a.emp_id,1,'MIS_SIS',sysdate,null,null  from EMP_POSITION a "
			+ "inner join POSITION_B b on a.pos_id=b.pos_id and org_id not  like '1%' and (POS_TYPE_ID = '" + RoleConstant.COMPANY_XG + "' or POS_TYPE_ID = '" + RoleConstant.BRANCH_XG + "')" // modify 2014-07-09 mandy 将pos_name like ‘销管’‘业管’ 修改成007分公司销管,008营业所销管
			+ "where a.emp_id not in (select USER_ID from USER_LEVEL )";

	private static String sqlcmd4b = "insert into USER_LEVEL select a.emp_id,1,'MIS_SIS',sysdate,null,null  from EMP_POSITION a "
			+ "inner join POSITION_B b on a.pos_id=b.pos_id and org_id not  like '1%' and (POS_TYPE_ID='" + RoleConstant.SALES + "' or POS_TYPE_ID='" + RoleConstant.ZSSALES + "') " // modify 2014-07-09 mandy 将pos_name like '%业代%' 修改成002业代,003资深业代
			+ "where a.emp_id not in (select USER_ID from USER_LEVEL )";

	private static String sqlcmd4c = "insert into USER_LEVEL select a.emp_id,1,'MIS_SIS',sysdate,null,null  from EMP_POSITION a "
			+ "inner join POSITION_B b on a.pos_id=b.pos_id and org_id not  like '1%' and POS_TYPE_ID='" + RoleConstant.DIRECTOR1 + "' " // modify 2014-07-09 mandy 将pos_name like '%主任%' 修改成005主任
			+ "where a.emp_id not in (select USER_ID from USER_LEVEL )";

	/**
	 * <pre>
	 * 建立员工工号与分公司，营业所关系
	 * 建立步骤
	 * 1.取得HR系统中的员工与营业所，分公司关系。
	 * 2.开始循环取得的业务帐号。
	 * 3.新增员工到分公司。（新增之前做删除）
	 * 4.新增员工到营业所。（新增之前做删除）
	 * 5.新增员工到总部或是地方关系，用来判断此员工是总部还是地方。（新增之前做删除）
	 * </pre>
	 * 
	 * @throws SQLException
	 * 
	 */
	public void createEMPCompanyBranchRel() throws SQLException {
		ArrayList emp_list = new ArrayList();
		Connection conn = getICustomerConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		PreparedStatement pstmt6 = null;
		PreparedStatement pstmt7 = null;
		PreparedStatement pstmt99 = null;
		PreparedStatement pstmt6d = null;
		PreparedStatement pstmt7d = null;
		PreparedStatement pstmt99d = null;
		PreparedStatement pstmt4 = null;
		PreparedStatement pstmt4b = null;
		PreparedStatement pstmt4c = null;

		// 取得所有业务帐号与基本资料
		logger.info("取得所有业务帐号与基本资料!");

		pstmt = conn.prepareStatement(sqlcmd1);
		rs = pstmt.executeQuery();
		while (rs.next()) {
			String[] temp_emp = { rs.getString("company_sap_id"),
					rs.getString("branch_sap_id"), rs.getString("emp_id") };
			emp_list.add(temp_emp);
		}
		rs.close();
		pstmt.close();

		pstmt = conn.prepareStatement(sqlcmd2);
		rs = pstmt.executeQuery();
		while (rs.next()) {
			String[] temp_emp = { rs.getString("company_sap_id"),
					rs.getString("branch_sap_id"), rs.getString("emp_id") };
			emp_list.add(temp_emp);
		}
		rs.close();
		pstmt.close();

		pstmt = conn.prepareStatement(sqlcmd3);
		rs = pstmt.executeQuery();
		while (rs.next()) {
			String[] temp_emp = { rs.getString("company_sap_id"),
					rs.getString("branch_sap_id"), rs.getString("emp_id") };
			emp_list.add(temp_emp);
		}
		rs.close();
		pstmt.close();

		logger.info("取得所有员工帐号与基本资料!数量：" + emp_list.size());
		// 准备更新
		pstmt6 = conn.prepareStatement(sqlcmd6);
		pstmt7 = conn.prepareStatement(sqlcmd7);
		pstmt99 = conn.prepareStatement(sqlcmd99);
		pstmt6d = conn.prepareStatement(sqlcmd6_del);
		pstmt7d = conn.prepareStatement(sqlcmd7_del);
		pstmt99d = conn.prepareStatement(sqlcmd99_del);
		pstmt4 = conn.prepareStatement(sqlcmd4);
		pstmt4b = conn.prepareStatement(sqlcmd4b);
		pstmt4c = conn.prepareStatement(sqlcmd4c);

		logger.info("开始循环取得的员工帐号");
		for (int j = 0; j < emp_list.size(); j++) {
			try {
				// 清空变量
				pstmt6.clearParameters();
				pstmt7.clearParameters();
				pstmt99.clearParameters();
				pstmt6d.clearParameters();
				pstmt7d.clearParameters();
				pstmt99d.clearParameters();
				
				String[] temp_emp = (String[]) emp_list.get(j);
				logger.info("建立成功，工号start:" + temp_emp[2]);
				pstmt6.setString(1, temp_emp[0]);
				pstmt6.setString(2, temp_emp[2]);
				pstmt6d.setString(1, temp_emp[2]);
				pstmt6d.executeUpdate();
				pstmt6.executeUpdate();

				pstmt7.setString(1, temp_emp[1]);
				pstmt7.setString(2, temp_emp[2]);
				pstmt7d.setString(1, temp_emp[2]);
				
				pstmt7d.executeUpdate();
				pstmt7.executeUpdate();
				logger.debug("建立成功，工号:" + temp_emp[2]);

				pstmt99.setString(1, temp_emp[2]);
				pstmt99.setString(2, "1");
				pstmt99d.setString(1, temp_emp[2]);

				pstmt99d.executeUpdate();
				pstmt99.executeUpdate();
				logger.debug("建立成功，工号end:" + temp_emp[2]);

			} catch (SQLException sqle) {
				sqle.getMessage();
			}
		}
		
		logger.info("---开始添加工号----");
		pstmt4.executeUpdate();
		pstmt4b.executeUpdate();
		pstmt4c.executeUpdate();
		logger.info("---添加工号完成----");
		logger.info("新建结束");

		pstmt.close();
		pstmt6.close();
		pstmt7.close();
		pstmt99.close();
		pstmt6d.close();
		pstmt7d.close();
		pstmt99d.close();
		conn.close();

	}
}
