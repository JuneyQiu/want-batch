package com.want.batch.job.monitor.runtime;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;

import com.want.batch.job.AbstractWantJob;
import com.want.service.SmsModel;

public class MonitorResponseTimeByAccessLog extends AbstractWantJob {

	private String downloadAccessFileRootPath;
	private String tempDirectPrefix;
	
	private int onceUpdateCount = 1000;
	private String splitPattern;
	private Map<Integer, String> columnIndexMap = new HashMap<Integer, String>();
	private String[] columns;
	private int accessLogFirstIndex = 0;
	private int maxResponseTime = 100;
	private int monitorResponseTime = 5;
	private List<String> accessLogContains = new ArrayList<String>();
	private Map<Long, String> phoneInfos = new HashMap<Long, String>();
	
	@Override
	public void execute() {
		File tempDirect = new File(String.format("%s\\%s", this.downloadAccessFileRootPath, tempDirectPrefix));
		
		logger.info("clear table start... ");
		this.getDataMartJdbcOperations().update("truncate table RPTLOG.ACCESS_LOG_DAILY");
		logger.info("clear table over. ");
		
		for (File file : tempDirect.listFiles()) {
			importData(file);
		}
		
		monitor();
	}

	@SuppressWarnings("unchecked")
	private void importData(File file) {

		// 准备写入db的语句
		String insertAccessLogSql = new StringBuilder()
			.append("INSERT INTO RPTLOG.ACCESS_LOG_DAILY ( ")
			.append("  CLIENT_IP, SERVER_IP, LOG_DATE, LOG_TIME, ")
			.append("  HTTP_METHOD, ACCESS_URI, STATUS_CODE, RESPONSE_TIME, BYTES )")
			.append("VALUES (")
			.append("  :clientIp, :serverIp, :date, :time, ")
			.append("  :httpMethod, :uri, :statusCode, :responseTime, :bytes )")
			.toString();

		String fileName = file.getName();

		// 导入笔数的计数器
		int count = 0;
		logger.info(fileName + " loading ... " + count);
		
		try {

			// 获取 local access log file
			List<String> accessLogs = FileUtils.readLines(file);

			// 获取数据，每次 onceUpdateCount 笔
			for (int i = this.accessLogFirstIndex; i < accessLogs.size(); i += this.onceUpdateCount) {
				int to = (i + this.onceUpdateCount < accessLogs.size()) ? 
						 (i + this.onceUpdateCount) : accessLogs.size();

				List<Map<String, ?>> accessLogMaps = new ArrayList<Map<String, ?>>();
				
				// 获取一次插入的数据
				for (String accessLog : accessLogs.subList(i, to)) {
					Map<String, ?> accessLogParameterMap = this.accessLogParameters(accessLog, StringUtils.replace(fileName, "_", ":"));
					if (!accessLogParameterMap.isEmpty()) {
						accessLogMaps.add(accessLogParameterMap);
					}
				}

				// 添加数据
				int[] onceCounts = this.getDataMartJdbcOperations().batchUpdate(
					insertAccessLogSql, 
					accessLogMaps.toArray(new Map[]{})
				);
				
				logger.debug(fileName + "loading ... " + (count += onceCounts.length));
			}

			logger.info(fileName + "loading over. " + count);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private void monitor() {
		
		String monitorHour = new DateTime().minusHours(1).toString("HH");
		
		List<Map<String, Object>> datas = 
			this.getDataMartJdbcOperations().queryForList(
				new StringBuilder()
					.append("SELECT SERVER_IP, COUNT(1) COUNT, AVG(RESPONSE_TIME) AVG_TIME ")
					.append("FROM RPTLOG.ACCESS_LOG_DAILY ")
					.append("WHERE SUBSTR(LOG_TIME, 0, 2) = ? ")
					.append("GROUP BY SERVER_IP ")
					.toString(), 
				monitorHour
			);

		logger.debug("monitor datas >>> " + datas);

		List<String> smsContents = new ArrayList<String>();

		for (Map<String, Object> data : datas) {
			String avgTime = data.get("AVG_TIME").toString();
			if (Double.parseDouble(avgTime) > this.monitorResponseTime) {
				smsContents.add(String.format("%s(%ss)", data.get("SERVER_IP"), StringUtils.left(avgTime, 4)));
			}
		}

		logger.debug("monitor sms contents >>> " + smsContents);
		
		if (smsContents.size() > 0) {
			
			Date currentDate = new Date();
			
			SmsModel smsModel = new SmsModel();
			smsModel.setFuncSid(7);
			smsModel.setFuncCaseId("ECAD MORITOR");
			smsModel.setCreateDate(currentDate);
			smsModel.setSendDate(currentDate);
			smsModel.setContent(String.format("%s at %s.", smsContents, monitorHour));

			for (Map.Entry<Long, String> phoneInfo : this.phoneInfos.entrySet()) {
				
				smsModel.setPhoneNumber(phoneInfo.getKey());
				smsModel.setPersonalName(phoneInfo.getValue());
				
				this.getSmsSendService().send(smsModel);
			}
			
			logger.debug("sms content >>> " + smsModel.getContent());
		}
		else {
			logger.info(String.format("Bpm response time is normal at %s~", monitorHour));
		}
	}

	private Map<String, ?> accessLogParameters(String accessLog, String serverIp) {

		String[] accessLogColumns = StringUtils.split(accessLog, this.splitPattern);

		Map<String, Object> parameters = new HashMap<String, Object>();

		for (int i = 0; i < accessLogColumns.length; i++) {
			parameters.put(this.columnIndexMap.get(i), accessLogColumns[i]);
		}

		if (checked(parameters)) {

			// 依照 serverName:port 的方式塞入serverIp栏位
			parameters.put("serverIp", serverIp);

			try {
				String date = parameters.get("date").toString();
				String time = parameters.get("time").toString();

				DateTime datetime = 
					new DateTime(DateUtils.parseDate(date + time, new String[]{"yyyy-MM-ddHH:mm:ss"}))
						.plusHours(8);

				parameters.put("date", datetime.toString("yyyy-MM-dd"));
				parameters.put("time", datetime.toString("HH:mm:ss"));
			} catch (ParseException pe) {
				throw new RuntimeException(pe);
			}
		}
		else {
			parameters.clear();
		}

		return parameters;
	}
	
	private boolean checked(Map<String, Object> parameters) {

		Object uri = parameters.get("uri");
		if (uri == null || StringUtils.isBlank(uri.toString())) {
			return false;
		}

		Object responseTime = parameters.get("responseTime");
		if (responseTime == null || StringUtils.isBlank(responseTime.toString())) {
			return false;
		}

		return checkUri(parameters.get("uri").toString()) 
			&& checkResponseTime(Double.parseDouble(parameters.get("responseTime").toString()));
	}

	private boolean checkResponseTime(double responseTime) {
		return this.maxResponseTime <= 0 || responseTime < this.maxResponseTime;
	}

	private boolean checkUri(String uri) {
		
		for (String containString : this.accessLogContains) {
			if (uri.contains(containString)) {
				return true;
			}
		}
		return false;
	}

	public void setDownloadAccessFileRootPath(String downloadAccessFileRootPath) {
		this.downloadAccessFileRootPath = downloadAccessFileRootPath;
	}

	public void setOnceUpdateCount(int onceUpdateCount) {
		this.onceUpdateCount = onceUpdateCount;
	}

	public void setSplitPattern(String splitPattern) {
		this.splitPattern = splitPattern;
	}

	public void setColumns(String columns) {
		this.columns = StringUtils.split(columns, this.splitPattern);
		for (int i = 0; i < this.columns.length; i++) {
			this.columnIndexMap.put(i, this.columns[i]);
		}
	}

	public void setAccessLogFirstIndex(int accessLogFirstIndex) {
		this.accessLogFirstIndex = accessLogFirstIndex;
	}

	public void setMaxResponseTime(int maxResponseTime) {
		this.maxResponseTime = maxResponseTime;
	}

	public void setMonitorResponseTime(int monitorResponseTime) {
		this.monitorResponseTime = monitorResponseTime;
	}

	public void setAccessLogContains(List<String> accessLogContains) {
		this.accessLogContains = accessLogContains;
	}

	public void setTempDirectPrefix(String tempDirectPrefix) {
		this.tempDirectPrefix = tempDirectPrefix;
	}

	public void setPhoneInfos(Map<Long, String> phoneInfos) {
		this.phoneInfos = phoneInfos;
	}

}
