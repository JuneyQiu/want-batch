package com.want.batch.job.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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
public class CreateXGCustomerRel extends AbstractWantJob {

	private static Log logger = LogFactory.getLog(CreateXGCustomerRel.class);

	public CreateXGCustomerRel() {
	}

	// 检查销管帐号是否存在
	private static String sqlisxgexisted = " select 1 from user_info_tbl where account=?";
	// 更新销管帐号变量
	private static String sqlupdatexgdata = " update user_info_tbl set user_type_sid=3,status=1 where account=?";
	// 建立销管帐号
	private static String sqlcreatexg = " insert into user_info_tbl "
			+ " (sid,ACCOUNT,PASSWORD,USER_NAME,USER_TYPE_SID,EMAIL,MOBILE,AREA_CODE,PHONE_NUM,STATUS,UPDATE_DATE,UPDATOR,CREATE_DATE,CREATOR,LAST_LOGIN_TIME,LAST_LOGIN_ADDS,CHANNEL_SID)  "
			+ " select USER_INFO_TBL_SID.nextval,?,USER_INFO_TBL_SID.currval,?,3,null,null,null,null,1,null,null,sysdate,'MIS_SYS',null,null,1  from dual ";
	// 删除销管权限
	private static String sqldeletexgauth = " delete AUTH_USER_GROUP_REL  where user_sid= ( select sid from user_info_tbl where account = ? ) and upper(CREATE_USER) like '%MIS%'";
	// 建立销管权限
	private static String sqlcreatexgauth = " insert into AUTH_USER_GROUP_REL(USER_SID, USERGROUP_SID, CREATE_USER, CREATE_DATE)  select sid ,'7', 'mis', sysdate  from  USER_INFO_TBL where account = ? ";

	// 删除此销管与所有客户关系
	private static String sqlcmd1_b = " delete CUSTOMER_USER_REL where USER_SID=(select sid from  USER_INFO_TBL where account = ?) ";
	// 建立此销管与营业所客户关系
	private static String sqlcmd1_c = " insert into CUSTOMER_USER_REL "
			+ " select sid,u_sid,u_id from ( select b.sid,(select sid from user_info_tbl where account=?) as  u_sid ,? as u_id  "
			+ " from CUSTOMER_BRANCH_TBL a inner join  customer_info_tbl b on a.customer_id=b.id  inner join COMPANY_BRANCH_VIEW c on a.BRANCH_ID=c.BRANCH_ID WHERE c.type=1  "
			+ " and BRANCH_ID = ? )group by  sid,u_sid,u_id ";

	//依据需求，增加营业所业管，营业所业管主管权限，同等于销管 on 2013/4/28 by David Luo
	// 1、modify 2014-07-09 mandy 将POS_NAME like '%销管%' or POS_NAME like '%营业所业管%'修改成007分公司销管，008营业所销管
	// 2、modify 2014-07-09 mandy 将b.POS_NAME not like '%现渠%' and b.POS_NAME not like '%直营%'修改成POS_PROPERTY_ID!='A09' and POS_PROPERTY_ID!='A21'
	private static String sqlcmd2_a = " select a.emp_id as EMP_ID, z.EMP_NAME as EMP_NAME  , c.ORG_ID as ORG_ID   "
			+ " from EMP_POSITION a "
			+ " inner join POSITION_B b on a.pos_id=b.pos_id  and (b.POS_TYPE_ID = '" + RoleConstant.COMPANY_XG + "' or b.POS_TYPE_ID = '" + RoleConstant.BRANCH_XG + "' or b.POS_NAME  like '%资源管理%' )  and b.POS_PROPERTY_ID <> '" + RoleConstant.XIANQU + "' and b.POS_PROPERTY_ID <> '" + RoleConstant.ZHIYING + "' "//這裡我改了
			+ " inner join ORGANIZATION_B c on b.org_id=c.org_id  and c.ORG_ID like '2%' "
			+ " inner join EMP z on z.emp_id=a.emp_id  ";
	// sapid和hrid的对应关系放到map中去
	private static String sqlHridToSapid_cmd = "select COMPANY_SAP_ID,COMPANY_BRANCH.COMPANY_HR_ID,COMPANY_BRANCH.BRANCH_SAP_ID,COMPANY_BRANCH.BRANCH_HR_ID from COMPANY_BRANCH";

