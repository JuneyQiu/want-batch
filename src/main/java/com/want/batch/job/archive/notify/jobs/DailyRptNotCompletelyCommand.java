package com.want.batch.job.archive.notify.jobs;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.want.batch.job.archive.notify.pojo.NotifyBo;
import com.want.batch.job.archive.notify.util.Constant;
import com.want.batch.job.archive.notify.util.Toolkit;

/**业代日报表录入不完整(32)
 * @author 00078588
 *
 */
@Component
public class DailyRptNotCompletelyCommand extends AbstractNotifyCommand {

	@Override
	protected List<NotifyBo> queryResults() {
		// TODO Auto-generated method stub
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH,-1);
		
		initNotifyDrNotCompletelyInterim(cal.getTime());
		
		return getNotifyBoOfDailyRptNotCompletely(cal.getTime());
	}
	
	/**
	 * 抓取某天的路线中日报录入不完整的相关客户
	 * 存入表notify_dr_not_complete_interim中
	 */
	private void initNotifyDrNotCompletelyInterim(Date dailyRptDate){
		String dateStr=Toolkit.dateToString(dailyRptDate, "yyyy-MM-dd");
		StringBuffer buf1=new StringBuffer("");
		StringBuffer buf2=new StringBuffer("");
		buf1.append(" delete from notify_dr_not_complete_interim ");
		buf2.append("  insert into notify_dr_not_complete_interim(subroute_sid,emp_id,forwarder_id,project_sid,visit_date)  ")
			.append(" (select a.subroute_sid,e.emp_id,f.forwarder_id,d.project_sid,c.visit_date ")
			
			.append("  from ")
			.append(" (select distinct sub3.subroute_sid ")
			.append("  from daily_report sub3 ")
			.append("  inner join hw09.subroute_info_tbl sub4 on sub3.subroute_sid=sub4.sid and to_char(sub4.visit_date,'yyyy-mm-dd')=? ")
			.append("  where sub3.update_user is not null) a ")
			.append(" inner join ")
			.append(" (select distinct sub1.subroute_sid ")
			.append("  from daily_report sub1  ")
			.append("  inner join hw09.subroute_info_tbl sub2 on sub1.subroute_sid=sub2.sid and to_char(sub2.visit_date,'yyyy-mm-dd')=? ")
			.append("  where sub1.update_user is null ")
			.append(" ) b on a.subroute_sid=b.subroute_sid ")
			.append(" inner join hw09.subroute_info_tbl c on a.subroute_sid=c.sid ")
			.append(" inner join hw09.route_info_tbl d on c.route_sid=d.sid  ")// 2015-06-04 mirabelle delete查询条件and d.emp_sid<100000000
			.append(" inner join hw09.emp_info_tbl e on d.emp_sid=e.sid ")
			.append(" inner join hw09.forwarder_info_tbl f on d.forwarder_sid=f.sid ")
			
			.append(" where d.project_sid=12 and d.route_att_sid=1 ")//强网乳品(行直)
			//休一渠道拆分为米果果冻和糖果炒货,新增25,26
			.append("       or d.project_sid in (4,17,18,14,10,25,26) and d.route_att_sid in (1,2,3) )");//县城、强网饮品和强网休闲(行直、车销和行直+车销)

		this.getiCustomerJdbcOperations().update(buf1.toString());
		this.getiCustomerJdbcOperations().update(buf2.toString(),new Object[]{dateStr,dateStr});
	}

	/**抓取业代日报表录入不完整的异常
	 * @param dailyRptDate
	 * @return
	 */
	private List<NotifyBo> getNotifyBoOfDailyRptNotCompletely(Date dailyRptDate){
		String drDateStr=Toolkit.dateToString(dailyRptDate, "yyyy/MM/dd");
		StringBuffer buf=new StringBuffer("");
		buf.append("select distinct  ?  as abnormality_type  ")
			.append(",'业代日报表录入不完整' as abnormality_des  ")
			.append(",'您有以下日报表录入不完整的异常：' as abnormality_info ")
			.append(",e.company_id")
			.append(",e.company_name")
			.append(",e.zjl_id")
			.append(",e.zjl_name")
			.append(",e.zongjian_id")
			.append(",e.zongjian_name")
			.append(",e.zongjian_pos")
			.append(",e.zhuanyuan_id")
			.append(",e.zhuanyuan_name")
			.append(",e.zhuanyuan_did")
			.append(",e.suozhang_id")
			.append(",e.suozhang_name")
			.append(",e.zhuren_id")
			.append(",e.zhuren_name")
			.append(",e.branch_id")
			.append(",e.branch_name")
			.append(",e.yedai_id")
			.append(",e.yedai_name")
			.append(",e.customer_id")
			.append(",e.customer_did as division_id")
			.append(",f.name as division_name")
			.append(",'1' as is_first_time")
			.append(",? as start_ymd     ")
			
			.append(" from notify_dr_not_complete_interim a ")
			.append(" inner join customer_info_tbl b on '00'||a.forwarder_id=b.id and b.status is null ")
			.append(" inner join divsion_project_rel c on a.project_sid=c.project_sid ")
			.append(" inner join abnormality_cust_interim d on d.division_id=c.divsion_sid and  d.customer_id=b.id and d.abnormal_type_id=? ")
			.append(" inner join all_customer_view e on b.id=e.customer_id and c.divsion_sid=e.customer_did ")
			.append("                                   and (a.emp_id=e.yedai_id or a.emp_id=e.zhuren_id) ")
			.append(" inner join divsion f on e.customer_did=f.sid and f.sid in (1,30,31,15,16,17) ");

		return this.getiCustomerJdbcOperations().query(buf.toString(), new NotifyBoRowMapper()
							, new Object[]{Constant.EXC_TYPE_CODE_DR_NOT_COMPLETELY,drDateStr,Constant.EXC_TYPE_CODE_DR_NOT_COMPLETELY});
	}
}
