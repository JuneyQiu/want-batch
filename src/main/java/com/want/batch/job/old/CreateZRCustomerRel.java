package com.want.batch.job.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
public class CreateZRCustomerRel extends AbstractWantJob {
	
	protected final Log logger = LogFactory.getLog(CreateZRCustomerRel.class);
	
	public static void main(String[] args) {
		// CreateZRCustomerRel.createCQZRCustomerRel();
		// CreateZRCustomerRel.createXCZRCustomerRel();
		// CreateZRCustomerRel.syncZRDivisionRel();
	}

	// 检查帐号是否存在
	private static String sqlisexisted = " select 1 from user_info_tbl where account=?";
	// 更新主任帐号变量
	private static String sqlupdatezrdata = " update user_info_tbl set user_type_sid=5,status=1 where account=?";

	// 建立主任帐号
	private static String sqlcreatezr = " insert into user_info_tbl "
			+ " (sid,ACCOUNT,PASSWORD,USER_NAME,USER_TYPE_SID,EMAIL,MOBILE,AREA_CODE,PHONE_NUM,STATUS,UPDATE_DATE,UPDATOR,CREATE_DATE,CREATOR,LAST_LOGIN_TIME,LAST_LOGIN_ADDS,CHANNEL_SID)  "
			+ "  select USER_INFO_TBL_SID.nextval,?,USER_INFO_TBL_SID.currval,?,5,null,null,null,null,1,null,null,sysdate,'MIS_SYS',null,null,1  from dual ";

	// 删除权限
	private static String sqldeleteauth = " delete AUTH_USER_GROUP_REL  where user_sid= ( select sid from user_info_tbl where account = ? ) ";
	// 建立县城主任权限
	private static String sqlcreatexczrauth = " insert into AUTH_USER_GROUP_REL(USER_SID, USERGROUP_SID, CREATE_USER, CREATE_DATE)  select sid ,'161', 'mis', sysdate  from  USER_INFO_TBL where account = ? ";
	// 建立城区主任权限
	private static String sqlcreatecqzrauth = " insert into AUTH_USER_GROUP_REL(USER_SID, USERGROUP_SID, CREATE_USER, CREATE_DATE)  select sid ,'161', 'mis', sysdate  from  USER_INFO_TBL where account = ? ";

	// 查出县城主任;2014-07-09 mirabelle update pos_name-->pos_type_id
	private static String sqlcmd1_a = " select g.emp_id as EMP_ID,g.emp_name as EMP_NAME  from EMP_POSITION a  "
			+ "   inner join POSITION_B b on a.pos_id=b.pos_id  and  b.POS_TYPE_ID in ('" + RoleConstant.DIRECTOR1 +"' ,'"+RoleConstant.DIRECTOR2 +"' ,'"+RoleConstant.DIRECTOR3+  "','"+RoleConstant.DIRECTOR4+"')  and b.POS_PROPERTY_ID  IN ('A10') "
			+ "   inner join ORGANIZATION_B c on b.org_id=c.org_id  "
			+ "  inner join EMP g on a.emp_id=g.emp_id ";

	// 删除县城主任与所有客户关系
	private static String sqlcmd1_b = " delete CUSTOMER_USER_REL where USER_SID=(select sid from  USER_INFO_TBL where account = ?) ";
	// 建立县城主任与营业所客户关系
	private static String sqlcmd1_c = "insert into  CUSTOMER_USER_REL "
			+ " select distinct a.customer_sid as CUSTOMER_SID,d.sid as USER_SID,d.account as USER_ID  from CUSTOMER_USER_REL a   "
			+ "  inner join user_info_tbl b on a.user_sid=b.sid and b.status=1  "
			+ "  inner join   EMP_POSITION c on trim(b.account)=trim(c.EMP_ID)  "
			+ " inner join user_info_tbl d on trim(c.DIRECTOR_EMP_ID)=trim(d.account)  "
			+ "  where d.account=? "
			+ " union "
			+ "  select distinct (select sid from customer_info_tbl where id=a.customer_id) , b.sid,b.account   from DIVSION_SALES_CUSTOMER_REL a   "
			+ "  inner join user_info_tbl b on a.user_id=b.account where a.user_id=? ";