	// sapid和hrid的对应关系放到map中去，增加销管的xx 营业所销管组特殊关系, modify by David Luo on
	// 20130314
	//依据需求，增加营业所业管，营业所业管主管权限，同等于销管 on 2013/4/28 by David Luo
	// modify 2014-07-09 mandy 将POS_NAME like '%销管%' or POS_NAME like '%营业所业管%'修改成007分公司销管，008营业所销管
	private static String sqlHridToSapid_cmd2 = " select  distinct f.BRANCH_SAP_ID,c.ORG_ID  from  POSITION_B b inner join ORGANIZATION_B c on b.org_id=c.org_id  and  (POS_TYPE_ID = '" + RoleConstant.COMPANY_XG + "' or POS_TYPE_ID = '" + RoleConstant.BRANCH_XG + "' )  and c.org_name like'%营业所%' inner join  COMPANY_BRANCH f on c.org_name like f.branch_name||'%'  and f.branch_name is not null ";

	// 删除此销管与所有客户关系
	private static String sqlcmd2_b = " delete CUSTOMER_USER_REL where USER_SID=(select sid from  USER_INFO_TBL where account = ?) ";
	// 建立此销管与分公司客户关系
	private static String sqlcmd2_c = " insert into CUSTOMER_USER_REL select sid,u_sid,u_id from  "
			+ "   ( select b.sid,(select sid from user_info_tbl where account=?) as  u_sid ,?as u_id  "
			+ "   from CUSTOMER_BRANCH_TBL a inner join  customer_info_tbl b on a.customer_id=b.id  inner join COMPANY_BRANCH_VIEW c  "
			+ "   on a.BRANCH_ID=c.BRANCH_ID WHERE c.type=1 and c.COMPANY_ID = ? )group by  sid,u_sid,u_id  ";

	// 若有，更新帐号资料-分公司
	private static String sqlinsertcompany = "insert into COMPANY_EMP values(?,?)";
	private static String sqlinsertcompany_del = "delete from COMPANY_EMP where EMP_ID=? ";

	// 若没有，建立帐号资料-营业所
	private static String sqlinsertbranch = "insert into BRANCH_EMP values(?,?) ";
	private static String sqlinsertbranch_del = "delete from BRANCH_EMP where EMP_ID=? ";
	
	
	
	//刪除了分公司销管主管和营业所之间的关系**********
	// modify 2014-07-09 mandy 将b.POS_NAME like '%销管课主管%'修改成POS_TYPE_ID='007'
	private static String sqldeleteXGyINGyS_del="delete from BRANCH_EMP where emp_id in (select a.emp_id as EMP_ID from EMP_POSITION a " +
			"inner join POSITION_B b on a.pos_id=b.pos_id and (b.POS_TYPE_ID = '" + RoleConstant.COMPANY_XG + "')" +
			"inner join ORGANIZATION_B c on b.org_id=c.org_id and c.ORG_ID like '2%'" +
			"inner join COMPANY_BRANCH cb on substr(c.ORG_ID,0,4) = substr(cb.COMPANY_HR_ID,0,4) )";
	//增加了分公司销管主管和营业所之间的关系******
	// modify 2014-07-09 mandy 将b.POS_NAME like '%销管课主管%'修改成POS_TYPE_ID='007'
	private static String sqlinsertXGyingYS="insert into BRANCH_EMP select cb.BRANCH_SAP_ID ," 
			+ "  a.emp_id as EMP_ID from EMP_POSITION a  " 
			+"inner join POSITION_B b on a.pos_id=b.pos_id and (b.POS_TYPE_ID = '" + RoleConstant.COMPANY_XG + "')"
			+ " inner join ORGANIZATION_B c on b.org_id=c.org_id and c.ORG_ID like '2%' " 
      		+"inner join COMPANY_BRANCH cb on substr(c.ORG_ID,0,4) = substr(cb.COMPANY_HR_ID,0,4)";
	

