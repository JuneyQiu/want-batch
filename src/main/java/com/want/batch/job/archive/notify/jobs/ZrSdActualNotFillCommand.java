package com.want.batch.job.archive.notify.jobs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.stereotype.Component;

import com.want.batch.job.archive.notify.pojo.NotifyBo;
import com.want.batch.job.archive.notify.pojo.SdInfo;
import com.want.batch.job.archive.notify.util.Constant;
import com.want.batch.job.archive.notify.util.Toolkit;

/**主任未提交特陈(实际)稽核信息(20)
 * @author 00078588
 * 
 */
@Component
public class ZrSdActualNotFillCommand extends AbstractNotifyCommand {

	@Override
	protected List<NotifyBo> queryResults() {
		// TODO Auto-generated method stub
		Calendar cal=Calendar.getInstance();
		int curYear=cal.get(Calendar.YEAR);
		int curMonth=cal.get(Calendar.MONTH)+1;
		String curYearMonth=Toolkit.formatData(curYear,"0000")+Toolkit.formatData(curMonth,"00");
		
		int startDay=26;//
		int endDay=30;//

		if(cal.get(Calendar.DAY_OF_MONTH)<startDay||cal.get(Calendar.DAY_OF_MONTH)>endDay) return new ArrayList<NotifyBo>();
		
		cal.add(Calendar.DAY_OF_MONTH, -1);
		initAllStoreOneDayTbl(Toolkit.dateToString(cal.getTime(), "yyyy-MM-dd"));//更新中间表
		return getNotifyBoOfZrSdActualNotFill(Constant.EXC_TYPE_CODE_ZR_SD_ACTUAL_NOT_FILL,curYearMonth);
	}

	@Override
	public void execute() {
		logger.info(this.getClass().getSimpleName()+":execute()----------start");
		List<NotifyBo> list=queryResults();
		logger.info(this.getClass().getSimpleName()+":execute()->results size="+list.size());
		for(NotifyBo notifyBo:list){
			long notifyId=save(notifyBo);//保存异常信息(主表)，返回新生成的主键
			for(SdInfo sdInfo:notifyBo.getSdInfoList()){
				sdInfo.setNotifyId(notifyId);
				
				save(sdInfo);//保存异常特陈(子表)
			}
		}
		logger.info(this.getClass().getSimpleName()+":execute()----------end");
	}
	
	/**all_store_oneday表中存放的是客户和业代需要拜访的终端(前一天)
	 * @param ruleMap
	 */
	private void initAllStoreOneDayTbl(String yesterday){
		StringBuffer buf1=new StringBuffer("");
		StringBuffer buf2=new StringBuffer("");
		buf1.append(" delete from all_store_oneday ");
		buf2.append(" insert into all_store_oneday(store_id,customer_id,emp_id,division_sid,create_time)  ")
			.append(" (  ")
			.append(" 	  select distinct hriv.store_id ")
			.append(" 	  ,'00'||hriv.forwarder_id as customer_id")
			.append("     ,hriv.emp_id")
			.append("     ,dpr.divsion_sid as division_sid")
			.append("     ,sysdate ")
			
			.append(" 	  from hw09.route_info_view hriv  ")
			.append(" 	  inner join divsion_project_rel dpr on dpr.project_sid=hriv.project_sid  ")
			
			.append(" 	  where hriv.project_sid in (10,12,14,4,17,18) ")
			.append("           and hriv.visit_date=to_date(?,'yyyy-mm-dd')  ")
//			.append(" 	        and hriv.emp_sid<100000000 ") 2015-06-04 mirabelle delete该查询条件
			.append(" ) ");

		this.getiCustomerJdbcOperations().update(buf1.toString());
		this.getiCustomerJdbcOperations().update(buf2.toString(),new Object[]{yesterday});
	}
	
