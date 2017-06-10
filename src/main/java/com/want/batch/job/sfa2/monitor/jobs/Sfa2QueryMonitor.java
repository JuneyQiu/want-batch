package com.want.batch.job.sfa2.monitor.jobs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.sfa2.monitor.pojo.MonitorParam;
import com.want.batch.job.sfa2.monitor.util.ParamUtil;
import com.want.batch.job.sfa2.monitor.util.SMSService;
import com.want.batch.job.utils.ProjectConfig;

@Component
public class Sfa2QueryMonitor extends AbstractWantJob {
	@Autowired
	private SendMail sendMail;
	
	private static int timeOut = Integer.parseInt(ProjectConfig.getInstance().getString("sfa.db.monitor.timeout"));
	private static String interfaceName;
	
	private static String baseDataSql="select * from sfa2.BASE_DATA";
	
	private static String storeSql = "select a.STORE_ID,a.STORE_NAME,a.ACREAGE,a.STORE_OWNER,a.ZIP,a.LATITUDE,a.LONGITUDE ,a.MOBILEPHONE,a.FIXEDPHONE,max(a.FILE_SID)as FILE_SID,a.FORTH_NAME, "
			+ "a.ADD1,a.ADD2,a.ADD3,a.ADD4,a.ADD1TYPESID,a.ADD2TYPESID,a.ADD3TYPESID,a.TYPE_NAME,a.STATUS from( "
			+ "SELECT a.*,f.FILE_SID,(b.COMPANY_NAME||'-'||b.BRANCH_NAME||'-'||b.THIRD_NAME||'-'||b.FORTH_NAME)AS FORTH_NAME,d.TYPE_NAME FROM MDM.STORE_INFO a "
			+ "INNER JOIN MDM.FULL_ORG_REL b ON a.FORTH_SID=b.FORTH_SID INNER JOIN MDM.STORE_TYPE_REL c ON a.SID=c.STORE_SID  "
			+ "INNER JOIN MDM.TYPE_INFO d ON c.TYPE_SID=d.SID INNER JOIN (SELECT DISTINCT c.STORE_ID FROM ROUTEMGR.WS_SALES_CONFIG a "
			+ "INNER JOIN ROUTEMGR.WS_AREA_REL b ON a.SID=b.CONFIG_SID INNER JOIN ROUTEMGR.AREA_STORE_REL c ON b.AREA_SID=c.AREA_SID WHERE a.EMP_ID=?)e  "
			+ "ON a.STORE_ID=e.store_id LEFT JOIN MDM.STORE_PHOTO_REL f ON a.sid=f.STORE_SID) a "
			+ "group by a.STORE_ID,a.STORE_NAME,a.ACREAGE,a.STORE_OWNER,a.ZIP,a.LATITUDE,a.LONGITUDE,a.MOBILEPHONE,a.FIXEDPHONE,a.FORTH_NAME,a.ADD1,a.ADD2,a.ADD3,a.ADD4,a.ADD1TYPESID,a.ADD2TYPESID,a.ADD3TYPESID,a.TYPE_NAME,a.STATUS";

	@Override
	public void execute() throws Exception {
		queryBaseData();
		queryStore();
	}
	
	public void queryBaseData(){
		long startTime = System.currentTimeMillis();
		logger.debug("queryBaseData start: "+startTime);
		List<Map<String, Object>> list = this.getSfa2JdbcOperations()
				.queryForList(baseDataSql, new Object[] {});
		double usedTime = (System.currentTimeMillis()-startTime)/1000.0;
		if(usedTime>=timeOut){
			interfaceName ="BaseData(SFA2)";
			SMSService.sendSMS(getNoticeContent(interfaceName), SMSService.getMobiles());
			sendMail.send(getNoticeContent(interfaceName));
		}
		logger.debug("queryBaseData  end ......Used Time:"+usedTime+" s");

	}
	
	public void queryStore() throws FileNotFoundException, IOException{
		MonitorParam param = ParamUtil.getParamFromFile();
		long startTime = System.currentTimeMillis();
		logger.debug("queryStore start: "+startTime);
		List<Map<String, Object>> list = this.getSfa2JdbcOperations()
				.queryForList(storeSql, new Object[] {param.getEmpId()});
		double usedTime = (System.currentTimeMillis()-startTime)/1000.0;
		if(usedTime>=timeOut){
			interfaceName ="StoreInfo(MDM,ROUTEMGR)";
			SMSService.sendSMS(getNoticeContent(interfaceName), SMSService.getMobiles());
			sendMail.send(getNoticeContent(interfaceName));
		}
		logger.debug("queryStore  end ......;emp:"+param.getEmpId()+";Used Time:"+usedTime+" s");

	}
	
	public String getNoticeContent(String interfaceName){
		String content="The Sql for interface【"+ interfaceName +"】 query timeout, please find out the cause ";
		return content;
	}
	
}
