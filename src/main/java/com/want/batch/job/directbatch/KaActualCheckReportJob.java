/**
 * 
 */
package com.want.batch.job.directbatch;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.directbatch.dao.KaActualCheckReportDao;
import com.want.batch.job.directbatch.dao.KaSdPlanDao;
import com.want.batch.job.directbatch.dao.KaSystemEmpDao;
import com.want.batch.job.directbatch.dao.RouteInfoDao;
import com.want.batch.job.utils.ProjectConfig;
import com.want.data.pojo.BatchStatus;

/**
 * @author MandyZhang
 *
 *	直营特陈实际检核报表Job.
 *	该排程每天执行，执行时一次捞取当前年月，及当前年月前一月的资料
 *
 */
@Component
public class KaActualCheckReportJob extends AbstractWantJob {

	public static int STORENUMBER = 1000;
	
	@Autowired
	private KaActualCheckReportDao kaActualCheckReportDao;
	
	@Autowired
	private KaSdPlanDao kaSdPlanDao;
	
	@Autowired
	private RouteInfoDao routeInfoDao;
	
	@Autowired
	private KaSystemEmpDao kaSystemEmpDao;
	
	@Autowired
	public static int INSERTNUMBER = 60000;
	
	@SuppressWarnings("static-access")
	public void execute() throws Exception {
		
		String divisionSids = ProjectConfig.getInstance().getString("divisionSids");
		
		// 删除KA_ACTUAL_CHECK_REPORT表中的资料
		this.getKaActualCheckReportDao().deleteAllData(this.getiCustomerJdbcOperations());
		
		// 取得当前系统年月
		Calendar currentCal = Calendar.getInstance();
		String currentYearMonth = new SimpleDateFormat("yyyyMM").format(currentCal.getTime());
		
		// 取得当前系统年月的上一月
		currentCal.add(Calendar.MONTH, -1);
		String preYearMonth = new SimpleDateFormat("yyyyMM").format(currentCal.getTime());
		
		String dateParam = "'" + currentYearMonth + "','" + preYearMonth + "'";
		
		// 取得当前系统月及系统前一月的直营特陈资料tchList
		List<Map<String, Object>> tchList = this.getKaSdPlanDao().getTwoYearMonthData(dateParam, this.getiCustomerJdbcOperations());
		
		if (tchList != null && tchList.size() != 0 ) {
			
			// 用于所有记录特陈终端
			Set<String> storeSet = new HashSet<String>();
			
			// 循环遍历tchList取得非重复的终端
			for (int i = 0; i < tchList.size(); i++) {
				
				Map<String, Object> map = tchList.get(i);
				
				storeSet.add((String) map.get("STORE_ID"));
			}
			
			// 将set转成list
			List<String> storeList = new ArrayList<String>(storeSet);
			
			// 用于拼接中的查询条件
			StringBuilder storeIds = new StringBuilder();
			
			// 用于记录特陈终端对应的路线终端
			List<Map<String, Object>> routeList = new ArrayList<Map<String,Object>>();
			
			int storeNumber = (int)Math.ceil(new BigDecimal(storeSet.toArray().length).divide(new BigDecimal(this.STORENUMBER)).doubleValue());
			for (int i = 0; i < storeNumber; i++) {
				
				List<String> subList = null;
				
				// 如果特陈终端的个数大于1千，则每一个终端进行查询
				if (i != storeNumber - 1) {
					
					subList = storeList.subList(i * this.STORENUMBER, (i + 1) * this.STORENUMBER);
				}
				else {
					
					subList = storeList.subList(i * this.STORENUMBER, storeList.size());
				}
				
				storeIds.append("'" + StringUtils.join(subList, "','") + "'");
				
				// 根据tchList中的去重复终端，查询路线上的终端对应的业代，若对应多个人，则显示存储多个人的工号和姓名routeList
				routeList = this.getRouteInfoDao().getRouteEmpByStoreIdsAndYearMonth(dateParam, StringUtils.removeEnd(storeIds.toString(), ","), divisionSids);
				
				// 将routeList中的对应的业代放入tchList中
				this.getRouteEmp(tchList, routeList);
				storeIds = new StringBuilder();
			}
			
			// 查询出全国各系统对应的主任，若对应多个人，则显示存储多个人的工号和姓名sysDirectList
			List<Map<String, Object>> sysDirectList = this.getKaSystemEmpDao().getSysDirector(divisionSids);
			
			// 将终端对应的主任放入tchList中
			this.getSysDirector(tchList, sysDirectList);
			
			// 批量新增，每6万笔新增一次
			this.insertData(tchList);
		}
		
	}
	
