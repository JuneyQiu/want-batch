package com.want.batch.job.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

import com.want.batch.WantBatchException;
import com.want.batch.job.AbstractWantJob;
import com.want.utils.ComparableStringArray;
import com.want.utils.SapDAO;

/**
 * <p>
 * Title: iCustomer
 * </p>
 * 
 * <p>
 * Description: Want Want Group
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author David
 * @version 1.0
 */
@Component
public class SyncZRFC19 extends AbstractWantJob {

	protected static Log logger = LogFactory.getLog(SyncZRFC19.class);
	private Connection conn = null;

	@Autowired
	public SapDAO sapDAO;

	public void execute() throws SQLException {
		conn = getICustomerConnection();
		logger.info("开始同步ZRFC19相关! ");
		
	 	synczrfc19(); // 同步sap中 DIVSION_SALES_CUSTOMER_REL
		createCustomerAccount(); // 建立客户帐号
		createUserAccount(); // 建立业代帐号
		syncCustomerDivision(); // 同步客户与事业部关系
		syncUserDivision(); // 同步业代与事业部关系1
		syncDivisionUserRel(); // 同步业代与事业部关系2
		createEmpCustomerRel(); // 同步业代与客户关系
//		CreateEmpAuth(); // 建立工号,默认全为业代
		updateEmpChannel(); // 建立工号,默认全为业代
//		createEmpCustomerRel(); // 同步业代与客户关系

		logger.info("---- ******   开始更新员工状态与手机号码！  -- David");
		closeUserInfoAuth();
		logger.info("---- ******   结束更新员工状态与手机号码！  -- David");
		
		conn.close();
	}

	private void synczrfc19() throws SQLException {
		int buffer = 2000;
		int allinsert = 0;
		HashMap querydata = new HashMap(5);
		querydata.put("PERNR", "00000000");
		ArrayList list = sapDAO.getSAPData("ZRFC19", "ZST019", querydata);

		if (list == null || list.size() == 0) {
			String msg = "读取 ZRFC19 ZST019 无数据";
			logger.error(msg);
			throw new WantBatchException(new WantBatchException(msg));
		}

		// truncate table
		String deletesqlcmd = "delete from DIVSION_SALES_CUSTOMER_REL";
		getiCustomerJdbcOperations().update(deletesqlcmd);
		logger.info("完成: " + deletesqlcmd);

		// insert table
		String sqlcmd2 = "INSERT INTO  DIVSION_SALES_CUSTOMER_REL "
				+ " (  USER_ID,  COMPANY_ID,  CUSTOMER_ID,  CREDIT_ID,  CHANNEL_ID,  PROD_GROUP_ID,  CITY_CODE,  START_TIME,  END_TIME,  CREATE_DATE) "
				+ " VALUES (  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  sysdate)";
		ArrayList<Object[]> params = new ArrayList<Object[]>();
		for (int l = 0; l < list.size(); l++) {
			HashMap temphm = (HashMap) list.get(l);
			params.add(new Object[] { temphm.get("PERNR"), temphm.get("VKORG"),
					temphm.get("KUNNR"), temphm.get("KKBER"),
					temphm.get("VTWEG"), temphm.get("SPART"), 
					temphm.get("CITYP_CODE"), temphm.get("ZSALEMPLYSTART"),temphm.get("ZSALEMPLYEND") });
			
			if (params.size() == buffer || (l + 1) == list.size()) {
				int[] inserts = getiCustomerJdbcOperations().batchUpdate(sqlcmd2,
						params);
				allinsert = allinsert+inserts.length;
				logger.info("新增笔数: 本次插入 " + inserts.length + ", " );
				params = new ArrayList<Object[]>();
			}
		}
		
		logger.info("sap接口读到的数: " + list.size() + ", " + sqlcmd2);
		logger.info("新增笔数: 插入的总数 " + allinsert + ", " + sqlcmd2);
	
	}


