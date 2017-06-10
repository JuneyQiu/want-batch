package com.want.batch.job.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.want.batch.WantBatchException;
import com.want.batch.job.AbstractWantJob;

@Component
public class SyncKAOrg extends AbstractWantJob {
	public SyncKAOrg() {
	}

	public static void main(String[] args) {
		// SyncKAOrg.synckaorgkhzg();
		// SyncKAOrg.synckaorgzy();
		// SyncZRFC19.createEmpCustomerRel(); //同步业代与客户关系
		// SyncKAOrg.synckaorgzy_pf();
	}

	/**
	 * 同步批发专员资料
	 */
	public void synckaorgzy_pf() {
		ArrayList<String[]> emp_list = new ArrayList<String[]>();
		Connection conn = getICustomerConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn.setAutoCommit(false);
			// 取得批发专员资料;2014-07-08 mirabelle update post_name like '%专员%'-->pos_type_id=006 add 2015-03-30 mandy 省专员010
			String sqlcmd1 = " select f.company_sap_id ,a.emp_id,g.emp_name,b.POS_PROPERTY_ID from EMP_POSITION a "
					+ " inner join POSITION_B b on a.pos_id=b.pos_id  "
					+ " inner join ORGANIZATION_B c on b.org_id=c.org_id  and  (POS_TYPE_ID = '" + RoleConstant.ZHUANYUAN + "' OR POS_TYPE_ID = '" + RoleConstant.SHENGZHUANYUAN +"' OR POS_TYPE_ID = '" + RoleConstant.SHENGJINGLI + "') and POS_PROPERTY_ID <> 'A09' and POS_PROPERTY_ID <> 'A21' "
					+ " inner join ORGANIZATION_B d on c.ORG_PARENT_DEPT=d.org_id   "
					+ " inner join  (select company_hr_id,company_sap_id from COMPANY_BRANCH group by company_hr_id,company_sap_id )  f on d.org_id=f.company_hr_id "
					+ " inner join emp g on a.emp_id=g.emp_id "
					+ " inner join SALES_AREA_REL z on b.POS_PROPERTY_ID=z.POS_PROPERTY_ID  and z.STATUS = '1' ";
			pstmt = conn.prepareStatement(sqlcmd1);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String data[] = { rs.getString("company_sap_id"),// 0
						rs.getString("emp_id"), // 1
						rs.getString("emp_name"),// 2
						rs.getString("POS_PROPERTY_ID") }; // 3
				emp_list.add(data);
			}
			rs.close();
			pstmt.close();
			// 2014-07-08 mirabelle update post_name like '%专员%'-->pos_type_id=006 add 2015-03-30 mandy 省专员010
			String sqlcmd1_1 = " select f.company_sap_id ,a.emp_id,g.emp_name,b.POS_PROPERTY_ID from EMP_POSITION a "
					+ " inner join POSITION_B b on a.pos_id=b.pos_id  "
					+ " inner join ORGANIZATION_B c on b.org_id=c.org_id  and  (POS_TYPE_ID = '" + RoleConstant.ZHUANYUAN + "' OR POS_TYPE_ID = '" + RoleConstant.SHENGZHUANYUAN + "' OR POS_TYPE_ID = '" + RoleConstant.SHENGJINGLI + "') and POS_PROPERTY_ID <> 'A09' and POS_PROPERTY_ID <> 'A21'  "
					+ " inner join ORGANIZATION_B d on c.ORG_PARENT_DEPT=d.org_id   "
					+ " inner join ORGANIZATION_B x on d.ORG_PARENT_DEPT=x.org_id   "
					+ " inner join  (select company_hr_id,company_sap_id from COMPANY_BRANCH group by company_hr_id,company_sap_id )  f on x.org_id=f.company_hr_id "
					+ " inner join emp g on a.emp_id=g.emp_id "
					+ " inner join SALES_AREA_REL z on b.POS_PROPERTY_ID=z.POS_PROPERTY_ID and STATUS = '1' ";
			pstmt = conn.prepareStatement(sqlcmd1_1);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String data[] = { rs.getString("company_sap_id"),// 0
						rs.getString("emp_id"), // 1
						rs.getString("emp_name"),// 2
						rs.getString("POS_PROPERTY_ID") }; // 3
				emp_list.add(data);
			}
			rs.close();
			pstmt.close();

			/** 开始开通权限 **/
			String sql = "";
			for (int i = 0; i < emp_list.size(); i++) {
				String data[] = (String[]) emp_list.get(i);
				logger.info(data[0] + "|" + data[1] + "|" + data[2]
						+ "|" + data[3]);
				/** 0.开通账号 **/
				sql = "select 1 from USER_INFO_TBL where ACCOUNT=?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, data[1]);
				rs = pstmt.executeQuery();
				boolean flag = rs.next();
				rs.close();
				pstmt.close();
				if (flag) {
					sql = "update USER_INFO_TBL set user_type_sid=7,update_date=sysdate, updator='MIS_SYS' where ACCOUNT=?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, data[1]);
					pstmt.executeUpdate();
					pstmt.close();
				} else {
					sql = "insert into user_info_tbl "
							+ " (sid,ACCOUNT,PASSWORD,USER_NAME,USER_TYPE_SID,EMAIL,MOBILE,AREA_CODE,PHONE_NUM,STATUS,UPDATE_DATE,UPDATOR,CREATE_DATE,CREATOR,LAST_LOGIN_TIME,LAST_LOGIN_ADDS,CHANNEL_SID)  "
							+ " select USER_INFO_TBL_SID.nextval,?,USER_INFO_TBL_SID.currval,?,7,null,null,null,null,1,null,null,sysdate,'MIS_SYS',null,null,2  from dual";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, data[1]);
					pstmt.setString(2, data[2]);
					pstmt.executeUpdate();
					pstmt.close();
				}

				/** 1.专员与客户的关系 **/
				sql = "delete CUSTOMER_USER_REL where user_id=? ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, data[1]);
				pstmt.executeUpdate();
				pstmt.close();
				sql = " insert into CUSTOMER_USER_REL ("
						+ " select distinct d.sid,(select SID from USER_INFO_TBL where ACCOUNT = ?),? "
						+ " from COMPANY_BRANCH_VIEW a "
						+ " inner join CUSTOMER_BRANCH_TBL b on a.BRANCH_ID=b.BRANCH_ID "
						+ " inner join CUSTOMER_DIVISION_REL c on b.CUSTOMER_ID = c.CUSTOMER_ID "
						+ " inner join customer_info_tbl d on b.CUSTOMER_ID=d.id "
						+ " left join SALES_AREA_REL e on c.DIVISION_SID = e.DIVSION_SID "
						+ " where a.COMPANY_ID = ? and e.POS_PROPERTY_ID = ? and e.STATUS = '1') ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, data[1]);
				pstmt.setString(2, data[1]);
				pstmt.setString(3, data[0]);
				pstmt.setString(4, data[3]);
				pstmt.executeUpdate();
				pstmt.close();

				/** 2.添加专员与分公司 **/
				sql = " delete from COMPANY_EMP where EMP_ID = ? ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, data[1]);
				pstmt.executeUpdate();
				pstmt.close();
				sql = "insert into COMPANY_EMP  select ?,? from  dual ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, data[0]);
				pstmt.setString(2, data[1]);
				pstmt.executeUpdate();
				pstmt.close();
				/** 3.添加专员与营业所 **/
				sql = " delete from BRANCH_EMP where EMP_ID = ? ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, data[1]);
				pstmt.executeUpdate();
				pstmt.close();
				sql = " insert into BRANCH_EMP (select distinct BRANCH_SAP_ID,b.EMP_ID from COMPANY_BRANCH a,COMPANY_EMP b where b.COMPANY_SAP_ID = a.COMPANY_SAP_ID and b.EMP_ID = ?)";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, data[1]);
				pstmt.executeUpdate();
				pstmt.close();
				/** 4.USER_DIVISION **/
				sql = " delete from USER_DIVISION where USER_ID = ? ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, data[1]);
				pstmt.executeUpdate();
				pstmt.close();
				sql = " insert into USER_DIVISION  values(?,(select DIVSION_SID from SALES_AREA_REL where POS_PROPERTY_ID =? and STATUS = '1'),'MIS-SYS',SYSDATE,null,null)";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, data[1]);
				pstmt.setString(2, data[3]);
				pstmt.executeUpdate();
				pstmt.close();
				/** 5.添加专员的群组 **/
				sql = " delete from AUTH_USER_GROUP_REL where USER_SID = (select SID from USER_INFO_TBL where ACCOUNT = ?) ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, data[1]);
				pstmt.executeUpdate();
				pstmt.close();
				sql = " insert into AUTH_USER_GROUP_REL values((select SID from USER_INFO_TBL where ACCOUNT = ?),185,'mis',sysdate)";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, data[1]);
				pstmt.executeUpdate();
				pstmt.close();
				sql = " insert into AUTH_USER_GROUP_REL values((select SID from USER_INFO_TBL where ACCOUNT = ?),16,'mis',sysdate)";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, data[1]);
				pstmt.executeUpdate();
				pstmt.close();
				/** 6.添加专员与AUTH_RP_YP **/
				sql = " delete from AUTH_RP_YP where USERID = ? ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, data[1]);
				pstmt.executeUpdate();
				pstmt.close();
				sql = " insert into AUTH_RP_YP values('ZY',?,?,(select CITY from COMPANY_INFO_TBL where COMPANY_ID = ?),(select DIVSION_SID from SALES_AREA_REL where POS_PROPERTY_ID =? and STATUS = '1'),?)";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, data[2]);
				pstmt.setString(2, data[1]);
				pstmt.setString(3, data[0]);
				pstmt.setString(4, data[3]);
				pstmt.setString(5, data[0]);
				pstmt.executeUpdate();
				pstmt.close();
				conn.commit();
				conn.setAutoCommit(true);
			}

		} catch (Exception s) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			s.printStackTrace();
			throw new WantBatchException(s);
		} finally {
			try {
				rs.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		}

	}

	public void synckaorgzy() throws SQLException {
		ArrayList emp_list = new ArrayList();
		Connection conn = getICustomerConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		// 2014-07-08 mirabelle update pos_name为pos_type_id add 2015-03-30 mandy 省专员010
		String sqlcmd1 = " select f.company_sap_id ,a.emp_id,g.emp_name from EMP_POSITION a "
				+ " inner join POSITION_B b on a.pos_id=b.pos_id  "
				+ "   inner join ORGANIZATION_B c on b.org_id=c.org_id  and  (POS_TYPE_ID='" + RoleConstant.ZHUANYUAN + "' OR POS_TYPE_ID='" + RoleConstant.SHENGZHUANYUAN + "')  and POS_PROPERTY_ID='A09' "
				+ " inner join ORGANIZATION_B d on c.ORG_PARENT_DEPT=d.org_id   "
				+ " inner join  (select company_hr_id,company_sap_id from COMPANY_BRANCH group by company_hr_id,company_sap_id )  f on d.org_id=f.company_hr_id "
				+ " inner join emp g on a.emp_id=g.emp_id";
		// 2014-07-08 mirabelle update pos_name为pos_type_id add 2015-03-30 mandy 省专员010
		String sqlcmd1_1 = " select f.company_sap_id ,a.emp_id,g.emp_name from EMP_POSITION a "
				+ " inner join POSITION_B b on a.pos_id=b.pos_id  "
				+ "   inner join ORGANIZATION_B c on b.org_id=c.org_id  and  (POS_TYPE_ID='" + RoleConstant.ZHUANYUAN + "' OR POS_TYPE_ID='" + RoleConstant.SHENGZHUANYUAN + "')  and POS_PROPERTY_ID='A09' "
				+ " inner join ORGANIZATION_B d on c.ORG_PARENT_DEPT=d.org_id   "
				+ " inner join ORGANIZATION_B x on d.ORG_PARENT_DEPT=x.org_id   "
				+ " inner join  (select company_hr_id,company_sap_id from COMPANY_BRANCH group by company_hr_id,company_sap_id )  f on x.org_id=f.company_hr_id "
				+ " inner join emp g on a.emp_id=g.emp_id";

		pstmt = conn.prepareStatement(sqlcmd1);
		rs = pstmt.executeQuery();
		while (rs.next()) {
			String data[] = { rs.getString("company_sap_id"), // 0
					rs.getString("emp_id"), // 1
					rs.getString("emp_name") }; // 2
			emp_list.add(data);
		}
		rs.close();
		pstmt.close();

		pstmt = conn.prepareStatement(sqlcmd1_1);
		rs = pstmt.executeQuery();
		while (rs.next()) {
			String data[] = { rs.getString("company_sap_id"), // 0
					rs.getString("emp_id"), // 1
					rs.getString("emp_name") }; // 2
			emp_list.add(data);
		}
		rs.close();
		pstmt.close();

		// logger.info("emp_list:"+emp_list.size());
		// / a.建立帐号 先检查帐号，存在则更新，不存在则建立
		for (int i = 0; i < emp_list.size(); i++) {
			String data[] = (String[]) emp_list.get(i);
			// logger.info("data[1] :" + data[1]);
			String sqlcmd2 = "select 1 from USER_INFO_TBL where ACCOUNT=?";
			pstmt = conn.prepareStatement(sqlcmd2);
			pstmt.setString(1, data[1]);
			rs = pstmt.executeQuery();
			boolean flag = rs.next();

			// logger.info("flag :" + flag);
			rs.close();
			pstmt.close();
			if (flag) {
				String sqlcmd4 = "update USER_INFO_TBL set user_type_sid=30,update_date=sysdate, updator='MIS_SYS' where ACCOUNT=?";
				pstmt = conn.prepareStatement(sqlcmd4);
				pstmt.setString(1, data[1]);
				pstmt.executeUpdate();
				pstmt.close();
			} else {
				String sqlcmd3 = "insert into user_info_tbl "
						+ " (sid,ACCOUNT,PASSWORD,USER_NAME,USER_TYPE_SID,EMAIL,MOBILE,AREA_CODE,PHONE_NUM,STATUS,UPDATE_DATE,UPDATOR,CREATE_DATE,CREATOR,LAST_LOGIN_TIME,LAST_LOGIN_ADDS,CHANNEL_SID)  "
						+ " select USER_INFO_TBL_SID.nextval,?,USER_INFO_TBL_SID.currval,?,30,null,null,null,null,1,null,null,sysdate,'MIS_SYS',null,null,2  from dual";
				pstmt = conn.prepareStatement(sqlcmd3);
				// logger.info("flag :" + flag);
				pstmt.setString(1, data[1]);
				pstmt.setString(2, data[2]);
				pstmt.executeUpdate();
				pstmt.close();
			}

			// b.建立权限 删除后建立
			String sqlcmd5 = "delete AUTH_USER_GROUP_REL where user_sid=(select sid from user_info_tbl where account=?) ";
			pstmt = conn.prepareStatement(sqlcmd5);
			pstmt.setString(1, data[1]);
			pstmt.executeUpdate();
			pstmt.close();
			String sqlcmd6 = "insert into AUTH_USER_GROUP_REL(USER_SID, USERGROUP_SID, CREATE_USER, CREATE_DATE) select sid ,2001, 'mis', sysdate  from  USER_INFO_TBL where account=?";
			pstmt = conn.prepareStatement(sqlcmd6);
			pstmt.setString(1, data[1]);
			pstmt.executeUpdate();
			pstmt.close();

			// c.建立user_division 删除后建立 division_sid=24
			String sqlcmd7 = "delete USER_DIVISION where user_id=? ";
			pstmt = conn.prepareStatement(sqlcmd7);
			pstmt.setString(1, data[1]);
			pstmt.executeUpdate();
			pstmt.close();
			String sqlcmd8 = "insert into USER_DIVISION select ?,24,'MIS_SYS',sysdate,null,null from  dual ";
			pstmt = conn.prepareStatement(sqlcmd8);
			pstmt.setString(1, data[1]);
			pstmt.executeUpdate();
			pstmt.close();

			// d.建立company_emp 删除后建立
			String sqlcmd9 = "delete COMPANY_EMP where EMP_ID=? ";
			pstmt = conn.prepareStatement(sqlcmd9);
			pstmt.setString(1, data[1]);
			pstmt.executeUpdate();
			pstmt.close();

			String sqlcmd10 = "insert into COMPANY_EMP  select ?,? from  dual ";
			pstmt = conn.prepareStatement(sqlcmd10);
			pstmt.setString(1, data[0]);
			pstmt.setString(2, data[1]);
			pstmt.executeUpdate();
			pstmt.close();
			// 建立客户主管权限
			String sqlcmd11 = " delete  AUTH_USER_GROUP_REL  where USER_SID in (select sid from  USER_INFO_TBL where account = ? )";
			pstmt = conn.prepareStatement(sqlcmd11);
			pstmt.setString(1, data[1]);
			pstmt.executeUpdate();
			pstmt.close();

			String sqlcmd12 = " insert into AUTH_USER_GROUP_REL(USER_SID, USERGROUP_SID, CREATE_USER, CREATE_DATE)  select sid ,'3002', 'mis', sysdate  from  USER_INFO_TBL where account = ? ";
			pstmt = conn.prepareStatement(sqlcmd12);
			pstmt.setString(1, data[1]);
			pstmt.executeUpdate();
			pstmt.close();

			String sqlcmd223 = "delete CUSTOMER_USER_REL where user_id=? ";
			pstmt = conn.prepareStatement(sqlcmd223);
			pstmt.setString(1, data[1]);
			pstmt.executeUpdate();
			pstmt.close();

			// 解决专员代客户主管填写问题，孙建
			String sqlcmd122 = " insert into CUSTOMER_USER_REL (select distinct * from (select c.customer_sid,(select sid from user_info_tbl where account=a.zy),a.zy "
					+ " from PERSON_LEVEL a inner join user_info_tbl b on a.KHZG = b.account inner join CUSTOMER_USER_REL c on b.sid=c.user_sid "
					+ " where a.ZY= ? group by CUSTOMER_SID,ZY "
					+ " union all "
					+ " select b.SID ,c.SID ,a.USER_ID "
					+ " from DIVSION_SALES_CUSTOMER_REL a "
					+ " inner join CUSTOMER_INFO_TBL b on a.CUSTOMER_ID = b.ID "
					+ " inner join USER_INFO_TBL c on a.USER_ID = c.ACCOUNT "
					+ " where a.USER_ID = ?))";

			pstmt = conn.prepareStatement(sqlcmd122);
			pstmt.setString(1, data[1]);
			pstmt.setString(2, data[1]);
			pstmt.executeUpdate();
			pstmt.close();
		}
		pstmt.close();
		conn.close();
	}

	public void synckaorgkhzg() throws SQLException {
		ArrayList emp_list = new ArrayList();
		Connection conn = getICustomerConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		// 取得直营专员资料;2014-07-09 mirabelle update POS_NAME改为POS_TYPE_ID
		String sqlcmd1 = "select f.company_sap_id ,a.emp_id,g.emp_name from EMP_POSITION a "
				+ " inner join POSITION_B b on a.pos_id=b.pos_id  "
				+ "   inner join ORGANIZATION_B c on b.org_id=c.org_id  and  ( POS_TYPE_ID in ('" + RoleConstant.DIRECTOR1 +"' ,'"+RoleConstant.DIRECTOR2 +"' ,'"+RoleConstant.DIRECTOR3+  "','"+RoleConstant.DIRECTOR4+"') ) and POS_PROPERTY_ID='A09' "
				+ " inner join ORGANIZATION_B d on c.ORG_PARENT_DEPT=d.org_id   "
				+ " inner join  (select company_hr_id,company_sap_id from COMPANY_BRANCH group by company_hr_id,company_sap_id )  f on d.org_id=f.company_hr_id "
				+ " inner join emp g on a.emp_id=g.emp_id";
		// 2014-07-09 mirabelle update POS_NAME改为POS_TYPE_ID
		String sqlcmd1_1 = "select f.company_sap_id ,a.emp_id,g.emp_name from EMP_POSITION a "
				+ "  inner join POSITION_B b on a.pos_id=b.pos_id  "
				+ "   inner join ORGANIZATION_B c on b.org_id=c.org_id  and  ( POS_TYPE_ID in ('" + RoleConstant.DIRECTOR1 +"' ,'"+RoleConstant.DIRECTOR2 +"' ,'"+RoleConstant.DIRECTOR3+ "','"+RoleConstant.DIRECTOR4+"') )   and POS_PROPERTY_ID='A09' "
				+ " inner join ORGANIZATION_B d on c.ORG_PARENT_DEPT=d.org_id  "
				+ " inner join ORGANIZATION_B q on d.ORG_PARENT_DEPT=q.org_id   "
				+ "  inner join  (select company_hr_id,company_sap_id from COMPANY_BRANCH group by company_hr_id,company_sap_id )  f on q.org_id=f.company_hr_id "
				+ " inner join emp g on a.emp_id=g.emp_id";
		// 2014-07-09 mirabelle update POS_NAME改为POS_TYPE_ID
		String sqlcmd_2 = "select f.company_sap_id ,a.emp_id,g.emp_name from EMP_POSITION a  "
				+ "      inner join POSITION_B b on a.pos_id=b.pos_id    "
				+ "     inner join ORGANIZATION_B c on b.org_id=c.org_id  and  ( POS_TYPE_ID in ('" + RoleConstant.DIRECTOR1 +"' ,'"+RoleConstant.DIRECTOR2 +"' ,'"+RoleConstant.DIRECTOR3+ "','"+RoleConstant.DIRECTOR4+"'))   and POS_PROPERTY_ID='A09'   "
				+ "      inner join ORGANIZATION_B d on c.ORG_PARENT_DEPT=d.org_id    "
				+ "      inner join ORGANIZATION_B q on d.ORG_PARENT_DEPT=q.org_id   "
				+ "       inner join ORGANIZATION_B z on q.ORG_PARENT_DEPT=z.org_id   "
				+ "        inner join  (select company_hr_id,company_sap_id from COMPANY_BRANCH group by company_hr_id,company_sap_id )  f on z.org_id=f.company_hr_id  "
				+ "       inner join emp g on a.emp_id=g.emp_id ";

		pstmt = conn.prepareStatement(sqlcmd1);
		rs = pstmt.executeQuery();
		while (rs.next()) {
			String data[] = { rs.getString("company_sap_id"), // 0
					rs.getString("emp_id"), // 1
					rs.getString("emp_name") }; // 2
			emp_list.add(data);
		}
		rs.close();
		pstmt.close();

		pstmt = conn.prepareStatement(sqlcmd1_1);
		rs = pstmt.executeQuery();
		while (rs.next()) {
			String data[] = { rs.getString("company_sap_id"), // 0
					rs.getString("emp_id"), // 1
					rs.getString("emp_name") }; // 2
			emp_list.add(data);
		}
		rs.close();
		pstmt.close();

		// lidd 0813
		/******** 北京层级关系多一层 START ********/
		pstmt = conn.prepareStatement(sqlcmd_2);
		rs = pstmt.executeQuery();
		while (rs.next()) {
			String data[] = { rs.getString("company_sap_id"), // 0
					rs.getString("emp_id"), // 1
					rs.getString("emp_name") }; // 2
			emp_list.add(data);
		}
		rs.close();
		pstmt.close();
		/********* 北京层级关系多一层 END *******/

		// / a.建立帐号 先检查帐号，存在则更新，不存在则建立
		for (int i = 0; i < emp_list.size(); i++) {
			String data[] = (String[]) emp_list.get(i);
			String sqlcmd2 = "select 1 from USER_INFO_TBL where ACCOUNT=?";
			pstmt = conn.prepareStatement(sqlcmd2);
			pstmt.setString(1, data[1]);
			rs = pstmt.executeQuery();
			boolean flag = rs.next();
			rs.close();
			pstmt.close();
			if (flag) {
				String sqlcmd4 = "update USER_INFO_TBL set user_type_sid=31, update_date=sysdate, updator='MIS_SYS' where ACCOUNT=?";
				pstmt = conn.prepareStatement(sqlcmd4);
				pstmt.setString(1, data[1]);
				pstmt.executeUpdate();
				pstmt.close();
			} else {
				String sqlcmd3 = "insert into user_info_tbl "
						+ " (sid,ACCOUNT,PASSWORD,USER_NAME,USER_TYPE_SID,EMAIL,MOBILE,AREA_CODE,PHONE_NUM,STATUS,UPDATE_DATE,UPDATOR,CREATE_DATE,CREATOR,LAST_LOGIN_TIME,LAST_LOGIN_ADDS,CHANNEL_SID)  "
						+ " select USER_INFO_TBL_SID.nextval,?,USER_INFO_TBL_SID.currval,?,31,null,null,null,null,1,null,null,sysdate,'MIS_SYS',null,null,2  from dual";
				pstmt = conn.prepareStatement(sqlcmd3);
				pstmt.setString(1, data[1]);
				pstmt.setString(2, data[2]);
				pstmt.executeUpdate();
				pstmt.close();
			}
			pstmt.close();

			// b.建立权限 删除后建立
			String sqlcmd5 = "delete AUTH_USER_GROUP_REL where user_sid=(select sid from user_info_tbl where account=?) ";
			pstmt = conn.prepareStatement(sqlcmd5);
			pstmt.setString(1, data[1]);
			pstmt.executeUpdate();
			pstmt.close();
			String sqlcmd6 = "insert into AUTH_USER_GROUP_REL(USER_SID, USERGROUP_SID, CREATE_USER, CREATE_DATE) select sid ,2002, 'mis', sysdate  from  USER_INFO_TBL where account=?";
			pstmt = conn.prepareStatement(sqlcmd6);
			pstmt.setString(1, data[1]);
			pstmt.executeUpdate();
			pstmt.close();

			// c.建立user_division 删除后建立 division_sid=24
			String sqlcmd7 = "delete USER_DIVISION where user_id=? ";
			pstmt = conn.prepareStatement(sqlcmd7);
			pstmt.setString(1, data[1]);
			pstmt.executeUpdate();
			pstmt.close();
			String sqlcmd8 = "insert into USER_DIVISION select ?,24,'MIS_SYS',sysdate,null,null from  dual ";
			pstmt = conn.prepareStatement(sqlcmd8);
			pstmt.setString(1, data[1]);
			pstmt.executeUpdate();
			pstmt.close();

			// d.建立company_emp 删除后建立
			String sqlcmd9 = "delete COMPANY_EMP where EMP_ID=? ";
			pstmt = conn.prepareStatement(sqlcmd9);
			pstmt.setString(1, data[1]);
			pstmt.executeUpdate();
			pstmt.close();

			String sqlcmd10 = "insert into COMPANY_EMP  select ?,? from  dual ";
			pstmt = conn.prepareStatement(sqlcmd10);
			pstmt.setString(1, data[0]);
			pstmt.setString(2, data[1]);
			pstmt.executeUpdate();
			pstmt.close();

			// 建立客户主管权限
			String sqlcmd11 = " delete  AUTH_USER_GROUP_REL  where USER_SID in (select sid from  USER_INFO_TBL where account = ? )";
			pstmt = conn.prepareStatement(sqlcmd11);
			pstmt.setString(1, data[1]);
			pstmt.executeUpdate();
			pstmt.close();

			String sqlcmd12 = " insert into AUTH_USER_GROUP_REL(USER_SID, USERGROUP_SID, CREATE_USER, CREATE_DATE)  select sid ,'3001', 'mis', sysdate  from  USER_INFO_TBL where account = ? ";
			pstmt = conn.prepareStatement(sqlcmd12);
			pstmt.setString(1, data[1]);
			pstmt.executeUpdate();
			pstmt.close();
		}
		conn.close();
	}

	@Override
	public void execute() throws Exception {
		// TODO Auto-generated method stub

	}
}