	// 查出城区主任;2014-07-08 mirabelle update pos_name为POS_TYPE_ID
	private static String sqlcmd2_a = " select g.emp_id as EMP_ID,g.emp_name as EMP_NAME  from EMP_POSITION a  "
			+ "   inner join POSITION_B b on a.pos_id=b.pos_id   and  b.POS_TYPE_ID in ('" + RoleConstant.DIRECTOR1 +"' ,'"+RoleConstant.DIRECTOR2 +"' ,'"+RoleConstant.DIRECTOR3+  "','"+RoleConstant.DIRECTOR4+"') and b.POS_PROPERTY_ID NOT IN ('A10','A09','A21')  "
			+ "  inner join ORGANIZATION_B c on b.org_id=c.org_id  "
			+ "  inner join EMP g on a.emp_id=g.emp_id ";
	// 删除城区主任与所有客户关系
	private static String sqlcmd2_b = " delete CUSTOMER_USER_REL where USER_SID=(select sid from  USER_INFO_TBL where account = ?) ";
	// 建立城区任与营业所客户关系
	private static String sqlcmd2_c = "insert into  CUSTOMER_USER_REL "
			+ " select distinct a.customer_sid as CUSTOMER_SID,d.sid as USER_SID,d.account as USER_ID  from CUSTOMER_USER_REL a   "
			+ "  inner join user_info_tbl b on a.user_sid=b.sid and b.status=1  "
			+ "  inner join   EMP_POSITION c on trim(b.account)=trim(c.EMP_ID)  "
			+ " inner join user_info_tbl d on trim(c.DIRECTOR_EMP_ID)=trim(d.account)  "
			+ "  where d.account=? "
			+ " union "
			+ "  select distinct (select sid from customer_info_tbl where id=a.customer_id) , b.sid,b.account   from DIVSION_SALES_CUSTOMER_REL a   "
			+ "  inner join user_info_tbl b on a.user_id=b.account where a.user_id=? ";
	// 建立主任事业部关系;2014-07-08 mirabelle update pos_name为POS_TYPE_ID
	private static String sqlcmd3_a = "select a.emp_id as EMP_ID,d.divsion_sid as DIVIVION_SID  from EMP_POSITION a   "
			+ " inner join POSITION_B b on a.pos_id=b.pos_id and b.POS_TYPE_ID in ('" + RoleConstant.DIRECTOR1 +"' ,'"+RoleConstant.DIRECTOR2 +"' ,'"+RoleConstant.DIRECTOR3+ "','"+RoleConstant.DIRECTOR4+"')"
			+ " inner join EMP_POSITION c on a.emp_id=c.DIRECTOR_EMP_ID  "
			+ " inner join (select USER_ID,DIVSION_SID from DIVSION_SALES_CUSTOMER_REL i inner join SALES_AREA_REL j on i.credit_id=j.credit_id group by  USER_ID,DIVSION_SID ) d   "
			+ " on c.emp_id=d.user_id group by  a.emp_id,d.divsion_sid  ";
	private static String sqlcmd3_b = "delete  USER_DIVISION where USER_ID=? ";
	private static String sqlcmd3_c = "INSERT INTO USER_DIVISION ( "
			+ "  USER_ID, DIVISION_ID, CREATE_USER, CREATE_DATE,UPDATE_USER, UPDATE_DATE) VALUES "
			+ " ( ?,?, 'MIS_SYS',SYSDATE, null, null) ";

