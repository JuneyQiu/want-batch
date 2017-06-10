package com.want.batch.job.sfa2.monitor.jobs;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.sfa2.monitor.util.SMSService;
import com.want.batch.job.utils.ProjectConfig;
@Component
public class Sfa2InsertAndDelMonitor extends AbstractWantJob{
	@Autowired
	private SendMail sendMail;
	
	private static String opertaorName;
	private static int timeOut = Integer.parseInt(ProjectConfig.getInstance().getString("sfa.db.monitor.timeout"));
	
	private static String atrrInsert ="INSERT INTO SFA2.ATTRIBUTE_INFO  VALUES ( '9999999',  'ABCD', 'TEST',  '测试数据')";
	private static String attrDel ="DELETE SFA2.ATTRIBUTE_INFO WHERE ATTRIBUTE_KEY ='9999999' and ATTRIBUTE_ID ='TEST'";
	private static String attrQuery = "SELECT * FROM SFA2.ATTRIBUTE_INFO WHERE ATTRIBUTE_KEY ='9999999' and ATTRIBUTE_ID ='TEST'";
	@Override
	public void execute() throws Exception {
		insertTestAttribute();
		deleteTestAttribute();
	}

	public void insertTestAttribute(){
		if(queryTestAttribute()){
			logger.info("TestAttribute already exists......");
			return ;
		}
		long startTime = System.currentTimeMillis();
		logger.debug("insertTestAttribute start: "+startTime);
		int insert = this.getSfa2JdbcOperations().update(atrrInsert, new Object[]{});
		double usedTime = (System.currentTimeMillis()-startTime)/1000.0;
		if(usedTime>=timeOut){
			opertaorName ="insert (SFA2)";
			SMSService.sendSMS(getNoticeContent(opertaorName), SMSService.getMobiles());
			sendMail.send(getNoticeContent(opertaorName));

		}
		logger.debug("insertTestAttribute  end ...... ;insert success:  "+insert+"条，Used Time:"+usedTime+" s");

	}
	
	public void deleteTestAttribute(){
		if(!queryTestAttribute()){
			logger.info("TestAttribute not exist......");
			return ;
		}
		long startTime = System.currentTimeMillis();
		logger.debug("deleteTestAttribute start: "+startTime);
		int insert = this.getSfa2JdbcOperations().update(attrDel, new Object[]{});
		double usedTime = (System.currentTimeMillis()-startTime)/1000.0;
		if(usedTime>=timeOut){
			opertaorName ="delete (SFA2)";
			SMSService.sendSMS(getNoticeContent(opertaorName), SMSService.getMobiles());
			sendMail.send(getNoticeContent(opertaorName));

		}
		logger.debug("deleteTestAttribute  end ...... ;delete success:  "+insert+"条，Used Time:"+usedTime+" s");

	}
	
	public boolean queryTestAttribute(){
		List<Map<String, Object>> list = this.getSfa2JdbcOperations().queryForList(attrQuery, new Object[]{});
		if(null == list || list.size()<=0){
			return false;
		}else{
			return true;
		}
		
	}
	
	public String getNoticeContent(String opertaorName){
		String content="The table【ATTRIBUTE_INFO】 "+ opertaorName +" timeout, please find out the cause ";		
		return content;
	}
}
