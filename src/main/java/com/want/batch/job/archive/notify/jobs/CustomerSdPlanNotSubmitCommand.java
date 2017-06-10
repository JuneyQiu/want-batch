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

/**客户未提交特陈计划表(14)
 * @author 00078588
 * 
 */
@Component
public class CustomerSdPlanNotSubmitCommand extends AbstractNotifyCommand {

	@Override
	protected List<NotifyBo> queryResults() {
		// TODO Auto-generated method stub
		Map<String,String> ruleMap=getRuleMap();
		return (ruleMap==null)?new ArrayList<NotifyBo>():getNotifyBoOfCustomerSdNotSubmit(ruleMap);
	}

	/**客户提交下月的特陈计划
	 * 规则十四：
	 * @return
	 */
	public Map<String,String> getRuleMap(){
		Map<String,String> ruleMap=new HashMap<String,String>();
		Calendar cal=Calendar.getInstance();
		int curYear=cal.get(Calendar.YEAR);//当前年
		int curMonth=cal.get(Calendar.MONTH)+1;//当前月
		int startDay=21;//记录成绩的日期
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
	/** 14客户未提交特陈计划表
	 * @param ruleMap
	 * @return
	 */
	private List<NotifyBo> getNotifyBoOfCustomerSdNotSubmit(Map<String,String> ruleMap){
		StringBuffer buf=new StringBuffer("");
		buf.append("select distinct  ?  as abnormality_type ")
			.append(",'客户未提交特陈计划' as abnormality_des ")
			.append(",'您有以下客户未提交特陈计划：' as abnormality_info ")
			.append(",d.zjl_id")
			.append(",d.zjl_name")
			.append(",d.company_id")
			.append(",d.company_name")
			.append(",d.zongjian_id")
			.append(",d.zongjian_name")
			.append(",d.zhuanyuan_id")
			.append(",d.zhuanyuan_name")
			.append(",d.branch_id")
			.append(",d.branch_name")
			.append(",d.suozhang_id")
			.append(",d.suozhang_name")
			.append(",d.customer_did as division_id")
			.append(",e.name as division_name")
			.append(",d.zhuren_id")
			.append(",d.zhuren_name")
			.append(",d.yedai_id")
			.append(",d.yedai_name")
			.append(",d.customer_id")
			.append(",? as is_first_time")
			.append(",? as start_ymd   ")
			
			.append(" from ")
			.append(" ( ")
			.append(" 		select distinct sdst.customer_sid ")
			.append("       			   ,sdst.customer_id ")
			.append("          			   ,decode(sdst.divsion_sid,'7','1', '8','1', '9','1',sdst.divsion_sid) as divsion_sid   ")
			.append("       from sp_disp_stdamount_tbl sdst   ")
			.append("       where sdst.yearmonth=?   ")
			.append(" ) a ")
			.append(" inner join abnormality_cust_interim b on a.customer_id=b.customer_id and a.divsion_sid=b.division_id and b.abnormal_type_id=?  ")
			.append(" left join ( ")
			.append("      select distinct sda.customer_sid,sdp.division_sid   ")
			.append("      from special_display_application sda   ")
			.append("      inner join special_display_policy sdp on sdp.sid=sda.policy_sid   ")
			.append("      where sda.year_month=?  ")
			.append(" ) c on c.customer_sid=a.customer_sid and c.division_sid=a.divsion_sid  ")
			.append(" inner join all_customer_view d on b.customer_id=d.customer_id and b.division_id=d.customer_did ")
			.append(" inner join divsion e on d.customer_did=e.sid ")
			.append(" inner join customer_info_tbl f on a.customer_id=f.id and f.status is null ")

			.append(" where c.customer_sid is null ");

		return this.getiCustomerJdbcOperations().query(buf.toString(),new NotifyBoRowMapper(),new Object[]{
			Constant.EXC_TYPE_CODE_CUSTOMER_SD_UNSUBMIT,ruleMap.get("IS_FIRST_TIME"),ruleMap.get("START_YMD")
			,ruleMap.get("COMPARE_YEAR_MONTH"),Constant.EXC_TYPE_CODE_CUSTOMER_SD_UNSUBMIT,ruleMap.get("COMPARE_YEAR_MONTH")});
	}
}