	// Modify by David Luo on 2012/8/30 for update KA_ZR user_type_sid
	// 查找直营主任工号;2014-07-09 mirabelle update POS_NAME-->pos_type_id,POS_PROPERTY_NAME-->pos_property_id
	private static String sqlcmdKAZR_1 = "select g.emp_id as EMP_ID,g.emp_name as EMP_NAME  from EMP_POSITION a "
			+ " inner join POSITION_B b on a.pos_id=b.pos_id and b.POS_TYPE_ID in ('" + RoleConstant.DIRECTOR1 +"' ,'"+RoleConstant.DIRECTOR2 +"' ,'"+RoleConstant.DIRECTOR3+ "','"+RoleConstant.DIRECTOR4+"')"
			+ " and b.POS_PROPERTY_ID='A21' "
			+ " inner join ORGANIZATION_B c on b.org_id=c.org_id inner join EMP g on a.emp_id=g.emp_id ";

	// 更新直营主任user_type_sid
	private static String sqlcmdKAZR_2 = " update USER_INFO_TBL set USER_TYPE_SID = 31 where ACCOUNT=? ";

	public void createXCZRCustomerRel() throws SQLException {
		Connection conn = getICustomerConnection();
		ArrayList xg_list = new ArrayList();
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;
		ResultSet rs = null;

		logger.info("取得县城主任清单");
		pstmt = conn.prepareStatement(sqlcmd1_a);
		rs = pstmt.executeQuery();
		while (rs.next()) {
			String[] temp_emp = { rs.getString("EMP_ID"),
					rs.getString("EMP_NAME") };
			xg_list.add(temp_emp);
		}
		logger.info("建立县城主任与营业所客户关系!");
		pstmt2 = conn.prepareStatement(sqlcmd1_b);
		pstmt3 = conn.prepareStatement(sqlcmd1_c);
		HashMap existed_map = new HashMap();
		for (int i = 0; i < xg_list.size(); i++) {
			pstmt2.clearParameters();
			pstmt3.clearParameters();
			String[] temp_emp = (String[]) xg_list.get(i);
			// 重整销管帐号
			createXCZR(temp_emp[0], temp_emp[1], conn);
			// logger.info("temp_emp:"+temp_emp[0]+"-"+temp_emp[1]+"-"+temp_emp[2]);
			if (!existed_map.containsValue(temp_emp[0])) {
				pstmt2.setString(1, temp_emp[0]);
				pstmt2.executeUpdate();
			}
			pstmt3.setString(1, temp_emp[0]);
			pstmt3.setString(2, temp_emp[0]);
			try {
				pstmt3.executeUpdate();
			} catch (Exception sqle) {
				sqle.printStackTrace();
				logger.info("主任异常:" + temp_emp[0]);
				throw new WantBatchException(sqle);
			}
			existed_map.put(temp_emp[0], temp_emp[0]);
		}
		
		
		//单独同步客户主任的渠道 --冀
		String delkehuzhuren = " delete from USER_DIVISION a where a.USER_ID in ( "
						+	" select distinct a.emp_id "
						+	" from EMP_POSITION_A a inner join POSITION_B b on a.pos_id = b.pos_id and b.POS_TYPE_ID = '013' "
						+	" inner join SALES_AREA_REL c on b.POS_PROPERTY_ID = c.POS_PROPERTY_ID )";
		pstmt2 = conn.prepareStatement(delkehuzhuren);
		pstmt2.clearParameters();
		pstmt2.executeUpdate();
		String insertkehuzhuren = " INSERT INTO USER_DIVISION "
				+	" select distinct a.emp_id ,c.DIVSION_SID,'MIS_SYS',sysdate,null,null "
				+	" from EMP_POSITION_A a inner join POSITION_B b on a.pos_id = b.pos_id and b.POS_TYPE_ID = '013' "
				+	" inner join SALES_AREA_REL c on b.POS_PROPERTY_ID = c.POS_PROPERTY_ID ";
		pstmt2 = conn.prepareStatement(insertkehuzhuren);
		pstmt2.clearParameters();
		pstmt2.executeUpdate();
		
		
		logger.info("新建完成");

		rs.close();
		pstmt.close();
		pstmt2.close();
		pstmt3.close();
		conn.close();
	}

