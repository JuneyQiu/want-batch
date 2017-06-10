package com.want.batch.job.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;

/**
 * <p>
 * Title:同步"地方"直营人员组织功能权限
 * </p>
 * 
 * <p>
 * Description: A.人员与功能组权限资料： 1.清空所有不存在emp表中的人员权限--所有渠道，非只有直营
 * 2.取得所有直营人员(A09),以POS_NAME,EMP_ID 3.依序取出员工
 * 4.清除该员工在KAAuthMap中的权限(清除该员工在3000~3003中的权限) 5.检查该员工是否有KAAuthMap中的权限
 * 6.依据该员工拥有的pos_name比对KAAuthMap给予权限 7.取下一个员工 B.人员与系统关系，人员与分公司关系：
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: Want Want group
 * </p>
 * 
 * @author David Luo
 * @version 1.0
 */
@Component
public class SyncKAOrg2 extends AbstractWantJob {
	protected final Log logger = LogFactory.getLog(SyncKAOrg2.class);

	private static HashMap<String, String[]> KAAuthMap = new HashMap<String, String[]>();
	private static HashSet<String> Final_emplist = new HashSet<String>();
	private static HashMap<String, String> UserTypeGroupRel = new HashMap<String, String>();
	/**
	 * 加入直营需要开通的岗位权限,若有多个权限， 直接在数组内加入也逗号分隔 ex: 3000,3009,3332
	 * 
	 * 增加直营与现渠销管权限
	 */
	private static String[] xdyd = { "3000" };// 巡店业代 - 2
	private static String[] kuzr = { "3001" };// 客户主任 - 31
	private static String[] qyzr = { "3001" };// 区域主任 - 31
	private static String[] kuzy = { "3002" };// 客户专员 -30
	private static String[] kaywzj = { "3003" };// 直营业务总监 - 12
	private static String[] ka_zyxiaoguan = { "3017" };// 销管 - 12

	public SyncKAOrg2() {
		// 加入现渠需要开通的岗位权限

		KAAuthMap.put("现渠巡店业代", xdyd);
		KAAuthMap.put("现渠业务主任", kuzr);
//		KAAuthMap.put("现渠课主管", kuzy);
//		KAAuthMap.put("现渠一课主管", kuzy);
//		KAAuthMap.put("现渠二课主管", kuzy);
//		KAAuthMap.put("现渠三课主管", kuzy);
//		KAAuthMap.put("现渠业务经理", kuzy);
//		KAAuthMap.put("现渠营运总监", kaywzj);

		KAAuthMap.put("巡店业代~", xdyd);
		KAAuthMap.put("客户主任~", kuzr);
		KAAuthMap.put("区域主任~", qyzr);
//		KAAuthMap.put("直营营运总监", kaywzj);

		// 加入直营权限 on 2012/10/22

		KAAuthMap.put("巡店业代", xdyd);
		KAAuthMap.put("中级巡店业代", xdyd);
		KAAuthMap.put("高级巡店业代", xdyd);
//		KAAuthMap.put("直营开单销管", ka_zyxiaoguan);
//		KAAuthMap.put("直营销管主管", ka_zyxiaoguan);// add on 2013/2/26

		KAAuthMap.put("客户主任", kuzr);
		KAAuthMap.put("驻统仓客户主任", kuzr);
		KAAuthMap.put("现渠客户主任", kuzr);
		KAAuthMap.put("宁波现渠客户主任", kuzr);
		KAAuthMap.put("杭州现渠客户主任", kuzr);
//		KAAuthMap.put("直营课主管", kuzy);
//		KAAuthMap.put("直营业务经理", kuzy);// add on 2013/2/26
//		KAAuthMap.put("直营业务总监", kaywzj);
//		KAAuthMap.put("现渠开单销管", ka_zyxiaoguan);
//		KAAuthMap.put("现渠销管课主管", ka_zyxiaoguan);// add on 2013/2/26

		//专员
		KAAuthMap.put("现渠业务经理", kuzy);
		KAAuthMap.put("现渠客户专员", kuzy);
		KAAuthMap.put("宁波现渠客户专员", kuzy);
		KAAuthMap.put("杭州现渠客户专员", kuzy);
		KAAuthMap.put("杭州现渠业务经理", kuzy);
		KAAuthMap.put("宁波现渠业务经理", kuzy);
		KAAuthMap.put("现渠课主管", kuzy);
		KAAuthMap.put("宁波现渠课主管", kuzy);
		KAAuthMap.put("现渠一课主管", kuzy);
		KAAuthMap.put("杭州现渠一课主管", kuzy);
		KAAuthMap.put("现渠二课主管", kuzy);
		KAAuthMap.put("杭州现渠二课主管", kuzy);
		KAAuthMap.put("现渠三课主管", kuzy);
		KAAuthMap.put("直营课主管", kuzy);
		KAAuthMap.put("宁波直营课主管", kuzy);
		KAAuthMap.put("杭州现渠行政经理", kuzy);
		KAAuthMap.put("宁波现渠行政经理", kuzy);
		//总监
		KAAuthMap.put("现渠行政总监", kaywzj);
		//销管
		KAAuthMap.put("现渠开单销管", ka_zyxiaoguan);
		KAAuthMap.put("现渠销管课主管", ka_zyxiaoguan);
		KAAuthMap.put("杭州现渠销管课主管", ka_zyxiaoguan);
		KAAuthMap.put("宁波现销管课主管", ka_zyxiaoguan);
		
		
		UserTypeGroupRel.put("3000", "2");
		UserTypeGroupRel.put("3003", "12");
		UserTypeGroupRel.put("3002", "30");
		UserTypeGroupRel.put("3001", "31");
		UserTypeGroupRel.put("3017", "3");
	}

