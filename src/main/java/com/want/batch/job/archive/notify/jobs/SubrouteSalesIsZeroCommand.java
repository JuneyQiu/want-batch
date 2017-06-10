package com.want.batch.job.archive.notify.jobs;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.want.batch.job.archive.notify.pojo.NotifyBo;
import com.want.batch.job.archive.notify.util.Constant;
import com.want.batch.job.archive.notify.util.Toolkit;

/**业代当日销售金额为0的异常(33)
 * @author 00078588
 *
 */
@Component
public class SubrouteSalesIsZeroCommand extends AbstractNotifyCommand {

	@Override
	protected List<NotifyBo> queryResults() {
		// TODO Auto-generated method stub
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH,-1);
		initNotifySalesIsZeroInterim(cal.getTime());
		
		return getNotifyBoOfSalesIsZeroInterim(cal.getTime());
	}

	/**
	 * 抓取某天的路线中"业代当日销售金额为0"(从表daily_sales中查询)的相关客户
	 * 存入表notify_dr_sales_0_interim中
	 */
	private void initNotifySalesIsZeroInterim(Date dailyRptDate){
		String dateStr=Toolkit.dateToString(dailyRptDate, "yyyy-MM-dd");
		StringBuffer buf1=new StringBuffer("");
		StringBuffer buf2=new StringBuffer("");
		buf1.append(" delete from notify_dr_sales_0_interim ");
		buf2.append(" insert into notify_dr_sales_0_interim(subroute_sid,emp_id,forwarder_id,project_sid,visit_date) ")
			.append(" (  select distinct a.sid as subroute_sid,c.emp_id,d.forwarder_id,b.project_sid,a.visit_date ")
			
			.append("    from hw09.subroute_info_tbl a  ")
			.append("    inner join hw09.route_info_tbl b on a.route_sid=b.sid  ")// 2015-06-04 mirabelle delete 查询条件and b.emp_sid<100000000
			.append("    inner join hw09.emp_info_tbl c on b.emp_sid=c.sid and c.status=1")
			.append("    inner join hw09.forwarder_info_tbl d on b.forwarder_sid=d.sid and d.status=1 ")
			.append("    left join daily_sales e on a.sid=e.subroute_sid  ")
			
			.append("    where to_char(a.visit_date,'yyyy-mm-dd')=? and (e.sales is null or e.sales=0) ")
			.append(" 	       and (b.project_sid=12 and b.route_att_sid=1 ")//强网乳品(行直)
			//休一渠道拆分为米果果冻和糖果炒货,新增25,26;14不用，未免出错，故保留
			.append("          		or b.project_sid in (4,17,18,14,10,25,26) and b.route_att_sid in (1,2,3)) ")//县城、强网饮品和强网休闲(行直、车销和行直+车销)
			.append("          and exists (select * from hw09.subroute_store_tbl f where f.subroute_sid=a.sid) )");
		this.getiCustomerJdbcOperations().update(buf1.toString());
		this.getiCustomerJdbcOperations().update(buf2.toString(),new Object[]{dateStr});
	}
	/**抓取"业代当日销售金额为0"的异常
	 * @param dailyRptDate
	 * @return
	 */
	private List<NotifyBo> getNotifyBoOfSalesIsZeroInterim(Date dailyRptDate){
		String drDateStr=Toolkit.dateToString(dailyRptDate, "yyyy/MM/dd");
		StringBuffer buf=new StringBuffer("");
		buf.append("select distinct  ?  as abnormality_type")
			.append(",'业代当日销售金额为0' as abnormality_des")
			.append(",'您有以下业代当日销售金额为0的异常：' as abnormality_info")
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
			
			.append(" from notify_dr_sales_0_interim a ")
			.append(" inner join customer_info_tbl b on '00'||a.forwarder_id=b.id and b.status is null ")
			.append(" inner join divsion_project_rel c on a.project_sid=c.project_sid ")
			.append(" inner join abnormality_cust_interim d on d.division_id=c.divsion_sid and  d.customer_id=b.id and d.abnormal_type_id=?  ")
			.append(" inner join all_customer_view e on b.id=e.customer_id and c.divsion_sid=e.customer_did ")
			.append("                                   and (a.emp_id=e.yedai_id or a.emp_id=e.zhuren_id) ")
			.append(" inner join divsion f on e.customer_did=f.sid and f.sid in (1,30,31,15,16,17) ");

		return this.getiCustomerJdbcOperations().query(buf.toString(), new NotifyBoRowMapper()
							, new Object[]{Constant.EXC_TYPE_CODE_DR_SALES_IS_0,drDateStr,Constant.EXC_TYPE_CODE_DR_SALES_IS_0});
	}
}
