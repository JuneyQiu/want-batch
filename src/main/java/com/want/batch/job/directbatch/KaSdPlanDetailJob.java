/**
 * 
 */
package com.want.batch.job.directbatch;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.directbatch.dao.KaSdPlanDao;

/**
 * @author MandyZhang
 *
 * 更新直营特陈计划明显表中业代和主任提交时间栏位(因为这个两个栏位是新添加的).
 * 
 */
@Component
public class KaSdPlanDetailJob extends AbstractWantJob {

	public static int INSERTNUMBER = 60000;
	
	@Autowired
	private KaSdPlanDao kaSdPlanDao;
	
	public void execute() throws Exception {
		
		// 取得当前系统年月
		Calendar currentCal = Calendar.getInstance();
		String currentYearMonth = new SimpleDateFormat("yyyyMM").format(currentCal.getTime());
		
		// 取得当前系统年月的上一月
		currentCal.add(Calendar.MONTH, -1);
		String preYearMonth = new SimpleDateFormat("yyyyMM").format(currentCal.getTime());
		
		String dateParam = "'" + currentYearMonth + "','" + preYearMonth + "'";
		
		// 业代
		List<Map<String, Object>> salesList = this.getKaSdPlanDao().getKaSdActualInputTime(dateParam, "sales");
		updateKaSdPlanTime(salesList, "sales");
		
		// 主任
		List<Map<String, Object>> directorList = this.getKaSdPlanDao().getKaSdActualInputTime(dateParam, "director");
		updateKaSdPlanTime(directorList, "director");
	}

	// 修改计划表中业代和主任提交时间
	@SuppressWarnings("static-access")
	private void updateKaSdPlanTime(List<Map<String, Object>> list, String flag) throws SQLException {
	
		List<Object[]> kaSdPlanList = new ArrayList<Object[]>();
		
		// 修改计划表中业代和主任提交时间
		for (int i = 0; i < list.size(); i++) {
			
			Map<String, Object> map = list.get(i);
			
			String updateDate = map.get("UPDATE_DATE").toString();
			
			Object[] obj = new Object[2];
			obj[0] = updateDate.substring(0, updateDate.length()-2);
			obj[1] = map.get("PLAN_DETAIL_SID");
			
			kaSdPlanList.add(obj);
			
			if ((i % this.INSERTNUMBER == 0)|| (i == list.size() - 1)) {
				
				this.getKaSdPlanDao().updateKaSdPlanTime(kaSdPlanList, flag, this.getiCustomerJdbcOperations());
				kaSdPlanList.clear();
			}
		}
	}
	
	/**
	 * @return the kaSdPlanDao
	 */
	public KaSdPlanDao getKaSdPlanDao() {
		return kaSdPlanDao;
	}

	/**
	 * @param kaSdPlanDao the kaSdPlanDao to set
	 */
	public void setKaSdPlanDao(KaSdPlanDao kaSdPlanDao) {
		this.kaSdPlanDao = kaSdPlanDao;
	}

}
