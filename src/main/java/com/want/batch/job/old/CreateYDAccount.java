package com.want.batch.job.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description: 业代账号及权限的同步排程
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
public class CreateYDAccount extends AbstractWantJob {

	@Override
	public void execute() throws SQLException {
		createSalesAccount();
	}

	private static String[] sqlcmd = {
			// 取得县城业代 index = 0,USERGROUP_SID ＝9
			" select  a.EMP_ID as ACCOUNT,a.EMP_NAME as USER_NAME,2 as USER_TYPE_SID,1 as CHANNEL_SID,9 as USERGROUP_SID   from EMP a "
					+ " inner join EMP_POSITION b on a.emp_id=b.emp_id "
					+ " inner join POSITION_B c on b.pos_id=c.pos_id  "
					//县城，县城乳饮，县城休闲
					// modify 2014-07-09 mandy 将POS_NAME like'%业代%'修改成POS_TYPE_ID='002' or POS_TYPE_ID='003'
					+ " WHERE c.POS_PROPERTY_ID in ('A10','A18','A19')  and (POS_TYPE_ID='" + RoleConstant.SALES + "' or POS_TYPE_ID='" + RoleConstant.ZSSALES + "') ",
			// 取得城区业代 index =1, index 1,USERGROUP_SID ＝99
			" select  a.EMP_ID as ACCOUNT,a.EMP_NAME as USER_NAME,2 as USER_TYPE_SID,2 as CHANNEL_SID,99  as USERGROUP_SID   from EMP a "
					+ " inner join EMP_POSITION b on a.emp_id=b.emp_id "
					+ " inner join POSITION_B c on b.pos_id=c.pos_id  "
					//--乳品利乐A26,乳品罐装A27,乳品营销A03,休一A05,休二A06,休闲点心营销A25,休闲食品事业群-A13,冰品营销A16,冷链营销A28
					//--哎哟点心营销A45,散装营销A23,旺礼营销A24,米果炒货营销A01,糖果果冻营销A02,膨化饼干营销A22,通路A08,配送A39,配送乳饮A40,配送休闲A41
					//--饮品营销A04,黑皮营销A17
					//+ " WHERE c.POS_PROPERTY_ID in ('A26','A27','A03','A05','A06','A25','A13','A16','A28','A45','A23','A24','A01','A02','A22','A08','A39','A40','A41','A04','A17')  "
					+ " WHERE c.POS_PROPERTY_ID in ('A01','A02','A03','A04','A09','A16','A17','A20','A21','A22','A23','A26','A27','A28','A39','A40','A41','A47','A48','A49','A50')  "
					//去掉车销业代，2013/03/28 changed by ji
					+ " and  POS_NAME not like '%行直业代%'  and   POS_NAME not like '%行直兼车销业代%' and   "
					// modify 2014-07-09 mandy 将POS_NAME like'%业代%'修改成POS_TYPE_ID='002' or POS_TYPE_ID='003'
					+ " (POS_TYPE_ID='" + RoleConstant.SALES + "' or POS_TYPE_ID='" + RoleConstant.ZSSALES + "') ",
			// 取得城区行直业代 index =2,index 2,USERGROUP_SID ＝1001
			" select  a.EMP_ID as ACCOUNT,a.EMP_NAME as USER_NAME,2 as USER_TYPE_SID,2 as CHANNEL_SID,1001 as USERGROUP_SID  from EMP a "
					+ " inner join EMP_POSITION b on a.emp_id=b.emp_id "
					+ " inner join POSITION_B c on b.pos_id=c.pos_id  "
					//--乳品利乐A26,乳品罐装A27,乳品营销A03,休一A05,休二A06,休闲点心营销A25,休闲食品事业群-A13,冰品营销A16,冷链营销A28
					//--哎哟点心营销A45,散装营销A23,旺礼营销A24,米果炒货营销A01,糖果果冻营销A02,膨化饼干营销A22,通路A08,配送A39,配送乳饮A40,配送休闲A41
					//--饮品营销A04,黑皮营销A17
					+ "  WHERE c.POS_PROPERTY_ID in ('A01','A02','A03','A04','A09','A16','A17','A20','A21','A22','A23','A26','A27','A28','A39','A40','A41','A47','A48','A49','A50') and  POS_NAME ='行直业代' ",
			// 取得城区行直兼车销业代 index=3index 3,USERGROUP_SID ＝1001
			" select a.EMP_ID as ACCOUNT,a.EMP_NAME as USER_NAME,2 as USER_TYPE_SID,2 as CHANNEL_SID,1001 as USERGROUP_SID   from EMP a "
					+ " inner join EMP_POSITION b on a.emp_id=b.emp_id "
					+ " inner join POSITION_B c on b.pos_id=c.pos_id  "
					//--乳品利乐A26,乳品罐装A27,乳品营销A03,休一A05,休二A06,休闲点心营销A25,休闲食品事业群-A13,冰品营销A16,冷链营销A28
					//--哎哟点心营销A45,散装营销A23,旺礼营销A24,米果炒货营销A01,糖果果冻营销A02,膨化饼干营销A22,通路A08,配送A39,配送乳饮A40,配送休闲A41
					//--饮品营销A04,黑皮营销A17
					+ " WHERE c.POS_PROPERTY_ID in ('A01','A02','A03','A04','A09','A16','A17','A20','A21','A22','A23','A26','A27','A28','A39','A40','A41','A47','A48','A49','A50') and   POS_NAME = '行直兼车销业代' " };