	/** 20主任未提交特陈(实际)稽核信息
	 * @param notifyId
	 * @return
	 */
	private List<NotifyBo> getNotifyBoOfZrSdActualNotFill(int notifyId,String curYearMonth){
		StringBuffer buf=new StringBuffer("");
		buf.append("select distinct j.id as abnormality_type")
			.append(",j.name as abnormality_des")
			.append(",j.name as abnormality_info")
			.append(",h.zjl_id")
			.append(",h.zjl_name")
			.append(",h.company_id")
			.append(",h.company_name")
			.append(",h.zongjian_id")
			.append(",h.zongjian_name")
			.append(",h.zhuanyuan_id")
			.append(",h.zhuanyuan_name")
			.append(",h.branch_id")
			.append(",h.branch_name")
			.append(",h.suozhang_id")
			.append(",h.suozhang_name")
			.append(",i.sid as division_id")
			.append(",i.name as division_name")
			.append(",h.zhuren_id")
			.append(",h.zhuren_name")
			.append(",h.yedai_id")
			.append(",h.yedai_name")
			.append(",h.customer_id")
			.append(",'1' as is_first_time")
			.append(",to_char(sysdate-1,'yyyy/mm/dd') as start_ymd      ")
			
			.append(" from all_store_oneday a ")
			.append(" inner join abnormality_cust_interim b on a.customer_id=b.customer_id ")
			.append("                                       and a.division_sid=b.division_id and b.abnormal_type_id=? ")
			.append(" inner join application_store c on a.store_id=c.store_id ")
			.append(" inner join special_display_application d on c.application_sid=d.sid and d.check_status=1 and d.year_month=? ")
			.append(" inner join application_store_display d1 on c.sid=d1.application_store_sid ")
			.append(" inner join special_display_policy e on d.policy_sid=e.sid and a.division_sid=e.division_sid ")
			.append(" inner join customer_info_tbl f on d.customer_sid=f.sid and a.customer_id=f.id and f.status is null ")
			.append(" left join  sd_actual_display g on d1.sid=g.store_display_sid and g.location_type_sid!=0 ")
			.append(" 									and g.create_user_type='2'  and to_char(g.create_date,'yyyymm')=?  ")
			.append(" inner join all_customer_view h on a.customer_id=h.customer_id and a.division_sid=h.customer_did ")
			.append("                                and (a.emp_id=h.yedai_id or h.yedai_id is null and a.emp_id=h.zhuren_id)  ")
			.append(" inner join divsion i on a.division_sid=i.sid   ")
			.append(" inner join notify_info_tbl j on b.abnormal_type_id=j.id ")
			
			.append(" where g.sid is null ");

		List<NotifyBo> list=this.getiCustomerJdbcOperations().query(buf.toString(),new NotifyBoRowMapper(),new Object[]{notifyId
			,curYearMonth,curYearMonth});

		for(NotifyBo notify:list){
			notify.setSdInfoList(getSdInfoList(notifyId,curYearMonth,notify.getCustId(),notify.getYedaiId()));
		}
		
		return list;
	}
	/**获取产生异常的特陈
	 * @param ruleMap
	 * @param customerId
	 * @param yedaiId
	 * @return
	 */
	private List<SdInfo> getSdInfoList(int notifyId,String curYearMonth,String customerId,String yedaiId){
		StringBuffer buf=new StringBuffer("");
		buf.append(" select distinct d1.sid as store_display_sid ")
			.append(",h.yedai_id as emp_id  ")
			.append(",0 as sid,0 as notify_id,'' as create_user,null as create_date   ")
			
			.append(" from all_store_oneday a ")
			.append(" inner join abnormality_cust_interim b on a.customer_id=b.customer_id ")
			.append("                                       and a.division_sid=b.division_id and b.abnormal_type_id=? ")
			.append(" inner join application_store c on a.store_id=c.store_id ")
			.append(" inner join special_display_application d on c.application_sid=d.sid and d.check_status=1 and d.year_month=? ")
			.append(" inner join application_store_display d1 on c.sid=d1.application_store_sid ")
			.append(" inner join special_display_policy e on d.policy_sid=e.sid and a.division_sid=e.division_sid ")
			.append(" inner join customer_info_tbl f on d.customer_sid=f.sid and a.customer_id=f.id and f.status is null ")
			.append(" left join  sd_actual_display g on d1.sid=g.store_display_sid and g.location_type_sid!=0 ")
			.append(" 									and g.create_user_type='2'  and to_char(g.create_date,'yyyymm')=?  ")
			.append(" inner join all_customer_view h on a.customer_id=h.customer_id and a.division_sid=h.customer_did ")
			.append("                                and (a.emp_id=h.yedai_id or h.yedai_id is null and a.emp_id=h.zhuren_id)  ")
			.append(" inner join divsion i on a.division_sid=i.sid   ")
			.append(" inner join notify_info_tbl j on b.abnormal_type_id=j.id ")
			
			.append(" where g.sid is null and h.customer_id=? and h.yedai_id=? and b.abnormal_type_id=? ");
		
		return this.getiCustomerJdbcOperations().query(buf.toString(),new SdInfoRowMapper(),new Object[]{
			notifyId,curYearMonth,curYearMonth,customerId,yedaiId,notifyId});
	}
	/**保存异常特陈
	 * abnormal_sd_tbl
	 * @param sdInfo
	 */
	private void save(SdInfo sdInfo){
		StringBuffer buf=new StringBuffer("");
		buf.append(" insert into abnormal_sd_tbl(sid,abnormality_info_sid,store_display_sid,emp_id,create_user,create_date)  ")
			.append(" values(abnormal_sd_tbl_seq.nextval,?,?,?,'sys',sysdate) ");
		
		this.getiCustomerJdbcOperations().update(buf.toString(), new Object[]{sdInfo.getNotifyId()
				,sdInfo.getStoreDisplaySid(),sdInfo.getEmpId()});
	}
}
