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

/**业代库存未录入(1)
 * @author 00078588
 *
 */
@Component
public class StorageNotInputCommand extends AbstractNotifyCommand {

	@Override
	protected List<NotifyBo> queryResults() {
		// TODO Auto-generated method stub
		Map<String,String> ruleMap=getRuleMap();
		return (ruleMap==null)?new ArrayList<NotifyBo>():getNotifyBoOfStorageNotInput(ruleMap);
	}
	/**
	 * 规则一：
	 * 每月6、16、26日中午12点后，开始检查每月5、15、25日应该录入的数据。
	 * @return
	 */
	public Map<String,String> getRuleMap(){
		Map<String,String> ruleMap=new HashMap<String,String>();
		Calendar cal=Calendar.getInstance();
		int compareYear=cal.get(Calendar.YEAR);//录库存的时间(年)
		int compareMonth=cal.get(Calendar.MONTH)+1;//录库存的时间(月)
		int compareDay=0;//录库存的时间(日)		
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
	private List<NotifyBo> getNotifyBoOfStorageNotInput(Map<String,String> ruleMap){
		StringBuffer buf=new StringBuffer("");
		buf.append("select distinct  ?  as abnormality_type ")
			.append(",'业代库存未录入' as abnormality_des ")
			.append(",'您有以下客户库存未录入：' as abnormality_info ")
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
			.append(",b.NAME AS DIVISION_NAME")
			.append(",? as is_first_time")
			.append(",? as start_ymd     ")
			
			.append(" from all_customer_view  A  ")
			.append(" inner join DIVSION b ON b.SID=A.CUSTOMER_DID  ")
			.append(" inner join customer_info_tbl c on a.customer_id=c.id and c.status is null  ")
			.append(" inner join ")
			.append("    ( ")
			.append("     select distinct AC.customer_id,AC.division_id    ")
			.append("     from abnormality_cust_interim ac ")
			.append(" 	  inner join sales_area_rel sar on ac.division_id=sar.divsion_sid ")
			.append("     left join CUSTOMER_STORAGE_INFO_TBL csit on ac.customer_id=csit.CUSTOMER_ID and sar.credit_id=csit.credit_id ")
			.append("                                                 and CSIT.YEARMONTH=? and CSIT.DAY=?  ")
			.append("     where ac.abnormal_type_id=? and CSIT.CUSTOMER_ID IS NULL   ")
			.append("    ) YCC ON YCC.customer_id=A.CUSTOMER_ID and YCC.division_id=A.CUSTOMER_DID  ");
		
		return this.getiCustomerJdbcOperations().query(buf.toString(),new NotifyBoRowMapper(),new Object[]{
			Constant.EXC_TYPE_CODE_STORAGE_UNFILLED,ruleMap.get("IS_FIRST_TIME"),ruleMap.get("START_YMD")
			,ruleMap.get("COMPARE_YEAR_MONTH"),ruleMap.get("COMPARE_DAY"),Constant.EXC_TYPE_CODE_STORAGE_UNFILLED});
	}
}
