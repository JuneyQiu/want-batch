package com.want.batch.job.sfa2.monitor.jobs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.sfa2.monitor.pojo.MonitorParam;
import com.want.batch.job.sfa2.monitor.util.ParamUtil;
@Component
public class QueryMonitorParam extends AbstractWantJob {
	private static String cp = "param.properties";

	String sql = "select  distinct project_sid,emp_id,'00'||forwarder_id as customer_id ,yearmonth from hw09.route_info_view a"
			+ " where yearmonth=? and a.company_sid = 17 and project_sid =12  and emp_id not like 'XX%'";

	@Override
	public void execute() throws Exception {
		MonitorParam param = getMonitorParam();
		ParamUtil.writeParamToFile(param);
	}

	public MonitorParam getMonitorParam() {
		List<Map<String, Object>> list = this.getHw09JdbcOperations()
				.queryForList(sql, new Object[] { getCurrentYearMonth() });
		if(null == list || list.size()==0){
			return null;
		}
		Map<String, Object> map = list.get(0);
		MonitorParam param = new MonitorParam();
		param.setProjectSid(map.get("project_sid").toString());
		param.setEmpId(map.get("emp_id").toString());
		param.setCustomerId(map.get("customer_id").toString());
		param.setDivisionSid(15 + "");
		param.setYearmonth(map.get("yearmonth").toString());

		return param;

	}

	/**
	 * 获取本月的YearMonth getCurrentYearMonth
	 * 
	 * @return String
	 */
	public String getCurrentYearMonth() {
		String YEARMONTH = "";
		try {
			SimpleDateFormat ymd = new SimpleDateFormat("yyyyMM");
			Timestamp s_date = new Timestamp(System.currentTimeMillis());
			YEARMONTH = ymd.format(s_date);
		} catch (Exception e) {

		}
		return YEARMONTH;
	}


	public static void main(String[] args) {
		// 当前类的绝对路径
		System.out.println(QueryMonitorParam.class.getResource("/").getFile());
		// 指定CLASSPATH文件的绝对路径
		System.out.println(QueryMonitorParam.class.getResource("/").getFile());
		// 指定CLASSPATH文件的绝对路径
		File f = new File(QueryMonitorParam.class.getResource("/").getFile());
		System.out.println(f.getPath());
	}
}
