package com.want.batch.job.archive.notify.jobs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.want.batch.job.archive.notify.pojo.NotifyBo;
import com.want.batch.job.archive.notify.util.Constant;
import com.want.batch.job.archive.notify.util.Toolkit;

/**主任未审核特陈计划表(15)
 * @author 00078588
 * 
 */
@Component
public class ZhurenSdPlanNotAuditCommand extends AbstractNotifyCommand {

	@Override
	protected List<NotifyBo> queryResults() {
		// TODO Auto-generated method stub
		Map<String,String> ruleMap=getRuleMap();
		return (ruleMap==null)?new ArrayList<NotifyBo>():getNotifyBoOfZhurenSdPlanNotAudit(ruleMap);
	}

	/**主任审核下月的特陈计划
	 * 规则十五：
	 * @return
	 */
	public Map<String,String> getRuleMap(){
		Map<String,String> ruleMap=new HashMap<String,String>();
		Calendar cal=Calendar.getInstance();
		int curYear=cal.get(Calendar.YEAR);//当前年
		int curMonth=cal.get(Calendar.MONTH)+1;//当前月
		int startDay=24;//记录成绩的日期
		int endDay=30;
		
		int compareYear=0;
		int compareMonth=0;
		String isFirstTime = "0";

		if(cal.get(Calendar.DAY_OF_MONTH)<startDay||cal.get(Calendar.DAY_OF_MONTH)>endDay) return null;

		if(curMonth == 12){
			compareYear=curYear+1;
			compareMonth=1;
		}else{
			compareYear=curYear;
			compareMonth=curMonth+1;
		}

		if(cal.get(Calendar.DAY_OF_MONTH)==startDay){
			isFirstTime = "1";
		}else{
			isFirstTime = "0";
		}
		
		ruleMap.put("IS_FIRST_TIME", isFirstTime);
		ruleMap.put("START_YMD",Toolkit.formatData(curYear,"0000")+"/"+Toolkit.formatData(curMonth,"00")
				+"/"+Toolkit.formatData(startDay,"00"));
		ruleMap.put("COMPARE_YEAR_MONTH", Toolkit.formatData(compareYear,"0000")+Toolkit.formatData(compareMonth,"00"));
	
		return ruleMap;
	}
	private List<NotifyBo> getNotifyBoOfZhurenSdPlanNotAudit(Map<String,String> ruleMap){
		StringBuffer buf=new StringBuffer("");
		buf.append("select distinct  ?  as abnormality_type ")
			.append(",'主任未审核特陈计划' as abnormality_des ")
			.append(",'您有以下未审核客户特陈计划：' as abnormality_info ")
			.append(",e.zjl_id")
			.append(",e.zjl_name")
			.append(",e.company_id")
			.append(",e.company_name")
			.append(",e.zongjian_id")
			.append(",e.zongjian_name")
			.append(",e.zhuanyuan_id")
			.append(",e.zhuanyuan_name")
			.append(",e.branch_id")
			.append(",e.branch_name")
			.append(",e.suozhang_id")
			.append(",e.suozhang_name")
			.append(",e.customer_did as division_id")
			.append(",f.name as division_name")
			.append(",e.zhuren_id")
			.append(",e.zhuren_name")
			.append(",e.yedai_id")
			.append(",e.yedai_name")
			.append(",e.customer_id")
			.append(",? as is_first_time")
			.append(",? as start_ymd     ")
			
			.append(" from special_display_application a ")
			.append(" inner join special_display_policy b on a.policy_sid=b.sid ")
			.append(" inner join customer_info_tbl c on a.customer_sid=c.sid and c.status is null ")
			.append(" inner join abnormality_cust_interim d on d.customer_id=c.id and d.division_id=b.division_sid and d.abnormal_type_id=?  ")
			.append(" inner join all_customer_view e on d.customer_id=e.customer_id and d.division_id=e.customer_did ")
			.append(" inner join divsion f on e.customer_did=f.sid ")
			
			.append(" where a.submit_status_customer=1 and a.submit_status_xg=1 and (a.submit_status_zr is null or a.submit_status_zr=0) ")
			.append(" and a.year_month=? ");

		return this.getiCustomerJdbcOperations().query(buf.toString(),new NotifyBoRowMapper(),new Object[]{
			Constant.EXC_TYPE_CODE_ZR_SD_PLAN_UNAUDIT,ruleMap.get("IS_FIRST_TIME"),ruleMap.get("START_YMD")
			,Constant.EXC_TYPE_CODE_ZR_SD_PLAN_UNAUDIT,ruleMap.get("COMPARE_YEAR_MONTH")});
	}
}
