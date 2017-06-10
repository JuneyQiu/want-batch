package com.want.batch.job.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

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
public class SyncDailyReportRevert extends AbstractWantJob {

	protected final Log sublogger = LogFactory
			.getLog(SyncDailyReportRevert.class);

	private boolean delete = false;
	private boolean city = false;
	private boolean town = false;

	private String s_today;
	private String s_current_month;
	private String s_first_date;
	private String s_after_14;
	private String s_last_day;

	public void set(String currentYearMonth, String startDay, String endDay) {
		s_current_month = currentYearMonth;
		s_today = s_current_month + startDay;
		s_after_14 = s_current_month + endDay;
		s_first_date = currentYearMonth + "01";
		s_last_day = s_after_14;
	}

//	public void set(String currentYearMonth, String startDay, String endDay,
//			int runCount) {
//		set(currentYearMonth, startDay, endDay);
//		this.runCount = runCount;
//	}

	@Override
	public void execute() throws SQLException {

//		if (++executeCount > runCount) {
//			sublogger.info("此次还原已经执行完成，无需再次执行！");
//			return;
//		}

		sublogger.info("本月:" + s_current_month);
		sublogger.info("本月第一天:" + s_first_date);
		sublogger.info("今天:" + s_today);
		sublogger.info("据今14天候:" + s_after_14);
		sublogger.info("本月最后一天:" + s_last_day);

		sublogger.info("日报总排程-START :" + new Date(System.currentTimeMillis()));

		sublogger.info("START syncTempRouteTbl:"
				+ new Date(System.currentTimeMillis()));
		syncTempRouteTbl();
		backupTempRouteTbl();
		sublogger.info("end  syncTempRouteTbl:"
				+ new Date(System.currentTimeMillis()));

		if (isDelete()) {
			if (isCity() && isTown()) {
				this.deleteDailyData();
			} else if (isCity()) {
				this.deleteCityDailyData();
			} else if (isTown()) {
				this.deleteTownDailyData();
			}
		}

		// 城区
		if (isCity()) {
			this.createCityDailyReport();
		}

		// 县城
		if (isTown()) {
			this.createTownDailyReport();
		}

		sublogger.info("日报总排程-END:" + new Date(System.currentTimeMillis()));

	}

	private void backupTempRouteTbl() {
		getiCustomerJdbcOperations()
				.update("INSERT INTO TEMP_ROUTE_TBL_BAK SELECT A.*, SYSDATE FROM TEMP_ROUTE_TBL A");
	}