	/**
	 * <pre>
	 * 2013-4-10 Mirabelle
	 * 	将终端对应的主任放入tchList中
	 * </pre>
	 * 
	 * @param tchList
	 * @param sysDirectList
	 */
	private void getSysDirector(List<Map<String, Object>> tchList, List<Map<String, Object>> sysDirectList) {

		// 记录分公司系统对应的主任工号
		String directId = "";
		
		// 记录分公司系统对应的主任姓名
		String directName = "";
		
		for (int i = 0; i < tchList.size(); i++) {
			
			Map<String, Object> map = tchList.get(i);
			
			for (int j = 0; j < sysDirectList.size(); j++) {
				
				// 如果同分公司，系统下，取得对应的主任
				if (String.valueOf(map.get("COMPANY_ID")).equals(String.valueOf(sysDirectList.get(j).get("COMPANY_ID")))){
					
					if(String.valueOf(map.get("SYS_ID")).equals(String.valueOf(sysDirectList.get(j).get("SYS_ID")))
							&& String.valueOf(map.get("DIVISION_SID")).equals(String.valueOf(sysDirectList.get(j).get("DIVISION_ID")))) {
					
						directId = directId + "," + sysDirectList.get(j).get("ACCOUNT");
						directName = directName + "," + sysDirectList.get(j).get("USER_NAME");
					}
				}
			}
			
			if (StringUtils.isNotEmpty(directId) && StringUtils.isNotEmpty(directName)) {
				
				// 放入map中.substring(1)
				map.put("USER_ACCOUNT_DIRECTOR", directId.substring(1));
				map.put("USER_NAME_DIRECTOR", directName.substring(1));
				
				directId = "";
				directName = "";
			}
		}
	}
	
	/**
	 * <pre>
	 * 2013-4-10 Mirabelle
	 * 	批量增入数据
	 * </pre>
	 * 
	 * @param tchList
	 */
	@SuppressWarnings("static-access")
	private void insertData(List<Map<String, Object>> tchList) {
		
		// 记录批量新增的资料
		List<Object[]> kaActualCheckReportList = new ArrayList<Object[]>();
		
		for (int i = 0; i < tchList.size(); i++) {
		
			Map<String, Object> map = tchList.get(i);
			
			Object[] obj = new Object[23];
			obj[0] = map.get("DIVISION_SID");
			obj[1] = map.get("SYS_ID");
			obj[2] = map.get("COMPANY_ID");
			obj[3] = map.get("STORE_ID");
			obj[4] = map.get("SD_NO");
			obj[5] = map.get("BPM_ID");
			obj[6] = map.get("TPM_ID");
			obj[7] = map.get("YEAR_MONTH");
			obj[8] = map.get("PROMOTION_DATE_START");
			obj[9] = map.get("PROMOTION_DATE_END");
			obj[10] = map.get("DISPLAY_ID");
			obj[11] = map.get("PROD_ID");
			obj[12] = map.get("UPDATE_USER_SALES");
			obj[13] = map.get("USER_ACCOUNT_SALES") != null ? String.valueOf(map.get("USER_ACCOUNT_SALES")) : "";
			obj[14] = map.get("USER_NAME_SALES") != null ? String.valueOf(map.get("USER_NAME_SALES")) : "";
			obj[15] = "1".equals(String.valueOf(map.get("SUBMIT_STATUS_SALES"))) ? "1" : "0";
			obj[16] = map.get("SUBMIT_DATE_SALES");
			obj[17] = map.get("UPDATE_USER_DIRECTOR");
			obj[18] = map.get("USER_ACCOUNT_DIRECTOR") != null ? String.valueOf(map.get("USER_ACCOUNT_DIRECTOR")) : "";
			obj[19] = map.get("USER_NAME_DIRECTOR") != null ? String.valueOf(map.get("USER_NAME_DIRECTOR")) : "";
			obj[20] = "1".equals(String.valueOf(map.get("SUBMIT_STATUS_DIRECTOR"))) ? "1" : "0";
			obj[21] = map.get("SUBMIT_DATE_DIRECTOR");
			obj[22] = new Date();
			
			kaActualCheckReportList.add(obj);
			
			if ((i % this.INSERTNUMBER == 0)|| (i == tchList.size() - 1)) {
				
				this.getKaActualCheckReportDao().insertKaActualCheckReport(kaActualCheckReportList, this.getiCustomerJdbcOperations());
				kaActualCheckReportList.clear();
			}
		}
		
	}
	