	public void s_syncgnqx() throws SQLException {
		syncgnqx();
	}


	/**
	 * syncgnqx 同步功能权限
	 * <p>
	 * 同步功能权限
	 * </p>
	 * 同步功能权限
	 * 
	 * @param 没有输入值
	 * @return 没有返回值
	 * @throws SQLException
	 */
	public void syncgnqx() throws SQLException {
		SyncKAOrg2 ska = new SyncKAOrg2();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<String[]> empList = new ArrayList<String[]>();

		Connection conn = getICustomerConnection();
		conn.setAutoCommit(false);
		// 2.取得所有直营人员(A09)，现渠(A21)，且为地方的员工,以ORG_ID,POS_NAME,EMP_ID
		StringBuffer sqlcmd1 = new StringBuffer("");
		sqlcmd1.append(" select y.ORG_ID as COMPANY_HR_ID,y.POS_NAME as POS_NAME,y.EMP_ID as EMP_ID,y.EMP_NAME as EMP_NAME,decode(y.POS_PROPERTY_ID,'A09','24','A21','35') as DIVISION_SID from  ( ");
		sqlcmd1.append(" select c.ORG_ID as ORG_ID, c.POS_NAME as POS_NAME,c.POS_PROPERTY_ID as POS_PROPERTY_ID,a.EMP_ID as EMP_ID,a.EMP_NAME as EMP_NAME from EMP a  ");
		sqlcmd1.append(" inner join EMP_POSITION b on a.emp_id=b.emp_id  ");
		sqlcmd1.append(" inner join POSITION_B c on b.pos_id=c.pos_id and POS_PROPERTY_ID in ('A09','A21') and substr(c.ORG_ID,0,1)<>'1' ) y ");
		// sqlcmd1.append(" inner join ORGANIZATION_B x on y.ORG_ID=x.ORG_ID ");
		// sqlcmd1.append(" inner join ORGANIZATION_B z on x.ORG_PARENT_DEPT=z.ORG_ID ");
		// sqlcmd1.append(" inner join ORGANIZATION_B zz on zz.ORG_ID= RPAD(substr(z.ORG_PARENT_DEPT,0,4),8,'0')");
		// sqlcmd1.append(" where y.EMP_ID='00000481' ");
		//现渠直营 的销管的岗位属性变动为销管的岗位，修改了获取直营和现渠人员的sql
		sqlcmd1.append(" union select y.ORG_ID as COMPANY_HR_ID,y.POS_NAME as POS_NAME,y.EMP_ID as EMP_ID,y.EMP_NAME as EMP_NAME, ");
		sqlcmd1.append(" decode(substr(y.POS_NAME,1,2),'现渠','24','直营','35') as DIVISION_SID from  (  ");
		sqlcmd1.append(" select c.ORG_ID as ORG_ID, c.POS_NAME as POS_NAME,c.POS_PROPERTY_ID as POS_PROPERTY_ID,a.EMP_ID as EMP_ID,a.EMP_NAME as EMP_NAME from EMP a  ");
		sqlcmd1.append("  inner join EMP_POSITION b on a.emp_id=b.emp_id  inner join POSITION_B c on b.pos_id=c.pos_id and c.POS_PROPERTY_ID='B05'  ");
		sqlcmd1.append(" and (c.POS_NAME like '直营%' or c.POS_NAME like '现渠%') and substr(c.ORG_ID,0,1)<>'1' ) y ");
		pstmt = conn.prepareStatement(sqlcmd1.toString());
		rs = pstmt.executeQuery();
		while (rs.next()) {
			String data[] = {
					rs.getString("POS_NAME"),// 0:岗位名称
					rs.getString("EMP_ID"),// 1：员工id
					CreateEMPCompanyRel.getCompanyId(
							rs.getString("COMPANY_HR_ID"), "分公司", conn), // 2：所属分公司id
					rs.getString("DIVISION_SID"),// 3：所属division－sid
					rs.getString("EMP_NAME") };// 4：员工名称
			// if("00000481".equals(data[1]))logger.debug("Data******:"
			// +data[0]+"|"+ data[1]+"|"+data[2]+"|"+data[3]+"|"+data[4]);
			empList.add(data);
		}
		//现渠海南分，因为只有一个人，顾帮他同步进来
		String dd[] = {"现渠客户专员","00222703","C29","24","林鹄诗"};
		
		empList.add(dd);
		rs.close();
		pstmt.close();
		
		// 3.依序取出员工
		for (int i = 0; i < empList.size(); i++) {
			String[] kaemp = (String[]) empList.get(i);
				System.out.print("开始处理：" + kaemp[1]);
			// 置换empid to user_sid
			String user_sid = ska.getUserSid(conn, kaemp[1]);
			if ("".equals(user_sid))
				user_sid = createUserInfo(conn, kaemp);

			if (!"".equals(user_sid)) {
				// 新增员工与分公司关系
				syncUserCompanyRel(conn, kaemp);
				// 4.如果该员工为此次同步第一次新增权限，则清除该员工在KAAuthMap中的权限(清除该员工在3000~3003中的权限)
				if (!Final_emplist.contains(user_sid))
					ska.removeOldKAAuth(conn, user_sid);
				syncUserDivision(conn, kaemp);
				syncUserType(conn, kaemp);
				// 5.检查该员工是否有KAAuthMap中的权限
				if (KAAuthMap.containsKey(kaemp[0])) {
					// 6.依据该员工拥有的pos_name比对KAAuthMap给予权限。
					String[] usergroup_sidlist = KAAuthMap.get(kaemp[0]);

					for (int j = 0; j < usergroup_sidlist.length; j++) {
						// logger.debug("TEST:"+user_sid+"-"+usergroup_sidlist[j].toString());
						ska.insertAuthUserGroupRel(conn, user_sid,
								usergroup_sidlist[j]);
					}
					Final_emplist.add(user_sid);
				}
			}
			logger.debug("处理完毕：" + kaemp[1]);
		}
		
		conn.commit();
		conn.setAutoCommit(true);

		conn.close();
	}

