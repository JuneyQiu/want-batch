package com.want.batch.job.old;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.want.batch.WantBatchException;
import com.want.batch.job.AbstractWantJob;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2010
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
@Component
public class CreateSZ_ZJCustomerRel extends AbstractWantJob {
	public CreateSZ_ZJCustomerRel() {
	}

	public static void main(String[] args) {
		// CreateSZ_ZJCustomerRel.createSZCustomerRel();
		// CreateSZ_ZJCustomerRel.createZJCustomerRel();
	}

	// 检查帐号是否存在
	private static String sqlisexisted = " select 1 from user_info_tbl where account=?";

	// 更新总监帐号变量，将帐号类型变更成总监user_type_sid=12，状态为在职中status=1
	private static String sqlupdatezjdata = " update user_info_tbl set user_type_sid=12,status=1 where account=?";

	// 更新所长帐号变量，将帐号类型变更成所长 user_type_sid=6，状态为在职中status=1
	private static String sqlupdateszdata = " update user_info_tbl set user_type_sid=6,status=1 where account=?";

	// 建立所长帐号
	private static String sqlcreatesz = " insert into user_info_tbl "
			+ " (sid,ACCOUNT,PASSWORD,USER_NAME,USER_TYPE_SID,EMAIL,MOBILE,AREA_CODE,PHONE_NUM,STATUS,UPDATE_DATE,UPDATOR,CREATE_DATE,CREATOR,LAST_LOGIN_TIME,LAST_LOGIN_ADDS,CHANNEL_SID)  "
			+ " select USER_INFO_TBL_SID.nextval,?,USER_INFO_TBL_SID.currval,?,6,null,null,null,null,1,null,null,sysdate,'MIS_SYS',null,null,1  from dual ";

	// 建立总监帐号
	private static String sqlcreatezj = " insert into user_info_tbl "
			+ " (sid,ACCOUNT,PASSWORD,USER_NAME,USER_TYPE_SID,EMAIL,MOBILE,AREA_CODE,PHONE_NUM,STATUS,UPDATE_DATE,UPDATOR,CREATE_DATE,CREATOR,LAST_LOGIN_TIME,LAST_LOGIN_ADDS,CHANNEL_SID)  "
			+ " select USER_INFO_TBL_SID.nextval,?,USER_INFO_TBL_SID.currval,?,7,null,null,null,null,1,null,null,sysdate,'MIS_SYS',null,null,1  from dual ";

	// 删除某人所有权限
	private static String sqldeleteauth = " delete AUTH_USER_GROUP_REL  where user_sid= ( select sid from user_info_tbl where account = ? ) ";

	// 建立所长权限
	private static String sqlcreateszauth = " insert into AUTH_USER_GROUP_REL(USER_SID, USERGROUP_SID, CREATE_USER, CREATE_DATE)  select sid ,'151', 'mis', sysdate  from  USER_INFO_TBL where account = ? ";
	// 建立总监权限
	private static String sqlcreatezjauth = " insert into AUTH_USER_GROUP_REL(USER_SID, USERGROUP_SID, CREATE_USER, CREATE_DATE)  select sid ,'71', 'mis', sysdate  from  USER_INFO_TBL where account = ? ";

	// 从HR系统找出所长
	// modify 2014-07-09 mandy 将b.POS_NAME like'%所长%'修改成004
	private static String sqlcmd1_a = " select  f.COMPANY_SAP_ID,f.branch_sap_id as BRANCH_ID,g.emp_id as EMP_ID,g.emp_name as EMP_NAME  from EMP_POSITION a  "
			+ " inner join POSITION_B b on  a.pos_id=b.pos_id  and ( a.JOB_NAME like'%所长%' or b.POS_TYPE_ID = '" + RoleConstant.SUOZHANG + "' ) "
			+ "  inner join ORGANIZATION_B c on b.org_id=c.org_id  "
			+ "  inner join  COMPANY_BRANCH f on c.org_id=f.BRANCH_HR_ID "
			+ "  inner join EMP g on a.emp_id=g.emp_id group by   f.COMPANY_SAP_ID,f.branch_sap_id ,g.emp_id ,g.emp_name ";

	// 删除此所长与所有客户关系
	private static String sqlcmd1_b = " delete CUSTOMER_USER_REL where USER_SID=(select sid from  USER_INFO_TBL where account = ?) ";