	/**
	 * Modify by Li_Guojun on 2013/07/31 for add CDY-11 in Daily Report
	 * Modify by Li_Guojun on 2013/07/23 for add CC5-16 in Daily Report
	 * Modify by Li_Guojun on 2013/04/27 for add CCH-27 CCI-28 in Daily Report
	 * Modify by Li_Guojun on 2013/03/01 for add CCF-25 CCG-26 in Daily Report
	 * Modify by David Luo on 2012/03/06 for add CC4-15 in Daily Report Modify
	 * by David Luo on 2012/05/07 for update CC4-15 to CC8-15 in Daily Report
	 * Modify by David Luo on 2012/11/30 for adding CCD(36)-21
	 * Modify by Ryan on 2015/10/15 查询条件中增加新渠道,38,39,40,41,42,43,44,45
	 * 
	 * @throws SQLException
	 */
	private void syncTempRouteTbl() throws SQLException {
		// 2014-07-10 mirabelle add 添加哎吆-点心project_sid=30
		// 2015-02-02 mandy add 添加32CCP, 33CCQ, 34CCR
		StringBuffer trunc_temp_route_sql = new StringBuffer(
				" delete from TEMP_ROUTE_TBL ");
		StringBuffer insert_temp_route_sql = new StringBuffer(
				" insert into TEMP_ROUTE_TBL ");
		insert_temp_route_sql
				.append("  select f.SAP_ID as COMPANY_ID,e.SAP_ID as BRANCH_ID, ");
		insert_temp_route_sql
				.append("  (select lpad(FORWARDER_ID,10,'0') from  HW09.FORWARDER_INFO_TBL where sid= a.FORWARDER_SID) as FORWARDER_ID , ");
		insert_temp_route_sql
				.append("  (select EMP_ID from  HW09.EMP_INFO_TBL where sid =a.EMP_SID) as EMP_ID, ");
		insert_temp_route_sql
				.append("  (select EMP_SALE_TYPE_SID from  HW09.EMP_INFO_TBL where sid =a.EMP_SID) as EMP_SALE_TYPE,a.ROUTE_ATT_SID, ");
		insert_temp_route_sql
				.append("  decode(PROJECT_SID,'14','CC1','10','CC2','12','CC3','4','CDI','17','CC6','18','CC7','15','CC8',21,'CCD','1','CD7','22','CC9','23','CCA','25','CCF','26','CCG','27','CCH','28','CCI','16','CC5','11','CDY','30','CCN','32','CCP','33','CCQ','34','CCR','38','CHE5','39','CHE6','40','CHE7','41','CHE8','42','CHE9','43','CHEA','44','CHEB','45','CHEC') as CREDIT_ID,"
						+ " b.sid,b.SUBROUTE_NAME, d.sid,d.ACREAGE  from HW09.ROUTE_INFO_TBL a ");
		insert_temp_route_sql
				.append("  inner join HW09.SUBROUTE_INFO_TBL b on a.SID=b.ROUTE_SID and a.YEARMONTH=? and a.ROUTE_ATT_SID in(1,2,3) "
						+ " and a.project_sid in (1,4,12,10,11,14,16,17,18,15,21,22,23,25,26,27,28,30,32,33,34,38,39,40,41,42,43,44,45)  ");
		insert_temp_route_sql
				.append("  and b.VISIT_DATE >=to_date(?,'yyyymmdd') and  b.VISIT_DATE <=to_date(?,'yyyymmdd') ");
		insert_temp_route_sql
				.append("  inner join  HW09.SUBROUTE_STORE_TBL c on b.sid =c.SUBROUTE_SID and c.MUST_VISIT='1'  ");
		insert_temp_route_sql
				.append("  inner join  HW09.store_info_tbl d on c.store_sid=d.sid ");
		insert_temp_route_sql
				.append("  inner join  HW09.BRANCH_INFO_TBL e on a.branch_sid=e.sid ");
		insert_temp_route_sql
				.append("  inner join  HW09.COMPANY_INFO_TBL f on e.company_sid=f.sid ");
		insert_temp_route_sql
		        .append("  where b.sid in ( select a.SUBROUTE_SID from hw09.ROUTE_INFO_VIEW a where a.yearmonth= ? and ");
		insert_temp_route_sql
                .append("  (a.COMPANY_SID  in (18,31,22) or ( a.COMPANY_SID  not in (18,31,22) and a.PROJECT_SID =16 ))) ");

		// 2015-02-02 mandy 添加最后一句SQL含义：业代日报18,31,22这三个分公司没上sfa所以要用业代日报，那不在这三个分公司里的，有些渠道是没上sfa的
		// 业代日报只取包头，乌鲁木齐，兰州三个分公司的资料，但冰品渠道取全部分公司资料。
		
		Connection conn = null;
		PreparedStatement pstmt = null;

		conn = this.getICustomerConnection();
		pstmt = conn.prepareStatement(trunc_temp_route_sql.toString());
		pstmt.executeUpdate();
		pstmt.close();

		pstmt = conn.prepareStatement(insert_temp_route_sql.toString());
		pstmt.setString(1, s_current_month);
		pstmt.setString(2, s_today);
		pstmt.setString(3, s_after_14);
		pstmt.setString(4, s_current_month);
		pstmt.executeUpdate();
		pstmt.close();
		pstmt.close();
		conn.close();

	}

	/**
	 * 删除县城
	 * 
	 * @throws SQLException
	 */
	private void deleteTownDailyData() throws SQLException {
		sublogger.info("START deleteTownDailyData:"
				+ new Date(System.currentTimeMillis()));
		deleteDailyData("4,17,18");
		sublogger.info("end deleteTownDailyData:"
				+ new Date(System.currentTimeMillis()));
	}

	/**
	 * 删除城区 modify by LiGuoJun for plus project sid 11 in deleteDailyData on
	 * 2013/07/31
	 * 
	 * 2015-02-02 mandy add 32,33,34
	 * Modify by Ryan on 2015/10/15 查询条件中增加新渠道,38,39,40,41,42,43,44,45
	 * 
	 * @throws SQLException
	 */
	private void deleteCityDailyData() throws SQLException {
		sublogger.info("START deleteCityDailyData:"
				+ new Date(System.currentTimeMillis()));
		deleteDailyData("1,10,12,14,15,16,21,22,23,25,26,27,28,11,30,32,33,34,38,39,40,41,42,43,44,45");
		sublogger.info("end deleteCityDailyData:"
				+ new Date(System.currentTimeMillis()));
	}

	/**
	 * 删除县城 & 城区 modify by LiGuoJun for plus project sid 11  in
	 * deleteDailyData on 2013/07/31
	 * 
	 * 2015-02-02 mandy add 32,33,34
	 * Modify by Ryan on 2015/10/15 查询条件中增加新渠道,38,39,40,41,42,43,44,45
	 * 
	 * @throws SQLException
	 */
	private void deleteDailyData() throws SQLException {
		sublogger.info("START deleteDailyData:"
				+ new Date(System.currentTimeMillis()));
		deleteDailyData("1,4,17,18,10,12,14,15,16,21,22,23,25,26,27,28,11,30,32,33,34,38,39,40,41,42,43,44,45");
		sublogger.info("end deleteDailyData:"
				+ new Date(System.currentTimeMillis()));
	}

