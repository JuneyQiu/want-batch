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

/**客户货需未录入(3)
 * @author 00078588
 * 
 */
@Component
public class RequireNotInputCommand extends AbstractNotifyCommand {

	@Override
	protected List<NotifyBo> queryResults() {
		// TODO Auto-generated method stub
		Map<String,String> ruleMap=getRuleMap();
		return (ruleMap==null)?new ArrayList<NotifyBo>():getNotifyBoOfRequireNotInput(ruleMap);
	}

	/**
	 * 规则三：
	 * 1.从7日00:01，检查客户货需未录入
	 * 2.当月只能录下个月的货需
	 */
	public Map<String,String> getRuleMap(){
		Map<String,String> ruleMap=new HashMap<String,String>();
		Calendar cal=Calendar.getInstance();
		int curYear=cal.get(Calendar.YEAR);//当前年
		int curMonth=cal.get(Calendar.MONTH)+1;//当前月
		int inputDay=2;//录货需的时间(日)
		
		int compareYear=0;
		int compareMonth=0;
		String isFirstTime = "0";
		if(cal.get(Calendar.DAY_OF_MONTH)<7||cal.get(Calendar.DAY_OF_MONTH)>11) return null;//只有7号～11号之间才需要通报此异常

		if(curMonth == 12){
			compareYear=curYear+1;
			compareMonth=1;
		}else{
			compareYear=curYear;
			compareMonth=curMonth+1;
		}
		
		if(cal.get(Calendar.DAY_OF_MONTH) == 7){//只有7号才需要记录成绩单
			isFirstTime = "1";
		}else{
			isFirstTime = "0";
		}
		
		ruleMap.put("IS_FIRST_TIME", isFirstTime);
		ruleMap.put("START_YMD",Toolkit.formatData(curYear,"0000")+"/"+Toolkit.formatData(curMonth,"00")
				+"/"+Toolkit.formatData(inputDay,"00"));
		ruleMap.put("COMPARE_YEAR", Toolkit.formatData(compareYear,"0000"));
		ruleMap.put("COMPARE_MONTH", Toolkit.formatData(compareMonth,"00"));
		return ruleMap;
	}
	/**
	 * 3客户货需未录入：所有异常客户及关系人
	 * 货需只有四个渠道需要录：强网三个+县城，2010-08-09 chenyi
	 * @param ruleMap
	 * @return
	 */
	private List<NotifyBo> getNotifyBoOfRequireNotInput(Map<String,String> ruleMap){
		StringBuffer buf=new StringBuffer("");
		buf.append("select distinct  ?  as abnormality_type ")
			.append(",'客户货需未录入' as abnormality_des ")
			.append(",'您有以下客户货需未录入：' as abnormality_info ")
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
			.append(",A.CREDIT_ID")
			.append(",? as is_first_time")
			.append(",? as start_ymd     ")
			
			.append(" from all_customer_view a ")
			.append(" inner join divsion b on a.customer_did=b.sid ")
			.append(" inner join abnormality_cust_interim c on a.customer_id=c.customer_id and a.customer_did=c.division_id and c.abnormal_type_id=? ")
			.append(" inner join user_info_tbl d on substr(a.customer_id,3,8)=substr(d.account,1,8) and d.user_type_sid=1 and d.status='1' ")
			.append(" inner join customer_info_tbl e on a.customer_id=e.id and e.status is null ")
			.append(" inner join AUTH_USER_GROUP_REL f on d.sid=f.user_sid ")
			.append(" inner join AUTH_GROUP_APPS_REL g on g.USERGROUP_SID=f.USERGROUP_SID AND g.USERGROUP_SID=0 ")
			.append(" inner join APPS_INFO_TBL h on g.apps_sid=h.sid and h.sid=12 ")
			.append(" left join REQUIRE_HEAD_TBL i on e.sid=i.customer_sid and a.credit_id=i.credit_id ")
			.append("                                 and i.require_year=? and i.require_month=? ")
			
			.append(" where i.customer_sid is null and a.customer_did in ('1','30','31','15','16','17','37','38')");//休一渠道拆分为米果果冻和糖果炒货,新增37,38;16不用，未免出错，故保留
		
		return this.getiCustomerJdbcOperations().query(buf.toString(),new NotifyBoRowMapper(),new Object[]{
			Constant.EXC_TYPE_CODE_REQUIRE_UNFILLED,ruleMap.get("IS_FIRST_TIME"),ruleMap.get("START_YMD")
			,Constant.EXC_TYPE_CODE_REQUIRE_UNFILLED,ruleMap.get("COMPARE_YEAR"),ruleMap.get("COMPARE_MONTH")});
	}
}
