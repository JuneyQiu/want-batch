package com.want.batch.job.monitor;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;

@Component
public class TaskStoreListMonitor extends AbstractWantJob {

	@Override
	public void execute() {

		logger.info("TaskStoreListMonitor排程开始。");

		List<Map<String, Object>> results = checkErrorData();
		
		insertErrorData(results);
		
		modifyErrorData(results);
		
		insertSMS(results);
		
		logger.info("TaskStoreListMonitor排程结束。");
	}

	private void modifyErrorData(List<Map<String, Object>> results) {
		
		for (Map<String, Object> result : results) {

			// 更新表：TASK_STORE_LIST，把错误的年份改成当前年份
			StringBuffer updateSQL = new StringBuffer();
			updateSQL.append(" UPDATE TASK_STORE_LIST ");
			updateSQL.append(" SET ");
			updateSQL.append(" ETIME=TO_DATE((TO_CHAR(SYSDATE, 'yyyy') || TO_CHAR(ETIME,'MM-dd HH24:mi:ss')),'yyyy-MM-dd HH24:mi:ss'), ");
			updateSQL.append(" STIME=TO_DATE((TO_CHAR(SYSDATE, 'yyyy') || TO_CHAR(STIME,'MM-dd HH24:mi:ss')),'yyyy-MM-dd HH24:mi:ss'), ");
			updateSQL.append(" CREATE_DATE=TO_DATE((TO_CHAR(SYSDATE, 'yyyy') || TO_CHAR(CREATE_DATE,'MM-dd HH24:mi:ss')),'yyyy-MM-dd HH24:mi:ss'), ");
			updateSQL.append(" UPDATE_DATE=TO_DATE((TO_CHAR(SYSDATE, 'yyyy') || TO_CHAR(UPDATE_DATE,'MM-dd HH24:mi:ss')),'yyyy-MM-dd HH24:mi:ss') ");
			updateSQL.append(" WHERE ");
			updateSQL.append(" SID=? ");
			
			int updates = this.getiCustomerJdbcOperations().update(updateSQL.toString(), result.get("SID"));
			logger.info("更新 TASK_STORE_LIST 笔树: " + updates);
		}
	}

	private void insertErrorData(List<Map<String, Object>> results) {
		
		if (results.size() > 0) {

			// 把异常时间的数据插入到错误记录表SQL
			StringBuffer insertSQL = new StringBuffer();
			insertSQL.append(" INSERT INTO TASK_STORE_LIST_ERROR ");
			insertSQL.append(" SELECT SID,TASK_STORE_SUBROUTE_SID,STORE_ID,STORE_NAME,IS_FIND,IS_APPLY,STIME,ETIME,REASON_LINE,LONGITUDE,LATITUDE,CREATE_USER,CREATE_DATE,UPDATE_USER,UPDATE_DATE,STORE_ABNORMAL_STATE,SPEC_ARR,SPEC_ARR_REA,IS_PASTDUE,IS_PASTDUE_REA,IS_TCH_APPLY,IS_CANCEL,CANCEL_REASON FROM TASK_STORE_LIST ");
			insertSQL.append(" WHERE ");
			insertSQL.append(" TO_NUMBER(TO_CHAR(ETIME, 'yyyy')) < 2010 OR TO_NUMBER(TO_CHAR(ETIME, 'yyyy')) > TO_NUMBER(TO_CHAR(SYSDATE, 'yyyy')) ");
			insertSQL.append(" OR ");
			insertSQL.append(" TO_NUMBER(TO_CHAR(STIME, 'yyyy')) < 2010 OR TO_NUMBER(TO_CHAR(STIME, 'yyyy')) > TO_NUMBER(TO_CHAR(SYSDATE, 'yyyy')) ");
			insertSQL.append(" OR ");
			insertSQL.append(" TO_NUMBER(TO_CHAR(CREATE_DATE, 'yyyy')) < 2010 OR TO_NUMBER(TO_CHAR(CREATE_DATE, 'yyyy')) > TO_NUMBER(TO_CHAR(SYSDATE, 'yyyy')) ");
			insertSQL.append(" OR ");
			insertSQL.append(" TO_NUMBER(TO_CHAR(UPDATE_DATE, 'yyyy')) < 2010 OR TO_NUMBER(TO_CHAR(UPDATE_DATE, 'yyyy')) > TO_NUMBER(TO_CHAR(SYSDATE, 'yyyy')) ");		
			
			int inserts = getiCustomerJdbcOperations().update(insertSQL.toString());
			logger.info("新增TASK_STORE_LIST_ERROR 笔树: " + inserts);
		}
	}

	private List<Map<String, Object>> checkErrorData() {
		
		// 检查异常日期是否存在SQL
		StringBuffer checkSQL = new StringBuffer();
		checkSQL.append(" SELECT SID FROM TASK_STORE_LIST ");
		checkSQL.append(" WHERE ");
		checkSQL.append(" TO_NUMBER(TO_CHAR(ETIME, 'yyyy')) < 2010 OR TO_NUMBER(TO_CHAR(ETIME, 'yyyy')) > TO_NUMBER(TO_CHAR(SYSDATE, 'yyyy')) ");
		checkSQL.append(" OR ");
		checkSQL.append(" TO_NUMBER(TO_CHAR(STIME, 'yyyy')) < 2010 OR TO_NUMBER(TO_CHAR(STIME, 'yyyy')) > TO_NUMBER(TO_CHAR(SYSDATE, 'yyyy')) ");
		checkSQL.append(" OR ");
		checkSQL.append(" TO_NUMBER(TO_CHAR(CREATE_DATE, 'yyyy')) < 2010 OR TO_NUMBER(TO_CHAR(CREATE_DATE, 'yyyy')) > TO_NUMBER(TO_CHAR(SYSDATE, 'yyyy')) ");
		checkSQL.append(" OR ");
		checkSQL.append(" TO_NUMBER(TO_CHAR(UPDATE_DATE, 'yyyy')) < 2010 OR TO_NUMBER(TO_CHAR(UPDATE_DATE, 'yyyy')) > TO_NUMBER(TO_CHAR(SYSDATE, 'yyyy')) ");
		
		List<Map<String, Object>> results = this.getiCustomerJdbcOperations().queryForList(checkSQL.toString());
		logger.info("取出笔树: " + results.size());
		return results;
	}
	
	private void insertSMS(List<Map<String, Object>> results) {
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
			insertSMSSQL.append("   '00084420', ");
			insertSMSSQL.append("   SYSDATE, ");
			insertSMSSQL.append("   NULL, ");
			insertSMSSQL.append("   SYSDATE, ");
			insertSMSSQL.append("   '13818656286', ");//TEL
			insertSMSSQL.append("   '").append("时间异常的记录个数为："+results.size()).append("', ");//
			insertSMSSQL.append("   1, ");
			insertSMSSQL.append("   NULL) ");
			
			this.getPortalJdbcOperations().update(insertSMSSQL.toString());
			logger.info("新增 SMS 笔树: " + results.size());
		} 
	}
}