	private void deleteDailyData(String projectSids) throws SQLException {

		StringBuffer del_sql_1 = new StringBuffer(
				" delete from TEMP_DR_SID  ");
		StringBuffer del_sql_2 = new StringBuffer(
				"  insert into TEMP_DR_SID select dr_sid from daily_report where subroute_sid in ( ");
		del_sql_2
				.append(" select b.sid  from HW09.route_info_tbl a  inner join HW09.subroute_info_tbl b on a.sid=b.route_sid ");
		del_sql_2
				.append("  where   b.VISIT_DATE =to_date(?,'yyyymmdd') and a.ROUTE_ATT_SID in(1,2,3) and a.project_sid in ("
						+ projectSids + ") )");

		StringBuffer del_DR_sql = new StringBuffer(
				"  delete  daily_report where  dr_sid in (select dr_sid from TEMP_DR_SID) ");

		Connection conn = null;
		int start_today = Integer.parseInt(s_today);
		int end_today = Integer.parseInt(s_last_day);

		conn = getICustomerConnection();
		PreparedStatement pstmt1 = conn.prepareStatement(del_sql_1.toString());
		PreparedStatement pstmt2 = conn.prepareStatement(del_sql_2.toString());
		PreparedStatement pstmt3 = conn.prepareStatement(del_DR_sql.toString());

		for (int i = start_today; i < end_today + 1; i++) {
			try {
				pstmt1.executeUpdate();

				sublogger.info("Delete date = " + i);
				pstmt2.clearParameters();
				pstmt2.setString(1, String.valueOf(i));
				int row = pstmt2.executeUpdate();
				sublogger.info("Delete :" + row + " rows!");

				pstmt3.executeUpdate();
			} catch (Exception s) {
				s.printStackTrace();
				logger.error(s.getMessage());
			}
		}
		pstmt1.close();
		pstmt2.close();
		pstmt3.close();
		conn.close();
	}