	// 建立此所长与营业所客户关系
	private static String sqlcmd1_c = " insert into CUSTOMER_USER_REL "
			+ " select sid,u_sid,u_id from ( select b.sid,(select sid from user_info_tbl where account=?) as  u_sid ,? as u_id  "
			+ " from CUSTOMER_BRANCH_TBL a inner join  customer_info_tbl b on a.customer_id=b.id  inner join COMPANY_BRANCH_VIEW c on a.BRANCH_ID=c.BRANCH_ID WHERE c.type=1  "
			+ " and BRANCH_ID = ? )group by  sid,u_sid,u_id ";

	// 新增所长营业所关系
	private static String sqlcmd1_d = " insert into branch_emp values(?,?)  ";

	// 新增所长营业所关系之前 删除旧关系
	private static String sqlcmd1_d_del = " delete from BRANCH_EMP where EMP_ID=? ";

	// 查出所长营业所对应的分公司
	// private static String
	// sqlcmd1_e="select COMPANY_SAP_ID from COMPANY_BRANCH where BRANCH_SAP_ID=?";

	// 新增所长分公司关系
	// 新增所长分公司关系之前删除旧关系

	// 删除总监所有客户关系
	private static String sqlcmd2_b = " delete CUSTOMER_USER_REL where USER_SID=(select sid from  USER_INFO_TBL where account = ?) ";

	// 建立总监与分公司所有客户关系
	private static String sqlcmd2_c = " insert into CUSTOMER_USER_REL select sid,u_sid,u_id from  "
			+ "   ( select b.sid,(select sid from user_info_tbl where account=?) as  u_sid ,? as u_id  "
			+ "   from CUSTOMER_BRANCH_TBL a inner join  customer_info_tbl b on a.customer_id=b.id  inner join COMPANY_BRANCH_VIEW c  "
			+ "   on a.BRANCH_ID=c.BRANCH_ID  inner join COMPANY_SAP_HR_REL d on c.COMPANY_ID = d.COMPANY_SAP_ID WHERE c.type=1 and d.COMPANY_HR_ID=?)group by  sid,u_sid,u_id  ";

	// 新增总监分公司关系
	private static String sqlcmd2_d = " insert into company_emp values(?,?)  ";

	// 新增总监分公司关系之前 删除旧关系
	private static String sqlcmd2_d_del = " delete from COMPANY_EMP where EMP_ID=? ";

	// 查出总监分公司的营业所
	private static String sqlcmd2_e = "select BRANCH_SAP_ID from COMPANY_BRANCH where COMPANY_SAP_ID=?";

	// 新增总监营业所关系
	// private static String sqlcmd2_f="insert into BRANCH_EMP values(?,?)";

	// 新增总监营业所关系之前删除旧关系
	// private static String
	// sqlcmd2_f_del="delete from BRANCH_EMP where EMP_ID=?";

	/**
	 * <pre>
	 * 建立所长与客户关系
	 * 建立步骤
	 * 1.取得所长与营业所关系。
	 * 2.循环取出所长与营业所关系。
	 * 3.检查是否已建立，不在就建立
	 * 4.将所长所属营业所下的客户与所长建立关系。
	 * </pre>
	 * 
	 * @throws SQLException
	 */
	public void createSZCustomerRel() throws SQLException {
		ArrayList sz_list = new ArrayList();
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;
		PreparedStatement pstmt4 = null;
		PreparedStatement pstmt4d = null;
		PreparedStatement pstmt5 = null;
		PreparedStatement pstmt5d = null;

		logger.info("取得所长与营业所关系!");
		Connection conn = getICustomerConnection();
		pstmt = conn.prepareStatement(sqlcmd1_a);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			String[] temp_emp = { rs.getString("BRANCH_id"),
					rs.getString("EMP_ID"), rs.getString("EMP_NAME"),
					rs.getString("COMPANY_SAP_ID") };
			sz_list.add(temp_emp);
		}
		rs.close();

		logger.info("sz_list.size" + sz_list.size());
		logger.info("建立所长与营业所客户关系!");
		pstmt2 = conn.prepareStatement(sqlcmd1_b);
		pstmt3 = conn.prepareStatement(sqlcmd1_c);
		pstmt4 = conn.prepareStatement(sqlcmd1_d);
		pstmt4d = conn.prepareStatement(sqlcmd1_d_del);
		pstmt5 = conn.prepareStatement(sqlcmd2_d);
		pstmt5d = conn.prepareStatement(sqlcmd2_d_del);