	public void createCQZRCustomerRel() throws SQLException {
		ArrayList xg_list = new ArrayList();

		logger.info("取得城区主任清单!");
		Connection conn = getICustomerConnection();
		PreparedStatement pstmt = conn.prepareStatement(sqlcmd2_a);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			String[] temp_emp = { rs.getString("EMP_ID"),
					rs.getString("EMP_NAME") };
			xg_list.add(temp_emp);
		}
		rs.close();
		pstmt.close();
		
		logger.info("建立城区主任与营业所客户关系!");
		PreparedStatement pstmt2 = conn.prepareStatement(sqlcmd2_b);
		PreparedStatement pstmt3 = conn.prepareStatement(sqlcmd2_c);
		HashMap existed_map = new HashMap();
		for (int i = 0; i < xg_list.size(); i++) {
			pstmt2.clearParameters();
			pstmt3.clearParameters();
			String[] temp_emp = (String[]) xg_list.get(i);
			// 重整销管帐号
			createCQZR(temp_emp[0], temp_emp[1], conn);
			if (!existed_map.containsValue(temp_emp[0])) {
				pstmt2.setString(1, temp_emp[0]);
				pstmt2.executeUpdate();
			}
			pstmt3.setString(1, temp_emp[0]);
			pstmt3.setString(2, temp_emp[0]);
			try {
				pstmt3.executeUpdate();
			} catch (Exception sqle) {
				sqle.printStackTrace();
				logger.info("主任异常:" + temp_emp[0] + ", " + sqle.getMessage());
				throw new WantBatchException(sqle);
			}

			existed_map.put(temp_emp[0], temp_emp[0]);
		}