	// Modify by Ryan on 2015/10/15 查询条件中增加新渠道,'CHE5','CHE6','CHE7','CHE8','CHE9','CHEA','CHEB','CHEC'
	private void createCityDailyReport() throws SQLException {

		sublogger.info("强网-START createDailyReport1:"
				+ new Date(System.currentTimeMillis()));
		// 2014-07-10 mirabelle add 添加哎吆-点心credit_id=CCN;2014-11-25 mirabelle add 调整面积范围
		// 2015-02-02 mandy add CCP配送一线、CCQ配送二线、CCR配送三线
		StringBuffer sql1 = new StringBuffer(" insert into DAILY_REPORT ");
		sql1.append(" select DR_SID_SEQ.nextval,SUBROUTE_SID,STORE_SID,LV_5_ID,null,EXHIBIT_STANDARD,null,'MIS_SYS',sysdate,null,null  from ");
		sql1.append(" (select s.SUBROUTE_SID,s.STORE_SID,b.LV_5_ID,EXHIBIT_STANDARD ");
		sql1.append("  from TEMP_ROUTE_TBL s  ");
		sql1.append(" inner join  STORE_PROD_SERIES b on  s.BRANCH_ID=b.BRANCH_ID and s.ROUTE_TYPE in (1,2,3) and  b.EFFECTIVE_DATE=to_date(?,'yyyymmdd') and b.RECOMMEND_ID='001' "
				+ " and s.CREDIT_ID IN ('CC1','CC2','CC3','CC8','CCD','CC9','CCA','CD7','CCF','CCG','CCH','CCI','CC5','CDY','CCN','CCP','CCQ','CCR','CHE5','CHE6','CHE7','CHE8','CHE9','CHEA','CHEB','CHEC') ");
		sql1.append(" inner join STORE_AREA_TYPE a on b.STORE_AREA_ID = a.STORE_AREA_ID   ");
		sql1.append(" inner join PROD_CUSTOMER_REL d on s.CREDIT_ID=d.KKBER  and s.FORWARDER_ID = d.customer_id ");
		sql1.append(" inner join PROD_INFO_TBL e on d.MATNR=e.prod_id and b.LV_5_ID=e.LV_5_ID ");
		sql1.append("  where s.ACREAGR<50   and  s.ACREAGR>=0 and nvl(AREA_UPPER_LIMIT,0) =50   and nvl(AREA_LOWER_LIMIT,0)=0  ");
		sql1.append(" group by  s.SUBROUTE_SID,s.STORE_SID,b.LV_5_ID,EXHIBIT_STANDARD) ");

		StringBuffer sql2 = new StringBuffer(" insert into DAILY_REPORT ");
		sql2.append(" select DR_SID_SEQ.nextval,SUBROUTE_SID,STORE_SID,LV_5_ID,null,EXHIBIT_STANDARD,null,'MIS_SYS',sysdate,null,null  from ");
		sql2.append(" (select s.SUBROUTE_SID,s.STORE_SID,b.LV_5_ID,EXHIBIT_STANDARD ");
		sql2.append("  from TEMP_ROUTE_TBL s  ");
		sql2.append(" inner join  STORE_PROD_SERIES b on  s.BRANCH_ID=b.BRANCH_ID and s.ROUTE_TYPE in (1,2,3) and  b.EFFECTIVE_DATE=to_date(?,'yyyymmdd')  and b.RECOMMEND_ID='001'  "
				+ " and s.CREDIT_ID IN ('CC1','CC2','CC3','CC8','CCD','CC9','CCA','CD7','CCF','CCG','CCH','CCI','CC5','CDY','CCN','CCP','CCQ','CCR','CHE5','CHE6','CHE7','CHE8','CHE9','CHEA','CHEB','CHEC') ");
		sql2.append(" inner join STORE_AREA_TYPE a on b.STORE_AREA_ID = a.STORE_AREA_ID   ");
		sql2.append(" inner join PROD_CUSTOMER_REL d on s.CREDIT_ID=d.KKBER  and s.FORWARDER_ID = d.customer_id ");
		sql2.append(" inner join PROD_INFO_TBL e on d.MATNR=e.prod_id and b.LV_5_ID=e.LV_5_ID ");
		sql2.append("  where s.ACREAGR<100   and  s.ACREAGR>=50 and nvl(AREA_UPPER_LIMIT,0) =100   and nvl(AREA_LOWER_LIMIT,0)=50  ");
		sql2.append(" group by  s.SUBROUTE_SID,s.STORE_SID,b.LV_5_ID,EXHIBIT_STANDARD) ");

		StringBuffer sql3 = new StringBuffer(" insert into DAILY_REPORT ");
		sql3.append(" select DR_SID_SEQ.nextval,SUBROUTE_SID,STORE_SID,LV_5_ID,null,EXHIBIT_STANDARD,null,'MIS_SYS',sysdate,null,null  from ");
		sql3.append(" (select s.SUBROUTE_SID,s.STORE_SID,b.LV_5_ID,EXHIBIT_STANDARD ");
		sql3.append("  from TEMP_ROUTE_TBL s  ");
		sql3.append(" inner join  STORE_PROD_SERIES b on  s.BRANCH_ID=b.BRANCH_ID and s.ROUTE_TYPE in (1,2,3) and  b.EFFECTIVE_DATE=to_date(?,'yyyymmdd')  and b.RECOMMEND_ID='001'  "
				+ " and s.CREDIT_ID IN ('CC1','CC2','CC3','CC8','CCD','CC9','CCA','CD7','CCF','CCG','CCH','CCI','CC5','CDY','CCN','CCP','CCQ','CCR','CHE5','CHE6','CHE7','CHE8','CHE9','CHEA','CHEB','CHEC') ");
		sql3.append(" inner join STORE_AREA_TYPE a on b.STORE_AREA_ID = a.STORE_AREA_ID   ");
		sql3.append(" inner join PROD_CUSTOMER_REL d on s.CREDIT_ID=d.KKBER  and s.FORWARDER_ID = d.customer_id ");
		sql3.append(" inner join PROD_INFO_TBL e on d.MATNR=e.prod_id and b.LV_5_ID=e.LV_5_ID ");
		sql3.append("  where s.ACREAGR<200   and  s.ACREAGR>=100 and nvl(AREA_UPPER_LIMIT,0) =200   and nvl(AREA_LOWER_LIMIT,0)=100  ");
		sql3.append(" group by  s.SUBROUTE_SID,s.STORE_SID,b.LV_5_ID,EXHIBIT_STANDARD) ");

		StringBuffer sql4 = new StringBuffer(" insert into DAILY_REPORT ");
		sql4.append(" select DR_SID_SEQ.nextval,SUBROUTE_SID,STORE_SID,LV_5_ID,null,EXHIBIT_STANDARD,null,'MIS_SYS',sysdate,null,null  from ");
		sql4.append(" (select s.SUBROUTE_SID,s.STORE_SID,b.LV_5_ID,EXHIBIT_STANDARD ");
		sql4.append("  from TEMP_ROUTE_TBL s  ");
		sql4.append(" inner join  STORE_PROD_SERIES b on  s.BRANCH_ID=b.BRANCH_ID and s.ROUTE_TYPE in (1,2,3) and  b.EFFECTIVE_DATE=to_date(?,'yyyymmdd')  and b.RECOMMEND_ID='001'  "
				+ " and s.CREDIT_ID IN ('CC1','CC2','CC3','CC8','CCD','CC9','CCA','CD7','CCF','CCG','CCH','CCI','CC5','CDY','CCN','CCP','CCQ','CCR','CHE5','CHE6','CHE7','CHE8','CHE9','CHEA','CHEB','CHEC') ");
		sql4.append(" inner join STORE_AREA_TYPE a on b.STORE_AREA_ID = a.STORE_AREA_ID   ");
		sql4.append(" inner join PROD_CUSTOMER_REL d on s.CREDIT_ID=d.KKBER  and s.FORWARDER_ID = d.customer_id ");
		sql4.append(" inner join PROD_INFO_TBL e on d.MATNR=e.prod_id and b.LV_5_ID=e.LV_5_ID ");
		sql4.append("  where s.ACREAGR<300   and  s.ACREAGR>=200 and nvl(AREA_UPPER_LIMIT,0) =300   and nvl(AREA_LOWER_LIMIT,0)=200  ");
		sql4.append(" group by  s.SUBROUTE_SID,s.STORE_SID,b.LV_5_ID,EXHIBIT_STANDARD) ");
		
		StringBuffer sql5 = new StringBuffer(" insert into DAILY_REPORT ");
		sql5.append(" select DR_SID_SEQ.nextval,SUBROUTE_SID,STORE_SID,LV_5_ID,null,EXHIBIT_STANDARD,null,'MIS_SYS',sysdate,null,null  from ");
		sql5.append(" (select s.SUBROUTE_SID,s.STORE_SID,b.LV_5_ID,EXHIBIT_STANDARD ");
		sql5.append("  from TEMP_ROUTE_TBL s  ");
		sql5.append(" inner join  STORE_PROD_SERIES b on  s.BRANCH_ID=b.BRANCH_ID and s.ROUTE_TYPE in (1,2,3) and  b.EFFECTIVE_DATE=to_date(?,'yyyymmdd')  and b.RECOMMEND_ID='001'  "
				+ " and s.CREDIT_ID IN ('CC1','CC2','CC3','CC8','CCD','CC9','CCA','CD7','CCF','CCG','CCH','CCI','CC5','CDY','CCN','CCP','CCQ','CCR','CHE5','CHE6','CHE7','CHE8','CHE9','CHEA','CHEB','CHEC') ");
		sql5.append(" inner join STORE_AREA_TYPE a on b.STORE_AREA_ID = a.STORE_AREA_ID   ");
		sql5.append(" inner join PROD_CUSTOMER_REL d on s.CREDIT_ID=d.KKBER  and s.FORWARDER_ID = d.customer_id ");
		sql5.append(" inner join PROD_INFO_TBL e on d.MATNR=e.prod_id and b.LV_5_ID=e.LV_5_ID ");
		sql5.append("  where s.ACREAGR<500   and  s.ACREAGR>=300 and nvl(AREA_UPPER_LIMIT,0) =500   and nvl(AREA_LOWER_LIMIT,0)=300  ");
		sql5.append(" group by  s.SUBROUTE_SID,s.STORE_SID,b.LV_5_ID,EXHIBIT_STANDARD) ");
		
		StringBuffer sql6 = new StringBuffer(" insert into DAILY_REPORT ");
		sql6.append(" select DR_SID_SEQ.nextval,SUBROUTE_SID,STORE_SID,LV_5_ID,null,EXHIBIT_STANDARD,null,'MIS_SYS',sysdate,null,null  from ");
		sql6.append(" (select s.SUBROUTE_SID,s.STORE_SID,b.LV_5_ID,EXHIBIT_STANDARD ");
		sql6.append("  from TEMP_ROUTE_TBL s  ");
		sql6.append(" inner join  STORE_PROD_SERIES b on  s.BRANCH_ID=b.BRANCH_ID and s.ROUTE_TYPE in (1,2,3) and  b.EFFECTIVE_DATE=to_date(?,'yyyymmdd')  and b.RECOMMEND_ID='001'  "
				+ " and s.CREDIT_ID IN ('CC1','CC2','CC3','CC8','CCD','CC9','CCA','CD7','CCF','CCG','CCH','CCI','CC5','CDY','CCN','CCP','CCQ','CCR','CHE5','CHE6','CHE7','CHE8','CHE9','CHEA','CHEB','CHEC') ");
		sql6.append(" inner join STORE_AREA_TYPE a on b.STORE_AREA_ID = a.STORE_AREA_ID   ");
		sql6.append(" inner join PROD_CUSTOMER_REL d on s.CREDIT_ID=d.KKBER  and s.FORWARDER_ID = d.customer_id ");
		sql6.append(" inner join PROD_INFO_TBL e on d.MATNR=e.prod_id and b.LV_5_ID=e.LV_5_ID ");
		sql6.append("  where s.ACREAGR<90000   and  s.ACREAGR>=500 and nvl(AREA_UPPER_LIMIT,0) =90000   and nvl(AREA_LOWER_LIMIT,0)=500  ");
		sql6.append(" group by  s.SUBROUTE_SID,s.STORE_SID,b.LV_5_ID,EXHIBIT_STANDARD) ");

		Connection conn = this.getICustomerConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql1.toString());
		pstmt.setString(1, s_first_date);
		sublogger.info("insert count:" + pstmt.executeUpdate());
		pstmt.close();