		HashMap existed_map = new HashMap();
		logger.info("建立所长与营业所的关系!");
		for (int i = 0; i < sz_list.size(); i++) {
			pstmt2.clearParameters();
			pstmt3.clearParameters();
			String[] temp_emp = (String[]) sz_list.get(i);
			createSZ(temp_emp[1], temp_emp[2]);
			if (!existed_map.containsValue(temp_emp[1])) {
				pstmt2.setString(1, temp_emp[1]);
				pstmt2.executeUpdate();
			}

			// logger.info(sqlcmd1_c);
			pstmt3.setString(1, temp_emp[1]);
			pstmt3.setString(2, temp_emp[1]);
			pstmt3.setString(3, temp_emp[0]);
			try {
				pstmt3.executeUpdate();
			} catch (Exception sqle) {
				throw new WantBatchException(sqle);
			}

			pstmt4.setString(1, temp_emp[0]);
			pstmt4.setString(2, temp_emp[1]);
			pstmt4d.setString(1, temp_emp[1]);
			try {
				pstmt4d.executeUpdate();
				pstmt4.executeUpdate();
			} catch (Exception sqle) {
				throw new WantBatchException(sqle);
			}

			pstmt5.setString(1, temp_emp[3]);
			pstmt5.setString(2, temp_emp[1]);
			pstmt5d.setString(1, temp_emp[1]);
			try {
				pstmt5d.executeUpdate();
				pstmt5.executeUpdate();
			} catch (Exception sqle) {
				throw new WantBatchException(sqle);
			}

			existed_map.put(temp_emp[1], temp_emp[1]);
		}
		logger.info("新建所长与客户关系完成");

