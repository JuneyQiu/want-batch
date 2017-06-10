package com.want.batch.job.archive.notify.jobs;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.want.batch.job.archive.notify.pojo.NotifyBo;
import com.want.batch.job.archive.notify.util.Constant;
import com.want.batch.job.archive.notify.util.Toolkit;

/**业代未录入日报异常(31)
 * @author 00078588
 *
 */
@Component
public class DailyRptNotInputCommand extends AbstractNotifyCommand {

	/* (non-Javadoc)
	 * @see com.want.batch.job.archive.notify.AbstractNotifyCommand#queryResults()
	 * 抓取未录入日报的异常
	 */
	@Override
	protected List<NotifyBo> queryResults() {
		// TODO Auto-generated method stub
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH,-1);
		initNotifyDrNotInputInterim(cal.getTime());
		
		return getNotifyBoOfDailyRptNotInput(cal.getTime());
	}

	/**
	 * 抓取某天的路线中未录入日报的相关客户
	 * 存入表notify_dr_not_input_interim中
	 */
	private void initNotifyDrNotInputInterim(Date dailyRptDate){
		String dateStr=Toolkit.dateToString(dailyRptDate, "yyyy-MM-dd");
		StringBuffer buf1=new StringBuffer("");
		StringBuffer buf2=new StringBuffer("");
		buf1.append(" delete from notify_dr_not_input_interim ");
		buf2.append(" insert into notify_dr_not_input_interim(subroute_sid,emp_id,forwarder_id,project_sid,visit_date) ")
			.append("( select a.subroute_sid,d.emp_id,e.forwarder_id,c.project_sid,b.visit_date  ")
			
			.append("  from   ")
			.append("  (select distinct a.subroute_sid ")
			.append("   from daily_report a ")
			.append("   inner join hw09.subroute_info_tbl b on a.subroute_sid=b.sid and to_char(b.visit_date,'yyyy-mm-dd')=?  ")
			.append("   where a.subroute_sid not in     ")
			.append("  (select distinct a.subroute_sid   ")
			.append("   from daily_report a ")
			.append("   inner join hw09.subroute_info_tbl b on a.subroute_sid=b.sid and to_char(b.visit_date,'yyyy-mm-dd')=? and a.update_user is not null)  ")
			.append("   ) a ")
			.append(" inner join hw09.subroute_info_tbl b on a.subroute_sid=b.sid ")
			.append(" inner join hw09.route_info_tbl c on b.route_sid=c.sid ")// 2015-06-04 mirabelle delete查询条件 and c.emp_sid<100000000
			.append(" inner join hw09.emp_info_tbl d on c.emp_sid=d.sid and d.status=1")
			.append(" inner join hw09.forwarder_info_tbl e on c.forwarder_sid=e.sid and e.status=1 ")
			
			.append(" where c.project_sid=12 and c.route_att_sid=1 ")//强网乳品(行直)
			//休一渠道拆分为米果果冻和糖果炒货,新增25,26
			.append("       or c.project_sid in (4,17,18,14,10,25,26) and c.route_att_sid in (1,2,3) )");//县城、强网饮品和强网休闲(行直、车销和行直+车销)

		this.getiCustomerJdbcOperations().update(buf1.toString());
		this.getiCustomerJdbcOperations().update(buf2.toString(),new Object[]{dateStr,dateStr});
	}

	/**抓取未录入日报的异常
	 * @param dailyRptDate
	 * @return
	 */
	private List<NotifyBo> getNotifyBoOfDailyRptNotInput(Date dailyRptDate){
		String drDateStr=Toolkit.dateToString(dailyRptDate, "yyyy/MM/dd");
		StringBuffer buf=new StringBuffer("");
		buf.append("select distinct  ?  as abnormality_type ")
			.append(",'业代日报未录入' as abnormality_des ")
			.append(",'您有以下日报未录入：' as abnormality_info ")
			.append(",i.company_id")
			.append(",i.company_name")
			.append(",i.zjl_id")
			.append(",i.zjl_name")
			.append(",i.zongjian_id")
			.append(",i.zongjian_name")
			.append(",i.zongjian_pos")
			.append(",i.zhuanyuan_id")
			.append(",i.zhuanyuan_name")
			.append(",i.zhuanyuan_did")
			.append(",i.suozhang_id")
			.append(",i.suozhang_name")
			.append(",i.zhuren_id")
			.append(",i.zhuren_name")
			.append(",i.branch_id")
			.append(",i.branch_name")
			.append(",i.yedai_id")
			.append(",i.yedai_name")
			.append(",i.customer_id")
			.append(",i.customer_did as division_id")
			.append(",j.name as division_name")
			.append(",'1' as is_first_time")
			.append(",? as start_ymd     ")
			
			.append(" from notify_dr_not_input_interim a ")
			.append(" inner join customer_info_tbl f on '00'||a.forwarder_id=f.id and f.status is null ")
			.append(" inner join divsion_project_rel g on a.project_sid=g.project_sid ")
			.append(" inner join abnormality_cust_interim h on h.division_id=g.divsion_sid and  h.customer_id=f.id and h.abnormal_type_id=? ")
			.append(" inner join all_customer_view i on f.id=i.customer_id and g.divsion_sid=i.customer_did ")
			.append("                                   and (a.emp_id=i.yedai_id or a.emp_id=i.zhuren_id) ")
			.append(" inner join divsion j on i.customer_did=j.sid and j.sid in (1,30,31,15,16,17) ");
		
		return this.getiCustomerJdbcOperations().query(buf.toString(), new NotifyBoRowMapper()
							, new Object[]{Constant.EXC_TYPE_CODE_DR_NOT_INPUT,drDateStr,Constant.EXC_TYPE_CODE_DR_NOT_INPUT});
	}
}
