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

/**客户库存未确认(2)
 * @author 00078588
 *
 */
@Component
public class StorageNotConfirmCommand extends AbstractNotifyCommand {

	@Override
	protected List<NotifyBo> queryResults() {
		// TODO Auto-generated method stub
		Map<String,String> ruleMap=getRuleMap();
		return (ruleMap==null)?new ArrayList<NotifyBo>():getNotifyBoOfStorageNotConfirm(ruleMap);
	}

	/**
	 * 规则二：
	 * 每月8、18、28日凌晨0:01 点后，开始检查每月5、15、25日录入 应该确认的数据，到14、24、4日晚上23:59点结束。
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
		// 临时调整2010国庆期间，客户库存确认时间由8日凌晨调整到11日凌晨，过了10月14日后可以恢复。20101027恢复-陈义。
		if(cal.get(Calendar.DAY_OF_MONTH) <= 4){// 正确为4
			compareDay = 25;
			stopDay = 27;
			if(compareMonth == 1){
				compareMonth = 12;
				compareYear -= 1;
			}else{
				compareMonth--;
			}
		}else if(cal.get(Calendar.DAY_OF_MONTH) >= 8 && cal.get(Calendar.DAY_OF_MONTH) <= 14){//8-->11-->8
			compareDay = 5;
			stopDay = 7;//07-->10-->07
		}else if(cal.get(Calendar.DAY_OF_MONTH) >= 18 && cal.get(Calendar.DAY_OF_MONTH) <= 24){
			compareDay = 15;
			stopDay = 17;
		}else if(cal.get(Calendar.DAY_OF_MONTH) >= 28){
			compareDay = 25;
			stopDay = 27;
		}else{
			return null;
		}
		if(cal.get(Calendar.DAY_OF_MONTH) == 8 || cal.get(Calendar.DAY_OF_MONTH) == 18 || cal.get(Calendar.DAY_OF_MONTH) == 28){
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
	private List<NotifyBo> getNotifyBoOfStorageNotConfirm(Map<String,String> ruleMap){
		StringBuffer buf=new StringBuffer("");
		buf.append("select distinct  ?  as abnormality_type ")
			.append(",'客户库存未确认' as abnormality_des ")
			.append(",'您有以下客户库存未确认：' as abnormality_info ")
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
			.append(",A.CUSTOMER_DID as division_id ")
			.append(",b.NAME AS DIVISION_NAME")
			.append(",A.CREDIT_ID")
			.append(",? as is_first_time")
			.append(",? as START_YMD      ")
			
			.append(" from all_customer_view a  ")
			.append(" inner join divsion b on a.customer_did=b.sid  ")			
			.append(" inner join abnormality_cust_interim c on a.customer_id=c.customer_id and a.customer_did=c.division_id  ")
			.append(" inner join customer_storage_info_tbl e on c.customer_id=e.customer_id and a.credit_id=e.credit_id  ")
			.append("                                          and e.YEARMONTH=? and e.DAY=?  ")
			.append("                                          and (e.STATUS IS NULL OR e.STATUS =0 OR e.STATUS=1) ")
			.append(" inner join customer_info_tbl f on a.customer_id=f.id and f.status is null ")
			
			.append(" where  c.abnormal_type_id=? ");

		return this.getiCustomerJdbcOperations().query(buf.toString(),new NotifyBoRowMapper(),new Object[]{
			Constant.EXC_TYPE_CODE_STORAGE_UNCONFIRM,ruleMap.get("IS_FIRST_TIME"),ruleMap.get("START_YMD")
			,ruleMap.get("COMPARE_YEAR_MONTH"),ruleMap.get("COMPARE_DAY"),Constant.EXC_TYPE_CODE_STORAGE_UNCONFIRM});
	}
}