		logger.info("新建完成");
		pstmt2.close();
		pstmt3.close();
		conn.close();
	}

	private void createXCZR(String emp_id, String emp_name, Connection conn) throws SQLException {
		// 检查帐号是否存在
		PreparedStatement pstmt = conn.prepareStatement(sqlisexisted);
		pstmt.setString(1, emp_id);
		ResultSet rs = pstmt.executeQuery();
		boolean flag = rs.next();
		rs.close();
		pstmt.close();

		if (flag) {
			// 存在，则更新资料 sqlupdatexgdata
			pstmt = conn.prepareStatement(sqlupdatezrdata);
			pstmt.setString(1, emp_id);
			pstmt.executeUpdate();
			// logger.info("更新帐号完成:" + emp_id);
		} else {
			// 不存在，则新增 sqlcreatexg
			pstmt = conn.prepareStatement(sqlcreatezr);
			pstmt.setString(1, emp_id);
			pstmt.setString(2, emp_name);
			pstmt.executeUpdate();
			// logger.info("新建帐号完成:" + emp_id);
		}
		pstmt.close();

		// 删除销管权限
		pstmt = conn.prepareStatement(sqldeleteauth);
		pstmt.setString(1, emp_id);
		pstmt.executeUpdate();
		pstmt.close();
		// 建立销管权限
		pstmt = conn.prepareStatement(sqlcreatexczrauth);
		pstmt.setString(1, emp_id);
		pstmt.executeUpdate();
		//logger.info("新建县城主任权限完成:" + emp_id);
		rs.close();
		pstmt.close();
	}

	private void createCQZR(String emp_id, String emp_name, Connection conn) throws SQLException {
		// 检查帐号是否存在
		PreparedStatement pstmt = conn.prepareStatement(sqlisexisted);
		pstmt.setString(1, emp_id);
		ResultSet rs = pstmt.executeQuery();
		boolean flag = rs.next();
		rs.close();
		pstmt.close();

		if (flag) {
			// 存在，则更新资料 sqlupdatexgdata
			pstmt = conn.prepareStatement(sqlupdatezrdata);
			pstmt.setString(1, emp_id);
			pstmt.executeUpdate();
			// logger.info("更新帐号完成:" + emp_id);
		} else {
			// 不存在，则新增 sqlcreatexg
			pstmt = conn.prepareStatement(sqlcreatezr);
			pstmt.setString(1, emp_id);
			pstmt.setString(2, emp_name);
			pstmt.executeUpdate();
			// logger.info("新建帐号完成:" + emp_id);
		}
		pstmt.close();

		// 删除销管权限
		pstmt = conn.prepareStatement(sqldeleteauth);
		pstmt.setString(1, emp_id);
		pstmt.executeUpdate();
		pstmt.close();

		// 建立销管权限
		pstmt = conn.prepareStatement(sqlcreatecqzrauth);
		pstmt.setString(1, emp_id);
		pstmt.executeUpdate();
		// logger.info("新建城区主任权限完成:" + emp_id);
		pstmt.close();
	}

	public void syncZRDivisionRel() throws SQLException {
		ArrayList xg_list = new ArrayList();
		logger.info("取得城区主任事业部清单!");
		Connection conn = getICustomerConnection();
		PreparedStatement pstmt = conn.prepareStatement(sqlcmd3_a);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			String[] temp_emp = { rs.getString("EMP_ID"),
					rs.getString("DIVIVION_SID") };
			xg_list.add(temp_emp);
		}
		rs.close();
		pstmt.close();

		logger.info("建立城区主任与事业部关系!");
		PreparedStatement pstmt2 = conn.prepareStatement(sqlcmd3_b);
		PreparedStatement pstmt3 = conn.prepareStatement(sqlcmd3_c);
		HashMap existed_map = new HashMap();
		for(int i = 0;i < xg_list.size();i++){
			pstmt2.clearParameters();
			String[] temp_emp = (String[]) xg_list.get(i);
			try {
				pstmt2.setString(1, temp_emp[0]);
				pstmt2.executeUpdate();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.info("主任异常:" + temp_emp[0]);
				throw new WantBatchException(e);
			}
		}
		for (int i = 0; i < xg_list.size(); i++) {
//			pstmt2.clearParameters();
			pstmt3.clearParameters();
			String[] temp_emp = (String[]) xg_list.get(i);

			try {
//				pstmt2.setString(1, temp_emp[0]);
//				pstmt2.executeUpdate();

				pstmt3.setString(1, temp_emp[0]);
				pstmt3.setString(2, temp_emp[1]);
				pstmt3.executeUpdate();
			} catch (Exception sqle) {
				// sqle.printStackTrace();
				logger.info("主任异常:" + temp_emp[0]);
				throw new WantBatchException(sqle);
			}

			existed_map.put(temp_emp[0], temp_emp[0]);
		}

		pstmt2.close();
		pstmt3.close();
		conn.close();
	}

	// 更新直营客户主任user_type_sid
	// //Modify by David Luo on 2012/8/30 for update KA_ZR user_type_sid
	public void updateKAZRUserTypeSid() throws SQLException {
		ArrayList kazr_list = new ArrayList();
		logger.info("取得直营主任清单!");
		Connection conn = getICustomerConnection();
		PreparedStatement pstmt = conn.prepareStatement(sqlcmdKAZR_1);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			String[] temp_emp = { rs.getString("EMP_ID"),
					rs.getString("EMP_NAME") };
			kazr_list.add(temp_emp);
		}
		rs.close();
		logger.info("建立城区主任与事业部关系!");
		PreparedStatement pstmt2 = conn.prepareStatement(sqlcmdKAZR_2);
		for (int i = 0; i < kazr_list.size(); i++) {
			pstmt2.clearParameters();
			String[] temp_emp = (String[]) kazr_list.get(i);

			try {
				pstmt2.setString(1, temp_emp[0]);
				pstmt2.executeUpdate();
			} catch (Exception sqle) {
				// sqle.printStackTrace();
				logger.info("主任异常:" + temp_emp[0]);
				throw new WantBatchException(sqle);
			}
		}

		logger.info("更新完成");
		rs.close();
		pstmt.close();
		pstmt2.close();
		conn.close();
	}

	@Override
	public void execute() throws Exception {
		// TODO Auto-generated method stub
	
	}
}