	/**
	 * <pre>
	 * 2013-4-10 Mirabelle
	 * 	将终端对应的业代放入直营特陈资料中
	 * </pre>
	 * 
	 * @param tchList 直营特陈对应的资料
	 * @param routeList 终端对应的业代
	 */
	private void getRouteEmp(List<Map<String, Object>> tchList, List<Map<String, Object>> routeList) {

		// 记录终端对应的业代工号
		String empId = "";
		
		// 记录终端对应的业代姓名
		String empName = "";
		
		for (int i = 0; i < tchList.size(); i++) {
			
			Map<String, Object> map = tchList.get(i);
			
			// 如果该笔资料对应的业代没有值，则循环routeList取得该终端对应的业代，对应多个业代时进行拼接
			if (map.get("USER_ACCOUNT_SALES") == null) {
				
				for (int j = 0; j < routeList.size(); j++) {
					
					// 如果特陈年月和路线年月相同，则判断终端和事业部，否则跳出循环
					if (String.valueOf(tchList.get(i).get("YEAR_MONTH")).equals(String.valueOf(routeList.get(j).get("YEARMONTH")))) {
						
						// 找到对应的终端
						if (String.valueOf(tchList.get(i).get("STORE_ID")).equals(String.valueOf(routeList.get(j).get("STORE_ID")))
								&& String.valueOf(tchList.get(i).get("DIVISION_SID")).equals(String.valueOf(routeList.get(j).get("DIVSION_SID")))) {
							
								empId = empId + "," + String.valueOf(routeList.get(j).get("EMP_ID"));
								empName = empName + "," + String.valueOf(routeList.get(j).get("EMP_NAME"));
							}
					}
				}
				
				if (StringUtils.isNotEmpty(empId) && StringUtils.isNotEmpty(empName)) {
					
					// 将业代资料放入tchlist中
					map.put("USER_ACCOUNT_SALES", empId.substring(1));
					map.put("USER_NAME_SALES", empName.substring(1));
					
					// 清空使用完的业代工号和业代姓名
					empId  = "";
					empName = "";
				}
			}
		}
	}

	/**
	 * @return the kaActualCheckReportDao
	 */
	public KaActualCheckReportDao getKaActualCheckReportDao() {
		return kaActualCheckReportDao;
	}

	/**
	 * @param kaActualCheckReportDao the kaActualCheckReportDao to set
	 */
	public void setKaActualCheckReportDao(
			KaActualCheckReportDao kaActualCheckReportDao) {
		this.kaActualCheckReportDao = kaActualCheckReportDao;
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

	/**
	 * @return the routeInfoDao
	 */
	public RouteInfoDao getRouteInfoDao() {
		return routeInfoDao;
	}

	/**
	 * @param routeInfoDao the routeInfoDao to set
	 */
	public void setRouteInfoDao(RouteInfoDao routeInfoDao) {
		this.routeInfoDao = routeInfoDao;
	}

	/**
	 * @return the kaSystemEmpDao
	 */
	public KaSystemEmpDao getKaSystemEmpDao() {
		return kaSystemEmpDao;
	}

	/**
	 * @param kaSystemEmpDao the kaSystemEmpDao to set
	 */
	public void setKaSystemEmpDao(KaSystemEmpDao kaSystemEmpDao) {
		this.kaSystemEmpDao = kaSystemEmpDao;
	}

	
}