	private void syncCustomerDivision() throws SQLException {
		logger.info("syncCustomerDivision：");
		ArrayList customer_list = new ArrayList();
		String sqlcmd = "select a.CUSTOMER_ID,b.divsion_sid  from DIVSION_SALES_CUSTOMER_REL a inner join  SALES_AREA_REL b on a.CREDIT_ID = b .CREDIT_ID and b.status=1 group by a.CUSTOMER_ID,b.divsion_sid";

		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt = conn.prepareStatement(sqlcmd);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			String data[] = { rs.getString("CUSTOMER_ID"),
					rs.getString("divsion_sid") };
			customer_list.add(data);
		}

		rs.close();
		pstmt.close();

		logger.info("customer_list: " + customer_list.size());
		String deletesqlcmd = "delete from CUSTOMER_DIVISION_REL  ";
		pstmt2 = conn.prepareStatement(deletesqlcmd);
		pstmt2.executeUpdate();
		logger.info("完成: " + deletesqlcmd);

		String sqlcmd2 = "INSERT INTO CUSTOMER_DIVISION_REL   "
				+ "  select ?,? from dual ";

		pstmt = conn.prepareStatement(sqlcmd2);
		int inserted = 0;
		for (int i = 0; i < customer_list.size(); i++) {
			String data[] = (String[]) customer_list.get(i);
			pstmt.setString(1, data[0]);
			pstmt.setString(2, data[1]);
			
			try {
				pstmt.execute();
				inserted++;
			} catch (SQLException sqle) {
				logger.error(sqle.getMessage());
			}
		}