		pstmt.close();
		pstmt2.close();
		pstmt3.close();
		pstmt4.close();
		pstmt4d.close();
		conn.close();
	}

	/**
	 * <pre>
	 * 建立总监与客户关系
	 * 建立步骤
	 * 1.取得总监与分公司关系。
	 * 2.循环取出总监与分公司关系。
	 * 3.检查是否已建立，不在就建立
	 * 4.将总监所属分公司下的客户与总监建立关系。
	 * </pre>
	 * 
	 * @throws SQLException
	 */
	public void createZJCustomerRel() throws SQLException {
		ArrayList xg_list = new ArrayList();
		ArrayList xg_list1 = new ArrayList();
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;
		PreparedStatement pstmt4 = null;
		PreparedStatement pstmt4d = null;
		PreparedStatement pstmt5 = null;
		PreparedStatement pstmt6 = null;
		PreparedStatement pstmt6d = null;
		ResultSet rs = null;

		logger.info("取得总监与分公司关系!");
		Connection conn = getICustomerConnection();
		
		// modify 2014-07-09 mandy 将pos_name like '%总监'修改成POS_TYPE_ID=’001‘
		pstmt = conn
				.prepareStatement("Select a.ORG_ID as ORG_ID, b.EMP_ID as EMP_ID, c.emp_name as EMP_NAME from POSITION_B a inner join EMP_POSITION b on a.pos_id=b.pos_id inner join EMP c on b.emp_id=c.emp_id  where a.POS_TYPE_ID = '" + RoleConstant.ZONGJIAN +"'");
		rs = pstmt.executeQuery();
		while (rs.next()) {
			String[] temp_emp = {
					CreateEMPCompanyRel.getCompanyId(rs.getString("ORG_ID"),
							"分公司", conn), rs.getString("EMP_ID"),
					rs.getString("EMP_NAME") };
			xg_list.add(temp_emp);
		}
		rs.close();

		logger.info("建立总监与分公司客户关系!");
		pstmt2 = conn.prepareStatement(sqlcmd2_b);
		pstmt3 = conn.prepareStatement(sqlcmd2_c);
		pstmt4 = conn.prepareStatement(sqlcmd2_d);
		pstmt4d = conn.prepareStatement(sqlcmd2_d_del);
		pstmt5 = conn.prepareStatement(sqlcmd2_e);
		pstmt6 = conn.prepareStatement(sqlcmd1_d);
		pstmt6d = conn.prepareStatement(sqlcmd1_d_del);

		HashMap existed_map = new HashMap();
		for (int i = 0; i < xg_list.size(); i++) {
			try {
				pstmt2.clearParameters();
				pstmt3.clearParameters();
				pstmt4.clearParameters();
				pstmt4d.clearParameters();
				pstmt5.clearParameters();
				pstmt6.clearParameters();
				pstmt6d.clearParameters();
				String[] temp_emp = (String[]) xg_list.get(i);
				createZJ(temp_emp[1], temp_emp[2], conn);
				if (!existed_map.containsValue(temp_emp[1])) {
					pstmt2.setString(1, temp_emp[1]);
					pstmt2.executeUpdate();
				}

				pstmt3.setString(1, temp_emp[1]);
				pstmt3.setString(2, temp_emp[1]);
				pstmt3.setString(3, temp_emp[0]);

				int a = pstmt3.executeUpdate();
				System.out
						.println("00010081".equals(temp_emp[1]) ? "***********************"
								+ temp_emp[1] + "-" + temp_emp[0]
								: "");
				logger.info("size:" + a);
				pstmt4.setString(1, temp_emp[0]);
				pstmt4.setString(2, temp_emp[1]);
				pstmt4d.setString(1, temp_emp[1]);
				pstmt4d.executeUpdate();
				pstmt4.executeUpdate();
				pstmt5.setString(1, temp_emp[0]);

				rs = pstmt5.executeQuery();
				while (rs.next()) {
					String[] temp_emp1 = { rs.getString("BRANCH_SAP_ID") };
					xg_list1.add(temp_emp1);
				}

				pstmt6d.setString(1, temp_emp[1]);

				// logger.info("开始删除Branch_emp中的指定数据："+temp_emp[1]);
				pstmt6d.executeUpdate();
				//logger.info(pstmt6d.getUpdateCount());
				// logger.info("删除成功！");

				// 新增该总监对应的最新的营业所
				for (int j = 0; j < xg_list1.size(); j++) {
					String[] temp_emp1 = (String[]) xg_list1.get(j);
					pstmt6.clearParameters();
					pstmt6.setString(1, temp_emp1[0]);
					pstmt6.setString(2, temp_emp[1]);

					try {
						pstmt6.executeUpdate();

					} catch (Exception sqle) {
						throw new WantBatchException(sqle);
					}
				}
				existed_map.put(temp_emp[1], temp_emp[1]);
				xg_list1.clear();

			} catch (Exception sqle) {
				throw new WantBatchException(sqle);
			}
		}

		createZJCompanyBranch();
		logger.info("新建总监与分公司关系完成");
		pstmt.close();
		pstmt2.close();
		pstmt3.close();
		pstmt4.close();
		pstmt4d.close();
		conn.close();
	}

	/**
	 * <pre>
	 * 建立总监帐号。
	 * 建立步骤。
	 * 1.检查帐号是否存在。
	 * 2.存在，则更新资料  。
	 * 3.不存在，则新增。
	 * 4.删除总监权限。
	 * 5.建立总监权限。
	 * </pre>
	 * 
	 * @param emp_id
	 *            String , 员工工号
	 * @param emp_name
	 *            String, 员工名称
	 * @param conn
	 *            Connection, 数据库连接
	 * @throws SQLException
	 */

	private void createZJ(String emp_id, String emp_name, Connection conn) throws SQLException {
		// 检查帐号是否存在
		PreparedStatement pstmt = conn.prepareStatement(sqlisexisted);
		pstmt.setString(1, emp_id);
		ResultSet rs = pstmt.executeQuery();
		boolean flag = rs.next();
		rs.close();
		pstmt.close();

		if (flag) {
			// 存在，则更新资料 sqlupdatexgdata
			pstmt = conn.prepareStatement(sqlupdatezjdata);
			pstmt.setString(1, emp_id);
			pstmt.executeUpdate();
			// logger.info("更新帐号完成:" + emp_id);
		} else {
			// 不存在，则新增 sqlcreatexg
			pstmt = conn.prepareStatement(sqlcreatezj);
			pstmt.setString(1, emp_id);
			pstmt.setString(2, emp_name);
			pstmt.executeUpdate();
			// logger.info("新建帐号完成:" + emp_id);
		}
		pstmt.close();
		// 删除总监权限
		pstmt = conn.prepareStatement(sqldeleteauth);
		pstmt.setString(1, emp_id);
		pstmt.executeUpdate();
		pstmt.close();
		// 建立总监权限
		pstmt = conn.prepareStatement(sqlcreatezjauth);
		pstmt.setString(1, emp_id);
		pstmt.executeUpdate();
		pstmt.close();

		//logger.info("新建总监权限完成:" + emp_id);
		rs.close();
		pstmt.close();
	}

	/**
	 * 建立所长帐号。 建立步骤。 1.检查帐号是否存在。 2.存在，则更新资料 。 3.不存在，则新增。 4.删除所长权限。 5.建立所长权限。
	 * </pre>
	 * 
	 * @param emp_id
	 *            String , 员工工号
	 * @param emp_name
	 *            String, 员工名称
	 * @param conn
	 *            Connection, 数据库连接
	 * @throws SQLException
	 */

	private void createSZ(String emp_id, String emp_name) throws SQLException {
		Connection conn = getICustomerConnection();
		// 检查帐号是否存在
		PreparedStatement pstmt = conn.prepareStatement(sqlisexisted);
		pstmt.setString(1, emp_id);
		ResultSet rs = pstmt.executeQuery();
		boolean flag = rs.next();
		rs.close();
		pstmt.close();

		if (flag) {
			// 存在，则更新资料 sqlupdatexgdata
			pstmt = conn.prepareStatement(sqlupdateszdata);
			pstmt.setString(1, emp_id);
			pstmt.executeUpdate();
			// logger.info("更新帐号完成:" + emp_id);
		} else {
			// 不存在，则新增 sqlcreatexg
			pstmt = conn.prepareStatement(sqlcreatesz);
			pstmt.setString(1, emp_id);
			pstmt.setString(2, emp_name);
			pstmt.executeUpdate();
			// logger.info("新建帐号完成:" + emp_id);
		}
		pstmt.close();

		// 删除所在权限
		pstmt = conn.prepareStatement(sqldeleteauth);
		pstmt.setString(1, emp_id);
		pstmt.executeUpdate();
		pstmt.close();

		// 建立所长权限
		pstmt = conn.prepareStatement(sqlcreateszauth);
		pstmt.setString(1, emp_id);
		pstmt.executeUpdate();
		// logger.info("新建权限完成:" + emp_id);
		rs.close();
		pstmt.close();
		conn.close();
	}

	// 建立总监/副总监 与分公司营业所关系
	public void createZJCompanyBranch() throws SQLException {

		// modify 2014-07-09 mandy 将pos_name like '%总监'修改成POS_TYPE_ID=’001‘
		String sqlGetZJ = "Select a.ORG_ID as ORG_ID,b.EMP_ID as EMP_ID from POSITION_B a inner join EMP_POSITION b "
				+ " on a.pos_id=b.pos_id where a.POS_TYPE_ID = '" + RoleConstant.ZONGJIAN + "'";
		// Adding by David on 2013/01/15 for update 总监/副总监 与分公司营业所关系
		// 建立总监/副总监与分公司关系
		// 取得总监/副总坚 org_id
		// 取得分公司营业所关系
		ArrayList list = new ArrayList();

		Connection conn = getICustomerConnection();
		PreparedStatement pstmt = conn.prepareStatement(sqlGetZJ);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			String[] tempdata = {
					rs.getString("EMP_ID"),
					CreateEMPCompanyRel.getCompanyId(rs.getString("ORG_ID"),
							"分公司", conn) };
			list.add(tempdata);
		}
		rs.close();
		pstmt.close();
		
		PreparedStatement pstmtDelete = conn.prepareStatement("delete BRANCH_EMP where EMP_ID=?");
		String sqlcmd_zj2 = "insert into BRANCH_EMP select distinct BRANCH_SAP_ID,? from COMPANY_BRANCH a "
				+ " inner join  COMPANY_SAP_HR_REL b on a.COMPANY_SAP_ID = b.COMPANY_SAP_ID where b.COMPANY_HR_ID=?";
		PreparedStatement pstmtInsert = conn.prepareStatement(sqlcmd_zj2);
		
		PreparedStatement pstmtDelCompanyEmp = conn.prepareStatement("delete COMPANY_EMP where EMP_ID=?");

		String sqlcmd_zj1 = "insert into COMPANY_EMP select distinct COMPANY_SAP_ID,?  from COMPANY_SAP_HR_REL where COMPANY_HR_ID=?";
		PreparedStatement pstmtInsertCompanyEmp = conn.prepareStatement(sqlcmd_zj1);
				
		for (int i = 0; i < list.size(); i++) {
			String[] tempdata = (String[]) list.get(i);
			pstmtDelete.clearParameters();
			pstmtInsert.clearParameters();
			pstmtDelCompanyEmp.clearParameters();
			pstmtInsertCompanyEmp.clearParameters();
			
			if (!"".equals(tempdata[1])) {
				// 删除营业所与总监旧关系
				pstmtDelete.setString(1, tempdata[0]);
				pstmtDelete.executeUpdate();

				// 建立营业所与总监关系
				pstmtInsert.setString(1, tempdata[0]);
				pstmtInsert.setString(2, tempdata[1]);
				pstmtInsert.executeUpdate();
				
				// 删除分公司与总监旧关系
				pstmtDelCompanyEmp.setString(1, tempdata[0]);
				pstmtDelCompanyEmp.executeUpdate();
				
				// 建立分公司与总监关系
				pstmtInsertCompanyEmp.setString(1, tempdata[0]);
				pstmtInsertCompanyEmp.setString(2, tempdata[1]);
				pstmtInsertCompanyEmp.executeUpdate();
			}
		}
		conn.close();
	}

	@Override
	public void execute() throws Exception {
		// TODO Auto-generated method stub

	}

}