		pstmt = conn.prepareStatement(sql2.toString());
		pstmt.setString(1, s_first_date);
		sublogger.info("insert count:" + pstmt.executeUpdate());
		pstmt.close();

		pstmt = conn.prepareStatement(sql3.toString());
		pstmt.setString(1, s_first_date);
		sublogger.info("insert count:" + pstmt.executeUpdate());
		pstmt.close();

		pstmt = conn.prepareStatement(sql4.toString());
		pstmt.setString(1, s_first_date);
		sublogger.info("insert count:" + pstmt.executeUpdate());
		pstmt.close();
		
		pstmt = conn.prepareStatement(sql5.toString());
		pstmt.setString(1, s_first_date);
		sublogger.info("insert count:" + pstmt.executeUpdate());
		pstmt.close();
		
		pstmt = conn.prepareStatement(sql6.toString());
		pstmt.setString(1, s_first_date);
		sublogger.info("insert count:" + pstmt.executeUpdate());
		pstmt.close();
		conn.close();
		
		sublogger.info("强网-end createDailyReport1:"
				+ new Date(System.currentTimeMillis()));
	}

	private void createTownDailyReport() throws SQLException {

		sublogger.info("县城-START createDailyReport2:"
				+ new Date(System.currentTimeMillis()));
		// 2014-11-25 mirabelle add 调整面积范围
		StringBuffer sql1 = new StringBuffer(" insert into DAILY_REPORT ");
		sql1.append(" select DR_SID_SEQ.nextval,SUBROUTE_SID,STORE_SID,LV_5_ID,null,EXHIBIT_STANDARD,null,'MIS_SYS',sysdate,null,null  from ");
		sql1.append(" (select s.SUBROUTE_SID,s.STORE_SID,b.LV_5_ID,EXHIBIT_STANDARD ");
		sql1.append("  from TEMP_ROUTE_TBL s  ");
		sql1.append(" inner join  STORE_PROD_SERIES b on  s.BRANCH_ID=b.BRANCH_ID and  b.EFFECTIVE_DATE=to_date(?,'yyyymmdd') and b.RECOMMEND_ID='001' "
				+ " and s.CREDIT_ID IN ('CDI','CC6','CC7') ");
		sql1.append(" inner join STORE_AREA_TYPE a on b.STORE_AREA_ID = a.STORE_AREA_ID   ");
		sql1.append(" inner join PROD_CUSTOMER_REL d on s.CREDIT_ID=d.KKBER  and s.FORWARDER_ID = d.customer_id ");
		sql1.append("       inner join PROD_DETAIL_VIEW_TEMP e on d.MATNR=e.prod_id and b.LV_5_ID=e.LV_5_ID  ");
		sql1.append(" inner join SALES_TYPE_PROD_REL f on e.LV_1_ID = f.PROD_GROUP_LV1  and s.EMP_TYPE=f.SALES_TYPE_SID ");
		sql1.append("  where s.ACREAGR<50   and  s.ACREAGR>=0 and nvl(AREA_UPPER_LIMIT,0) =50   and nvl(AREA_LOWER_LIMIT,0)=0  ");
		sql1.append(" group by  s.SUBROUTE_SID,s.STORE_SID,b.LV_5_ID,EXHIBIT_STANDARD) ");

		StringBuffer sql2 = new StringBuffer(" insert into DAILY_REPORT ");
		sql2.append(" select DR_SID_SEQ.nextval,SUBROUTE_SID,STORE_SID,LV_5_ID,null,EXHIBIT_STANDARD,null,'MIS_SYS',sysdate,null,null  from ");
		sql2.append(" (select s.SUBROUTE_SID,s.STORE_SID,b.LV_5_ID,EXHIBIT_STANDARD ");
		sql2.append("  from TEMP_ROUTE_TBL s  ");
		sql2.append(" inner join  STORE_PROD_SERIES b on  s.BRANCH_ID=b.BRANCH_ID and b.EFFECTIVE_DATE=to_date(?,'yyyymmdd')  and b.RECOMMEND_ID='001' "
				+ " and s.CREDIT_ID IN ('CDI','CC6','CC7') ");
		sql2.append(" inner join STORE_AREA_TYPE a on b.STORE_AREA_ID = a.STORE_AREA_ID   ");
		sql2.append(" inner join PROD_CUSTOMER_REL d on s.CREDIT_ID=d.KKBER  and s.FORWARDER_ID = d.customer_id ");
		sql2.append("       inner join PROD_DETAIL_VIEW_TEMP e on d.MATNR=e.prod_id and b.LV_5_ID=e.LV_5_ID  ");
		sql2.append(" inner join SALES_TYPE_PROD_REL f on e.LV_1_ID  = f.PROD_GROUP_LV1  and s.EMP_TYPE=f.SALES_TYPE_SID ");
		sql2.append("  where s.ACREAGR<100   and  s.ACREAGR>=50 and nvl(AREA_UPPER_LIMIT,0) =100   and nvl(AREA_LOWER_LIMIT,0)=50  ");
		sql2.append(" group by  s.SUBROUTE_SID,s.STORE_SID,b.LV_5_ID,EXHIBIT_STANDARD) ");

		StringBuffer sql3 = new StringBuffer(" insert into DAILY_REPORT ");
		sql3.append(" select DR_SID_SEQ.nextval,SUBROUTE_SID,STORE_SID,LV_5_ID,null,EXHIBIT_STANDARD,null,'MIS_SYS',sysdate,null,null  from ");
		sql3.append(" (select s.SUBROUTE_SID,s.STORE_SID,b.LV_5_ID,EXHIBIT_STANDARD ");
		sql3.append("  from TEMP_ROUTE_TBL s  ");
		sql3.append(" inner join  STORE_PROD_SERIES b on  s.BRANCH_ID=b.BRANCH_ID and  b.EFFECTIVE_DATE=to_date(?,'yyyymmdd')  and b.RECOMMEND_ID='001' "
				+ " and s.CREDIT_ID IN ('CDI','CC6','CC7') ");
		sql3.append(" inner join STORE_AREA_TYPE a on b.STORE_AREA_ID = a.STORE_AREA_ID   ");
		sql3.append(" inner join PROD_CUSTOMER_REL d on s.CREDIT_ID=d.KKBER  and s.FORWARDER_ID = d.customer_id ");
		sql3.append("       inner join PROD_DETAIL_VIEW_TEMP e on d.MATNR=e.prod_id and b.LV_5_ID=e.LV_5_ID  ");
		sql3.append(" inner join SALES_TYPE_PROD_REL f on e.LV_1_ID = f.PROD_GROUP_LV1  and s.EMP_TYPE=f.SALES_TYPE_SID ");
		sql3.append("  where s.ACREAGR<200   and  s.ACREAGR>=100 and nvl(AREA_UPPER_LIMIT,0) =200   and nvl(AREA_LOWER_LIMIT,0)=100  ");
		sql3.append(" group by  s.SUBROUTE_SID,s.STORE_SID,b.LV_5_ID,EXHIBIT_STANDARD) ");

		StringBuffer sql4 = new StringBuffer(" insert into DAILY_REPORT ");
		sql4.append(" select DR_SID_SEQ.nextval,SUBROUTE_SID,STORE_SID,LV_5_ID,null,EXHIBIT_STANDARD,null,'MIS_SYS',sysdate,null,null  from ");
		sql4.append(" (select s.SUBROUTE_SID,s.STORE_SID,b.LV_5_ID,EXHIBIT_STANDARD ");
		sql4.append("  from TEMP_ROUTE_TBL s  ");
		sql4.append(" inner join  STORE_PROD_SERIES b on  s.BRANCH_ID=b.BRANCH_ID and  b.EFFECTIVE_DATE=to_date(?,'yyyymmdd')  and b.RECOMMEND_ID='001' "
				+ " and s.CREDIT_ID IN ('CDI','CC6','CC7') ");
		sql4.append(" inner join STORE_AREA_TYPE a on b.STORE_AREA_ID = a.STORE_AREA_ID   ");
		sql4.append(" inner join PROD_CUSTOMER_REL d on s.CREDIT_ID=d.KKBER  and s.FORWARDER_ID = d.customer_id ");
		sql4.append("       inner join PROD_DETAIL_VIEW_TEMP e on d.MATNR=e.prod_id and b.LV_5_ID=e.LV_5_ID  ");
		sql4.append(" inner join SALES_TYPE_PROD_REL f on e.LV_1_ID  = f.PROD_GROUP_LV1  and s.EMP_TYPE=f.SALES_TYPE_SID ");
		sql4.append("  where s.ACREAGR<300   and  s.ACREAGR>=200 and nvl(AREA_UPPER_LIMIT,0) =300   and nvl(AREA_LOWER_LIMIT,0)=200  ");
		sql4.append(" group by  s.SUBROUTE_SID,s.STORE_SID,b.LV_5_ID,EXHIBIT_STANDARD) ");
		
		StringBuffer sql5 = new StringBuffer(" insert into DAILY_REPORT ");
		sql5.append(" select DR_SID_SEQ.nextval,SUBROUTE_SID,STORE_SID,LV_5_ID,null,EXHIBIT_STANDARD,null,'MIS_SYS',sysdate,null,null  from ");
		sql5.append(" (select s.SUBROUTE_SID,s.STORE_SID,b.LV_5_ID,EXHIBIT_STANDARD ");
		sql5.append("  from TEMP_ROUTE_TBL s  ");
		sql5.append(" inner join  STORE_PROD_SERIES b on  s.BRANCH_ID=b.BRANCH_ID and  b.EFFECTIVE_DATE=to_date(?,'yyyymmdd')  and b.RECOMMEND_ID='001' "
				+ " and s.CREDIT_ID IN ('CDI','CC6','CC7') ");
		sql5.append(" inner join STORE_AREA_TYPE a on b.STORE_AREA_ID = a.STORE_AREA_ID   ");
		sql5.append(" inner join PROD_CUSTOMER_REL d on s.CREDIT_ID=d.KKBER  and s.FORWARDER_ID = d.customer_id ");
		sql5.append("       inner join PROD_DETAIL_VIEW_TEMP e on d.MATNR=e.prod_id and b.LV_5_ID=e.LV_5_ID  ");
		sql5.append(" inner join SALES_TYPE_PROD_REL f on e.LV_1_ID  = f.PROD_GROUP_LV1  and s.EMP_TYPE=f.SALES_TYPE_SID ");
		sql5.append("  where s.ACREAGR<500   and  s.ACREAGR>=300 and nvl(AREA_UPPER_LIMIT,0) =500   and nvl(AREA_LOWER_LIMIT,0)=300  ");
		sql5.append(" group by  s.SUBROUTE_SID,s.STORE_SID,b.LV_5_ID,EXHIBIT_STANDARD) ");
		
		StringBuffer sql6 = new StringBuffer(" insert into DAILY_REPORT ");
		sql6.append(" select DR_SID_SEQ.nextval,SUBROUTE_SID,STORE_SID,LV_5_ID,null,EXHIBIT_STANDARD,null,'MIS_SYS',sysdate,null,null  from ");
		sql6.append(" (select s.SUBROUTE_SID,s.STORE_SID,b.LV_5_ID,EXHIBIT_STANDARD ");
		sql6.append("  from TEMP_ROUTE_TBL s  ");
		sql6.append(" inner join  STORE_PROD_SERIES b on  s.BRANCH_ID=b.BRANCH_ID and  b.EFFECTIVE_DATE=to_date(?,'yyyymmdd')  and b.RECOMMEND_ID='001' "
				+ " and s.CREDIT_ID IN ('CDI','CC6','CC7') ");
		sql6.append(" inner join STORE_AREA_TYPE a on b.STORE_AREA_ID = a.STORE_AREA_ID   ");
		sql6.append(" inner join PROD_CUSTOMER_REL d on s.CREDIT_ID=d.KKBER  and s.FORWARDER_ID = d.customer_id ");
		sql6.append("       inner join PROD_DETAIL_VIEW_TEMP e on d.MATNR=e.prod_id and b.LV_5_ID=e.LV_5_ID  ");
		sql6.append(" inner join SALES_TYPE_PROD_REL f on e.LV_1_ID  = f.PROD_GROUP_LV1  and s.EMP_TYPE=f.SALES_TYPE_SID ");
		sql6.append("  where s.ACREAGR<90000   and  s.ACREAGR>=500 and nvl(AREA_UPPER_LIMIT,0) =90000   and nvl(AREA_LOWER_LIMIT,0)=500  ");
		sql6.append(" group by  s.SUBROUTE_SID,s.STORE_SID,b.LV_5_ID,EXHIBIT_STANDARD) ");

		Connection conn = getICustomerConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql1.toString());
		pstmt.setString(1, s_first_date);
		sublogger.info("insert count:" + pstmt.executeUpdate());
		pstmt.close();

		pstmt = conn.prepareStatement(sql2.toString());
		pstmt.setString(1, s_first_date);
		sublogger.info("insert count:" + pstmt.executeUpdate());
		pstmt.close();

		pstmt = conn.prepareStatement(sql3.toString());
		pstmt.setString(1, s_first_date);
		sublogger.info("insert count:" + pstmt.executeUpdate());
		pstmt.close();

		pstmt = conn.prepareStatement(sql4.toString());
		pstmt.setString(1, s_first_date);
		sublogger.info("insert count:" + pstmt.executeUpdate());
		pstmt.close();
		
		pstmt = conn.prepareStatement(sql5.toString());
		pstmt.setString(1, s_first_date);
		sublogger.info("insert count:" + pstmt.executeUpdate());
		pstmt.close();
		
		pstmt = conn.prepareStatement(sql6.toString());
		pstmt.setString(1, s_first_date);
		sublogger.info("insert count:" + pstmt.executeUpdate());
		pstmt.close();
		conn.close();
		
		sublogger.info("县城-end createDailyReport2:"
				+ new Date(System.currentTimeMillis()));
	}

	public boolean isDelete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	public boolean isCity() {
		return city;
	}

	public void setCity(boolean city) {
		this.city = city;
	}

	public boolean isTown() {
		return town;
	}

	public void setTown(boolean town) {
		this.town = town;
	}

}
