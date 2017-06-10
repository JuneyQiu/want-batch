package com.want.batch.job.sfa2.monitor.jobs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.sfa2.monitor.pojo.MonitorParam;
import com.want.batch.job.sfa2.monitor.util.ParamUtil;
import com.want.batch.job.sfa2.monitor.util.SMSService;
import com.want.batch.job.utils.ProjectConfig;
import com.want.component.mail.MailService;

@Component
public class ICustomerQueryMonitor extends AbstractWantJob {
	@Autowired
	private SendMail sendMail;
	private static int timeOut = Integer.parseInt(ProjectConfig.getInstance().getString("sfa.db.monitor.timeout"));

	private static String interfaceName;
	private static String visitPlan = "SELECT forwarder.FORWARDER_ID ,div.DIVSION_SID ,subroute.VISIT_DATE,subroute.SID,substore.VISIT_ORDER,store.MDM_STORE_ID FROM "
			+ "ROUTE_INFO_TBL route INNER JOIN HW09.FORWARDER_INFO_TBL forwarder ON route.FORWARDER_SID = forwarder.sid INNER JOIN  HW09.EMP_INFO_TBL emp ON emp.SID=route.EMP_SID "
			+ "INNER JOIN ICUSTOMER.DIVSION_PROJECT_REL div ON route.project_sid = div.PROJECT_SID INNER JOIN  SUBROUTE_INFO_TBL subroute ON subroute.ROUTE_SID = route.SID "
			+ "INNER JOIN SUBROUTE_STORE_TBL substore ON substore.SUBROUTE_SID = subroute.SID INNER JOIN  STORE_INFO_TBL store ON store.SID = substore.STORE_SID "
			+ "WHERE  route.PROJECT_SID=? AND emp.EMP_ID=?  AND route.YEARMONTH= ?";

	private static String sdapplication = "SELECT a.SID,s.MDM_STORE_ID,b.APPLICATION_SID,a.SID AS STORE_DISPLAY_SID,a.ASSETS_ID,a.DISPLAY_ACREAGE,a.DISPLAY_SIDECOUNT,a.DISPLAY_TYPE_SID,a.LOCATION_TYPE_SID "
			+ "FROM APPLICATION_STORE_DISPLAY a INNER JOIN APPLICATION_STORE b ON a.APPLICATION_STORE_SID = b.SID INNER JOIN SPECIAL_DISPLAY_APPLICATION c ON c.SID= b.APPLICATION_SID "
			+ "INNER JOIN SPECIAL_DISPLAY_POLICY d ON c.POLICY_SID = d.SID INNER JOIN CUSTOMER_INFO_TBL e ON c.CUSTOMER_SID = e.SID INNER JOIN HW09.STORE_INFO_TBL s ON b.STORE_ID= s.STORE_ID "
			+ "WHERE e.ID in ( ? ) AND c.YEAR_MONTH=? AND d.DIVISION_SID= ? ";

	@Override
	public void execute() throws Exception {
		queryVisitPlan();
		querySDApplication();
	}

	public void queryVisitPlan() throws FileNotFoundException, IOException {
		MonitorParam param = ParamUtil.getParamFromFile();
		long startTime = System.currentTimeMillis();
		logger.debug("queryVisitPlan start: "+startTime);
		List<Map<String, Object>> list = this.getHw09JdbcOperations()
				.queryForList(visitPlan, new Object[] {param.getProjectSid(),param.getEmpId(),param.getYearmonth()});
		double usedTime = (System.currentTimeMillis()-startTime)/1000.0;
		if(usedTime>=timeOut){
			interfaceName ="VisitPlan(HW09)";
			SMSService.sendSMS(getNoticeContent(interfaceName), SMSService.getMobiles());
			sendMail.send(getNoticeContent(interfaceName));

		}
		logger.debug("queryVisitPlan  end ......;emp:"+param.getEmpId()+";Used Time:"+usedTime+" s");

	}
	
	
	public void querySDApplication() throws FileNotFoundException, IOException{
		MonitorParam param = ParamUtil.getParamFromFile();
		long startTime = System.currentTimeMillis();
		logger.debug("querySDApplication start: "+startTime);
		List<Map<String, Object>> list = this.getiCustomerJdbcOperations()
				.queryForList(sdapplication, new Object[] {param.getCustomerId(),param.getYearmonth(),param.getDivisionSid()});
		double usedTime = (System.currentTimeMillis()-startTime)/1000.0;
		if(usedTime>=timeOut){
			interfaceName ="SDApplication(ICUSTOMER)";
			SMSService.sendSMS(getNoticeContent(interfaceName), SMSService.getMobiles());
			sendMail.send(getNoticeContent(interfaceName));

		}
		logger.debug("querySDApplication  end ......;customer: "+param.getCustomerId()+";Used Time:"+usedTime+" s");

	}
	
	public String getNoticeContent(String interfaceName){
		String content="The Sql for interface【"+ interfaceName +"】 query timeout, please find out the cause ";
		return content;
	}
	
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
}
