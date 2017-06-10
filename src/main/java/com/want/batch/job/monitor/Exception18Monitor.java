package com.want.batch.job.monitor;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;

@Component
public class Exception18Monitor extends AbstractWantJob {

	@Override
	public void execute() {
		logger.info("Exception18Monitor排程开始。start!");
		
		List<Map<String, Object>> results = checkException18Data();
		if(results.size() > 0){
			logger.info("short massage insert successed!" + insertSMS(results, "Exception18 happened!"));
			logger.info("Exception Data deleted successed!" + deleteErrorData());
			logger.info("short massage insert successed!" + insertSMS(results, "Exception18 excluded!"));
		}
		
		logger.info("Exception18Monitor排程开始。end!");

	}
	
	private List<Map<String, Object>> checkException18Data() {
		
		// 检查异常日期是否存在SQL
		StringBuffer checkSQL = new StringBuffer();
		checkSQL.append(" SELECT SID ");
		checkSQL.append(" FROM ALL_ABNORMALITY_INFO_TBL ");
		checkSQL.append(" WHERE ");
		checkSQL.append(" TO_CHAR(CREATE_DATE,'HH24') = '00' ");
		checkSQL.append(" AND CREATE_DATE<TRUNC(sysdate)+1/2  ");
		checkSQL.append(" AND CREATE_DATE>TRUNC(sysdate-1)+1/2  ");
		checkSQL.append(" AND ABNORMAL_TYPE ='18'  ");
		
		List<Map<String, Object>> results = this.getiCustomerJdbcOperations().queryForList(checkSQL.toString());
		return results;
	}
	
	// 删除异常资料
	private int deleteErrorData() {
		int dsum = 0;

		StringBuffer deleteSQL = new StringBuffer();
		deleteSQL.append(" DELETE ALL_ABNORMALITY_INFO_TBL ");
		deleteSQL.append(" WHERE ");
		deleteSQL.append(" TO_CHAR(CREATE_DATE,'HH24') = '00' ");
		deleteSQL.append(" AND CREATE_DATE<TRUNC(sysdate)+1/2  ");
		deleteSQL.append(" AND CREATE_DATE>TRUNC(sysdate-1)+1/2  ");
		deleteSQL.append(" AND ABNORMAL_TYPE ='18'  ");
		
		dsum = this.getiCustomerJdbcOperations().update(deleteSQL.toString());
		
		return dsum;
	}
	
	// 发送短信通知
	private int insertSMS(List<Map<String, Object>> results, String massage) {
		int sum = 0;
		if (results.size() > 0) {
			StringBuffer insertSMSSQL = new StringBuffer();
			insertSMSSQL.append("");
			insertSMSSQL.append(" INSERT INTO ");
			insertSMSSQL.append("   SMS_TBL ");
			insertSMSSQL.append("   (SID, ");
			insertSMSSQL.append("   FUNC_SID, ");
			insertSMSSQL.append("   FUNC_CASE_ID, ");
			insertSMSSQL.append("   CREATE_TIME, ");
			insertSMSSQL.append("   UPDATE_TIME, ");
			insertSMSSQL.append("   SEND_TIME, ");
			insertSMSSQL.append("   PHONE_NUMBER, ");
			insertSMSSQL.append("   CONTENT, ");
			insertSMSSQL.append("   STATUS, ");
			insertSMSSQL.append("   LOG) ");
			insertSMSSQL.append(" VALUES ");
			insertSMSSQL.append("   ( ");
			insertSMSSQL.append("   SMS_SID_SEQ.NEXTVAL,");
			insertSMSSQL.append("   7, ");
			insertSMSSQL.append("   '00076072', ");
			insertSMSSQL.append("   SYSDATE, ");
			insertSMSSQL.append("   NULL, ");
			insertSMSSQL.append("   SYSDATE, ");
			insertSMSSQL.append("   '13524165886', ");//TEL
			insertSMSSQL.append("   '").append(massage).append("', ");//
			insertSMSSQL.append("   1, ");
			insertSMSSQL.append("   NULL) ");
			
			sum = this.getPortalJdbcOperations().update(insertSMSSQL.toString());
		}
		return sum;
	}

}