	// 检查帐号是否建立
	private static String sqlcmd5 = "select 1 from USER_INFO_TBL where account=? ";
	// 若有，更新帐号资料
	private static String sqlcmd6 = "update  USER_INFO_TBL set status=1, user_type_sid=?, channel_sid=?, UPDATE_DATE=SYSDATE, UPDATOR='MIS' where account=?";
	// 若没有，建立帐号资料
	private static String sqlcmd7 = "insert into user_info_tbl "
			+ " (sid,ACCOUNT,PASSWORD,USER_NAME,USER_TYPE_SID,EMAIL,MOBILE,AREA_CODE,PHONE_NUM,STATUS,UPDATE_DATE,UPDATOR,CREATE_DATE,CREATOR,LAST_LOGIN_TIME,LAST_LOGIN_ADDS,CHANNEL_SID)  "
			+ " select USER_INFO_TBL_SID.nextval,?,USER_INFO_TBL_SID.currval,?,?, null,null,null,null,1,null,null,sysdate,'mis',null,null,?  from dual ";
	// 删除此帐号权限
	private static String sqlcmd8 = "delete AUTH_USER_GROUP_REL where user_sid =(select sid from user_info_tbl where account = ?) ";
	// 新增此帐号权限
	private static String sqlcmd9 = "insert into AUTH_USER_GROUP_REL(USER_SID, USERGROUP_SID, CREATE_USER, CREATE_DATE)  select sid ,?, 'mis', sysdate  from  USER_INFO_TBL where account =? ";
	// 检查帐号权限是否建立
	private static String sqlcmd10 = "select 1 from AUTH_USER_GROUP_REL where USERGROUP_SID=? and USER_SID = (select sid from USER_INFO_TBL where  account =?) ";

	public CreateYDAccount() {
	}