	/**
	 * <pre>
	 * 建立销管与营业所客户关系
	 * 建立步骤
	 * 1.取得销管与营业所关系。
	 * 2.循环取出销管与营业所关系。
	 * 3.建立销管帐号。
	 * 4.建立销管与营业所所有客户关系
	 * </pre>
	 * 
	 * @throws SQLException
	 */
	public void createBranchXGCustomerRel(List xg_list_yys) throws SQLException {

		Connection conn = getICustomerConnection();
		logger.info("取得销管与营业所客户关系!");
		logger.info("建立销管与营业所客户关系!");

		int count2 = 0;
		PreparedStatement pstmt2 = conn.prepareStatement(sqlcmd1_b);

		int count3 = 0;
		PreparedStatement pstmt3 = conn.prepareStatement(sqlcmd1_c);

		int count4 = 0;
		PreparedStatement pstmt4 = conn.prepareStatement(sqlinsertbranch);

		int count4d = 0;
		PreparedStatement pstmt4d = conn.prepareStatement(sqlinsertbranch_del);
		// pstmt5 = conn.prepareStatement(sqlinsertcompany);
		// pstmt5d = conn.prepareStatement(sqlinsertcompany_del);
		HashMap existed_map = new HashMap();
		for (int i = 0; i < xg_list_yys.size(); i++) {
			pstmt2.clearParameters();
			pstmt3.clearParameters();
			pstmt4.clearParameters();
			pstmt4d.clearParameters();
			try {
				String[] temp_emp = (String[]) xg_list_yys.get(i);
				// 重整销管帐号
				createXG(temp_emp[1], temp_emp[2], conn);
				// logger.info("temp_emp:" + temp_emp[0] + "-" +
				// temp_emp[1] + "-" + temp_emp[2]);
				if (!existed_map.containsValue(temp_emp[1])) {
					pstmt2.setString(1, temp_emp[1]);
					count2 = pstmt2.executeUpdate() + count2;
				}
				pstmt3.setString(1, temp_emp[1]);
				pstmt3.setString(2, temp_emp[1]);
				pstmt3.setString(3, temp_emp[0]);
				count3 = pstmt3.executeUpdate() + count3;
				pstmt4.setString(1, temp_emp[0]);
				pstmt4.setString(2, temp_emp[1]);
				pstmt4d.setString(1, temp_emp[1]);
				count4d = pstmt4d.executeUpdate() + count4d;
				count4 = pstmt4.executeUpdate() + count4;
				existed_map.put(temp_emp[1], temp_emp[1]);
			} catch (Exception sqle) {
				logger.error(sqle.getMessage());
			}
		}

		logger.info("新建结束");

		pstmt2.close();
		pstmt3.close();
		pstmt4.close();
		pstmt4d.close();
		conn.close();
	}

	/**
	 * <pre>
	 * 建立销管与分公司客户关系
	 * 建立步骤
	 * 1.取得销管与分公司关系。
	 * 2.循环取出销管与分公司关系。
	 * 3.建立销管帐号。
	 * 4.建立销管与分公司所有客户关系
	 * </pre>
	 * 
	 * @throws SQLException
	 */
	
	public void createCompanyXGCustomerRel() throws SQLException {
		// 分公司List
		ArrayList xg_list = new ArrayList();
		// 营业所List
		ArrayList xg_list_yys = new ArrayList();
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;
		PreparedStatement pstmt5 = null;
		PreparedStatement pstmt5d = null;
		PreparedStatement pstmt6 = null;
		
		
		//PreparedStatement pstmt7 = null;//.....在這裡加一個  pstmt7？.
		

		ResultSet rs = null;
		logger.info("开始建立销管与分公司客户关系!");

		Connection conn = getICustomerConnection();
        pstmt6 = conn.prepareStatement(sqlHridToSapid_cmd);
		rs = pstmt6.executeQuery();
		Map<String, String> map = new HashMap<String, String>();
		while (rs.next()) {
			map.put(rs.getString(2), rs.getString(1));
			map.put(rs.getString(4), rs.getString(3));
		}
		rs.close();
		pstmt6.close();
		// sapid和hrid的对应关系放到map中去，增加销管的xx 营业所销管组特殊关系, modify by David Luo on
		// 20130314
		pstmt6 = conn.prepareStatement(sqlHridToSapid_cmd2);
		rs = pstmt6.executeQuery();
		while (rs.next()) {
			map.put(rs.getString(2), rs.getString(1));
		}
		rs.close();
		pstmt6.close();
		pstmt = conn.prepareStatement(sqlcmd2_a);
		rs = pstmt.executeQuery();
		while (rs.next()) {
			// 分公司id
			String org_id = CreateEMPCompanyRel.getCompanyId(
					rs.getString("ORG_ID"), "分公司", conn);
			if (!org_id.equals("") && !org_id.equals("10000000")) {
				String[] temp_emp = { map.get(org_id), rs.getString("EMP_ID"),
						rs.getString("EMP_NAME") };
				xg_list.add(temp_emp);
			}
			// 营业所id
			String org_id2 = CreateEMPCompanyRel.getCompanyId(
					rs.getString("ORG_ID"), "营业所", conn);
			if (!org_id2.equals("") && !org_id2.equals("10000000")) {
				String[] temp_emp2 = { map.get(org_id2),
						rs.getString("EMP_ID"), rs.getString("EMP_NAME"),
						org_id };
				xg_list_yys.add(temp_emp2);
			}

		}
		rs.close();
		pstmt.close();

		int count2 = 0;
	pstmt2 = conn.prepareStatement(sqlcmd2_b);
		int count3 = 0;
		pstmt3 = conn.prepareStatement(sqlcmd2_c);
		int count5 = 0;
		pstmt5 = conn.prepareStatement(sqlinsertcompany);

		int count6 = 0;
		pstmt5d = conn.prepareStatement(sqlinsertcompany_del);

		HashMap existed_map = new HashMap();
		for (int i = 0; i < xg_list.size(); i++) {
			pstmt2.clearParameters();
		pstmt3.clearParameters();
			pstmt5.clearParameters();
			pstmt5d.clearParameters();
			String[] temp_emp = (String[]) xg_list.get(i);
			// 重整销管帐号
			createXG(temp_emp[1], temp_emp[2], conn);
			if (!existed_map.containsValue(temp_emp[1])) {
				pstmt2.setString(1, temp_emp[1]);
				count2 = pstmt2.executeUpdate() + count2;
			}
			pstmt3.setString(1, temp_emp[1]);
			pstmt3.setString(2, temp_emp[1]);
			pstmt3.setString(3, temp_emp[0]);
			try {
				count3 = pstmt3.executeUpdate() + count3;
			} catch (Exception sqle) {
				logger.error(sqle.getMessage());
			throw new WantBatchException(sqle);
			}

			// 检测分公司是否存在，如果不存在则不执行对COMPANY_EMP的删除和新增操作 2014-09-02 modify mandy
			if(StringUtils.isNotBlank(temp_emp[0])) {
				
				pstmt5.setString(1, temp_emp[0]);
				pstmt5.setString(2, temp_emp[1]);
				pstmt5d.setString(1, temp_emp[1]);

				try {
					// logger.info(temp_emp[0]+"-"+temp_emp[1]+"-"+temp_emp[2]);
					count6 = pstmt5d.executeUpdate() + count6;
					count5 = pstmt5.executeUpdate() + count5;
				} catch (Exception sqle) {
					logger.info(temp_emp[0]+"-"+temp_emp[1]+"-"+temp_emp[2]);
					logger.error(sqle.getMessage());
					throw new WantBatchException(sqle);
				}
			}
			
			existed_map.put(temp_emp[1], temp_emp[1]);
		}

		logger.info(sqlcmd2_b + ": " + count2);
		logger.info(sqlcmd2_c + ": " + count3);
		logger.info(sqlinsertcompany_del + ": " + count5);
		logger.info(sqlinsertcompany_del + ": " + count6);
	
		// 更新营业所
		createBranchXGCustomerRel(xg_list_yys);
	logger.info("开始建立销管与分公司客户关系!共完成：" + xg_list.size());
	
		//執行分公司销管主管和营业所之间關係的sql
		PreparedStatement pstmt7 = null;
		int count7=0;
		pstmt7=conn.prepareStatement(sqldeleteXGyINGyS_del);
		count7=pstmt7.executeUpdate();
		logger.info("sqldeleteXGyINGyS_del 执行成功"+count7);
		pstmt7.close();
		PreparedStatement pstmt8 = null;
		int count8=0;
		pstmt8=conn.prepareStatement(sqlinsertXGyingYS);
		count8=pstmt8.executeUpdate();
		logger.info("sqlinsertXGyingYS 执行成功"+count8);
		rs.close();
		pstmt8.close(); 
		

		pstmt.close();
		pstmt2.close();
		pstmt3.close();
		pstmt5.close();
		pstmt5d.close();
		conn.close();
	}