		logger.info("新增笔数: " + inserted + ", " + sqlcmd2);
		pstmt.close();
	}

	private void syncUserDivision() throws SQLException {
		ArrayList<ComparableStringArray> customer_list = new ArrayList<ComparableStringArray>();
		String sqlcmd = "select a.USER_ID,b.divsion_sid  from DIVSION_SALES_CUSTOMER_REL a "
				+ "inner join  SALES_AREA_REL b on a.CREDIT_ID = b .CREDIT_ID  and b.STATUS = '1' "
				+ "inner join USER_INFO_TBL UIT ON UIT.ACCOUNT=a.USER_ID AND UIT.USER_TYPE_SID != 6 "
				+ "group by a.USER_ID,b.divsion_sid ";

		PreparedStatement pstmt = conn.prepareStatement(sqlcmd);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			String uid = rs.getString("USER_ID");
			String did = rs.getString("divsion_sid");
			ComparableStringArray ca = new ComparableStringArray(uid, did);
			if (!customer_list.contains(ca))
				customer_list.add(ca);
		}
		
		rs.close();
		pstmt.close();

		// Modify by David Luo on 2012/08/01 for resolve KA sales division
		// problem,
		// KA sales and his division relation will not be maintain in
		// DIVSION_SALES_CUSTOMER_REL,
		// We get the relation from position_b.pos_property_id.
		ArrayList<ComparableStringArray> userDivisionList = getUserDivisionFromPosProperty();
		for (ComparableStringArray i: userDivisionList)
			if (!customer_list.contains(i))
				customer_list.add(i);
		logger.info("读取比数: " + customer_list.size());

		ArrayList<String> users = new ArrayList<String>();
		ArrayList<Object[]> uniq = new ArrayList<Object[]>();
		for (ComparableStringArray i: customer_list)
			if (!users.contains(i.get(0))) {
				users.add(i.get(0));
				uniq.add(new String[]{i.get(0)});
			}
		
		String deletesqlcmd = "delete USER_DIVISION where USER_ID=? ";
		int[] deletes = getiCustomerJdbcOperations().batchUpdate(deletesqlcmd, uniq);
		logger.info("删除: " + deletes.length + ", " + deletesqlcmd);
		
		
		ArrayList<Object[]> insertList = new ArrayList<Object[]>();
		for (ComparableStringArray i: customer_list)
			insertList.add(i.get());

		String sqlcmd2 = "INSERT INTO USER_DIVISION  select ?,?,'MIS-SYS',sysdate,null,null from dual ";
		int[] inserts = getiCustomerJdbcOperations().batchUpdate(sqlcmd2, insertList);
		logger.info("新增: " + inserts.length + ", " + sqlcmd2);		
	}

	private void syncDivisionUserRel() throws SQLException {
		ArrayList customer_list = new ArrayList();
		String sqlcmd = "select  c.sid,b.divsion_sid  from DIVSION_SALES_CUSTOMER_REL a inner join  SALES_AREA_REL b on a.CREDIT_ID = b .CREDIT_ID inner join user_info_tbl c on  user_id=trim(c.account)  group by c.sid,b.divsion_sid ";

		PreparedStatement pstmt = conn.prepareStatement(sqlcmd);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			String data[] = { rs.getString("sid"), rs.getString("divsion_sid") };
			customer_list.add(data);
		}
		rs.close();
		pstmt.close();
		logger.info("customer_list: " + customer_list.size());

		
		String DELETE_SQL = "delete DIVSION_USER_REL where USER_SID=? ";
		PreparedStatement pstmtDelete = conn.prepareStatement(DELETE_SQL);
		int deleted = 0;
		
		for (int i = 0; i < customer_list.size(); i++) {
			String data[] = (String[]) customer_list.get(i);
			pstmtDelete.setString(1, data[0]);
			try {
				pstmtDelete.execute();
				deleted ++;
			} catch (SQLException sqle) {
				logger.error(sqle.getMessage());
			}
		}
		logger.info("完成" + deleted + ", " + DELETE_SQL);
		pstmtDelete.close();

		String INSERT_SQL = "INSERT INTO DIVSION_USER_REL   "
				+ "  select DIVSION_USER_REL_SEQ.nextval,?,? from dual ";
		PreparedStatement pstmtInsert = conn.prepareStatement(INSERT_SQL);
		int inserted = 0;
		for (int i = 0; i < customer_list.size(); i++) {
			String data[] = (String[]) customer_list.get(i);
			pstmtInsert.setString(1, data[1]);
			pstmtInsert.setString(2, data[0]);
			try {
				pstmtInsert.execute();
				inserted++;
			} catch (SQLException sqle) {
				logger.error(sqle.getMessage());
				logger.error("user sid : " + data[0] + "----");
				logger.error("divsion id : " + data[1] + "----");
			}
		}
		logger.info("完成" + inserted + ", " + INSERT_SQL);
		pstmtInsert.close();
	}

	private void createCustomerAccount() throws SQLException {
		ArrayList customer_list = new ArrayList();
		String sqlcmd = "select CUSTOMER_ID from DIVSION_SALES_CUSTOMER_REL  a left join user_info_tbl b on b.account = substr(a.CUSTOMER_ID,3,8)||'-1' where b.sid is null group by CUSTOMER_ID  ";
		// Connection conn = null;

		PreparedStatement pstmt = conn.prepareStatement(sqlcmd);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			customer_list.add(rs.getString("CUSTOMER_ID"));
		}

		rs.close();
		pstmt.close();
		// conn.close();
		logger.info("customer_list: " + customer_list.size());
		String sqlcmd1 = "insert into user_info_tbl "
				+ " (sid,ACCOUNT,PASSWORD,USER_NAME,USER_TYPE_SID,EMAIL,MOBILE,AREA_CODE,PHONE_NUM,STATUS,UPDATE_DATE,UPDATOR,CREATE_DATE,CREATOR,LAST_LOGIN_TIME,LAST_LOGIN_ADDS,CHANNEL_SID)  "
				+ " select USER_INFO_TBL_SID.nextval,substr(id,3,8)||'-1',nvl(phone,'password'),name,1,null,phone,null,mobile,1,null,null,sysdate,'MIS_SYS',null,null,2 from customer_info_tbl where id=?";
		String sqlcmd2 = "insert into CUSTOMER_USER_REL select a.sid,b.sid,b.account from customer_info_tbl a  inner join user_info_tbl b on 1=1 where b.account=substr(id,3,8)||'-1' and  a.id= lpad(?,10,'0')";
		String sqlcmd3 = "insert into AUTH_USER_GROUP_REL(USER_SID, USERGROUP_SID, CREATE_USER, CREATE_DATE) "
				+ " select sid ,'0', 'mis', sysdate  from  USER_INFO_TBL where account=substr(?,3,8)||'-1'";

		// conn = SyncZRFC19.getTestConnection();
		pstmt = conn.prepareStatement(sqlcmd1);
		int inserted = 0;
		PreparedStatement pstmt2 = conn.prepareStatement(sqlcmd2);
		int inserted2 = 0;
		PreparedStatement pstmt3 = conn.prepareStatement(sqlcmd3);
		int inserted3 = 0;

		for (int i = 0; i < customer_list.size(); i++) {
			String c_id = (String) customer_list.get(i);
			pstmt.setString(1, c_id);
			pstmt2.setString(1, c_id);
			pstmt3.setString(1, c_id);
			try {
				pstmt.execute();
				inserted++;
			} catch (SQLException sqle) {
				logger.error(sqle.getMessage());
			}
			try {
				pstmt2.execute();
				inserted2++;
			} catch (SQLException sqle2) {
				logger.error(sqle2.getMessage());
			}
			try {
				pstmt3.execute();
				inserted3++;
			} catch (SQLException sqle3) {
				logger.error(sqle3.getMessage());
			}
		}

		logger.info("新增笔数: " + inserted + ", " + sqlcmd1);
		logger.info("新增笔数: " + inserted2 + ", " + sqlcmd2);
		logger.info("新增笔数: " + inserted3 + ", " + sqlcmd3);

		//增加客户和客户的对应关系，ji-hongxing@want-want.com
		try {
			String DELETE_customer_user_SQL = " delete from CUSTOMER_USER_REL where USER_ID like '11%-%' ";
			PreparedStatement pstmtDelete = conn
					.prepareStatement(DELETE_customer_user_SQL);
			pstmtDelete.execute();
			String insert_customer_user_SQL = "insert into CUSTOMER_USER_REL (CUSTOMER_SID,USER_SID,USER_ID ) "+
					" select csid,usid,acc from (  "+
					" select distinct a.sid csid,b.sid usid,b.account acc ,bb.user_id from  CUSTOMER_INFO_TBL a inner join user_info_tbl b on b.USER_TYPE_SID  in ('1', '10')  and  b.STATUS = '1' and b.account = substr(a.ID,3,8)||'-1' "+ 
					"   left join CUSTOMER_USER_REL bb on a.sid =  bb.CUSTOMER_SID and b.sid = bb.USER_SID "+
					" where bb.user_id is null "+
					" )  ";
			PreparedStatement pstmtInsert = conn
					.prepareStatement(insert_customer_user_SQL);
			pstmtInsert.execute();
			logger.info(" 删除并增加customer—user-rel中的客户对客户的 ------------------成功了-------------------- ");
		} catch (Exception e) {
			logger.info(" 删除并增加customer—user-rel中的客户对客户的 ----------------失败了--------------");
		}
		
		
		
		
		pstmt.close();
		pstmt2.close();
		pstmt3.close();
	}

	// 重复了，计划删除，待确认。
	private void createUserAccount() throws SQLException {
		ArrayList customer_list = new ArrayList();
		String sqlcmd = "select USER_ID,c.emp_name,a.CREDIT_ID from DIVSION_SALES_CUSTOMER_REL  a inner join emp c on a.user_id=c.emp_id left join user_info_tbl b on b.account = user_id where b.sid is null group by USER_ID ,c.emp_name,a.CREDIT_ID ";

		PreparedStatement pstmt = conn.prepareStatement(sqlcmd);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			String data[] = { rs.getString("USER_ID"),
					rs.getString("emp_name"), rs.getString("CREDIT_ID") };
			customer_list.add(data);
		}

		rs.close();
		pstmt.close();
		// conn.close();
		logger.info("customer_list: " + customer_list.size());
		String sqlcmd1 = "insert into user_info_tbl "
				+ " (sid,ACCOUNT,PASSWORD,USER_NAME,USER_TYPE_SID,EMAIL,MOBILE,AREA_CODE,PHONE_NUM,STATUS,UPDATE_DATE,UPDATOR,CREATE_DATE,CREATOR,LAST_LOGIN_TIME,LAST_LOGIN_ADDS,CHANNEL_SID)  "
				+ " select USER_INFO_TBL_SID.nextval,?,USER_INFO_TBL_SID.currval,?,2,null,null,null,null,1,null,null,sysdate,'MIS_SYS',null,null,1  from dual";
		String sqlcmd3 = "insert into AUTH_USER_GROUP_REL(USER_SID, USERGROUP_SID, CREATE_USER, CREATE_DATE) "
				+ " select sid ,?, 'mis', sysdate  from  USER_INFO_TBL where account=?";

		// conn = SyncZRFC19.getTestConnection();
		pstmt = conn.prepareStatement(sqlcmd1);
		int inserted1 = 0;
		PreparedStatement pstmt3 = conn.prepareStatement(sqlcmd3);
		int inserted3 = 0;
		String usergroup_sid = "99";

		for (int i = 0; i < customer_list.size(); i++) {
			String data[] = (String[]) customer_list.get(i);
			pstmt.setString(1, data[0]);
			pstmt.setString(2, data[1]);
			if ("CC3".equalsIgnoreCase(data[2])
					|| "CC2".equalsIgnoreCase(data[2])
					|| "CC1".equalsIgnoreCase(data[2])) {
				usergroup_sid = "1001";
			} else if ("CDI".equalsIgnoreCase(data[2])) {
				usergroup_sid = "9";
			} else {
				usergroup_sid = "99";
			}
			pstmt3.setString(1, usergroup_sid);
			pstmt3.setString(2, data[0]);
			try {
				pstmt.execute();
				inserted1++;
			} catch (SQLException sqle) {
				logger.error("pstmt error:" + data[0]);
			}
			try {
				pstmt3.execute();
				inserted3++;
			} catch (SQLException sqle3) {
				logger.error("pstmt3 error:" + data[0]);
			}
		}

		logger.error("user Account 新增数量：" + inserted1 + ", " + sqlcmd1);
		logger.error("user Account 新增数量：" + inserted3 + ", " + sqlcmd3);
		pstmt.close();
		pstmt3.close();
	}

	/*
	private void createEmpCustomerRel() throws SQLException {
		ArrayList customer_list = new ArrayList();
		String sqlcmd = "select CUSTOMER_ID,USER_ID  from DIVSION_SALES_CUSTOMER_REL  a  "
				+ " group by CUSTOMER_ID,USER_ID  ";

		PreparedStatement pstmt = conn.prepareStatement(sqlcmd);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			String data[] = { rs.getString("CUSTOMER_ID"),
					rs.getString("USER_ID") };
			customer_list.add(data);
		}

		rs.close();
		pstmt.close();
		// conn.close();

		logger.info("customer_list: " + customer_list.size());
		String sqlcmd2 = "insert into CUSTOMER_USER_REL select a.sid,b.sid,b.account from customer_info_tbl a  inner join user_info_tbl b on 1=1 where b.account=? and  a.id= lpad(?,10,'0')";
		pstmt = conn.prepareStatement(sqlcmd2);
		int inserted = 0;

		for (int i = 0; i < customer_list.size(); i++) {
			String data[] = (String[]) customer_list.get(i);
			pstmt.setString(1, data[1]);
			pstmt.setString(2, data[0]);
			try {
				pstmt.execute();
				inserted++;
			} catch (SQLException sqle) {
				logger.error(sqle.getMessage());
			}

		}

		logger.info("user 关系 新增数量：" + inserted);
		pstmt.close();
	}
	*/
	private static final String CUSTOMER_USER_REL_SQL = " (select b.sid user_sid"
			+ " from customer_info_tbl a, user_info_tbl b," 
			+ " (select CUSTOMER_ID,USER_ID  from DIVSION_SALES_CUSTOMER_REL  a group by CUSTOMER_ID,USER_ID) c" 
			+ " where b.account=c.user_id and  a.id= lpad(c.customer_id,10,'0'))";
	
	private static final String INSERT_CUSTOMER_USER_REL_SQL = " (select a.sid customer_sid ,b.sid user_sid,b.account user_id"
		+ " from customer_info_tbl a, user_info_tbl b," 
		+ " (select CUSTOMER_ID,USER_ID  from DIVSION_SALES_CUSTOMER_REL  a group by CUSTOMER_ID,USER_ID) c" 
		+ " where b.account=c.user_id and  a.id= lpad(c.customer_id,10,'0'))";
	private static final String DEL_CUSTOMER_USER_REL_SQL = "delete from customer_user_rel where ( user_sid) in " +  CUSTOMER_USER_REL_SQL;//**********
	private static final String INS_CUSTOMER_USER_REL_SQL = "insert into customer_user_rel " +  INSERT_CUSTOMER_USER_REL_SQL;//*
	//增加业代的上级主任和客户的关系;2014-07-08 mirabelle update pos_name like '%主任%'-->pos_type_id=005
	private static final String INS_CUSTOMER_USER_REL_SQL2 = " insert into customer_user_rel " 
				+ " select a.CUSTOMER_SID cus_sid,b.SID usersid,e.EMP_ID empid from  CUSTOMER_USER_REL a  "
				+ " inner join ICUSTOMER.EMP_POSITION c on c.EMP_ID = a.USER_ID "
				+ " inner join ICUSTOMER.EMP_POSITION e on c.DIRECTOR_EMP_ID = e.EMP_ID "
				+ " inner join POSITION_B f on e.POS_ID = f.POS_ID and f.POS_TYPE_ID in ('" + RoleConstant.DIRECTOR1 +"' ,'"+RoleConstant.DIRECTOR2 +"' ,'"+RoleConstant.DIRECTOR3+ "','"+RoleConstant.DIRECTOR4+"')"
				+ " inner join user_info_tbl b on e.EMP_ID = b.account and b.STATUS = '1' "
				+ " minus  "
				+ " select aa.CUSTOMER_SID,aa.USER_sID,aa.USER_ID from ICUSTOMER.CUSTOMER_USER_REL aa ";
	
	@Autowired
	private SimpleJdbcOperations iCustomerJdbcOperations;
			
	private void createEmpCustomerRel() throws SQLException {
		iCustomerJdbcOperations.update(DEL_CUSTOMER_USER_REL_SQL);
		logger.info("Complete: " + DEL_CUSTOMER_USER_REL_SQL+"好了");
		iCustomerJdbcOperations.update(INS_CUSTOMER_USER_REL_SQL);
		iCustomerJdbcOperations.update(INS_CUSTOMER_USER_REL_SQL2);
		logger.info("Complete: " + INS_CUSTOMER_USER_REL_SQL+"ok！！！！！！！！！！！");
	}
	

	private void updateEmpChannel() throws SQLException {
		ArrayList customer_list = new ArrayList();
		String sqlcmd = "select decode(CREDIT_ID,'CDI',1,2) as CHANNEL,USER_ID  from DIVSION_SALES_CUSTOMER_REL  a  group by CREDIT_ID,USER_ID  ";

		PreparedStatement pstmt = conn.prepareStatement(sqlcmd);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			String data[] = { rs.getString("CHANNEL"), rs.getString("USER_ID") };
			customer_list.add(data);
		}

		rs.close();
		pstmt.close();
		// conn.close();

		logger.info("customer_list: " + customer_list.size());
		String sqlcmd2 = "update user_info_tbl set CHANNEL_SID=? where  account=? ";

		// conn = SyncZRFC19.getTestConnection();
		pstmt = conn.prepareStatement(sqlcmd2);
		int updated = 0;
		for (int i = 0; i < customer_list.size(); i++) {
			String data[] = (String[]) customer_list.get(i);
			pstmt.setString(1, data[0]);
			pstmt.setString(2, data[1]);
			try {
				pstmt.execute();
				updated++;
			} catch (SQLException sqle) {
				logger.error(sqle);
			}
		}

		logger.info("user 关系 修改数量：" + updated);
		pstmt.close();
	}

	private boolean checkTodayVaild(String start_date, String end_date) {
		boolean flag = false;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date cal_today = Calendar.getInstance().getTime();
			Date start_day = df.parse(start_date);
			Date end_day = df.parse(end_date);
			flag = cal_today.after(start_day) && cal_today.before(end_day);
			if (flag) {
				// logger.info("today true");
			} else {
				// logger.info("today false");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			throw new WantBatchException(e);
		}
		return flag;
	}

	// 重复了，计划删除，待确认。
	private void CreateEmpAuth() throws SQLException {
		ArrayList emp_list = new ArrayList();
		String sqlcmd = "select USER_ID,c.emp_name,a.CREDIT_ID from DIVSION_SALES_CUSTOMER_REL  a  inner join emp c on a.user_id=c.emp_id  group by USER_ID ,c.emp_name,a.CREDIT_ID ";

		int count = 0;
		PreparedStatement pstmt = conn.prepareStatement(sqlcmd);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			String data[] = { rs.getString("USER_ID"),
					rs.getString("emp_name"), rs.getString("CREDIT_ID") };
			emp_list.add(data);
		}
		logger.info("customer_list: " + emp_list.size());
		rs.close();
		pstmt.close();
		String sqlcmd3 = "insert into AUTH_USER_GROUP_REL(USER_SID, USERGROUP_SID, CREATE_USER, CREATE_DATE) "
				+ " select sid ,?, 'mis', sysdate  from  USER_INFO_TBL where account=?";

		// conn = SyncZRFC19.getTestConnection();
		pstmt = conn.prepareStatement(sqlcmd3);
		String usergroup_sid = "99";
		for (int i = 0; i < emp_list.size(); i++) {
			String data[] = (String[]) emp_list.get(i);
			if ("CC3".equalsIgnoreCase(data[2])
					|| "CC2".equalsIgnoreCase(data[2])
					|| "CC1".equalsIgnoreCase(data[2])
					|| "CC4".equalsIgnoreCase(data[2])
					|| "CC5".equalsIgnoreCase(data[2])) {
				usergroup_sid = "1001";
			} else if ("CDI".equalsIgnoreCase(data[2])) {
				usergroup_sid = "9";
			} else {
				usergroup_sid = "99";
			}
			pstmt.setString(1, usergroup_sid);
			pstmt.setString(2, data[0]);
			try {
				pstmt.execute();
				count++;
			} catch (SQLException sqle) {
				logger.error(sqle);
			}

		}
		pstmt.close();
		logger.info("新权限成功数：" + count);
	}

	private boolean checkCompanyVaild(String company_id) {
		/*
		 * boolean flag = false; String a[] = {"C221", "C231", "C191", "C361",
		 * "C371", "C321", "C311", "C291", "C461", "C491", "C551", "C111"}; for
		 * (int i = 0; i < a.length; i++) {
		 * 
		 * if (a[i].equalsIgnoreCase(company_id)) { flag = true; //
		 * logger.info(company_id + ":true"); break; }
		 * 
		 * }
		 * 
		 * return flag;
		 */
		return true;
	}

	// 检查用户帐号 user_type_sid not in 1 or 10 的是否存在emp表中，不存在的将其状态关闭，存在的开启
	private void closeUserInfoAuth() {
		// 1.将所有员工帐号设成0
		StringBuffer sqlcmd1 = new StringBuffer(
				" update USER_INFO_TBL set STATUS=0, UPDATOR='MIS-stu=0', UPDATE_DATE=sysdate  where USER_TYPE_SID not in (1,10) ");
		// 2.将存在EMP表中的员工帐号设成1
		StringBuffer sqlcmd2 = new StringBuffer(
				" update USER_INFO_TBL set STATUS=1, UPDATOR='MIS-stu=1', UPDATE_DATE=sysdate  where USER_TYPE_SID not in (1,10)  and substr(ACCOUNT,0,8) in (select EMP_ID from EMP) ");
		// 3.更新员工手机资料
		StringBuffer sqlcmd3 = new StringBuffer(
				" update USER_INFO_TBL a set a.MOBILE=(select b.EMP_MOBILE from EMP b where b.emp_id=substr(account,0,8)), UPDATOR='MIS-upd-pho', UPDATE_DATE=sysdate    where substr(account,0,8) in (select emp_id from emp ) ");
		// 4.更新客户手机资料
		StringBuffer sqlcmd4 = new StringBuffer(
				" update USER_INFO_TBL a set a.MOBILE=(select b.MOBILE from CUSTOMER_INFO_TBL b where b.ID='00'||substr(a.account,0,8)) , UPDATOR='MIS-upd-pho', UPDATE_DATE=sysdate   where '00'||substr(a.account,0,8) in (select id from CUSTOMER_INFO_TBL ) ");
		// 5.更新员工手机资料
		StringBuffer sqlcmd5 = new StringBuffer(
				" update USER_INFO_TBL a set a.USER_NAME=(select b.EMP_NAME from EMP b where b.emp_id=substr(account,0,8)), UPDATOR='MIS-upd-pho', UPDATE_DATE=sysdate    where substr(account,0,8) in (select emp_id from emp ) ");
		// 6.更新客户手机资料
		StringBuffer sqlcmd6 = new StringBuffer(
				" update USER_INFO_TBL a set a.USER_NAME=(select b.NAME from CUSTOMER_INFO_TBL b where b.ID='00'||substr(a.account,0,8)) , UPDATOR='MIS-upd-pho', UPDATE_DATE=sysdate   where '00'||substr(a.account,0,8) in (select id from CUSTOMER_INFO_TBL ) ");

		PreparedStatement pstmt = null;
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sqlcmd1.toString());
			logger.info(" ******   开始更新员工状态与手机号码  " + pstmt.executeUpdate());
			pstmt.close();

			pstmt = conn.prepareStatement(sqlcmd2.toString());
			logger.info(" 				将所有员工帐号设成0 成功  " + pstmt.executeUpdate());
			pstmt.close();

			pstmt = conn.prepareStatement(sqlcmd3.toString());
			logger.info(" 				将存在EMP表中的员工帐号设成1 成功  " + pstmt.executeUpdate());
			pstmt.close();

			pstmt = conn.prepareStatement(sqlcmd4.toString());
			logger.info(" 				更新员工手机资料（包含-1,-2 ） 成功  " + pstmt.executeUpdate());
			pstmt.close();

			pstmt = conn.prepareStatement(sqlcmd5.toString());
			logger.info(" 				更新客户手机资料（包含-1,-2 ） 成功  " + pstmt.executeUpdate());
			pstmt.close();

			pstmt = conn.prepareStatement(sqlcmd6.toString());
			logger.info(" 				更新员工姓名资料（包含-1,-2 ） 成功  " + pstmt.executeUpdate());
			pstmt.close();

			conn.setAutoCommit(true);
			logger.info(" ******   更新员工状态与手机号码 成功  ");

			pstmt.close();
		} catch (Exception s) {
			logger.error(s.getMessage());
			try {
				conn.rollback();
				conn.setAutoCommit(true);
			} catch (SQLException sqle) {
				logger.error(sqle);
				sqle.printStackTrace();
				throw new WantBatchException(sqle);
			}
			s.printStackTrace();
			throw new WantBatchException(s);
		}
	}
	
	private ArrayList<ComparableStringArray> getUserDivisionFromPosProperty() throws SQLException {
		HashMap pos_pro = new HashMap();
		// 设定Position 表中，property_id与division的关系
		pos_pro.put("A09", new String[] { "24" });
		ArrayList<ComparableStringArray> emp_list = new ArrayList<ComparableStringArray>();
		String sqlcmd = "select EMP_ID,POS_PROPERTY_ID from EMP_POSITION_A a "
				+ " inner join POSITION_B b on a.POS_ID=b.POS_ID where length( b.POS_PROPERTY_ID) >=3 ";

		PreparedStatement pstmt = conn.prepareStatement(sqlcmd);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			String data[] = { rs.getString("EMP_ID"),
					rs.getString("POS_PROPERTY_ID") };

			if (pos_pro.containsKey(data[1])) {
				String[] divisions = (String[]) pos_pro.get(data[1]);
				for (int i = 0; i < divisions.length; i++) {
					String divisionid = divisions[i];
					emp_list.add(new ComparableStringArray(data[0], divisionid));
				}
			}
		}
		rs.close();
		pstmt.close();
		return emp_list;
	}

}