	/**
	 * 建立业代帐号 建立步骤 1.取得HR系统中的业务资料（县城，城区业代，城区行直业代，城区行直＋车销夜代） 2.开始循环取得的业务帐号
	 * 3.检查号是否存在 4.若存在，则更新帐号状态，更新帐号类型，更新渠道 5.若不在，则新增帐号
	 * 6.删除已有权限（现在先不删除权限--20100418） 7.建立新权限
	 * 
	 * @throws SQLException
	 */
	public void createSalesAccount() throws SQLException {
		Connection conn = getICustomerConnection();
		ArrayList<String[]> emp_list = new ArrayList<String[]>();

		// 取得所有业务帐号与基本资料
		logger.info("取得所有业务帐号与基本资料!");
		for (int i = 0; i < sqlcmd.length; i++) {
			PreparedStatement pstmt = conn.prepareStatement(sqlcmd[i]);
			logger.debug(sqlcmd[i]);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String[] temp_emp = { rs.getString("ACCOUNT"),
						rs.getString("USER_NAME"),
						rs.getString("USER_TYPE_SID"),
						rs.getString("CHANNEL_SID"),
						rs.getString("USERGROUP_SID") };
				emp_list.add(temp_emp);
			}
			rs.close();
			pstmt.close();
		}
		logger.info("取得所有业务帐号与基本资料!数量：" + emp_list.size());
		// 准备更新
		PreparedStatement pstmt5 = conn.prepareStatement(sqlcmd5);
		PreparedStatement pstmt6 = conn.prepareStatement(sqlcmd6);
		PreparedStatement pstmt7 = conn.prepareStatement(sqlcmd7);
		PreparedStatement pstmt8 = conn.prepareStatement(sqlcmd8);
		PreparedStatement pstmt9 = conn.prepareStatement(sqlcmd9);
		PreparedStatement pstmt10 = conn.prepareStatement(sqlcmd10);

		/*
		 * 开始循环取得的业务帐号 1.检查号是否存在 2.若存在，则更新帐号状态，更新帐号类型，更新渠道 3.若不在，则新增帐号
		 * 4.删除已有权限（现在先不删除权限--20100418） 5.建立新权限 temp_emp[0]:"ACCOUNT"
		 * temp_emp[1]:"EMP_NAME" temp_emp[2]:"USER_TYPE_SID"
		 * temp_emp[3]:"CHANNEL_SID" temp_emp[4]:"USERGROUP_SID"
		 */
		logger.info("开始循环取得的业务帐号");
		for (int j = 0; j < emp_list.size(); j++) {
			try {
				// 清空变量
				pstmt5.clearParameters();
				pstmt6.clearParameters();
				pstmt7.clearParameters();
				pstmt8.clearParameters();
				pstmt9.clearParameters();
				pstmt10.clearParameters();

				String[] temp_emp = (String[]) emp_list.get(j);
				// 1.检查号是否存在
				pstmt5.setString(1, temp_emp[0]);
				ResultSet rs = pstmt5.executeQuery();

				if (rs.next()) {
					// 2.若存在，则更新帐号状态，更新帐号类型，更新渠道
					pstmt6.setString(1, temp_emp[2]);
					pstmt6.setString(2, temp_emp[3]);
					pstmt6.setString(3, temp_emp[0]);
					pstmt6.executeUpdate();
				} else {
					// 3.若不在，则新增帐号
					pstmt7.setString(1, temp_emp[0]);
					pstmt7.setString(2, temp_emp[1]);
					pstmt7.setString(3, temp_emp[2]);
					pstmt7.setString(4, temp_emp[3]);
					logger.info("工号：" + temp_emp[0]);
					pstmt7.executeUpdate();
				}
				rs.close();

				// 4.删除已有权限（现在先不删除权限--20100418）
				// pstmt8.setString(1,temp_emp[0]);
				// pstmt8.executeUpdate();

				//检查此权限是否存在
				pstmt10.setString(1, temp_emp[4]);
				pstmt10.setString(2, temp_emp[0]);
				rs = pstmt10.executeQuery();
				if (!rs.next()) {
					// 5.建立新权限
					pstmt9.setString(1, temp_emp[4]);
					pstmt9.setString(2, temp_emp[0]);
					pstmt9.executeUpdate();
				} else logger.error("already exit !"+temp_emp[4]+"-----"+temp_emp[0]);
				rs.close();
				
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		}
		pstmt5.close();
		pstmt6.close();
		pstmt7.close();
		pstmt9.close();
		logger.info("业代账号权限排程结束！");

		conn.close();
	}

}