	/**
	 * removeOldKAAuth 删除单一工号原本在KA的权限
	 * <p>
	 * 删除单一工号原本在KA的权限
	 * </p>
	 * 删除单一工号原本在KA的权限
	 * 
	 * @param conn
	 *            数据源
	 * @param user_sid
	 *            经销商网站中 USER_INFO_TBL.SID
	 * @return 没有返回值
	 */

	private void removeOldKAAuth(Connection conn, String user_sid)
			throws SQLException {

		// 准备删除此工号原本在KA且不是由用户手动建立的权限
		String sqlcmd = "delete AUTH_USER_GROUP_REL where USER_SID=? and (upper(CREATE_USER) not like 'MIS%' or USERGROUP_SID in ("
				+ getAllUserGroupSid() + ")) ";
		PreparedStatement pstmt = conn.prepareStatement(sqlcmd);
		pstmt.setInt(1, Integer.parseInt(user_sid));
		pstmt.execute();
		pstmt.close();

	}

	/**
	 * getUserSid 使用员工工号找到 icustomer.USER_INFO_TBL中的SID
	 * <p>
	 * 使用员工工号找到 icustomer.USER_INFO_TBL中的SID
	 * </p>
	 * 使用员工工号找到 icustomer.USER_INFO_TBL中的SID
	 * 
	 * @param conn
	 *            数据源
	 * @param account
	 *            员工工号
	 * @return String icustomer.USER_INFO_TBL中的SID
	 */
	private String getUserSid(Connection conn, String account)
			throws SQLException {
		String usersid = "";
		String sqlcmd = "select SID from  USER_INFO_TBL where ACCOUNT=?";
		PreparedStatement pstmt = conn.prepareStatement(sqlcmd);
		pstmt.setString(1, account);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next())
			usersid = rs.getString("SID");
		rs.close();
		pstmt.close();
		return usersid;
	}

	/**
	 * insertAuthUserGroupRel 建立人员与权限组关系
	 * <p>
	 * 建立人员与权限组关系
	 * </p>
	 * 将传入的资料建立在AUTH_USER_GROUP_REL中
	 * 
	 * @param conn
	 *            数据源
	 * @param user_sid
	 *            经销商网站中 USER_INFO_TBL.SID
	 * @param usergroup_sid
	 *            经销商网站中 权限群组sid
	 * @return 没有返回值
	 */
	private void insertAuthUserGroupRel(Connection conn, String user_sid,
			String usergroup_sid) {
		// 建立人员与权限组关系
		String sqlcmd = "insert into AUTH_USER_GROUP_REL values(?,?,'MIS_SYS',SYSDATE)";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sqlcmd);
			pstmt.setInt(1, Integer.parseInt(user_sid));
			pstmt.setInt(2, Integer.parseInt(usergroup_sid));
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException sqle) {
			logger.error("发生异常：user_sid:" + user_sid + ", usergroup_sid:"
					+ usergroup_sid + ", Message:" + sqle.getMessage());
		}
	}

	/**
	 * syncKAData 同步资料权限
	 * <p>
	 * 同步资料权限
	 * </p>
	 * a.KA_SYSTEM_EMP 系统与人员关系表：同步巡店业代、客户主任、区域主任对应的系统关系 步骤1~10
	 * b.KA_COMPANY_SYSTEM分公司系统关系表：通过客户对应系统及客户所属分公司取得该关系 步骤11~17
	 * 
	 * @param 没有输入值
	 * @return 没有返回值
	 * @throws SQLException 
	 */
	public void syncKAData() throws SQLException {
		// 1.取得巡店业代与系统关系
		StringBuffer sqlcmd1_1 = new StringBuffer("");
		sqlcmd1_1
				.append(" select (select EMP_ID from  HW09.EMP_INFO_TBL where SID=f.emp_sid) as EMP_ID,f.BUSINESS_ID  as SYS_ID from ( ");
		sqlcmd1_1
				.append(" select a.EMP_SID,e.BUSINESS_ID from HW09.ROUTE_INFO_TBL a  ");
		sqlcmd1_1
				.append(" inner join HW09.SUBROUTE_INFO_TBL b on a.SID=b.ROUTE_SID and a.yearmonth= to_char(sysdate,'yyyymm') ");
		sqlcmd1_1
				.append(" inner join HW09.SUBROUTE_STORE_TBL c on b.SID=c.SUBROUTE_SID  and b.yearmonth= to_char(sysdate,'yyyymm') ");
		sqlcmd1_1
				.append(" inner join HW09.STORE_INFO_TBL d on c.STORE_SID=d.SID ");
		sqlcmd1_1
				.append(" inner join HW09.STORE_BUSINESS_REL e on d.SID=e.STORE_SID ");
		sqlcmd1_1.append(" group by a.EMP_SID,e.BUSINESS_ID ) f ");

		// 2.客户主任以及区域主任都会维护到客户业代关系表，所以用客户业代关系表中的客户去找其系统
		StringBuffer sqlcmd1_2 = new StringBuffer("");
		sqlcmd1_2
				.append(" select USER_ID as EMP_ID,substr(CUSTOMER_SYSTEM,1,2) as SYS_ID  from DIVSION_SALES_CUSTOMER_REL a ");
		sqlcmd1_2
				.append(" inner join CUSTOMER_INFO_TBL b on a.CUSTOMER_ID=b.ID and a.CHANNEL_ID='C4' and b.status is null ");
		sqlcmd1_2
				.append(" and length(b.CUSTOMER_SYSTEM)>6 group by USER_ID,substr(CUSTOMER_SYSTEM,1,2) ");

		// 3.清空KA_SYSTEM_EMP 资料
		String deletesql1 = " delete from KA_SYSTEM_EMP ";

		// 4.将以上两个结果放到同一个list中，insert到 KA_SYSTEM_EMP
		HashMap<String, String[]> ka_map = new HashMap<String, String[]>();

		String insertsql1 = "insert into KA_SYSTEM_EMP values(?,?)";

		// 11.分公司系统关系表
		StringBuffer sqlcmd2_1 = new StringBuffer("");
		sqlcmd2_1
				.append("select b.COMPANY_ID as COMPANY_ID,a.SYS_ID as SYS_ID  from (select substr(SUBCITY_NAME,1,4)as THIRD_ID,substr(CUSTOMER_SYSTEM,1,2) as SYS_ID ");
		sqlcmd2_1
				.append(" from CUSTOMER_INFO_TBL where length(substr(CUSTOMER_SYSTEM,1,6))=6 group by substr(SUBCITY_NAME,1,4),substr(CUSTOMER_SYSTEM,1,2) ) a ");
		sqlcmd2_1
				.append(" inner join FULL_MARKET_LEVEL_VIEW b on a.THIRD_ID= b.THIRD_ID group by b.COMPANY_ID,a.SYS_ID ");

		// 12.清空KA_SYSTEM_EMP 资料
		String deletesql2 = " delete from KA_COMPANY_SYSTEM ";

		// 13.将结果 insert 到 KA_COMPANY_SYSTEM
		String insertsql2 = "insert into KA_COMPANY_SYSTEM values(?,?)";

		Connection conn = getICustomerConnection();
		conn.setAutoCommit(false);

		// 5.KA_SYSTEM_EMP
		// -------------------start------------------------------------------
		// 6.取得巡店业代与系统关系放入kasysemp_list
		PreparedStatement pstmt = conn.prepareStatement(sqlcmd1_1.toString());
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			String[] data = { rs.getString("EMP_ID"), rs.getString("SYS_ID") };
			ka_map.put(data[0] + "-" + data[1], data);
		}
		rs.close();
		pstmt.close();
		// 7.取得客户主任以及区域主任都对系统的关系
		pstmt = conn.prepareStatement(sqlcmd1_2.toString());
		rs = pstmt.executeQuery();
		while (rs.next()) {
			String[] data = { rs.getString("EMP_ID"), rs.getString("SYS_ID") };
			ka_map.put(data[0] + "-" + data[1], data);
		}
		rs.close();
		pstmt.close();

		// 8.如果有取得资料，先掺除KA_SYSTEM_EMP
		if (ka_map.size() > 0) {
			pstmt = conn.prepareStatement(deletesql1);
			pstmt.execute();
			pstmt.close();
		}
		// 9.准备insert到 KA_SYSTEM_EMP
		pstmt = conn.prepareStatement(insertsql1);
		Iterator it = ka_map.keySet().iterator();
		while (it.hasNext()) {
			String[] data = ka_map.get(it.next());
			pstmt.setString(1, data[0]);
			pstmt.setString(2, data[1]);
			pstmt.addBatch();
		}
		pstmt.executeBatch();
		pstmt.close();
		// 10.KA_SYSTEM_EMP
		// -------------------end------------------------------------------

		// 14.KA_COMPANY_SYSTEM
		// -------------------start------------------------------------------
		// 取得分公司与直营系统系统关系表
		ka_map.clear();
		pstmt = conn.prepareStatement(sqlcmd2_1.toString());
		rs = pstmt.executeQuery();
		while (rs.next()) {
			String[] data = { rs.getString("COMPANY_ID"),
					rs.getString("SYS_ID") };
			ka_map.put(data[0] + "-" + data[1], data);
		}
		rs.close();
		pstmt.close();
		// 15.如果有取得资料，先掺除KA_COMPANY_SYSTEM
		if (ka_map.size() > 0) {
			pstmt = conn.prepareStatement(deletesql2);
			pstmt.execute();
			pstmt.close();
		}
		// 16.准备insert到 KA_COMPANY_SYSTEM
		pstmt = conn.prepareStatement(insertsql2);
		it = ka_map.keySet().iterator();
		while (it.hasNext()) {
			String[] data = ka_map.get(it.next());
			pstmt.setString(1, data[0]);
			pstmt.setString(2, data[1]);
			pstmt.addBatch();
		}
		pstmt.executeBatch();
		pstmt.close();
		// 17.KA_SYSTEM_EMP
		// -------------------end------------------------------------------
		conn.commit();
		conn.setAutoCommit(true);
		conn.close();
	}

	// 建立员工基本资料
	private String createUserInfo(Connection conn, String[] userinfo)
			throws SQLException {
		// logger.debug("userinfo[0]：" + userinfo[0]);
		String user_sid = "";
		int user_type_sid = -1;
		// 取得user_type_sid
		if (KAAuthMap.containsKey(userinfo[0])) {
			String[] auth_group = (String[]) KAAuthMap.get(userinfo[0]);
			// logger.debug("auth_group[0]：" + auth_group[0]);

			if (auth_group != null && auth_group.length > 0) {
				// logger.debug("auth_group.length：" + auth_group.length);
				for (int i = 0; i < auth_group.length; i++) {
					logger.debug("auth_group[i]：" + auth_group[i]);
					if (UserTypeGroupRel.containsKey(auth_group[i])) {
						user_type_sid = Integer.parseInt(UserTypeGroupRel
								.get(auth_group[i]));
					}
				}
			}
		} else {
			logger.warn("KAAuthMap doesn't containsKey：" + userinfo[0]);
		}
		if (user_type_sid > 0) {
			// 建立user_info//1:account,2:password,3:user name,3:user_type_sid
			StringBuffer create_emp_sql = new StringBuffer(
					"insert into user_info_tbl "
							+ " (sid,ACCOUNT,PASSWORD,USER_NAME,USER_TYPE_SID,EMAIL,MOBILE,AREA_CODE,PHONE_NUM,STATUS,UPDATE_DATE,UPDATOR,CREATE_DATE,CREATOR,LAST_LOGIN_TIME,LAST_LOGIN_ADDS,CHANNEL_SID)  "
							+ " select USER_INFO_TBL_SID.nextval,?,USER_INFO_TBL_SID.currval,?,?,null,null,null,null,1,null,null,sysdate,'mis',null,null,2  from dual ");

			PreparedStatement pstmt = conn.prepareStatement(create_emp_sql
					.toString());
			pstmt.setString(1, userinfo[1]);
			pstmt.setString(2, userinfo[4]);
			pstmt.setInt(3, user_type_sid);
			pstmt.executeUpdate();
			pstmt.close();

			syncUserDivision(conn, userinfo);

		} else {
			logger.warn("can't create USER_info：" + userinfo[1]
					+ " -  user_type_sid:" + user_type_sid);
		}
		logger.info("created USER_info：" + userinfo[1] + ":" + user_sid);
		return user_sid;
	}

	private boolean syncUserDivision(Connection conn, String[] userinfo)
			throws SQLException {
		boolean flag = false;
		// 删除 DIVSION_USER_REL1:user_sid
		String user_sid = getUserSid(conn, userinfo[1]);
		
		// add 2014-09-02 mandy 添加判断，当事业部和人员都不为空的时候再执行操作
		if (!"".equals(user_sid) && StringUtils.isNotBlank(userinfo[3])) {
			StringBuffer delete_emp_div_sql1 = new StringBuffer(
					"delete DIVSION_USER_REL where USER_SID=?");
			PreparedStatement pstmt = conn.prepareStatement(delete_emp_div_sql1
					.toString());
			pstmt.setInt(1, Integer.parseInt(user_sid));
			pstmt.executeUpdate();
			pstmt.close();

			// 建立 DIVSION_USER_REL1:division_sid,2:user_sid
			StringBuffer create_emp_div_sql1 = new StringBuffer(
					"insert into DIVSION_USER_REL values( DIVSION_USER_REL_SEQ.nextval, ?,?)");
			pstmt = conn.prepareStatement(create_emp_div_sql1.toString());
			pstmt.setString(1, userinfo[3]);
			pstmt.setInt(2, Integer.parseInt(user_sid));
			pstmt.executeUpdate();
			pstmt.close();

			// 删除 USER_DIVISION 1:user_id
			StringBuffer delete_emp_div_sql2 = new StringBuffer(
					"delete USER_DIVISION where USER_ID=? ");
			pstmt = conn.prepareStatement(delete_emp_div_sql2.toString());
			pstmt.setString(1, userinfo[1]);
			pstmt.executeUpdate();
			pstmt.close();

			// 建立USER_DIVISION//1:user_id,2:division_sid
			StringBuffer create_emp_div_sql2 = new StringBuffer(
					"insert into USER_DIVISION values(?,?,'MIS-SYS',sysdate,null,null) ");
			pstmt = conn.prepareStatement(create_emp_div_sql2.toString());
			pstmt.setString(1, userinfo[1]);
			pstmt.setString(2, userinfo[3]);
			pstmt.executeUpdate();
			pstmt.close();
			flag = true;
		}
		return flag;
	}

	private boolean syncUserType(Connection conn, String[] userinfo)
			throws SQLException {
		boolean flag = false;
		int user_type_sid = -1;
		if (KAAuthMap.containsKey(userinfo[0])) {
			String[] auth_group = (String[]) KAAuthMap.get(userinfo[0]);
			// logger.debug("auth_group[0]：" + auth_group[0]);

			if (auth_group != null && auth_group.length > 0) {
				// logger.debug("auth_group.length：" + auth_group.length);
				for (int i = 0; i < auth_group.length; i++) {
					logger.debug("auth_group[i]：" + auth_group[i]);
					if (UserTypeGroupRel.containsKey(auth_group[i])) {
						user_type_sid = Integer.parseInt(UserTypeGroupRel
								.get(auth_group[i]));
					}
				}
			}
		}
		logger.debug("user_type_sid：" + user_type_sid);

		if (user_type_sid > 0) {
			PreparedStatement pstmt = conn
					.prepareStatement("update USER_INFO_TBL set user_type_sid = ? where account=?");
			pstmt.setInt(1, user_type_sid);
			pstmt.setString(2, userinfo[1]);
			pstmt.executeUpdate();
			pstmt.close();
			flag = true;
		}
		return flag;
	}

	private boolean syncUserCompanyRel(Connection conn, String[] userinfo)
			throws SQLException {
		boolean flag = false;
		String deleteRelSQL = "delete COMPANY_EMP where EMP_ID = ?";
		String checkHRORGID = "select COMPANY_SAP_ID from COMPANY_SAP_HR_REL where COMPANY_HR_ID=?";
		String createRelSQL = " insert into COMPANY_EMP values(?,?)";
		PreparedStatement pstmt = conn.prepareStatement(deleteRelSQL);
		pstmt.setString(1, userinfo[1]);
		pstmt.executeUpdate();
		pstmt.close();

		pstmt = conn.prepareStatement(checkHRORGID);
		pstmt.setString(1, userinfo[2]);
		ResultSet rs = pstmt.executeQuery();
		String company_sap_id = "C00";

		if (rs.next()) {
			company_sap_id = rs.getString("COMPANY_SAP_ID");
		}
		//海南分就这一个人，特殊处理
		if (userinfo[1].equals("00222703")) {
			company_sap_id="C29";
		}
		rs.close();
		pstmt.close();
		logger.debug("company_sap_id：" + company_sap_id
				+ "; userinfo[1]:" + userinfo[1]);
		pstmt = conn.prepareStatement(createRelSQL);
		pstmt.setString(1, company_sap_id);
		pstmt.setString(2, userinfo[1]);
		pstmt.executeUpdate();
		pstmt.close();

		flag = true;
		logger.debug("flag：" + flag);
		return flag;
	}

	private String getAllUserGroupSid() {

		StringBuffer kaautharray = new StringBuffer("");
		String common = "";
		Iterator<String> it = SyncKAOrg2.KAAuthMap.keySet().iterator();
		while (it.hasNext()) {
			String[] usergroup_sid = SyncKAOrg2.KAAuthMap.get(it.next());
			for (int a = 0; a < usergroup_sid.length; a++) {
				kaautharray.append(common);
				kaautharray.append(usergroup_sid[a]);
				common = ",";
			}
		}
		logger.debug("kaautharray.toString():" + kaautharray.toString());
		return kaautharray.toString();
	}

	@Override
	public void execute() throws Exception {
		// TODO Auto-generated method stub
		syncgnqx();
	}

}
