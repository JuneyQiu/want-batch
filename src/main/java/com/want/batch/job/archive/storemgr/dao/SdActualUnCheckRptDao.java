package com.want.batch.job.archive.storemgr.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.want.batch.job.archive.storemgr.pojo.SdActualUnCheckRpt;

public class SdActualUnCheckRptDao {
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	/**
	 * 将特陈实际未检核数据转入SD_ACTUAL_NOTCHECK_REPORT表中
	 */
	public void transmitToSdActualNotCheckRptTbl(Date date,Connection conn){
		Calendar c=Calendar.getInstance();
		c.setTime(date);
		
		int year=c.get(Calendar.YEAR);
		int month=c.get(Calendar.MONTH)+1;
		
		String yearMonth=year+((month<10)?"0":"")+month;
		int day=c.get(Calendar.DAY_OF_MONTH);
		
		int maxCount=1000;//批量迁移数据的最大数量
		
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		StringBuffer buf=new StringBuffer("");
		buf.append("select distinct a.emp_id")
			.append(",a.emp_name")
			.append(",a.emp_status")
			.append(",a.store_id")
			.append(",a.store_name")
			.append(",a.yearmonth")
			.append(",to_number(a.subroute_name) as subroute_name")
			.append(",c.third_lv_id")
			.append(",c.third_lv_name")
			.append(",d.sap_id as branch_id")
			.append(",e.sap_id as company_id")
			.append(",g.id as customer_id")
			.append(",h.sd_no")
			.append(",j.divsion_sid")
			//.append(",decode(n.create_date,null,null,'1') as is_lock")
			.append(",decode(n.location_type_sid,null,null,'1') as is_lock")//异常4903：按照陈列位置判断填写状态-- by zhangl on 20120412
			.append(",n.display_acreage")
			.append(",n.display_sidecount")
			.append(",n.display_param_id")
			.append(",n.location_type_sid")
			.append(",n.display_type_sid")
			
			.append("  from (select rv.forwarder_id,rv.STORE_SID,rv.EMP_SID,rv.STORE_ID,rv.STORE_NAME,rv.EMP_ID ")
			.append("               ,rv.EMP_NAME,rv.PROJECT_SID,eit.status as emp_status,rv.YEARMONTH,rv.subroute_name ")
			.append("        from hw09.route_info_view rv inner join hw09.emp_info_tbl eit on rv.emp_sid = eit.sid ) a   ")
			.append(" inner join hw09.ALL_ORG_STORE_VIEW b on a.store_sid=b.store_sid ")
			.append(" inner join hw09.THIRD_LV_INFO_TBL c on b.third_sid=c.sid ")
			.append(" inner join hw09.branch_info_tbl d on b.branch_sid=d.sid ")
			.append(" inner join hw09.COMPANY_INFO_TBL e on b.company_sid=e.sid ")
			.append(" left join APPLICATION_STORE f on a.store_id=f.store_id ")
			.append(" left join customer_info_tbl g on '00'||a.forwarder_id=g.id and g.status is null and g.id is not null ")
			.append(" left join SPECIAL_DISPLAY_APPLICATION h on a.yearmonth=h.year_month and g.sid=h.customer_sid ")
			.append("          and f.application_sid=h.sid and h.check_status='1'  ")
			.append(" left join SPECIAL_DISPLAY_POLICY i on h.policy_sid=i.sid ")
			.append(" left join DIVSION_PROJECT_REL j on a.project_sid=j.project_sid and i.division_sid=j.divsion_sid ")
			.append(" inner join hw09.PROJECT_INFO_TBL k on j.project_sid=k.sid ")
			.append(" left join SPECIAL_DISPLAY_ACTUAL l on h.sid=l.application_sid ")
			.append(" left join APPLICATION_STORE_DISPLAY m on f.sid=m.application_store_sid ")
			.append(" left join SD_ACTUAL_DISPLAY n on l.sid=n.actual_sid and m.sid=n.store_display_sid and n.CREATE_USER_TYPE='1' ")
			
			.append(" where a.yearmonth=? and to_number(a.subroute_name)=?");// 2015-06-04 mirabelle delete 取消条件and a.emp_sid<100000000
		logger.info("SyncSdActualUnCheckRpt:execute------sql");
		logger.info(buf.toString());
		logger.info("SyncSdActualUnCheckRpt:execute------params:" + yearMonth + ";day:" + day); 
		try{
			pstmt=conn.prepareStatement(buf.toString());
			pstmt.setString(1,yearMonth);
			pstmt.setInt(2,day);
			rs=pstmt.executeQuery();
	
			List<SdActualUnCheckRpt> list=new ArrayList<SdActualUnCheckRpt>();
			SdActualUnCheckRpt rpt=null;
			while(rs.next()){
				rpt=new SdActualUnCheckRpt().initDivisionSid(rs.getInt("divsion_sid"))
					.initCompanyId(rs.getString("company_id"))
					.initBranchId(rs.getString("branch_id"))
					.initThirdId(rs.getString("third_lv_id"))
					.initThirdName(rs.getString("third_lv_name"))
					.initCustomerId(rs.getString("customer_id"))
					.initSdNo(rs.getString("sd_no"))
					.initEmpId(rs.getString("emp_id"))
					.initEmpName(rs.getString("emp_name"))
					.initEmpStatus(rs.getString("emp_status"))
					.initStoreId(rs.getString("store_id"))
					.initStoreName(rs.getString("store_name"))
					.initIsLock(rs.getString("is_lock"))
					.initLocationTypeSid(rs.getInt("location_type_sid"))
					.initDisplayTypeSid(rs.getInt("display_type_sid"))
					.initDisplayAcreage(rs.getDouble("display_acreage"))
					.initDisplaySideCount(rs.getDouble("display_sidecount"))
					.initDisplayParamId(rs.getInt("display_param_id"))
					.initYearMonth(rs.getString("yearmonth"))
					.initDay(rs.getInt("subroute_name"))
					.initCreateDate(date);

				rpt.setVisitCount(this.getVisitCount(rpt.getEmpId(),rpt.getCustomerId(),rpt.getDivisionSid(),
						rpt.getStoreId(),rpt.getYearMonth(),rpt.getDay(),conn));
				list.add(rpt);
				if(list.size()>=maxCount&&batchSaveSdActualUnCheckRpt(list,conn)){
					list.clear();
				}
			}
			if(list.size()>0&&batchSaveSdActualUnCheckRpt(list,conn));
		}catch(Exception e){
			logger.error("SdActualUnCheckRptDao:transmitToSdActualNotCheckRptTbl----------"+e.getLocalizedMessage());
		}finally{
			try{
				if(rs!=null) rs.close();
			}catch(SQLException e){
				logger.error("SdActualUnCheckRptDao:transmitToSdActualNotCheckRptTbl--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("SdActualUnCheckRptDao:transmitToSdActualNotCheckRptTbl--------------"+e.getLocalizedMessage());
			}
		}
	}
	public boolean batchSaveSdActualUnCheckRpt(List<SdActualUnCheckRpt> list,Connection conn){
		PreparedStatement pstmt=null;
		logger.info("SdActualUnCheckRptDao:list.size:"+list.size());
		StringBuffer buf=new StringBuffer("");
		buf.append("insert into SD_ACTUAL_NOTCHECK_REPORT(SID,DIVISION_SID,COMPANY_ID,BRANCH_ID,THIRD_LV_ID ")
			.append(",THIRD_LV_NAME,CUSTOMER_ID,SD_NO,EMP_ID,EMP_NAME,EMP_STATUS,STORE_ID,STORE_NAME,IS_LOCK,LOCATION_TYPE_SID ")
			.append(",DISPLAY_TYPE_SID,DISPLAY_ACREAGE,DISPLAY_SIDECOUNT,DISPLAY_PARAM_ID,VISIT_COUNT,CREATE_DATE) ")
			.append(" values(SD_ACTUAL_NOTCHECK_REPORT_SEQ.nextval,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate) ");
		logger.info("SdActualUnCheckRptDao:insert sql: " + buf.toString());
		try{
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(buf.toString());
			for(int i=0;i<list.size();i++){
				SdActualUnCheckRpt rpt=list.get(i);
				pstmt.setInt(1,rpt.getDivisionSid());
				pstmt.setString(2,rpt.getCompanyId());
				pstmt.setString(3,rpt.getBranchId());
				pstmt.setString(4,rpt.getThirdId());
				pstmt.setString(5,rpt.getThirdName());
				pstmt.setString(6,rpt.getCustomerId());
				pstmt.setString(7,rpt.getSdNo());
				pstmt.setString(8,rpt.getEmpId());
				pstmt.setString(9,rpt.getEmpName());
				pstmt.setString(10,rpt.getEmpStatus());
				pstmt.setString(11,rpt.getStoreId());
				pstmt.setString(12,rpt.getStoreName());
				pstmt.setString(13,rpt.getIsLock());
				pstmt.setInt(14,rpt.getLocationTypeSid());
				pstmt.setInt(15,rpt.getDisplayTypeSid());
				pstmt.setDouble(16,rpt.getDisplayAcreage());
				pstmt.setDouble(17,rpt.getDisplaySideCount());
				pstmt.setInt(18,rpt.getDisplayParamId());
				pstmt.setInt(19,rpt.getVisitCount());
//				pstmt.setTimestamp(20,new Timestamp(rpt.getCreateDate().getTime()));
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			conn.commit();
			return true;
		}catch(Exception e){
			logger.info("SdActualUnCheckRptDao: exception...");
			try{
				conn.rollback();
			}catch(SQLException e1){
				logger.error("SdActualUnCheckRptDao:batchSaveSdActualUnCheckRpt--------------"+e1.getLocalizedMessage());
			}
			logger.error("SdActualUnCheckRptDao:batchSaveSdActualUnCheckRpt--------------"+e.getLocalizedMessage());
			return false;
		}finally{
			logger.info("SdActualUnCheckRptDao: end...");
			try{
				conn.setAutoCommit(true);
			}catch(SQLException e){
				logger.error("SdActualUnCheckRptDao:batchSaveSdActualUnCheckRpt--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("SdActualUnCheckRptDao:batchSaveSdActualUnCheckRpt--------------"+e.getLocalizedMessage());
			}
		}
	}
	
	/**业代+客户+事业部+终端+年月+日---->查拜访次数
	 * @return
	 */
	public int getVisitCount(String empId,String customerId,int divisionSid,String storeId
			,String yearMonth,int day,Connection iCustomerConnParam){
		int visitCount=0;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		StringBuffer buf=new StringBuffer("");
		buf.append("select count(1) as visit_count")
		
			.append(" from hw09.SUBROUTE_STORE_TBL a inner join hw09.STORE_INFO_TBL b on a.store_sid=b.sid ")
			.append(" inner join hw09.SUBROUTE_INFO_TBL c on a.subroute_sid=c.sid ")
			.append(" inner join hw09.ROUTE_INFO_TBL d on c.route_sid=d.sid ")
			.append(" inner join hw09.EMP_INFO_TBL e on d.emp_sid=e.sid ")
			.append(" inner join DIVSION_PROJECT_REL f on d.project_sid=f.project_sid ")
			.append(" inner join hw09.FORWARDER_INFO_TBL g on d.forwarder_sid=g.sid ")
			
			.append(" where b.store_id=? ")
			.append(" and a.yearmonth=? ")
			.append(" and to_number(c.subroute_name)<=? ")
			.append(" and e.emp_id=? ")
			.append(" and f.divsion_sid=? ")
			.append(" and '00'||g.forwarder_id=? ");
		
		try{
			pstmt=iCustomerConnParam.prepareStatement(buf.toString());
			pstmt.setString(1,storeId);
			pstmt.setString(2,yearMonth);
			pstmt.setInt(3,day);
			pstmt.setString(4,empId);
			pstmt.setInt(5,divisionSid);
			pstmt.setString(6,customerId);
			
			rs=pstmt.executeQuery();
			if(rs.next()){
				visitCount=rs.getInt("visit_count");
			}
		}catch(Exception e){
			logger.error("SdActualUnCheckRptDao:getVisitCount--------------"+e.getLocalizedMessage());
		}finally{
			try{
				if(rs!=null) rs.close();
			}catch(SQLException e){
				logger.error("SdActualUnCheckRptDao:getVisitCount--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("SdActualUnCheckRptDao:getVisitCount--------------"+e.getLocalizedMessage());
			}
		}
		return visitCount;
	}
	
	public void transmitToSdActualNotCheckRptBataTbl(Date date,Connection conn){
		
		// 记录前天的日期
		Calendar beforeDayCal=Calendar.getInstance();
		beforeDayCal.add(Calendar.DAY_OF_YEAR, -2);
		beforeDayCal.set(Calendar.HOUR_OF_DAY, 0);
		beforeDayCal.set(Calendar.MINUTE, 0);
		beforeDayCal.set(Calendar.SECOND, 0);
		
		int year=beforeDayCal.get(Calendar.YEAR);
		int month=beforeDayCal.get(Calendar.MONTH)+1;
		
		String yearMonth=year+((month<10)?"0":"")+month;
		int day=beforeDayCal.get(Calendar.DAY_OF_MONTH);
		String days = ((day>9) ? "" + day : "0" + day); 
		
		int maxCount=1000;//批量迁移数据的最大数量
		
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		// 2014-08-01 mirabelle update 修改数据查询的日期为路线日期
		StringBuffer buf=new StringBuffer("");
		buf.append("select distinct a.emp_id")
			.append(",a.emp_name")
			.append(",a.emp_status")
			.append(",a.store_id")
			.append(",a.store_name")
			.append(",a.yearmonth")
//			.append(",to_number(a.subroute_name) as subroute_name")
			.append(",c.third_lv_id")
			.append(",c.third_lv_name")
			.append(",d.sap_id as branch_id")
			.append(",e.sap_id as company_id")
			.append(",g.id as customer_id")
			.append(",h.sd_no")
			.append(",j.divsion_sid")
			//.append(",decode(n.create_date,null,null,'1') as is_lock")
			.append(",decode(n.location_type_sid,null,null,'1') as is_lock")//异常4903：按照陈列位置判断填写状态-- by zhangl on 20120412
			.append(",n.display_acreage")
			.append(",n.display_sidecount")
			.append(",n.display_param_id")
			.append(",n.location_type_sid")
			.append(",n.display_type_sid")
			.append(",b.MDM_STORE_ID")// 2014-06-16 mirabelle add 新终端编码
			.append(",m.sid store_display_sid")// 2014-06-16 mirabelle add 流水码
			.append(",n.create_date first_input_date")// 2014-06-16 mirabelle add 取业代第一次填写的时间
			// 2014-06-19 mirabelle add 调整sql效能 
			.append("  from (select rv.forwarder_id,rv.STORE_SID,rv.EMP_SID,rv.STORE_ID,rv.STORE_NAME,rv.EMP_ID ")
			.append("               ,rv.EMP_NAME,rv.PROJECT_SID,eit.status as emp_status,rv.YEARMONTH,rv.subroute_name ")
			.append("        from hw09.route_info_view rv inner join hw09.emp_info_tbl eit on rv.emp_sid = eit.sid    ")
			.append("   and rv.yearmonth=?) a")
			.append(" inner join hw09.ALL_ORG_STORE_VIEW b on a.store_sid=b.store_sid ")
			.append("   and to_number(a.subroute_name)=?")// 2015-06-04 mirabelle delete 取消条件and a.emp_sid<100000000
			.append(" inner join hw09.THIRD_LV_INFO_TBL c on b.third_sid=c.sid ")
			.append(" inner join hw09.branch_info_tbl d on b.branch_sid=d.sid ")
			.append(" inner join hw09.COMPANY_INFO_TBL e on b.company_sid=e.sid ")
			.append(" left join APPLICATION_STORE f on a.store_id=f.store_id ")
			.append(" left join customer_info_tbl g on '00'||a.forwarder_id=g.id and g.status is null and g.id is not null ")
			.append(" left join SPECIAL_DISPLAY_APPLICATION h on a.yearmonth=h.year_month and g.sid=h.customer_sid ")
			.append("          and f.application_sid=h.sid and h.check_status='1'  ")
			.append("          and h.YEAR_MONTH=?")
			.append(" left join SPECIAL_DISPLAY_POLICY i on h.policy_sid=i.sid ")
			.append(" left join DIVSION_PROJECT_REL j on a.project_sid=j.project_sid and i.division_sid=j.divsion_sid ")
			.append(" inner join hw09.PROJECT_INFO_TBL k on j.project_sid=k.sid ")
			.append(" left join SPECIAL_DISPLAY_ACTUAL l on h.sid=l.application_sid ")
			.append(" left join APPLICATION_STORE_DISPLAY m on f.sid=m.application_store_sid ")
			.append(" left join SD_ACTUAL_DISPLAY n on l.sid=n.actual_sid and m.sid=n.store_display_sid and n.CREATE_USER_TYPE='1' ")
			
			.append(" where a.yearmonth=? and to_number(a.subroute_name)=? ");// 2015-06-04 mirabelle delete 取消条件and a.emp_sid<100000000
		try{
			pstmt=conn.prepareStatement(buf.toString());
			pstmt.setString(1, yearMonth);
			pstmt.setString(2, days);
			pstmt.setString(3,yearMonth);
			pstmt.setString(4, yearMonth );
			pstmt.setString(5, days);
			rs=pstmt.executeQuery();
			List<SdActualUnCheckRpt> list=new ArrayList<SdActualUnCheckRpt>();
			SdActualUnCheckRpt rpt=null;
			while(rs.next()){
				rpt=new SdActualUnCheckRpt().initDivisionSid(rs.getInt("divsion_sid"))
					.initCompanyId(rs.getString("company_id"))
					.initBranchId(rs.getString("branch_id"))
					.initThirdId(rs.getString("third_lv_id"))
					.initThirdName(rs.getString("third_lv_name"))
					.initCustomerId(rs.getString("customer_id"))
					.initSdNo(rs.getString("sd_no"))
					.initEmpId(rs.getString("emp_id"))
					.initEmpName(rs.getString("emp_name"))
					.initEmpStatus(rs.getString("emp_status"))
					.initStoreId(rs.getString("store_id"))
					.initStoreName(rs.getString("store_name"))
					.initIsLock(rs.getString("is_lock"))
					.initLocationTypeSid(rs.getInt("location_type_sid"))
					.initDisplayTypeSid(rs.getInt("display_type_sid"))
					.initDisplayAcreage(rs.getDouble("display_acreage"))
					.initDisplaySideCount(rs.getDouble("display_sidecount"))
					.initDisplayParamId(rs.getInt("display_param_id"))
					.initYearMonth(rs.getString("yearmonth"))
					.initDay(day)
					.initCreateDate(date)
					.initMdmStoreId(rs.getString("MDM_STORE_ID"))
					.initStoreDisplaySid(rs.getInt("store_display_sid"))
					.initFirstInputDate(rs.getTimestamp("first_input_date"));

				rpt.setVisitCount(this.getVisitCount(rpt.getEmpId(),rpt.getCustomerId(),rpt.getDivisionSid(),
						rpt.getStoreId(),rpt.getYearMonth(),rpt.getDay(),conn));
				list.add(rpt);
				if(list.size()>=maxCount&&batchSaveSdActualUnCheckRptBata(list,conn)){
					list.clear();
				}
			}
			if(list.size()>0&&batchSaveSdActualUnCheckRptBata(list,conn));
		}catch(Exception e){
			logger.error("SdActualUnCheckRptDao:batchSaveSdActualUnCheckRptBata----------"+e.getLocalizedMessage());
		}finally{
			try{
				if(rs!=null) rs.close();
			}catch(SQLException e){
				logger.error("SdActualUnCheckRptDao:batchSaveSdActualUnCheckRptBata--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("SdActualUnCheckRptDao:batchSaveSdActualUnCheckRptBata--------------"+e.getLocalizedMessage());
			}
		}
	}
	
	public boolean batchSaveSdActualUnCheckRptBata(List<SdActualUnCheckRpt> list,Connection conn){
		PreparedStatement pstmt=null;
		StringBuffer buf=new StringBuffer("");
		// 2014-10-14 修改create_date与非bata版的日期保持一致
		buf.append("insert into SD_ACTUAL_NOTCHECK_REPORT_BATA(SID,DIVISION_SID,COMPANY_ID,BRANCH_ID,THIRD_LV_ID ")
			.append(",THIRD_LV_NAME,CUSTOMER_ID,SD_NO,EMP_ID,EMP_NAME,EMP_STATUS,STORE_ID,STORE_NAME,IS_LOCK,LOCATION_TYPE_SID ")
			.append(",DISPLAY_TYPE_SID,DISPLAY_ACREAGE,DISPLAY_SIDECOUNT,DISPLAY_PARAM_ID,VISIT_COUNT,CREATE_DATE,MDM_STORE_ID,STORE_DISPLAY_SID,FIRST_INPUT_DATE) ")
			.append(" values(SD_ACTUAL_NOTCHECK_REPORT_SEQ.nextval,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate-1,?,?,?) ");

		try{
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(buf.toString());
			for(int i=0;i<list.size();i++){
				SdActualUnCheckRpt rpt=list.get(i);
				pstmt.setInt(1,rpt.getDivisionSid());
				pstmt.setString(2,rpt.getCompanyId());
				pstmt.setString(3,rpt.getBranchId());
				pstmt.setString(4,rpt.getThirdId());
				pstmt.setString(5,rpt.getThirdName());
				pstmt.setString(6,rpt.getCustomerId());
				pstmt.setString(7,rpt.getSdNo());
				pstmt.setString(8,rpt.getEmpId());
				pstmt.setString(9,rpt.getEmpName());
				pstmt.setString(10,rpt.getEmpStatus());
				pstmt.setString(11,rpt.getStoreId());
				pstmt.setString(12,rpt.getStoreName());
				pstmt.setString(13,rpt.getIsLock());
				pstmt.setInt(14,rpt.getLocationTypeSid());
				pstmt.setInt(15,rpt.getDisplayTypeSid());
				pstmt.setDouble(16,rpt.getDisplayAcreage());
				pstmt.setDouble(17,rpt.getDisplaySideCount());
				pstmt.setInt(18,rpt.getDisplayParamId());
				pstmt.setInt(19,rpt.getVisitCount());
				pstmt.setString(20, rpt.getMdm_store_id());
				pstmt.setInt(21, rpt.getStore_display_sid());
				pstmt.setTimestamp(22, rpt.getFirst_input_date()!=null ? new Timestamp(rpt.getFirst_input_date().getTime()):null);
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			conn.commit();
			return true;
		}catch(Exception e){
			try{
				conn.rollback();
			}catch(SQLException e1){
				logger.error("SdActualUnCheckRptDao:batchSaveSdActualUnCheckRptBata--------------"+e1.getLocalizedMessage());
			}
			logger.error("SdActualUnCheckRptDao:batchSaveSdActualUnCheckRptBata--------------"+e.getLocalizedMessage());
			return false;
		}finally{
			try{
				conn.setAutoCommit(true);
			}catch(SQLException e){
				logger.error("SdActualUnCheckRptDao:batchSaveSdActualUnCheckRptBata--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("SdActualUnCheckRptDao:batchSaveSdActualUnCheckRptBata--------------"+e.getLocalizedMessage());
			}
		}
	}
}
