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

/**分公司客户货需未确认(5)
 * @author 00078588
 * 
 */
@Component
public class RequireNotAuditByCompanyCommand extends AbstractNotifyCommand {

	@Override
	protected List<NotifyBo> queryResults() {
		// TODO Auto-generated method stub
		Map<String,String> ruleMap=getRuleMap();
		return (ruleMap==null)?new ArrayList<NotifyBo>():getNotifyBoOfRequireNotAuditByCompany(ruleMap);
	}

	/**
	 * 规则五：
	 * 1.从11日00:01--11日23:59止，检查分公司客户货需未审核
	 * 2.当月只能录下个月的货需
	 */
	public Map<String,String> getRuleMap(){
		Map<String,String> ruleMap=new HashMap<String,String>();
		Calendar cal=Calendar.getInstance();
		int curYear=cal.get(Calendar.YEAR);//当前年
		int curMonth=cal.get(Calendar.MONTH)+1;//当前月
		int workDay=12;//分公司总监或专员审核货需的时间(日)

		int compareYear=0;
		int compareMonth=0;
		String isFirstTime = "1";//
		if(cal.get(Calendar.DAY_OF_MONTH)!=workDay) return null;

		if(curMonth == 12){
			compareYear=curYear+1;
			compareMonth=1;
		}else{
			compareYear=curYear;
			compareMonth=curMonth+1;
		}
		
		ruleMap.put("IS_FIRST_TIME", isFirstTime);
		ruleMap.put("START_YMD",Toolkit.formatData(curYear,"0000")+"/"+Toolkit.formatData(curMonth,"00")
				+"/"+Toolkit.formatData(workDay,"00"));
		ruleMap.put("COMPARE_YEAR", Toolkit.formatData(compareYear,"0000"));
		ruleMap.put("COMPARE_MONTH", Toolkit.formatData(compareMonth,"00"));
		return ruleMap;
	}
	/** 5分公司客户货需未确认：所有异常客户及关系人
	 * 货需只有四个渠道需要录：强网三个+县城，2010-08-09
	 * @param ruleMap
	 * @return
	 */
	private List<NotifyBo> getNotifyBoOfRequireNotAuditByCompany(Map<String,String> ruleMap){
		StringBuffer buf=new StringBuffer("");
		buf.append("select distinct  ?  as abnormality_type ")
			.append(",'分公司客户货需未审核' as abnormality_des ")
			.append(",'您有以下客户货需未审核：' as abnormality_info ")
			.append(",A.COMPANY_ID")
			.append(",A.COMPANY_NAME")
			.append(",A.ZJL_ID")
			.append(",A.ZJL_NAME")
			.append(",A.ZONGJIAN_ID")
			.append(",A.ZONGJIAN_NAME")
			.append(",A.ZONGJIAN_POS")
			.append(",A.ZHUANYUAN_ID")
			.append(",A.ZHUANYUAN_NAME")
			.append(",A.ZHUANYUAN_DID")
			.append(",A.SUOZHANG_ID")
			.append(",A.SUOZHANG_NAME")
			.append(",A.ZHUREN_ID")
			.append(",A.ZHUREN_NAME")
			.append(",A.BRANCH_ID")
			.append(",A.BRANCH_NAME")
			.append(",A.YEDAI_ID")
			.append(",A.YEDAI_NAME")
			.append(",A.CUSTOMER_ID")
			.append(",A.CUSTOMER_DID as division_id")
			.append(",g.NAME AS DIVISION_NAME")
			.append(",A.CREDIT_ID")
			.append(",? as is_first_time")
			.append(",? as start_ymd     ")
			
			.append(" from require_head_tbl b ")
			.append(" inner join customer_info_tbl c on b.customer_sid=c.sid and c.status is null ")
			.append(" inner join sales_area_rel d on b.credit_id=d.credit_id ")
			.append(" inner join abnormality_cust_interim e on c.id=e.customer_id and d.divsion_sid=e.division_id and e.abnormal_type_id=? ")
			.append(" inner join all_customer_view a on c.id=a.customer_id and d.divsion_sid=a.customer_did ")
			.append(" inner join divsion g on a.customer_did=g.sid and g.sid in (1,30,31,15,16,17,37,38)  ")//休一渠道拆分为米果果冻和糖果炒货,新增37,38
			
			.append(" where b.zj_status='0' and b.require_year=? and b.require_month=? ");

		return this.getiCustomerJdbcOperations().query(buf.toString(),new NotifyBoRowMapper(),new Object[]{
			Constant.EXC_TYPE_CODE_REQUIRE_UNAUDIT_BY_COMPANY,ruleMap.get("IS_FIRST_TIME"),ruleMap.get("START_YMD")
			,Constant.EXC_TYPE_CODE_REQUIRE_UNAUDIT_BY_COMPANY,ruleMap.get("COMPARE_YEAR"),ruleMap.get("COMPARE_MONTH")});
	}
}
