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

/**促销品库存未录入(10)
 * @author 00078588
 * 
 */
@Component
public class WenStorageNotInputCommand extends AbstractNotifyCommand {

	@Override
	protected List<NotifyBo> queryResults() {
		// TODO Auto-generated method stub
		Map<String,String> ruleMap=getRuleMap();
		return (ruleMap==null)?new ArrayList<NotifyBo>():getNotifyBoOfWenStorageNotInput(ruleMap);
	}

	/**
	 * 规则十(同一)：
	 * 每月6、16、26日中午12点后，开始检查每月5、15、25日应该录入的数据。
	 * @return
	 */
	public Map<String,String> getRuleMap(){
		Map<String,String> ruleMap=new HashMap<String,String>();
		Calendar cal=Calendar.getInstance();
		int compareYear=cal.get(Calendar.YEAR);
		int compareMonth=cal.get(Calendar.MONTH)+1;
		int compareDay=0;
		int stopDay=0;
		
		String isFirstTime = "";
		
		if(cal.get(Calendar.DAY_OF_MONTH) <= 4){
			compareDay = 25;
			stopDay = 26;
			if(compareMonth == 1){
				compareMonth = 12;
				compareYear -= 1;
			}else{
				compareMonth--;
			}
		// 临时调整2010国庆期间，库存录入时间由6日改为9日。过了10月14日后再调整过来。20101027恢复-陈义。
		}else if(cal.get(Calendar.DAY_OF_MONTH) == 6 && cal.get(Calendar.HOUR_OF_DAY) >= 12 
				|| cal.get(Calendar.DAY_OF_MONTH) > 6 && cal.get(Calendar.DAY_OF_MONTH) <= 14){
			compareDay = 5;
			stopDay = 6;
//		}else if((intNowDay == 9 && intNowHour >= 12) || ((intNowDay > 9) && (intNowDay <= 14))){
//			compareDay = "05";
//			stopDay = "09";
        }else if(cal.get(Calendar.DAY_OF_MONTH) == 16 && cal.get(Calendar.HOUR_OF_DAY) >= 12 
        		|| cal.get(Calendar.DAY_OF_MONTH) > 16 && cal.get(Calendar.DAY_OF_MONTH) <= 24){
        	compareDay = 15;
        	stopDay = 16;
        }else if(cal.get(Calendar.DAY_OF_MONTH) == 26 && cal.get(Calendar.HOUR_OF_DAY) >= 12 
        		|| cal.get(Calendar.DAY_OF_MONTH) > 26){
        	compareDay = 25;
        	stopDay = 26;
        }else{
        	return null;
        }
		
		if(cal.get(Calendar.DAY_OF_MONTH) == 6 || cal.get(Calendar.DAY_OF_MONTH) == 16 || cal.get(Calendar.DAY_OF_MONTH) == 26){
			isFirstTime = "1";
		}else{
			isFirstTime = "0";
		}
		
		ruleMap.put("IS_FIRST_TIME", isFirstTime);
		ruleMap.put("START_YMD", Toolkit.formatData(compareYear, "0000") + "/" + Toolkit.formatData(compareMonth, "00")
				+ "/" + Toolkit.formatData(stopDay, "00"));
		ruleMap.put("COMPARE_YEAR_MONTH", Toolkit.formatData(compareYear, "0000")+Toolkit.formatData(compareMonth, "00"));
		ruleMap.put("COMPARE_DAY", Toolkit.formatData(compareDay, "00"));
		
		return ruleMap;
	}
	/** 10促销品库存未录入(限县城 --送旺客户)：所有异常客户及关系人
	 * @param ruleMap
	 * @return
	 */
	private List<NotifyBo> getNotifyBoOfWenStorageNotInput(Map<String,String> ruleMap){
		StringBuffer buf=new StringBuffer("");
		buf.append("select distinct  ?  as abnormality_type ")
			.append(",'促销品库存未录入' as abnormality_des ")
			.append(",'您有以下客户促销品库存未录入：' as abnormality_info ")
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
			
			.append(" from abnormality_cust_interim a ")
			.append(" inner join customer_info_tbl b on a.customer_id=b.id and b.status is null ")
			.append(" inner join user_info_tbl c on substr(b.id,3,8)=substr(c.account,1,8) and c.user_type_sid=1 ")
			.append("                            and c.status='1' and a.abnormal_type_id=? ")
			.append(" left join ")
			.append("      (  ")
			.append("          select distinct customer_sid ")
			.append("          from wen_customer_store_tbl wcst ")
			.append("          where wcst.store_date_num=? ")
			.append("      ) d on b.sid=d.customer_sid  ")
			.append(" inner join all_customer_view e on a.customer_id=e.customer_id and a.division_id=e.customer_did ")
			.append(" inner join divsion f on a.division_id=f.sid ")
			
			.append(" where a.division_id in (1,30,31) and d.customer_sid is null ");

		return this.getiCustomerJdbcOperations().query(buf.toString(),new NotifyBoRowMapper(),new Object[]{
			Constant.EXC_TYPE_CODE_WEN_STORAGE_UNINPUT,ruleMap.get("IS_FIRST_TIME"),ruleMap.get("START_YMD")
			,Constant.EXC_TYPE_CODE_WEN_STORAGE_UNINPUT,ruleMap.get("COMPARE_YEAR_MONTH")+ruleMap.get("COMPARE_DAY")});
	}
}
