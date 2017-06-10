package com.want.batch.job.archive.notify.jobs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.stereotype.Component;

import com.want.batch.job.archive.notify.pojo.NotifyBo;
import com.want.batch.job.archive.notify.util.Constant;

/**主任未提交特陈(实际)检核信息(19)
 * @author 00078588
 * 
 */
@Component
public class ZrSdActualNotSubmitCommand extends AbstractNotifyCommand {

	@Override
	protected List<NotifyBo> queryResults() {
		// TODO Auto-generated method stub
		Calendar cal=Calendar.getInstance();
		int startDay=5;//
		int endDay=13;//

		if(cal.get(Calendar.DAY_OF_MONTH)<startDay||cal.get(Calendar.DAY_OF_MONTH)>endDay) return new ArrayList<NotifyBo>();
		return getNotifyBoOfZrSdActualNotSubmit(Constant.EXC_TYPE_CODE_ZR_SD_ACTUAL_UNSUBMIT);
	}

	/** 19主任未提交特陈(实际)检核信息
	 * @param notifyId
	 * @return
	 */
	private List<NotifyBo> getNotifyBoOfZrSdActualNotSubmit(int notifyId){
		StringBuffer buf=new StringBuffer("");
		buf.append("select distinct h.id as abnormality_type")
			.append(",h.name as abnormality_des")
			.append(",h.name as abnormality_info")
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
			.append(",f.sid as division_id")
			.append(",f.name as division_name")
			.append(",e.zhuren_id")
			.append(",e.zhuren_name")
			.append(",'-' as yedai_id")
			.append(",'-' as yedai_name")
			.append(",e.customer_id")
			.append(",'1' as is_first_time")
			.append(",to_char(sysdate-1,'yyyy/mm/dd') as start_ymd       ")
			
			.append(" from special_display_actual a ")
			.append(" inner join special_display_application b on a.application_sid=b.sid ")
			.append(" inner join special_display_policy c on b.policy_sid=c.sid ")
			.append(" inner join customer_info_tbl d on b.customer_sid=d.sid and d.status is null ")
			.append(" inner join all_customer_view e on d.id=e.customer_id and c.division_sid=e.customer_did ")
			.append(" inner join divsion f on c.division_sid=f.sid ")
			.append(" inner join abnormality_cust_interim g on d.id=g.customer_id and f.sid=g.division_id ")
			.append(" inner join notify_info_tbl h on g.abnormal_type_id=h.id ")
			
			.append(" where b.year_month=(case ")
			.append("                        when to_char(sysdate,'mm')='01' then to_char(sysdate,'yyyy')-1||'12' ")
			.append("                        else to_char(sysdate,'yyyymm')-1||'' ")
			.append("                     end) ")
			.append("       and a.submit_check_status_zr<>'1' and h.id=?  ");

		return this.getiCustomerJdbcOperations().query(buf.toString(),new NotifyBoRowMapper(),new Object[]{notifyId});
	}
	
}