	/**
	 * <pre>
	 * 建立销管帐号
	 * 建立步骤
	 * 1.取得销管与营业所关系。
	 * 2.循环取出销管与营业所关系。
	 * 3.建立销管帐号。
	 * 4.建立销管与营业所所有客户关系
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
	private void createXG(String emp_id, String emp_name, Connection conn)
			throws SQLException {
		// 检查帐号是否存在
		PreparedStatement pstmt = conn.prepareStatement(sqlisxgexisted);
		pstmt.setString(1, emp_id);
		ResultSet rs = pstmt.executeQuery();
		boolean flag = rs.next();
		rs.close();
		pstmt.close();

		if (flag) {
			// 存在，则更新资料 sqlupdatexgdata
			pstmt = conn.prepareStatement(sqlupdatexgdata);
			pstmt.setString(1, emp_id);
			pstmt.executeUpdate();
			// logger.info("更新帐号完成:"+emp_id);
		} else {
			// 不存在，则新增 sqlcreatexg
			pstmt = conn.prepareStatement(sqlcreatexg);
			pstmt.setString(1, emp_id);
			pstmt.setString(2, emp_name);
			pstmt.executeUpdate();
			// logger.info("新建帐号完成:"+emp_id);
		}
		pstmt.close();

		// 删除销管权限
		pstmt = conn.prepareStatement(sqldeletexgauth);
		pstmt.setString(1, emp_id);
		pstmt.executeUpdate();
		pstmt.close();

		// 建立销管权限
		pstmt = conn.prepareStatement(sqlcreatexgauth);
		pstmt.setString(1, emp_id);
		pstmt.executeUpdate();
		pstmt.close();
		// logger.info("新建权限完成:"+emp_id);
	}

	@Override
	public void execute() throws Exception {
		// TODO Auto-generated method stub
		createCompanyXGCustomerRel();
	}

}
