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

/**客户库存总金额异常(8)
 * @author 00078588
 * 
 */
@Component
public class CustomerStorageAmountExcCommand extends AbstractNotifyCommand {

	@Override
	protected List<NotifyBo> queryResults() {
		// TODO Auto-generated method stub
		Map<String,String> ruleMap=getRuleMap();
		return (ruleMap==null)?new ArrayList<NotifyBo>():getNotifyBoOfCustomerStorageAmountExc(ruleMap);
	}

	/**
	 * 规则八：
	 * 从26日12点 检查客户库存总金额>=客户次月签约目标额
	 * 
	 */
	public Map<String,String> getRuleMap(){
		Map<String,String> ruleMap=new HashMap<String,String>();
		Calendar cal=Calendar.getInstance();
		int curYear=cal.get(Calendar.YEAR);
		int curMonth=cal.get(Calendar.MONTH)+1;
		int compareDay=25;
		
		int nextYear=0;
		int nextMonth=0;
		String isFirstTime = "1";//每个月只有26号这一天检查异常，全部记录成绩单
		
		if(cal.get(Calendar.DAY_OF_MONTH) != 26) return null;
		
		if(curMonth == 12){
			nextYear=curYear+1;
			nextMonth=1;
		}else{
			nextYear=curYear;
			nextMonth=curMonth+1;
		}
		
		ruleMap.put("IS_FIRST_TIME", isFirstTime);
		ruleMap.put("START_YMD",Toolkit.formatData(curYear,"0000")+"/"+Toolkit.formatData(curMonth,"00")+"/"+"26");
		ruleMap.put("COMPARE_YEAR_MONTH",Toolkit.formatData(curYear,"0000")+Toolkit.formatData(curMonth,"00"));
		ruleMap.put("COMPARE_DAY",Toolkit.formatData(compareDay,"00"));
		ruleMap.put("NEXT_YEAR_MONTH", Toolkit.formatData(nextYear,"0000")+Toolkit.formatData(nextMonth,"00"));
		return ruleMap;		
	}
	/** 8截止到每月25日送旺客户库存总金额异常客户：所有异常客户及关系人
	 * @param ruleMap
	 * @return
	 */
	private List<NotifyBo> getNotifyBoOfCustomerStorageAmountExc(Map<String,String> ruleMap){
		StringBuffer buf=new StringBuffer("");
		buf.append("select distinct  ?  as abnormality_type ")
			.append(",'客户库存总金额异常' as abnormality_des ")
			.append(",'您有以下客户库存总金额异常：' as abnormality_info ")
			.append(",f.zjl_id")
			.append(",f.zjl_name")
			.append(",f.company_id")
			.append(",f.company_name")
			.append(",f.zongjian_id")
			.append(",f.zongjian_name")
			.append(",f.zhuanyuan_id")
			.append(",f.zhuanyuan_name")
			.append(",f.branch_id")
			.append(",f.branch_name")
			.append(",f.suozhang_id")
			.append(",f.suozhang_name")
			.append(",f.customer_did as division_id")
			.append(",g.name as division_name")
			.append(",f.zhuren_id")
			.append(",f.zhuren_name")
			.append(",f.yedai_id")
			.append(",f.yedai_name")
			.append(",f.customer_id")
			.append(",? as is_first_time")
			.append(",? as start_ymd     ")
			
			.append(" from ")
			.append("   ( ")
			.append("      select a.customer_id,a.division_id,sum(c.prod_price*c.total_qty) as total_amount ")
			.append("      from abnormality_cust_interim a ")
			.append("      inner join sales_area_rel b on a.division_id=b.divsion_sid ")
			.append("      inner join customer_storage_info_tbl c on a.customer_id=c.customer_id and b.credit_id=c.credit_id ")
			.append("      where a.abnormal_type_id=? and c.yearmonth=? and c.day=? and c.total_qty>0 ")
			.append("      group by a.customer_id,a.division_id ")
			.append("   ) d ")
			.append(" inner join customer_target_tbl e on d.customer_id=e.customer_id and d.division_id=e.division_sid ")
			.append("                                  and d.total_amount>=e.target_amount and e.yearmonth=?  ")
			.append(" inner join all_customer_view f on d.customer_id=f.customer_id and d.division_id=f.customer_did ")
			.append(" inner join divsion g on d.division_id=g.sid ")
			.append(" inner join customer_info_tbl h on f.customer_id=h.id and h.status is null  ");

		return this.getiCustomerJdbcOperations().query(buf.toString(),new NotifyBoRowMapper(),new Object[]{
			Constant.EXC_TYPE_CODE_CUST_STORAGE_TOTAL_AMOUNT,ruleMap.get("IS_FIRST_TIME"),ruleMap.get("START_YMD")
			,Constant.EXC_TYPE_CODE_CUST_STORAGE_TOTAL_AMOUNT,ruleMap.get("COMPARE_YEAR_MONTH"),ruleMap.get("COMPARE_DAY")
			,ruleMap.get("NEXT_YEAR_MONTH")});
	}
}
